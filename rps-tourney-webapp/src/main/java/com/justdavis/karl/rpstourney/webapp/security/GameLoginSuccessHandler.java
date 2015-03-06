package com.justdavis.karl.rpstourney.webapp.security;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedUriSyntaxException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.LoginIdentities;
import com.justdavis.karl.rpstourney.service.api.auth.LoginProvider;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.webapp.account.AccountController;
import com.justdavis.karl.rpstourney.webapp.config.AppConfig;
import com.justdavis.karl.rpstourney.webapp.util.CookiesUtils;

/**
 * <p>
 * This Spring Security {@link AuthenticationSuccessHandler} should be fired for
 * all successful logins via a {@link GameLoginIdentity}. It will handle the
 * "bookkeeping"/cookie management necessary as users de-authenticate from one
 * {@link Account} and authenticate to a different one (if that's the case for
 * the login event being handled). Primarily, it adjusts the value of the
 * client's {@link CustomRememberMeServices#COOKIE_NAME} cookie to reference the
 * new {@link Account}'s {@link GuestLoginIdentity} (one will be created if it
 * doesn't already exist).
 * </p>
 * <p>
 * In addition, this {@link AuthenticationSuccessHandler} helps ensure that
 * clients do not lose the game history from guest-only accounts. It does so by
 * recording the value of the client's previous guest login in a new
 * {@link #MERGEABLE_ACCOUNTS_COOKIE_NAME} cookie, but only if the following
 * conditions are met:
 * </p>
 * <ol>
 * <li>The user was previously authenticated via a {@link GuestLoginIdentity}.</li>
 * <li>The {@link Account} that they were previously authenticated with was
 * <strong>not</strong> also associated with a {@link GameLoginIdentity}.</li>
 * </ol>
 * <p>
 * After all of this "bookkeeping" is taken care of users will be able to use
 * the {@link AccountController} page to merge the game history from their
 * previous guest-only {@link Account}s. (The {@link SecurityConfig} setup
 * redirects users there, unless the authentication was performed as part of
 * another request for a secured page.)
 * </p>
 * <p>
 * Design note: It would be a lot simpler to just include all of this logic in
 * the {@link GameLoginAuthenticationProvider} itself. Unfortunately though,
 * Spring doesn't give auth providers access to the request or response at all,
 * so we can't do that.
 * </p>
 */
@Component
public final class GameLoginSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	/**
	 * The name of the client cookie used to track the {@link AuthToken}s of any
	 * previous guest-only {@link Account}s that the client had used. Note: If
	 * those {@link Account}s are merged into other non-guest-only
	 * {@link Account}s, then the associated tokens should be removed from this
	 * cookie's value.
	 */
	public static final String MERGEABLE_ACCOUNTS_COOKIE_NAME = "MergeableGuestAuthTokens";

	private final AppConfig appConfig;
	private final ClientConfig clientConfig;
	private final IAccountsResource accountsClient;

	/**
	 * Constructs a new {@link GameLoginSuccessHandler} instance.
	 * 
	 * @param appConfig
	 *            the {@link AppConfig} for the application
	 * @param clientConfig
	 *            the {@link ClientConfig} to use for web service clients
	 * @param accountsClient
	 *            the {@link IAccountsResource} web service client to use
	 */
	@Inject
	public GameLoginSuccessHandler(AppConfig appConfig,
			ClientConfig clientConfig, IAccountsResource accountsClient) {
		this.appConfig = appConfig;
		this.clientConfig = clientConfig;
		this.accountsClient = accountsClient;
	}

	/**
	 * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse,
	 *      org.springframework.security.core.Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		// Extract the old AuthToken value (if any) from the request's cookies.
		Cookie authTokenCookieForPrevLogin = CookiesUtils.extractCookie(
				CustomRememberMeServices.COOKIE_NAME, request);
		boolean hadPreviousLogin = authTokenCookieForPrevLogin != null;

		if (hadPreviousLogin) {
			String authTokenValueForPrevLogin = hadPreviousLogin ? authTokenCookieForPrevLogin
					.getValue() : null;

			IAccountsResource accountsClientForPrevLogin = createAccountsClient(
					request, authTokenValueForPrevLogin);

			/*
			 * Was the user previously logged in to an anonymous account? If so,
			 * record it in a separate cookie. Note: At this point in the login
			 * process and this method, 'accountsClient' will still be
			 * authenticating as the user's previous Account (if any).
			 */
			boolean previousLoginWasAnon = true;
			LoginIdentities logins = accountsClientForPrevLogin.getLogins();
			for (ILoginIdentity login : logins.getLogins())
				if (login.getLoginProvider() != LoginProvider.GUEST)
					previousLoginWasAnon = false;

			/*
			 * If previous login was anonymous, save its AuthToken in a separate
			 * cookie.
			 */
			if (previousLoginWasAnon) {
				/*
				 * Get the previous AuthToken and append it to the list of
				 * AuthTokens (if any) that were already in this cookie.
				 */
				Cookie mergeableAccountsCookie = CookiesUtils.extractCookie(
						MERGEABLE_ACCOUNTS_COOKIE_NAME, request);
				String[] authTokenValues = mergeableAccountsCookie != null ? mergeableAccountsCookie
						.getValue().split(",") : new String[] {};
				List<String> authTokenValuesList = new ArrayList<>(
						Arrays.asList(authTokenValues));
				authTokenValuesList.add(authTokenValueForPrevLogin);

				// Build the new cookie and apply it to the response.
				mergeableAccountsCookie = createMergeableAccountsCookie(
						authTokenValuesList, appConfig);
				response.addCookie(mergeableAccountsCookie);
			}
		}

		/*
		 * Set the 'AuthToken' cookie for the account the user is newly
		 * authenticated to.
		 */
		AuthToken authTokenForNewLogin = accountsClient
				.selectOrCreateAuthToken();
		Cookie authTokenCookie = CustomRememberMeServices
				.createRememberMeCookie(appConfig, request,
						authTokenForNewLogin.getToken().toString());
		response.addCookie(authTokenCookie);

		/*
		 * Call the superclass' implementation, so it can do all of the good
		 * stuff that it does, too.
		 */
		super.onAuthenticationSuccess(request, response, authentication);
	}

	/**
	 * @param request
	 *            the request which caused the successful authentication being
	 *            handled
	 * @param authTokenValueForPrevLogin
	 *            the {@link AuthToken#getToken()} value for the {@link Account}
	 *            to create an {@link IAccountsResource} client for
	 * @return an {@link IAccountsResource} for the {@link Account} with the
	 *         specified {@link AuthToken#getToken()} value
	 */
	private IAccountsResource createAccountsClient(HttpServletRequest request,
			String authTokenValueForPrevLogin) {
		try {
			CookieStore cookieStoreForPrevLogin = new CookieStore();
			URI requestUri = new URI(request.getRequestURI());
			NewCookie authTokenCookieForPrevLogin = AuthTokenCookieHelper
					.createAuthTokenCookie(authTokenValueForPrevLogin,
							requestUri);
			cookieStoreForPrevLogin.remember(authTokenCookieForPrevLogin);
			IAccountsResource accountsClientForPrevLogin = new AccountsClient(
					clientConfig, cookieStoreForPrevLogin);
			return accountsClientForPrevLogin;
		} catch (URISyntaxException e) {
			// Should not ever happen, as URI was valid enough for the request.
			throw new UncheckedUriSyntaxException(e);
		}
	}

	/**
	 * @param authTokenValues
	 *            the {@link AuthToken#getToken()} values to create the
	 *            {@link Cookie} for
	 * @param appConfig
	 *            the {@link AppConfig} for the application
	 * @return a {@link Cookie} that contains the specified
	 *         {@link AuthToken#getToken()} values, with the name specified in
	 *         {@link #MERGEABLE_ACCOUNTS_COOKIE_NAME}
	 */
	private static Cookie createMergeableAccountsCookie(
			List<String> authTokenValues, AppConfig appConfig) {
		StringBuffer cookieValue = new StringBuffer();
		ListIterator<String> authTokenValuesIter = authTokenValues
				.listIterator();
		while (authTokenValuesIter.hasNext()) {
			cookieValue.append(authTokenValuesIter.next());
			if (authTokenValuesIter.hasNext())
				cookieValue.append(',');
		}

		Cookie mergeableAccountsCookie = new Cookie(
				MERGEABLE_ACCOUNTS_COOKIE_NAME, cookieValue.toString());
		CookiesUtils.applyCookieSecurityProperties(mergeableAccountsCookie,
				appConfig);
		int maxAge = 60 * 60 * 24 * 365 * 5;
		mergeableAccountsCookie.setMaxAge(maxAge);

		return mergeableAccountsCookie;
	}
}
