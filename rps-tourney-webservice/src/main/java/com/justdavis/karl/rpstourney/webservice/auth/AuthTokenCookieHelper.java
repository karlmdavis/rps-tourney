package com.justdavis.karl.rpstourney.webservice.auth;

import java.net.URI;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

/**
 * <p>
 * Contains utility methods for working with this application's authentication
 * token cookies.
 * </p>
 * <p>
 * These cookies store the {@link Account#getAuthToken()} for the current user
 * and represent an active login. Users wishing to logout just need to
 * delete/drop the cookie from their requests.
 * </p>
 */
public final class AuthTokenCookieHelper {

	/**
	 * @param account
	 *            the {@link Account} of the user to create the auth
	 *            {@link NewCookie} for
	 * @param requestUri
	 *            a {@link URI} representing the webservice request, which will
	 *            be used to set the {@link NewCookie}'s domain
	 * @return a {@link NewCookie} that contains the specified {@link Account}'s
	 *         {@link Account#getAuthToken()}, and which, when passed back in
	 *         requests, will represent a login
	 */
	public static NewCookie createAuthTokenCookie(Account account,
			URI requestUri) {
		String authTokenString = account.getAuthToken().toString();
		String path = "/";
		String comment = "";
		int maxAge = 60 * 60 * 24 * 365 * 1;
		NewCookie authCookie = new NewCookie(
				AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN, authTokenString, path,
				requestUri.getHost(), Cookie.DEFAULT_VERSION, comment, maxAge,
				true);

		/*
		 * FIXME The pre-release of JAX-RS 2.0 used in Apache CXF 2.7.7 doesn't
		 * support the 'HttpOnly' flag for cookies. Once Apache CXF 3.0 is
		 * available, this flag should be used. It will help prevent cross-site
		 * scripting cookie theft.
		 */

		return authCookie;
	}

	/**
	 * The name of the {@link Cookie} used to track the
	 * {@link Account#getAuthToken()} value.
	 */
	public static final String COOKIE_NAME_AUTH_TOKEN = "authToken";
}
