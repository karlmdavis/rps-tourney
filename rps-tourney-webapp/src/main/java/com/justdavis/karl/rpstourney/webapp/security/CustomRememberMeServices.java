package com.justdavis.karl.rpstourney.webapp.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Component;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.webapp.config.AppConfig;
import com.justdavis.karl.rpstourney.webapp.util.CookiesUtils;

/**
 * <p>
 * A custom Spring Security {@link RememberMeServices} implementation. Allows
 * for the use of cookie authentication tokens that authenticate requests to a
 * user's {@link Account}.
 * </p>
 * <p>
 * This custom implementation is necessary, as
 * {@link PersistentTokenBasedRememberMeServices} requires a
 * {@link UserDetailsService}, which in turn requires that every account have a
 * username. If, at some point, {@link Account}s do all have a unique username,
 * this class can be replaced.
 * </p>
 */
@Component
public class CustomRememberMeServices implements RememberMeServices {
	/**
	 * The {@link Cookie#getName()} value for the "remember me" cookie used by
	 * this class.
	 */
	static final String COOKIE_NAME = "AuthToken";

	/**
	 * <p>
	 * This is the value used to construct
	 * {@link RememberMeAuthenticationToken#getKeyHash()} for all
	 * {@link RememberMeAuthenticationToken}s created by the application.
	 * </p>
	 * <p>
	 * In the standard {@link RememberMeServices} implementations, this value is
	 * included in all of the cookies, as some sort of guard against cookie
	 * spoofing. That, however, seems wholly ineffective as cookies are in no
	 * way unreadable. Accordingly, we're just using a hardcoded string here.
	 * </p>
	 */
	static final String REMEMBER_ME_TOKEN_KEY = "foobar";

	private static final AuthenticationDetailsSource<HttpServletRequest, ?> AUTH_DETAILS_BUILDER = new WebAuthenticationDetailsSource();

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomRememberMeServices.class);

	private final AppConfig appConfig;
	private final CookieStore serviceClientCookieStore;
	private final IAccountsResource accountsClient;

	/**
	 * Constructs a new {@link CustomRememberMeServices} instance.
	 * 
	 * @param appConfig
	 *            the {@link AppConfig} for the application
	 * @param serviceClientCookieStore
	 *            the injected {@link CookieStore} for all web service clients
	 *            (session scoped)
	 * @param accountsClient
	 *            the injected {@link AccountsClient} to use
	 */
	@Inject
	public CustomRememberMeServices(AppConfig appConfig,
			CookieStore serviceClientCookieStore,
			IAccountsResource accountsClient) {
		this.appConfig = appConfig;
		this.serviceClientCookieStore = serviceClientCookieStore;
		this.accountsClient = accountsClient;
	}

	/**
	 * @see org.springframework.security.web.authentication.RememberMeServices#autoLogin(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Authentication autoLogin(HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.trace("Authenticating request...");

		/*
		 * This method will be called during every unauthenticated request,
		 * giving this class a chance to check for a valid "remember me" login
		 * token, and authenticate the rest of the request.
		 */

		// Grab the cookie (if any).
		Cookie rememberMeCookie = CookiesUtils.extractCookie(COOKIE_NAME,
				request);
		if (rememberMeCookie == null) {
			LOGGER.trace("Request had no auth token.");
			return null;
		}
		LOGGER.trace("Request had auth token.");

		// Extract the authentication token from the cookie.
		String rememberMeValue = rememberMeCookie.getValue();
		if (rememberMeValue == null || rememberMeValue.length() <= 0) {
			LOGGER.warn("Cookie had no value: '{}'", rememberMeValue);
			return null;
		}

		/*
		 * Snag the auth token from this web app cookie, and use it to create a
		 * cookie in the web service client. This cookie will saved in our
		 * CookieStore, and used in turn by any web service clients in the same
		 * session. (This is a bit hokey, but it saves us from needing to create
		 * our token store in this layer of the application.)
		 */
		String requestUrlString = request.getRequestURL().toString();
		URI requestUri;
		try {
			requestUri = new URI(requestUrlString);
		} catch (URISyntaxException e) {
			/*
			 * If this ever occurs, it's almost certainly due to an encoding
			 * issue that needs to be fixed in the above code.
			 */
			throw new BadCodeMonkeyException(e);
		}
		NewCookie serviceClientAuthCookie = AuthTokenCookieHelper
				.createAuthTokenCookie(rememberMeValue, requestUri);
		serviceClientCookieStore.remember(serviceClientAuthCookie);

		/*
		 * Just having a cookie doesn't mean we're actually authenticated.
		 * Verify that using the web service.
		 */
		Account validatedAccount = null;
		try {
			validatedAccount = accountsClient.validateAuth();
		} catch (HttpClientException e) {
			if (e.getStatus().getStatusCode() == Status.UNAUTHORIZED
					.getStatusCode()) {
				/*
				 * Called the web service successfully, but it said the login
				 * was invalid.
				 */
				LOGGER.warn(String.format(
						"Invalid remember me token '%s' on request '%s'.",
						rememberMeValue, request));
				CookiesUtils.cancelCookie(COOKIE_NAME, response);
				return null;
			}

			// Seems to be a problem contacting the web service.
			throw new IllegalStateException(
					"Unable to validate login with service.", e);
		}

		/*
		 * Create a Spring Security 'Authentication' token for the login. This
		 * token will end up being saved in the session. The principal saved in
		 * the token will be passed to anything else that asks for the request's
		 * security/authorization principal.
		 */
		List<SimpleGrantedAuthority> grantedAuthorities = new LinkedList<>();
		for (SecurityRole role : validatedAccount.getRoles())
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getId()));
		RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(
				REMEMBER_ME_TOKEN_KEY, validatedAccount, grantedAuthorities);
		auth.setDetails(AUTH_DETAILS_BUILDER.buildDetails(request));

		return auth;
	}

	/**
	 * @see org.springframework.security.web.authentication.RememberMeServices#loginFail(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void loginFail(HttpServletRequest request,
			HttpServletResponse response) {
		/*
		 * This method's JavaDoc instructs that, whenever a user tries to login
		 * and supplies invalid credentials, that their auto-login token should
		 * be cancelled. Given the use case here though, logins that are _only_
		 * tracked by the token, we can only cancel authentication tokens when
		 * the user has another login mechanism to recover access to their
		 * account. And even then, it's dubious as to whether or not there's any
		 * value in doing so. For now, we'll just ignore this.
		 */
	}

	/**
	 * @see org.springframework.security.web.authentication.RememberMeServices#loginSuccess(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse,
	 *      org.springframework.security.core.Authentication)
	 */
	@Override
	public void loginSuccess(HttpServletRequest request,
			HttpServletResponse response,
			Authentication successfulAuthentication) {
		/*
		 * Typical RememberMeServices implementations would check the request
		 * for a parameter indicating whether or not the user checked the
		 * "Remember my login" option in the login form. However, this
		 * application does not have such an option in its login forms. Instead,
		 * it assumes that the user always wants that option. This is a security
		 * risk for users using the application on a public computer, but it's
		 * just a freaking rock paper scissors game.
		 */

		saveRememberMeToken(request, response, successfulAuthentication);
	}

	/**
	 * Saves a "remember me" authentication token in a cookie in the outgoing
	 * {@link HttpServletResponse}.
	 * 
	 * @param request
	 *            an {@link HttpServletRequest} that resulted in a successful
	 *            authentication
	 * @param response
	 *            the outgoing {@link HttpServletResponse} to save the cookie in
	 * @param successfulAuthentication
	 *            the principal that was successfully authenticated in the
	 *            specified {@link HttpServletRequest}
	 */
	private void saveRememberMeToken(HttpServletRequest request,
			HttpServletResponse response,
			Authentication successfulAuthentication) {
		/*
		 * This is a bit hokey, but to avoid having to create a separate model
		 * class, table, etc. to store authentication tokens for this layer of
		 * the application, we re-use the web service's setup for that here.
		 */
		/*
		 * TODO: Should 'successfulAuthentication' be required to still have a
		 * password in it? Because someone needs to also login to the web
		 * service client, and if we aren't passed a PW here, that will have to
		 * be the responsibility of whoever logged in the Account in the first
		 * place. I'm thinking that it's the responsibility of whoever logged in
		 * the Account, as that seems out of scope given this method's name.
		 */

		if (successfulAuthentication == null)
			throw new IllegalArgumentException();

		/*
		 * It's great that we've got a successful Authentication here and all,
		 * but the object itself isn't too helpful, as Account instances
		 * returned by the web service never contain AuthToken (for paranoia
		 * reasons). So, we'll pretty much ignore it and instead snag the
		 * AuthToken out of the CookieStore.
		 */
		javax.ws.rs.core.Cookie serviceClientAuthTokenCookie = serviceClientCookieStore
				.get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN);

		/*
		 * Convert the CookieStore's JAX-RS cookie into a servlet API cookie.
		 */
		Cookie servletResponseAuthTokenCookie = createRememberMeCookie(
				appConfig, request, serviceClientAuthTokenCookie.getValue());

		// Add the cookie to the response.
		response.addCookie(servletResponseAuthTokenCookie);
	}

	/**
	 * @param appConfig
	 *            the {@link AppConfig} for the application
	 * @param request
	 *            the {@link HttpServletRequest} that the {@link Cookie} is
	 *            being created in response to
	 * @param authTokenCookieValue
	 *            the {@link AuthToken#getToken()} value to be stored in the
	 *            cookie, for the user it will help authenticate
	 * @return the "remember me" cookie used by this application, which
	 *         basically just contains the same {@link AuthToken} as is used by
	 *         the web service clients
	 */
	static Cookie createRememberMeCookie(AppConfig appConfig,
			HttpServletRequest request, String authTokenCookieValue) {
		Cookie servletResponseAuthTokenCookie = new Cookie(COOKIE_NAME,
				authTokenCookieValue);

		servletResponseAuthTokenCookie.setVersion(0);
		CookiesUtils.applyCookieSecurityProperties(
				servletResponseAuthTokenCookie, appConfig);
		int maxAge = 60 * 60 * 24 * 365 * 1;
		servletResponseAuthTokenCookie.setMaxAge(maxAge);

		return servletResponseAuthTokenCookie;
	}
}
