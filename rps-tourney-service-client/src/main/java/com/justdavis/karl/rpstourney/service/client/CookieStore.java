package com.justdavis.karl.rpstourney.service.client;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

/**
 * <p>
 * Stores the cookies returned from calls to the web service and helps apply
 * those cookies to future outbound requests.
 * </p>
 * <p>
 * The lifecycle for this component must be managed carefully: it should not be
 * shared between different users. For example, if used in a server-side web
 * application, care should be taken to ensure that a separate instance is used
 * for each user session. Otherwise, users might end up accessing other users
 * accounts.
 * </p>
 */
public class CookieStore {
	private final Map<String, NewCookie> cookies;

	/**
	 * Constructs a new {@link CookieStore} instance.
	 */
	public CookieStore() {
		this.cookies = new HashMap<>();
	}

	/**
	 * Stores the specified cookies, so that they can be applied to future
	 * requests (via {@link #applyCookies(Builder)}).
	 * 
	 * @param cookies
	 *            the {@link Map} of cookies to store, using the
	 *            {@link NewCookie#getName()} values as the keys
	 */
	public void remember(Map<String, NewCookie> cookies) {
		for (String cookieName : cookies.keySet()) {
			NewCookie cookie = cookies.get(cookieName);
			remember(cookie);
		}
	}

	/**
	 * Stores the specified cookie, so that it can be applied to future requests
	 * (via {@link #applyCookies(Builder)}).
	 * 
	 * @param cookie
	 *            the {@link NewCookie} to store
	 */
	public void remember(NewCookie cookie) {
		this.cookies.put(cookie.getName(), cookie);
	}

	/**
	 * @param cookieName
	 *            the {@link Cookie#getName()} of the {@link Cookie} to return
	 * @return the matching {@link Cookie}, or <code>null</code> if no such
	 *         {@link Cookie} is found
	 */
	public Cookie get(String cookieName) {
		return this.cookies.get(cookieName);
	}

	/**
	 * Applies the stored cookies to the specified request {@link Builder}, so
	 * that they will be sent along with any requests made by it.
	 * 
	 * @param requestBuilder
	 *            the request {@link Builder} to apply the stored cookies (if
	 *            any) to
	 */
	public void applyCookies(Builder requestBuilder) {
		// TODO Respect max age & expiry.
		for (NewCookie cookie : cookies.values()) {
			requestBuilder.cookie(cookie.toCookie());
		}
	}

	/**
	 * Clears/forgets all of the previously-remembered cookies.
	 */
	public void clear() {
		this.cookies.clear();
	}
}
