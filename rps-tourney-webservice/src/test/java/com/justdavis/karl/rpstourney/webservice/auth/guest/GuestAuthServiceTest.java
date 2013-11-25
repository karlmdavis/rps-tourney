package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.net.URI;
import java.util.UUID;

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

/**
 * Unit tests for {@link GuestAuthService}.
 */
public final class GuestAuthServiceTest {
	/**
	 * FIXME Remove or rework once actual persistence is in place.
	 */
	@After
	public void removeAccounts() {
		AccountService.existingAccounts.clear();
		GuestAuthService.existingLogins.clear();
	}

	/**
	 * Ensures that {@link GuestAuthService} creates new
	 * {@link GuestLoginIdentity}s as expected.
	 */
	@Test
	public void createLogin() {
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
		GuestAuthService authService = new GuestAuthService(securityContext,
				uriInfo);

		// Call the service.
		Response loginResponse = authService.loginAsGuest();

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
	 * Ensures that {@link GuestAuthService#loginAsGuest()} behaves as expected
	 * when the user/client already has an active login.
	 */
	@Test
	public void existingLogin() {
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
		GuestAuthService authService = new GuestAuthService(securityContext,
				uriInfo);

		// Call the service once to login and create an Account.
		authService.loginAsGuest();

		// Call the service a second time logged in as the new Account.
		securityContext = new AccountSecurityContext(
				AccountService.existingAccounts.get(0));
		authService = new GuestAuthService(securityContext, uriInfo);
		Response secondLoginResponse = authService.loginAsGuest();

		// Verify the results
		Assert.assertEquals(Status.CONFLICT.getStatusCode(),
				secondLoginResponse.getStatus());
	}
}
