package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.net.URI;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Clock;

import com.justdavis.karl.rpstourney.webservice.MockUriInfo;
import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.AuthToken;
import com.justdavis.karl.rpstourney.webservice.auth.AuthTokenCookieHelper;
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
		AccountSecurityContext securityContext = new AccountSecurityContext();
		UriInfo uriInfo = new MockUriInfo() {
			/**
			 * @see com.justdavis.karl.rpstourney.webservice.MockUriInfo#getRequestUri()
			 */
			@Override
			public URI getRequestUri() {
				return URI.create("http://localhost/");
			}
		};
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGameLoginIdentitiesDao loginsDao = new MockGameLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GameAuthService authService = new GameAuthService();
		authService.setAccountSecurityContext(securityContext);
		authService.setUriInfo(uriInfo);
		authService.setAccountDao(accountsDao);
		authService.setGameLoginIdentitiesDao(loginsDao);

		// Call the service.
		Response loginResponse = authService.createGameLogin(
				new InternetAddress("foo@example.com"), "secret");

		// Verify the results
		Assert.assertNotNull(loginResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				loginResponse.getStatus());
		Account account = (Account) loginResponse.getEntity();
		Assert.assertNotNull(account);
		UUID authToken = UUID.fromString(loginResponse.getCookies()
				.get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN).getValue());
		Assert.assertEquals(1, loginsDao.logins.size());
		Assert.assertEquals(accountsDao.accounts.get(0).getAuthTokens()
				.iterator().next().getToken(), authToken);
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
		UriInfo uriInfo = new MockUriInfo() {
			/**
			 * @see com.justdavis.karl.rpstourney.webservice.MockUriInfo#getRequestUri()
			 */
			@Override
			public URI getRequestUri() {
				return URI.create("http://localhost/");
			}
		};

		// Create the service.
		GameAuthService authService = new GameAuthService();
		authService.setAccountSecurityContext(securityContext);
		authService.setUriInfo(uriInfo);
		authService.setAccountDao(accountsDao);
		authService.setGameLoginIdentitiesDao(loginsDao);

		// Create a new game login.
		Response createResponse = authService.createGameLogin(
				new InternetAddress("foo@example.com"), "secret");

		// Verify the results.
		Assert.assertNotNull(createResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				createResponse.getStatus());
		UUID authTokenValue = UUID.fromString(createResponse.getCookies()
				.get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN).getValue());
		Assert.assertEquals(randomAuthToken, authTokenValue);
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
		AccountSecurityContext securityContext = new AccountSecurityContext();
		UriInfo uriInfo = new MockUriInfo() {
			/**
			 * @see com.justdavis.karl.rpstourney.webservice.MockUriInfo#getRequestUri()
			 */
			@Override
			public URI getRequestUri() {
				return URI.create("http://localhost/");
			}
		};
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGameLoginIdentitiesDao loginsDao = new MockGameLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GameAuthService authService = new GameAuthService();
		authService.setAccountSecurityContext(securityContext);
		authService.setUriInfo(uriInfo);
		authService.setAccountDao(accountsDao);
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
		Response loginResponse = authService.loginWithGameAccount(
				login.getEmailAddress(), "secret");

		// Verify the results.
		Assert.assertNotNull(loginResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				loginResponse.getStatus());
		UUID authTokenValue = UUID.fromString(loginResponse.getCookies()
				.get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN).getValue());
		Assert.assertEquals(randomAuthToken, authTokenValue);
	}
}
