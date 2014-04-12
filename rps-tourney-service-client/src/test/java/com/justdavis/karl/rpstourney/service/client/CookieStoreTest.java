package com.justdavis.karl.rpstourney.service.client;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link CookieStore}.
 */
public final class CookieStoreTest {
	/**
	 * Tests {@link CookieStore#remember(java.util.Map)} and
	 * {@link CookieStore#applyCookies(javax.ws.rs.client.Invocation.Builder)}.
	 */
	@Test
	public void rememberAndApply() {
		// Create a mock cookie.
		Calendar expiryDate = Calendar.getInstance();
		expiryDate.set(3000, 1, 1);
		NewCookie cookie1 = new NewCookie("foo", "bar", "/", "example.com",
				NewCookie.DEFAULT_VERSION, "foo", 300, expiryDate.getTime(),
				true, true);

		// Put the cookie in the CookieStore.
		Map<String, NewCookie> cookies = new HashMap<>();
		cookies.put(cookie1.getName(), cookie1);
		CookieStore cookieStore = new CookieStore();
		cookieStore.remember(cookies);

		// Create a mock request builder.
		MockRequestBuilder requestBuilder = new MockRequestBuilder();

		// Apply the CookieStore to the request builder.
		cookieStore.applyCookies(requestBuilder);

		// Verify that the cookies were applied.
		Assert.assertEquals(1, requestBuilder.cookies.size());
		Assert.assertEquals(cookie1.getName(), requestBuilder.cookies.get(0)
				.getName());
		Assert.assertEquals(cookie1.getValue(), requestBuilder.cookies.get(0)
				.getValue());
	}

	/**
	 * Tests {@link CookieStore#clear()}.
	 */
	@Test
	public void clear() {
		// Create a mock cookie.
		Calendar expiryDate = Calendar.getInstance();
		expiryDate.set(3000, 1, 1);
		NewCookie cookie1 = new NewCookie("foo", "bar", "/", "example.com",
				NewCookie.DEFAULT_VERSION, "foo", 300, expiryDate.getTime(),
				true, true);

		// Put the cookie in the CookieStore.
		Map<String, NewCookie> cookies = new HashMap<>();
		cookies.put(cookie1.getName(), cookie1);
		CookieStore cookieStore = new CookieStore();
		cookieStore.remember(cookies);

		// Then, clear the CookieStore.
		cookieStore.clear();

		// Create a mock request builder.
		MockRequestBuilder requestBuilder = new MockRequestBuilder();

		// Apply the CookieStore to the request builder.
		cookieStore.applyCookies(requestBuilder);

		// Verify that no cookies were applied.
		Assert.assertEquals(0, requestBuilder.cookies.size());
	}

	/**
	 * A mock request {@link Builder} for use in tests.
	 */
	private static final class MockRequestBuilder implements Builder {
		private final List<Cookie> cookies = new LinkedList<>();

		/**
		 * @see javax.ws.rs.client.SyncInvoker#get()
		 */
		@Override
		public Response get() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#get(java.lang.Class)
		 */
		@Override
		public <T> T get(Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#get(javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T get(GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#put(javax.ws.rs.client.Entity)
		 */
		@Override
		public Response put(Entity<?> entity) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#put(javax.ws.rs.client.Entity,
		 *      java.lang.Class)
		 */
		@Override
		public <T> T put(Entity<?> entity, Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#put(javax.ws.rs.client.Entity,
		 *      javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T put(Entity<?> entity, GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#post(javax.ws.rs.client.Entity)
		 */
		@Override
		public Response post(Entity<?> entity) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#post(javax.ws.rs.client.Entity,
		 *      java.lang.Class)
		 */
		@Override
		public <T> T post(Entity<?> entity, Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#post(javax.ws.rs.client.Entity,
		 *      javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T post(Entity<?> entity, GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#delete()
		 */
		@Override
		public Response delete() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#delete(java.lang.Class)
		 */
		@Override
		public <T> T delete(Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#delete(javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T delete(GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#head()
		 */
		@Override
		public Response head() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#options()
		 */
		@Override
		public Response options() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#options(java.lang.Class)
		 */
		@Override
		public <T> T options(Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#options(javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T options(GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#trace()
		 */
		@Override
		public Response trace() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#trace(java.lang.Class)
		 */
		@Override
		public <T> T trace(Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#trace(javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T trace(GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#method(java.lang.String)
		 */
		@Override
		public Response method(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#method(java.lang.String,
		 *      java.lang.Class)
		 */
		@Override
		public <T> T method(String name, Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#method(java.lang.String,
		 *      javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T method(String name, GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#method(java.lang.String,
		 *      javax.ws.rs.client.Entity)
		 */
		@Override
		public Response method(String name, Entity<?> entity) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#method(java.lang.String,
		 *      javax.ws.rs.client.Entity, java.lang.Class)
		 */
		@Override
		public <T> T method(String name, Entity<?> entity, Class<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.SyncInvoker#method(java.lang.String,
		 *      javax.ws.rs.client.Entity, javax.ws.rs.core.GenericType)
		 */
		@Override
		public <T> T method(String name, Entity<?> entity,
				GenericType<T> responseType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#build(java.lang.String)
		 */
		@Override
		public Invocation build(String method) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#build(java.lang.String,
		 *      javax.ws.rs.client.Entity)
		 */
		@Override
		public Invocation build(String method, Entity<?> entity) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#buildGet()
		 */
		@Override
		public Invocation buildGet() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#buildDelete()
		 */
		@Override
		public Invocation buildDelete() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#buildPost(javax.ws.rs.client.Entity)
		 */
		@Override
		public Invocation buildPost(Entity<?> entity) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#buildPut(javax.ws.rs.client.Entity)
		 */
		@Override
		public Invocation buildPut(Entity<?> entity) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#async()
		 */
		@Override
		public AsyncInvoker async() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#accept(java.lang.String[])
		 */
		@Override
		public Builder accept(String... mediaTypes) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#accept(javax.ws.rs.core.MediaType[])
		 */
		@Override
		public Builder accept(MediaType... mediaTypes) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#acceptLanguage(java.util.Locale[])
		 */
		@Override
		public Builder acceptLanguage(Locale... locales) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#acceptLanguage(java.lang.String[])
		 */
		@Override
		public Builder acceptLanguage(String... locales) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#acceptEncoding(java.lang.String[])
		 */
		@Override
		public Builder acceptEncoding(String... encodings) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#cookie(javax.ws.rs.core.Cookie)
		 */
		@Override
		public Builder cookie(Cookie cookie) {
			cookies.add(cookie);
			return this;
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#cookie(java.lang.String,
		 *      java.lang.String)
		 */
		@Override
		public Builder cookie(String name, String value) {
			cookies.add(new NewCookie(name, value));
			return this;
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#cacheControl(javax.ws.rs.core.CacheControl)
		 */
		@Override
		public Builder cacheControl(CacheControl cacheControl) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#header(java.lang.String,
		 *      java.lang.Object)
		 */
		@Override
		public Builder header(String name, Object value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#headers(javax.ws.rs.core.MultivaluedMap)
		 */
		@Override
		public Builder headers(MultivaluedMap<String, Object> headers) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.client.Invocation.Builder#property(java.lang.String,
		 *      java.lang.Object)
		 */
		@Override
		public Builder property(String name, Object value) {
			throw new UnsupportedOperationException();
		}
	}
}
