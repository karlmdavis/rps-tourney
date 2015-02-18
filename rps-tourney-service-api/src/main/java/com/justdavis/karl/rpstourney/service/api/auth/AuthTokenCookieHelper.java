package com.justdavis.karl.rpstourney.service.api.auth;

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
	 * The name of the {@link Cookie} used to track the
	 * {@link Account#getAuthToken()} value.
	 */
	public static final String COOKIE_NAME_AUTH_TOKEN = "authToken";

	/**
	 * @param authToken
	 *            the {@link AuthToken} to create the {@link NewCookie} for
	 * @param requestUri
	 *            a {@link URI} representing the webservice request, which will
	 *            be used to set the {@link NewCookie}'s domain
	 * @return a {@link NewCookie} that contains the specified {@link AuthToken}
	 *         's {@link AuthToken#getToken()} value, and which, when passed
	 *         back in requests, will represent a login
	 */
	public static NewCookie createAuthTokenCookie(AuthToken authToken,
			URI requestUri) {
		String authTokenString = authToken.getToken().toString();
		return createAuthTokenCookie(authTokenString, requestUri);
	}

	/**
	 * @param authTokenValue
	 *            the {@link AuthToken#getToken()} value to create the
	 *            {@link NewCookie} for
	 * @param requestUri
	 *            a {@link URI} representing the webservice request, which will
	 *            be used to set the {@link NewCookie}'s domain
	 * @return a {@link NewCookie} that contains the specified
	 *         {@link AuthToken#getToken()} value, and which, when passed back
	 *         in requests, will represent a login
	 */
	public static NewCookie createAuthTokenCookie(String authTokenValue,
			URI requestUri) {
		String path = "/";
		String comment = "";
		int maxAge = 60 * 60 * 24 * 365 * 1;
		NewCookie authCookie = new NewCookie(
				AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN, authTokenValue,
				path, requestUri.getHost(), comment, maxAge, true, true);

		return authCookie;
	}
}
