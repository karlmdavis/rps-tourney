package com.justdavis.karl.rpstourney.webapp.security;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.client.auth.IAccountsClientFactory;
import com.justdavis.karl.rpstourney.webapp.config.AppConfig;

/**
 * Unit tests for {@link GameLoginSuccessHandler}.
 */
public final class GameLoginSuccessHandlerTest {
	/**
	 * Tests {@link GameLoginSuccessHandler} as if the login event was for a
	 * user that was not already authenticated.
	 * 
	 * @throws ServletException
	 *             (shouldn't happen, but indicates test failure if it does)
	 * @throws IOException
	 *             (shouldn't happen, but indicates test failure if it does)
	 */
	@Test
	public void handleWithNoPreviousLogin() throws URISyntaxException, IOException, ServletException {
		// Create the mocks needed for the test.
		AppConfig appConfig = new AppConfig(new URL("http://example.com/"), new URL("http://example.com/"));
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		Account account = new Account();
		account.getAuthTokens().add(new AuthToken(account, UUID.randomUUID()));
		CustomMockAccountsClient accountsClient = new CustomMockAccountsClient(account);
		CustomMockAccountsClientFactory accountsClientFactory = null;
		Authentication auth = new UsernamePasswordAuthenticationToken("foo@example.com", "secret");

		// Create the success handler and test it.
		GameLoginSuccessHandler successHandler = new GameLoginSuccessHandler(appConfig, accountsClient,
				accountsClientFactory);
		successHandler.onAuthenticationSuccess(mockRequest, mockResponse, auth);
		Cookie authTokenCookie = mockResponse.getCookie(CustomRememberMeServices.COOKIE_NAME);
		Assert.assertNotNull(authTokenCookie);
	}

	/**
	 * Tests {@link GameLoginSuccessHandler} as if the login event was for a
	 * user that was already authenticated to an Account that was not anonymous.
	 * 
	 * @throws ServletException
	 *             (shouldn't happen, but indicates test failure if it does)
	 * @throws IOException
	 *             (shouldn't happen, but indicates test failure if it does)
	 * @throws AddressException
	 *             (won't happen: addresses are hardcoded here)
	 */
	@Test
	public void handleWithPreviousNonAnonLogin()
			throws URISyntaxException, IOException, ServletException, AddressException {
		// Create the mocks needed for the test.
		AppConfig appConfig = new AppConfig(new URL("http://example.com/"), new URL("http://example.com/"));
		Account accountForPreviousLogin = new Account();
		accountForPreviousLogin.getAuthTokens().add(new AuthToken(accountForPreviousLogin, UUID.randomUUID()));
		accountForPreviousLogin.getLogins()
				.add(new GameLoginIdentity(accountForPreviousLogin, new InternetAddress("foo@example.com"), "secret"));
		Account accountForNewLogin = new Account();
		accountForNewLogin.getAuthTokens().add(new AuthToken(accountForNewLogin, UUID.randomUUID()));
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		Cookie cookieForExistingLogin = CustomRememberMeServices.createRememberMeCookie(appConfig, mockRequest,
				accountForPreviousLogin.getAuthToken().getToken().toString());
		mockRequest.setCookies(cookieForExistingLogin);
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		CustomMockAccountsClient accountsClient = new CustomMockAccountsClient(accountForNewLogin);
		CustomMockAccountsClientFactory accountsClientFactory = new CustomMockAccountsClientFactory(
				accountForPreviousLogin);
		Authentication auth = new UsernamePasswordAuthenticationToken("foo@example.com", "secret");

		// Create the success handler and test it.
		GameLoginSuccessHandler successHandler = new GameLoginSuccessHandler(appConfig, accountsClient,
				accountsClientFactory);
		successHandler.onAuthenticationSuccess(mockRequest, mockResponse, auth);
		Cookie authTokenCookie = mockResponse.getCookie(CustomRememberMeServices.COOKIE_NAME);
		Assert.assertNotNull(authTokenCookie);
		Assert.assertEquals(accountForNewLogin.getAuthToken().getToken().toString(), authTokenCookie.getValue());
		Assert.assertNull(accountsClient.getMergeTargetAccountId());
		Assert.assertNull(accountsClient.getMergeSourceAccountAuthTokenValue());
	}

	/**
	 * Tests {@link GameLoginSuccessHandler} as if the login event was for a
	 * user that was already authenticated to an Account that was anonymous.
	 * 
	 * @throws ServletException
	 *             (shouldn't happen, but indicates test failure if it does)
	 * @throws IOException
	 *             (shouldn't happen, but indicates test failure if it does)
	 * @throws AddressException
	 *             (won't happen: addresses are hardcoded here)
	 * @throws SecurityException
	 *             (won't happen: no security for tests)
	 * @throws NoSuchFieldException
	 *             (won't happen: using hardcoded fields)
	 * @throws IllegalAccessException
	 *             (won't happen: using hardcoded fields)
	 * @throws IllegalArgumentException
	 *             (won't happen: using hardcoded fields)
	 */
	@Test
	public void handleWithPreviousAnonLogin()
			throws URISyntaxException, IOException, ServletException, AddressException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		// Create the mocks needed for the test.
		AppConfig appConfig = new AppConfig(new URL("http://example.com/"), new URL("http://example.com/"));
		Account accountForPreviousLogin = new Account();
		accountForPreviousLogin.getAuthTokens().add(new AuthToken(accountForPreviousLogin, UUID.randomUUID()));
		accountForPreviousLogin.getLogins().add(new GuestLoginIdentity(accountForPreviousLogin));
		Account accountForNewLogin = new Account();
		Field accountIdField = Account.class.getDeclaredField("id");
		accountIdField.setAccessible(true);
		accountIdField.set(accountForNewLogin, 3);
		accountForNewLogin.getAuthTokens().add(new AuthToken(accountForNewLogin, UUID.randomUUID()));
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		Cookie cookieForExistingLogin = CustomRememberMeServices.createRememberMeCookie(appConfig, mockRequest,
				accountForPreviousLogin.getAuthToken().getToken().toString());
		mockRequest.setCookies(cookieForExistingLogin);
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		CustomMockAccountsClient accountsClient = new CustomMockAccountsClient(accountForNewLogin);
		CustomMockAccountsClientFactory accountsClientFactory = new CustomMockAccountsClientFactory(
				accountForPreviousLogin);
		Authentication auth = new UsernamePasswordAuthenticationToken("foo@example.com", "secret");

		// Create the success handler and test it.
		GameLoginSuccessHandler successHandler = new GameLoginSuccessHandler(appConfig, accountsClient,
				accountsClientFactory);
		successHandler.onAuthenticationSuccess(mockRequest, mockResponse, auth);
		Cookie authTokenCookie = mockResponse.getCookie(CustomRememberMeServices.COOKIE_NAME);
		Assert.assertNotNull(authTokenCookie);
		Assert.assertEquals(accountForNewLogin.getAuthToken().getToken().toString(), authTokenCookie.getValue());
		Assert.assertEquals(new Long(accountForNewLogin.getId()), accountsClient.getMergeTargetAccountId());
		Assert.assertEquals(accountForPreviousLogin.getAuthToken().getToken(),
				accountsClient.getMergeSourceAccountAuthTokenValue());
	}

	/**
	 * A mock {@link IAccountsResource} client for use in
	 * {@link GameLoginSuccessHandlerTest}.
	 */
	private static final class CustomMockAccountsClient extends MockAccountsClient {
		private final Account account;
		private Long mergeTargetAccountId;
		private UUID mergeSourceAccountAuthTokenValue;

		/**
		 * Constructor.
		 * 
		 * @param account
		 *            the mock {@link Account} that will be used
		 */
		public CustomMockAccountsClient(Account account) {
			this.account = account;

			this.mergeTargetAccountId = null;
			this.mergeSourceAccountAuthTokenValue = null;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient#getAccount()
		 */
		@Override
		public Account getAccount() {
			return account;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient#selectOrCreateAuthToken()
		 */
		@Override
		public AuthToken selectOrCreateAuthToken() {
			AuthToken authToken = account.getAuthToken();
			if (authToken == null)
				throw new IllegalStateException();

			return authToken;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient#mergeAccount(long,
		 *      java.util.UUID)
		 */
		@Override
		public void mergeAccount(long targetAccountId, UUID sourceAccountAuthTokenValue) {
			this.mergeTargetAccountId = targetAccountId;
			this.mergeSourceAccountAuthTokenValue = sourceAccountAuthTokenValue;
		}

		/**
		 * @return the <code>targetAccountId</code> value that was passed to
		 *         {@link #mergeAccount(long, UUID)}, or <code>null</code> if it
		 *         has not been called
		 */
		public Long getMergeTargetAccountId() {
			return mergeTargetAccountId;
		}

		/**
		 * @return the <code>sourceAccountAuthTokenValue</code> value that was
		 *         passed to {@link #mergeAccount(long, UUID)}, or
		 *         <code>null</code> if it has not been called
		 */
		public UUID getMergeSourceAccountAuthTokenValue() {
			return mergeSourceAccountAuthTokenValue;
		}
	}

	/**
	 * An {@link IAccountsClientFactory} implementation that produces
	 * {@link CustomMockAccountsClient} instances.
	 */
	private static final class CustomMockAccountsClientFactory implements IAccountsClientFactory {
		private final Account account;

		/**
		 * Constructs a new {@link CustomMockAccountsClientFactory} instance.
		 * 
		 * @param account
		 *            the mock {@link Account} that will be used in the
		 *            {@link CustomMockAccountsClient} instances
		 */
		public CustomMockAccountsClientFactory(Account account) {
			this.account = account;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.client.auth.IAccountsClientFactory#createAccountsClient(java.lang.String)
		 */
		@Override
		public IAccountsResource createAccountsClient(String authTokenValueForAccount) {
			return new CustomMockAccountsClient(account);
		}
	}
}
