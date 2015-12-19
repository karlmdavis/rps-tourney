package com.justdavis.karl.rpstourney.webapp.security;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.client.auth.IAccountsClientFactory;
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
 * calling the {@link IAccountsResource#mergeFromDifferentAccount(Account)} web
 * service method, but only when the {@link Account} that they were previously
 * authenticated with was anonymous (i.e. <strong>not</strong> also associated
 * with a {@link GameLoginIdentity}).
 * </p>
 * <p>
 * Design note: It would be a lot simpler to just include all of this logic in
 * the {@link GameLoginAuthenticationProvider} itself. Unfortunately though,
 * Spring doesn't give auth providers access to the request or response at all,
 * so we can't do that.
 * </p>
 */
@Component
public final class GameLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private final AppConfig appConfig;
	private final IAccountsResource accountsClientForCurrentAuth;
	private final IAccountsClientFactory accountsClientFactory;

	/**
	 * Constructs a new {@link GameLoginSuccessHandler} instance.
	 * 
	 * @param appConfig
	 *            the {@link AppConfig} for the application
	 * @param accountsClientForCurrentAuth
	 *            the {@link IAccountsResource} web service client to use when
	 *            accessing the session's currently logged-in {@link Account}
	 *            (if any)
	 * @param accountsClientFactory
	 *            the {@link IAccountsClientFactory} to use when creating
	 *            {@link IAccountsResource} clients with different credentials
	 */
	@Inject
	public GameLoginSuccessHandler(AppConfig appConfig, IAccountsResource accountsClientForCurrentAuth,
			IAccountsClientFactory accountsClientFactory) {
		this.appConfig = appConfig;
		this.accountsClientForCurrentAuth = accountsClientForCurrentAuth;
		this.accountsClientFactory = accountsClientFactory;
	}

	/**
	 * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse,
	 *      org.springframework.security.core.Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// Extract the old AuthToken value (if any) from the request's cookies.
		Cookie authTokenCookieForPrevLogin = CookiesUtils.extractCookie(CustomRememberMeServices.COOKIE_NAME, request);
		boolean hadPreviousLogin = authTokenCookieForPrevLogin != null;

		if (hadPreviousLogin) {
			String authTokenValueForPrevLogin = hadPreviousLogin ? authTokenCookieForPrevLogin.getValue() : null;
			IAccountsResource accountsClientForPrevLogin = accountsClientFactory
					.createAccountsClient(authTokenValueForPrevLogin);

			/*
			 * Was the user previously logged in to an anonymous account? Note:
			 * At this point in the login process and this method,
			 * 'accountsClientForCurrentAuth' will still be authenticating as
			 * the user's previous Account (if any).
			 */
			boolean previousLoginWasAnon = accountsClientForPrevLogin.getAccount().isAnonymous();

			/*
			 * If previous Account was anonymous, we will automatically merge
			 * that Account into the newly-logged-into one. This is done
			 * because, if it isn't, users would lose all of the history from
			 * that anonymous Account, and have no way of recovering it.
			 */
			if (previousLoginWasAnon) {
				AuthToken authTokenForPrevLogin = accountsClientForPrevLogin.selectOrCreateAuthToken();
				Account accountForCurrentLogin = accountsClientForCurrentAuth.getAccount();
				accountsClientForCurrentAuth.mergeAccount(accountForCurrentLogin.getId(),
						authTokenForPrevLogin.getToken());
			}
		}

		/*
		 * Set the 'AuthToken' cookie for the account the user is newly
		 * authenticated to.
		 */
		AuthToken authTokenForNewLogin = accountsClientForCurrentAuth.selectOrCreateAuthToken();
		Cookie authTokenCookie = CustomRememberMeServices.createRememberMeCookie(appConfig, request,
				authTokenForNewLogin.getToken().toString());
		response.addCookie(authTokenCookie);

		/*
		 * Call the superclass' implementation, so it can do all of the good
		 * stuff that it does, too.
		 */
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
