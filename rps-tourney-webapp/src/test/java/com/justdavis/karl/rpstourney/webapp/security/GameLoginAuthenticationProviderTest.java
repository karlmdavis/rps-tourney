package com.justdavis.karl.rpstourney.webapp.security;

import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;

/**
 * Unit tests for {@link GameLoginAuthenticationProvider}.
 */
public final class GameLoginAuthenticationProviderTest {
	/**
	 * Tests
	 * {@link GameLoginAuthenticationProvider#authenticate(Authentication)} when
	 * presented with invalid credentials.
	 */
	@Test(expected = BadCredentialsException.class)
	public void authenticate_failedLogin() {
		// Create the mocks needed for the test.
		IGameAuthResource gameAuthClient = new MockGameAuthClient(null);
		Authentication auth = new UsernamePasswordAuthenticationToken(
				"foo@example.com", "secret");

		// Verify the method works correctly (should throw an exception).
		GameLoginAuthenticationProvider authProvider = new GameLoginAuthenticationProvider(
				gameAuthClient);
		authProvider.authenticate(auth);
	}

	/**
	 * Tests
	 * {@link GameLoginAuthenticationProvider#authenticate(Authentication)} when
	 * presented with valid credentials.
	 */
	@Test
	public void authenticate_successfulLogin() {
		// Create the mocks needed for the test.
		Account account = new Account();
		IGameAuthResource gameAuthClient = new MockGameAuthClient(account);
		Authentication auth = new UsernamePasswordAuthenticationToken(
				"foo@example.com", "secret");

		// Verify the method works correctly.
		GameLoginAuthenticationProvider authProvider = new GameLoginAuthenticationProvider(
				gameAuthClient);
		Authentication authResult = authProvider.authenticate(auth);
		Assert.assertNotNull(authResult);
		Assert.assertEquals(account, authResult.getPrincipal());
	}

	/**
	 * Tests {@link GameLoginAuthenticationProvider#supports(Class)}.
	 */
	@Test
	public void supports() {
		// Create the mocks needed for the test.
		IGameAuthResource gameAuthClient = new MockGameAuthClient(null);

		// Verify the method works correctly.
		GameLoginAuthenticationProvider authProvider = new GameLoginAuthenticationProvider(
				gameAuthClient);
		Assert.assertTrue(authProvider
				.supports(UsernamePasswordAuthenticationToken.class));
		Assert.assertFalse(authProvider
				.supports(RememberMeAuthenticationToken.class));
	}

	/**
	 * A mock client-side implementation of {@link IGameAuthResource}, for use
	 * in tests.
	 */
	private static final class MockGameAuthClient implements IGameAuthResource {
		private final Account accountToLogin;

		/**
		 * Constructs a new {@link MockGameAuthClient} instance.
		 * 
		 * @param accountToLogin
		 *            the {@link Account} to return from all calls to
		 *            {@link #loginWithGameAccount(InternetAddress, String)}, or
		 *            <code>null</code> if all logins should fail
		 */
		public MockGameAuthClient(Account accountToLogin) {
			this.accountToLogin = accountToLogin;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource#loginWithGameAccount(javax.mail.internet.InternetAddress,
		 *      java.lang.String)
		 */
		@Override
		public Account loginWithGameAccount(InternetAddress emailAddress,
				String password) {
			if (accountToLogin != null)
				return accountToLogin;
			else
				throw new HttpClientException(Status.UNAUTHORIZED);
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource#createGameLogin(javax.mail.internet.InternetAddress,
		 *      java.lang.String)
		 */
		@Override
		public Account createGameLogin(InternetAddress emailAddress,
				String password) {
			throw new UnsupportedOperationException();
		}
	}
}
