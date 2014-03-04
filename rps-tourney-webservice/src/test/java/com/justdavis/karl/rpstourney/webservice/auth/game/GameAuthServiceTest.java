package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.threeten.bp.Clock;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.MockAccountsDao;

/**
 * Unit tests for {@link GameAuthService}.
 */
public final class GameAuthServiceTest {
	/**
	 * Ensures that {@link GameAuthService} creates new
	 * {@link GameLoginIdentity}s as expected.
	 * 
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void createLogin() throws AddressException {
		// Create the mock params to pass to the service.
		HttpServletRequest httpRequest = new MockHttpServletRequest();
		AccountSecurityContext securityContext = new AccountSecurityContext();
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGameLoginIdentitiesDao loginsDao = new MockGameLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GameAuthService authService = new GameAuthService();
		authService.setHttpServletRequest(httpRequest);
		authService.setAccountSecurityContext(securityContext);
		authService.setAccountsDao(accountsDao);
		authService.setGameLoginIdentitiesDao(loginsDao);

		// Call the service.
		Account loggedInAccount = authService.createGameLogin(
				new InternetAddress("foo@example.com"), "secret");

		// Verify the results
		Assert.assertNotNull(loggedInAccount);
		Assert.assertEquals(1, loginsDao.logins.size());
	}

	/**
	 * Ensures that
	 * {@link GameAuthService#createGameLogin(InternetAddress, String)} behaves
	 * as expected when the user/client already has an active login.
	 * 
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void createLoginWithAuthToken() throws AddressException {
		// Create a guest login (manually).
		HttpServletRequest httpRequest = new MockHttpServletRequest();
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGameLoginIdentitiesDao loginsDao = new MockGameLoginIdentitiesDao(
				accountsDao);
		UUID randomAuthToken = UUID.randomUUID();
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, randomAuthToken, Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);
		accountsDao.accounts.add(account);

		// Create the mock params to pass to the service .
		AccountSecurityContext securityContext = new AccountSecurityContext(
				account);

		// Create the service.
		GameAuthService authService = new GameAuthService();
		authService.setHttpServletRequest(httpRequest);
		authService.setAccountSecurityContext(securityContext);
		authService.setAccountsDao(accountsDao);
		authService.setGameLoginIdentitiesDao(loginsDao);

		// Create a new game login.
		Account loggedInAccount = authService.createGameLogin(
				new InternetAddress("foo@example.com"), "secret");

		// Verify the results.
		Assert.assertNotNull(loggedInAccount);
		Assert.assertSame(account, loggedInAccount);
		Assert.assertEquals(1, loginsDao.logins.size());
	}

	/**
	 * Ensures that
	 * {@link GameAuthService#loginWithGameAccount(InternetAddress, String)}
	 * behaves as expected when the user/client is not already logged in.
	 * 
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void login() throws AddressException {
		// Create the mock params to pass to the service .
		HttpServletRequest httpRequest = new MockHttpServletRequest();
		AccountSecurityContext securityContext = new AccountSecurityContext();
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGameLoginIdentitiesDao loginsDao = new MockGameLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GameAuthService authService = new GameAuthService();
		authService.setHttpServletRequest(httpRequest);
		authService.setAccountSecurityContext(securityContext);
		authService.setAccountsDao(accountsDao);
		authService.setGameLoginIdentitiesDao(loginsDao);

		// Create the login (manually).
		UUID randomAuthToken = UUID.randomUUID();
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, randomAuthToken, Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);
		accountsDao.accounts.add(account);
		GameLoginIdentity login = new GameLoginIdentity(account,
				new InternetAddress("foo@example.com"),
				GameAuthService.hashPassword("secret"));
		loginsDao.logins.add(login);

		// Login.
		Account loggedInAccount = authService.loginWithGameAccount(
				login.getEmailAddress(), "secret");

		// Verify the results.
		Assert.assertNotNull(loggedInAccount);
		Assert.assertSame(account, loggedInAccount);
	}
}
