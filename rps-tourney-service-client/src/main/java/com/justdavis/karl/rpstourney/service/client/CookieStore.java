package com.justdavis.karl.rpstourney.service.client;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
 * <p>
 * This class implements the {@link Externalizable} interface (which extends
 * {@link Serializable}) so that it may be saved in user's sessions.
 * </p>
 */
public class CookieStore implements Externalizable {
	/**
	 * A guard value to catch errors when reading in unsupported serialized
	 * data.
	 */
	private static final int SERIALIZATION_VERSION = 1;

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

			/*
			 * Empty-valued cookies are used by servers as a
			 * "please remove this cookie" message.
			 */
			if (cookie.getValue() != null && !cookie.getValue().isEmpty())
				remember(cookie);
			else
				forget(cookie.getName());
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
	 * Removes any cookies with the specified name from this {@link CookieStore}
	 * 
	 * @param cookieName
	 *            the {@link Cookie#getName()} value for the cookies to be
	 *            removed
	 */
	public void forget(String cookieName) {
		this.cookies.remove(cookieName);
	}

	/**
	 * Clears/forgets all of the previously-remembered cookies.
	 */
	public void clear() {
		this.cookies.clear();
	}

	/**
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(SERIALIZATION_VERSION);

		/*
		 * We can't directly serialize NewCookie instances, because they're not
		 * Serializable. Additionally, it seems as if NewCookie.toString() and
		 * NewCookie.valueOf(...) omit all of the extra fields added by
		 * NewCookie. Instead, we have to serialize each NewCookie manually.
		 */
		Collection<NewCookie> cookiesCollection = cookies.values();
		out.writeInt(cookiesCollection.size());
		for (NewCookie cookie : cookiesCollection) {
			/*
			 * Here's the list of fields from NewCookie's constructor that
			 * includes every field: [String name, String value, String path,
			 * String domain, int version, String comment, int maxAge, Date
			 * expiry, boolean secure, boolean httpOnly].
			 */
			out.writeObject(cookie.getName());
			out.writeObject(cookie.getValue());
			out.writeObject(cookie.getPath());
			out.writeObject(cookie.getDomain());
			out.writeInt(cookie.getVersion());
			out.writeObject(cookie.getComment());
			out.writeInt(cookie.getMaxAge());
			out.writeObject(cookie.getExpiry());
			out.writeBoolean(cookie.isSecure());
			out.writeBoolean(cookie.isHttpOnly());
		}
	}

	/**
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		/*
		 * First, read in the version field and verify it's correct, so we don't
		 * attempt to read in an unsupported version of this class' data.
		 */
		int serializationVersion = in.readInt();
		if (serializationVersion != SERIALIZATION_VERSION)
			throw new IOException(String.format(
					"Version mismatch for %s: '%d' instead of '%d'.",
					this.getClass(), serializationVersion,
					SERIALIZATION_VERSION));

		/*
		 * Read in the list of directly-serialized NewCookies. Populate this
		 * CookieStore with each.
		 */
		int numCookies = in.readInt();
		for (int i = 0; i < numCookies; i++) {
			/*
			 * Here's the list of fields from NewCookie's constructor that
			 * includes every field: [String name, String value, String path,
			 * String domain, int version, String comment, int maxAge, Date
			 * expiry, boolean secure, boolean httpOnly].
			 */
			String name = (String) in.readObject();
			String value = (String) in.readObject();
			String path = (String) in.readObject();
			String domain = (String) in.readObject();
			int version = in.readInt();
			String comment = (String) in.readObject();
			int maxAge = in.readInt();
			Date expiry = (Date) in.readObject();
			boolean secure = in.readBoolean();
			boolean httpOnly = in.readBoolean();

			NewCookie cookie = new NewCookie(name, value, path, domain,
					version, comment, maxAge, expiry, secure, httpOnly);
			this.remember(cookie);
		}
	}
}
