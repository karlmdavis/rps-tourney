package com.justdavis.karl.rpstourney.webapp.security;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedUriSyntaxException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.webapp.config.AppConfig;

/**
 * Unit tests for {@link CustomRememberMeServices}.
 */
public final class CustomerRememberMeServicesTest {
	/**
	 * Tests
	 * {@link CustomRememberMeServices#autoLogin(HttpServletRequest, HttpServletResponse)}
	 * on requests with no authentication token.
	 * 
	 * @throws MalformedURLException
	 *             (won't occur, as {@link URL}s are hardcoded)
	 */
	@Test
	public void autoLogin_noToken() throws MalformedURLException {
		// Create the mocks needed for the test.
		AppConfig appConfig = new AppConfig(new URL("https://example.com/app"), new URL("https://example.com/svc"));
		CookieStore clientCookies = new CookieStore();
		HttpServletRequest mockRequest = new MockHttpServletRequest();
		HttpServletResponse mockResponse = new MockHttpServletResponse();
		AccountsClient accountsClient = null;

		// Try a login.
		CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(appConfig, clientCookies,
				accountsClient);
		Assert.assertNull(rememberMeServices.autoLogin(mockRequest, mockResponse));
	}

	/**
	 * Tests
	 * {@link CustomRememberMeServices#autoLogin(HttpServletRequest, HttpServletResponse)}
	 * on requests with an empty authentication token.
	 * 
	 * @throws MalformedURLException
	 *             (won't occur, as {@link URL}s are hardcoded)
	 */
	@Test
	public void autoLogin_emptyToken() throws MalformedURLException {
		// Create the mocks needed for the test.
		AppConfig appConfig = new AppConfig(new URL("https://example.com/app"), new URL("https://example.com/svc"));
		CookieStore clientCookies = new CookieStore();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setCookies(new Cookie(CustomRememberMeServices.COOKIE_NAME, ""));
		HttpServletResponse mockResponse = new MockHttpServletResponse();
		AccountsClient accountsClient = null;

		// Try a login.
		CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(appConfig, clientCookies,
				accountsClient);
		Assert.assertNull(rememberMeServices.autoLogin(mockRequest, mockResponse));
	}

	/**
	 * Tests
	 * {@link CustomRememberMeServices#autoLogin(HttpServletRequest, HttpServletResponse)}
	 * on requests with an invalid authentication token.
	 * 
	 * @throws MalformedURLException
	 *             (won't occur, as {@link URL}s are hardcoded)
	 */
	@Test
	public void autoLogin_invalidToken() throws MalformedURLException {
		// Create the mocks needed for the test.
		AppConfig appConfig = new AppConfig(new URL("https://example.com/app"), new URL("https://example.com/svc"));
		CookieStore clientCookies = new CookieStore();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setCookies(new Cookie(CustomRememberMeServices.COOKIE_NAME, "bogus"));
		HttpServletResponse mockResponse = new MockHttpServletResponse();
		AccountsClient accountsClient = new MockAccountsClient(clientCookies) {
			/**
			 * @see com.justdavis.karl.rpstourney.service.client.auth.AccountsClient#validateAuth()
			 */
			@Override
			public Account validateAuth() {
				// Reject all validation attempts.
				throw new HttpClientException(Status.UNAUTHORIZED);
			}
		};

		// Try a login.
		CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(appConfig, clientCookies,
				accountsClient);
		Assert.assertNull(rememberMeServices.autoLogin(mockRequest, mockResponse));
	}

	/**
	 * Tests
	 * {@link CustomRememberMeServices#autoLogin(HttpServletRequest, HttpServletResponse)}
	 * on requests with a valid authentication token.
	 * 
	 * @throws MalformedURLException
	 *             (won't occur, as {@link URL}s are hardcoded)
	 */
	@Test
	public void autoLogin_validToken() throws MalformedURLException {
		// Create the mocks needed for the test.
		AppConfig appConfig = new AppConfig(new URL("https://example.com/app"), new URL("https://example.com/svc"));
		CookieStore clientCookies = new CookieStore();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setCookies(new Cookie(CustomRememberMeServices.COOKIE_NAME, "totallylegit"));
		HttpServletResponse mockResponse = new MockHttpServletResponse();
		final Account mockAccount = new Account(SecurityRole.USERS);
		AccountsClient accountsClient = new MockAccountsClient(clientCookies) {
			/**
			 * @see com.justdavis.karl.rpstourney.service.client.auth.AccountsClient#validateAuth()
			 */
			@Override
			public Account validateAuth() {
				// Accept all validation attempts.
				return mockAccount;
			}
		};

		// Try a login.
		CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(appConfig, clientCookies,
				accountsClient);
		Authentication auth = rememberMeServices.autoLogin(mockRequest, mockResponse);
		Assert.assertNotNull(auth);
	}

	/**
	 * Tests that
	 * {@link CustomRememberMeServices#loginSuccess(HttpServletRequest, HttpServletResponse, Authentication)}
	 * saves an authentication cookie as expected.
	 * 
	 * @throws MalformedURLException
	 *             (won't occur, as {@link URL}s are hardcoded)
	 * @throws URISyntaxException
	 *             (only hardcoded URIs are used here; won't happen)
	 */
	@Test
	public void loginSuccess() throws MalformedURLException, URISyntaxException {
		// Create the mocks needed for the first call.
		AppConfig appConfig = new AppConfig(new URL("https://example.com/app"), new URL("https://example.com/svc"));
		CookieStore clientCookies = new CookieStore();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRequestURI("http://example.com/foo");
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		final Account mockAccount = new Account(SecurityRole.USERS);
		AuthToken mockToken = new AuthToken(mockAccount, UUID.randomUUID());
		mockAccount.getAuthTokens().add(mockToken);
		RememberMeAuthenticationToken mockAuth = new RememberMeAuthenticationToken(
				CustomRememberMeServices.REMEMBER_ME_TOKEN_KEY, mockAccount, null);
		clientCookies
				.remember(AuthTokenCookieHelper.createAuthTokenCookie(mockToken, new URI("http://example.com/foo")));
		AccountsClient accountsClient = new MockAccountsClient(clientCookies);

		// Try calling loginSuccess.
		CustomRememberMeServices rememberMeServices = new CustomRememberMeServices(appConfig, clientCookies,
				accountsClient);
		rememberMeServices.loginSuccess(mockRequest, mockResponse, mockAuth);

		// Make sure the response now has the expected cookie.
		Cookie responseAuthCookie = mockResponse.getCookie(CustomRememberMeServices.COOKIE_NAME);
		Assert.assertNotNull(responseAuthCookie);
		Assert.assertEquals(mockToken.getToken().toString(), responseAuthCookie.getValue());
	}

	/**
	 * A mock {@link AccountsClient} implementation for use in tests.
	 */
	private static class MockAccountsClient extends AccountsClient {
		/**
		 * Constructs a new {@link MockAccountsClient} instance.
		 * 
		 * @param cookieStore
		 *            the {@link CookieStore} to use
		 */
		public MockAccountsClient(CookieStore cookieStore) {
			super(buildMockConfig(), cookieStore);
		}

		/**
		 * @return a mock {@link ClientConfig} instance
		 */
		private static ClientConfig buildMockConfig() {
			try {
				return new ClientConfig(new URI("http://example.com/"));
			} catch (URISyntaxException e) {
				throw new UncheckedUriSyntaxException(e);
			}
		}
	}
}
