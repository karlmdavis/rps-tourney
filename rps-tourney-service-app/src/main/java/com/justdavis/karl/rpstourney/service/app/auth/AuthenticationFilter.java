package com.justdavis.karl.rpstourney.service.app.auth;

import java.io.IOException;
import java.util.ListIterator;
import java.util.UUID;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.AuthTokenCookieHelper;

/**
 * <p>
 * {@link AuthenticationFilter} will filter both requests and responses, to apply the application's authentication
 * scheme to them.
 * </p>
 * <p>
 * For requests, it checks for authentication token cookies, and if found, sets the {@link SecurityContext} for the rest
 * of the request to use the logged-in {@link Account}. For responses, it sets/refreshes those authentication token
 * cookies, to ensure that the expiration date and other properties of those cookies are updated every time a client
 * makes a webservice call.
 * </p>
 */
@Priority(Priorities.AUTHENTICATION)
@PreMatching
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthenticationFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

	/**
	 * The custom authentication scheme ID used by the application.
	 *
	 * @see SecurityContext#getAuthenticationScheme()
	 */
	public static String AUTH_SCHEME = "GameAuth";

	/**
	 * The {@link HttpServletRequest#getAttribute(String)} key that should be used to signal/record successful logon
	 * events. If the value of this attribute is a valid {@link AuthToken} instance, the {@link AuthenticationFilter}
	 * will set a cookie on outgoing responses that, if returned, will be used to authenticate that user/account on
	 * future requests.
	 */
	public static final String LOGIN_PROPERTY = AuthenticationFilter.class.getName() + ".login";

	private IAccountsDao accountsDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for request-scoped beans).
	 */
	public AuthenticationFilter() {
	}

	/**
	 * @param accountsDao
	 *            the injected {@link IAccountsDao} to use
	 */
	@Inject
	public void setAccountDao(IAccountsDao accountsDao) {
		// Sanity check: null IAccountsDao?
		if (accountsDao == null)
			throw new IllegalArgumentException();

		this.accountsDao = accountsDao;
	}

	/**
	 * This callback will be passed each request towards the start of the processing chain for it. It checks the request
	 * for a cookie containing an authentication token. If such a cookie is found, it sets the {@link SecurityContext}
	 * for the request to reflect the {@link Account} of the user/client making the request.
	 *
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		LOGGER.trace("Entering {}.filter(...)", AuthenticationFilter.class);

		// Is there an auth token cookie? If so, grab the Account for it.
		Cookie authTokenCookie = requestContext.getCookies().get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN);
		Account userAccount = null;
		if (authTokenCookie != null) {
			UUID authTokenUuid = UUID.fromString(authTokenCookie.getValue());
			userAccount = accountsDao.getAccountByAuthToken(authTokenUuid);
		}

		// Was the request made over SSL?
		boolean secure = requestContext.getUriInfo().getRequestUri().getScheme().equals("https");

		// Set the SecurityContext for the rest of the request.
		AccountSecurityContext securityContext = new AccountSecurityContext(userAccount, secure);
		requestContext.setSecurityContext(securityContext);
	}

	/**
	 * This callback will be passed each request and response towards the end of the processing chain for it. It checks
	 * the {@link SecurityContext} for the request, and if it contains a valid {@link Account} for the user/client that
	 * made the request, it will refresh the cookie containing the authentication token for that {@link Account}.
	 *
	 * @see javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.ContainerRequestContext,
	 *      javax.ws.rs.container.ContainerResponseContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		/*
		 * This filter method will be run towards the end of each request's response chain.
		 */

		// Pull the login the request came in with (if any).
		AccountSecurityContext securityContext = (AccountSecurityContext) requestContext.getSecurityContext();

		/*
		 * Pull the auth token for the login that was set during the response (if any).
		 */
		AuthToken authTokenForLogin = (AuthToken) requestContext.getProperty(LOGIN_PROPERTY);

		// Set or refresh the login cookie, if needed.
		if (authTokenForLogin != null) {
			setLoginCookie(authTokenForLogin, requestContext, responseContext);
		} else if (securityContext != null && securityContext.getUserPrincipal() != null) {
			refreshLoginCookie(securityContext, requestContext, responseContext);
		}
	}

	/**
	 * <p>
	 * Includes the specified {@link AuthToken} as a new cookie in the response.
	 * </p>
	 * <p>
	 * Should be used instead of
	 * {@link #refreshLoginCookie(AccountSecurityContext, ContainerRequestContext, ContainerResponseContext)} when the
	 * request didn't already come in with a valid login/cookie.
	 * </p>
	 *
	 * @param authTokenForLogin
	 *            the {@link AuthToken} to be included as a cookie in the response
	 * @param requestContext
	 *            the request being processed
	 * @param responseContext
	 *            the response being generated
	 */
	private static void setLoginCookie(AuthToken authTokenForLogin, ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
		// Create the login/auth Cookie we'll drop in to the response.
		NewCookie authTokenCookie = AuthTokenCookieHelper.createAuthTokenCookie(authTokenForLogin,
				requestContext.getUriInfo().getRequestUri());

		addCookieToResponse(responseContext, authTokenCookie);
	}

	/**
	 * <p>
	 * Refreshes the current login {@link AuthToken} cookie on the outgoing response being generated.
	 * </p>
	 * <p>
	 * Should be used instead of
	 * {@link #refreshLoginCookie(AccountSecurityContext, ContainerRequestContext, ContainerResponseContext)} when the
	 * request didn't already come in with a valid login/cookie.
	 * </p>
	 *
	 * @param securityContext
	 *            the {@link AccountSecurityContext} containing the active login to be refreshed
	 * @param requestContext
	 *            the request being processed
	 * @param responseContext
	 *            the response being generated
	 */
	private void refreshLoginCookie(AccountSecurityContext securityContext, ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
		// Pull the Account that's logged in.
		Account userAccount = securityContext.getUserPrincipal();

		// Find the AuthToken to use, reusing the current one where possible.
		Cookie currentAuthCookie = requestContext.getCookies().get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN);
		UUID currentAuthTokenValue = currentAuthCookie != null ? UUID.fromString(currentAuthCookie.getValue()) : null;
		AuthToken authToken;
		if (userAccount.isValidAuthToken(currentAuthTokenValue))
			authToken = userAccount.getAuthToken(currentAuthTokenValue);
		else
			authToken = accountsDao.selectOrCreateAuthToken(userAccount);

		// Add a new login Cookie to the response.
		NewCookie newAuthCookie = AuthTokenCookieHelper.createAuthTokenCookie(authToken,
				requestContext.getUriInfo().getRequestUri());
		addCookieToResponse(responseContext, newAuthCookie);
	}

	/**
	 * Adds the specified {@link NewCookie} instance to the specified {@link ContainerResponseContext} response
	 * instance. Will replace any existing cookie that matches the specified cookie's {@link NewCookie#getName()}.
	 *
	 * @param responseContext
	 *            the {@link ContainerResponseContext} instance that represents the response being generated
	 * @param authTokenCookie
	 *            the {@link NewCookie} to include in the response
	 */
	private static void addCookieToResponse(ContainerResponseContext responseContext, NewCookie authTokenCookie) {
		/*
		 * Loop through the Cookies, checking to see if any match the one being set.
		 */
		boolean matchingCookieFound = false;
		if (responseContext.getHeaders().containsKey(HttpHeaders.SET_COOKIE)) {
			ListIterator<Object> cookies = responseContext.getHeaders().get(HttpHeaders.SET_COOKIE).listIterator();
			while (cookies.hasNext()) {
				String cookieBody = cookies.next().toString();
				if (cookieBody.startsWith(authTokenCookie.getName())) {
					matchingCookieFound = true;
					cookies.set(authTokenCookie);
				}
			}
		}

		// If not, add this cookie.
		if (!matchingCookieFound)
			responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, authTokenCookie);
	}
}
