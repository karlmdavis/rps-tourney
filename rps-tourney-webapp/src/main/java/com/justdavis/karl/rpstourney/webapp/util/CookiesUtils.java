package com.justdavis.karl.rpstourney.webapp.util;

import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.rpstourney.webapp.config.AppConfig;

/**
 * Contains static helper methods related to request/response cookie management.
 */
public final class CookiesUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CookiesUtils.class);

	/**
	 * A regex for validating the <code>Domain</code> property of cookies: it
	 * must start with a '<code>.</code>' and contain a second one somewhere
	 * else.
	 */
	private static final Pattern VALID_COOKIE_DOMAIN = Pattern.compile("\\..*\\..*");

	/**
	 * A regex that will match against IP-only values. (Note: This will allow
	 * components greater than 255, so isn't 100% effective, but it's good
	 * enough for our purposes here.)
	 */
	private static final Pattern LIKELY_IP_ADDRESS = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

	/**
	 * Private constructor; instances of this class aren't needed, as all
	 * methods are static.
	 */
	private CookiesUtils() {
	}

	/**
	 * @param cookieName
	 *            the name of the {@link Cookie} to extract
	 * @param request
	 *            the {@link HttpServletRequest} to extract the specified
	 *            {@link Cookie} from
	 * @return the specified {@link Cookie} in the specified
	 *         {@link HttpServletRequest}, or <code>null</code> if no such
	 *         {@link Cookie} is present
	 */
	public static Cookie extractCookie(String cookieName, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length <= 0)
			return null;

		for (Cookie cookie : cookies)
			if (cookie.getName().equals(cookieName))
				return cookie;

		return null;
	}

	/**
	 * Cancels a cookie for the client that made the request, by including a new
	 * expired version of the cookie in the response.
	 * 
	 * @param response
	 *            the {@link HttpServletResponse} to cancel the cookie in
	 */
	public static void cancelCookie(String cookieName, HttpServletResponse response) {
		Cookie cancellationCookie = new Cookie(cookieName, null);
		cancellationCookie.setMaxAge(0);
		cancellationCookie.setPath("/");

		response.addCookie(cancellationCookie);
	}

	/**
	 * Configures the {@link Cookie#setHttpOnly(boolean)},
	 * {@link Cookie#setDomain(String)}, {@link Cookie#setPath(String)}, and
	 * {@link Cookie#setSecure(boolean)} properties for the specified
	 * {@link Cookie}, based on the specified {@link AppConfig}.
	 * 
	 * @param cookie
	 *            the {@link Cookie} to be configured
	 * @param appConfig
	 *            the {@link AppConfig} for the application a {@link URL}
	 */
	public static void applyCookieSecurityProperties(Cookie cookie, AppConfig appConfig) {
		URL baseUrl = appConfig.getBaseUrl();

		cookie.setHttpOnly(true);
		String cookieDomainProperty = computeCookieDomainProperty(baseUrl);
		if (cookieDomainProperty != null)
			cookie.setDomain(cookieDomainProperty);

		String path = baseUrl.getPath().isEmpty() ? "/" : baseUrl.getPath();
		cookie.setPath(path);

		/*
		 * This application will store user's authentication status in session
		 * cookies and will also store AuthTokens themselves in cookies (which
		 * are just s private as a username and password). It MUST be hosted
		 * securely.
		 */
		boolean isHttps = "https".equals(baseUrl.getProtocol());
		if (isHttps)
			cookie.setSecure(isHttps);
		else
			LOGGER.warn("Application not hosted at a secure URL: " + "authentication credentials will be vulnerable!");
	}

	/**
	 * @param applicationUrl
	 *            a {@link URL} that the application to build a cookie for is
	 *            being hosted at (does not have to be the base/root {@link URL}
	 *            )
	 * @return the cookie <code>Domain</code> property to use for application
	 *         being hosted at the specified {@link URL}, or <code>null</code>
	 *         if no <code>Domain</code> property is allowed (per the specs) for
	 *         that {@link URL}
	 */
	private static String computeCookieDomainProperty(URL applicationUrl) {
		String domain = applicationUrl.getHost();

		/*
		 * Per http://www.ietf.org/rfc/rfc2109.txt, the Domain property must
		 * always have a leading '.'. Testing with FF and Chromium, though,
		 * indicates that browsers will also accept a non-prefixed IP address.
		 */
		boolean isLikelyAnIpAddress = LIKELY_IP_ADDRESS.matcher(domain).matches();
		if (!isLikelyAnIpAddress && !domain.startsWith("."))
			domain = "." + domain;

		/*
		 * Per http://code.google.com/p/chromium/issues/detail?id=56211 and
		 * http://curl.haxx.se/rfc/cookie_spec.html, the Domain property must
		 * never refer to top-level domains. Chrome, in particular, gets very
		 * upset about this and ignores the cookies entirely. So, if we're about
		 * to try and do that: stop. Leave the property unassigned, instead.
		 */
		if (!isLikelyAnIpAddress && !VALID_COOKIE_DOMAIN.matcher(domain).matches())
			return null;

		return domain;
	}
}
