package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.net.URI;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.MockUriInfo;
import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.AccountService;
import com.justdavis.karl.rpstourney.webservice.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestAuthService;
import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestLoginIdentity;

/**
 * Unit tests for {@link GameAuthService}.
 */
public final class GameAuthServiceTest {
	/**
	 * FIXME Remove or rework once actual persistence is in place.
	 */
	@After
	public void removeAccounts() {
		AccountService.existingAccounts.clear();
		GuestAuthService.existingLogins.clear();
		GameAuthService.existingLogins.clear();
	}

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

		// Create the service.
		GameAuthService authService = new GameAuthService(securityContext,
				uriInfo);

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
		Assert.assertEquals(1, AccountService.existingAccounts.size());
		Assert.assertEquals(AccountService.existingAccounts.get(0)
				.getAuthToken(), authToken);
		// TODO verify Account (once that's been fleshed out)
		// TODO ensure the login was saved to the DB (once we have a DB)
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
		UUID randomAuthToken = UUID.randomUUID();
		Account account = new Account(randomAuthToken);
		AccountService.existingAccounts.add(account);
		GuestLoginIdentity login = new GuestLoginIdentity(account);
		GuestAuthService.existingLogins.add(login);

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
		GameAuthService authService = new GameAuthService(securityContext,
				uriInfo);

		// Create a new game login.
		Response createResponse = authService.createGameLogin(
				new InternetAddress("foo@example.com"), "secret");

		// Verify the results.
		Assert.assertNotNull(createResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				createResponse.getStatus());
		UUID authToken = UUID.fromString(createResponse.getCookies()
				.get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN).getValue());
		Assert.assertEquals(randomAuthToken, authToken);
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

		// Create the service.
		GameAuthService authService = new GameAuthService(securityContext,
				uriInfo);

		// Create the login (manually).
		UUID randomAuthToken = UUID.randomUUID();
		Account account = new Account(randomAuthToken);
		AccountService.existingAccounts.add(account);
		GameLoginIdentity login = new GameLoginIdentity(account,
				new InternetAddress("foo@example.com"),
				GameAuthService.hashPassword("secret"));
		GameAuthService.existingLogins.add(login);

		// Login.
		Response loginResponse = authService.loginWithGameAccount(
				login.getEmailAddress(), "secret");

		// Verify the results.
		Assert.assertNotNull(loginResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				loginResponse.getStatus());
		UUID authToken = UUID.fromString(loginResponse.getCookies()
				.get(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN).getValue());
		Assert.assertEquals(randomAuthToken, authToken);
	}
}
