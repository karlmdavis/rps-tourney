package com.justdavis.karl.rpstourney.webapp.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.justdavis.karl.rpstourney.webapp.config.AppConfig;

/**
 * Unit tests for {@link CookiesUtils}.
 */
public final class CookiesUtilsTest {
	/**
	 * Tests {@link CookiesUtils#extractCookie(String, HttpServletRequest)}.
	 */
	@Test
	public void extractCookie() {
		// Create the mocks needed for the test.
		Cookie cookie = new Cookie("foo", "bar");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setCookies(cookie);

		// Extract the cookie.
		Cookie extractedCookie = CookiesUtils.extractCookie("foo", request);
		Assert.assertNotNull(extractedCookie);
		Assert.assertEquals(cookie.getName(), extractedCookie.getName());
		Assert.assertEquals(cookie.getValue(), extractedCookie.getValue());
	}

	/**
	 * Tests {@link CookiesUtils#cancelCookie(String, HttpServletResponse)}.
	 */
	@Test
	public void cancelCookie() {
		// Create the mocks needed for the test.
		MockHttpServletResponse response = new MockHttpServletResponse();

		// Cancel a cookie.
		CookiesUtils.cancelCookie("foo", response);
		Assert.assertNotNull(response.getCookie("foo"));
		Assert.assertEquals(0, response.getCookie("foo").getMaxAge());
	}

	/**
	 * Tests
	 * {@link CookiesUtils#applyCookieSecurityProperties(Cookie, AppConfig)}.
	 * 
	 * @throws MalformedURLException
	 *             (won't occur, as {@link URL}s are hardcoded)
	 */
	@Test
	public void applyCookieSecurityProperties() throws MalformedURLException {
		// Create the mocks needed for the test.
		Cookie cookie1 = new Cookie("foo", "bar");
		AppConfig appConfig1 = new AppConfig(new URL("https://example.com/foo"), new URL("https://example.com/bar"));

		// Set the cookie's security properties.
		CookiesUtils.applyCookieSecurityProperties(cookie1, appConfig1);
		Assert.assertEquals(".example.com", cookie1.getDomain());
		Assert.assertEquals("/foo", cookie1.getPath());
		Assert.assertTrue(cookie1.isHttpOnly());
		Assert.assertTrue(cookie1.getSecure());

		// Create the mocks needed for the test.
		Cookie cookie2 = new Cookie("foo", "bar");
		AppConfig appConfig2 = new AppConfig(new URL("http://localhost/foo"), new URL("https://localhost/bar"));

		// Set the cookie's security properties.
		CookiesUtils.applyCookieSecurityProperties(cookie2, appConfig2);
		Assert.assertNull(cookie2.getDomain());
		Assert.assertEquals("/foo", cookie2.getPath());
		Assert.assertTrue(cookie2.isHttpOnly());
		Assert.assertFalse(cookie2.getSecure());
	}
}
