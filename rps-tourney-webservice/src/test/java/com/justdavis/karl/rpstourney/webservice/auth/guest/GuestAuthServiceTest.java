package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.MockUriInfo;
import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.webservice.auth.MockAccountsDao;

/**
 * Unit tests for {@link GuestAuthService}.
 */
public final class GuestAuthServiceTest {
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
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGuestLoginIdentitiesDao loginsDao = new MockGuestLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GuestAuthService authService = new GuestAuthService();
		authService.setAccountSecurityContext(securityContext);
		authService.setUriInfo(uriInfo);
		authService.setAccountDao(accountsDao);
		authService.setGuestLoginIdentitiesDao(loginsDao);

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
		Assert.assertEquals(1, loginsDao.logins.size());
		Assert.assertEquals(accountsDao.accounts.get(0).getAuthTokens()
				.iterator().next().getToken(), authToken);
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
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGuestLoginIdentitiesDao loginsDao = new MockGuestLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GuestAuthService authService = new GuestAuthService();
		authService.setAccountSecurityContext(securityContext);
		authService.setUriInfo(uriInfo);
		authService.setAccountDao(accountsDao);
		authService.setGuestLoginIdentitiesDao(loginsDao);

		// Call the service once to login and create an Account.
		authService.loginAsGuest();

		// Call the service a second time logged in as the new Account.
		securityContext = new AccountSecurityContext(
				accountsDao.accounts.get(0));
		authService.setAccountSecurityContext(securityContext);
		Response secondLoginResponse = authService.loginAsGuest();

		// Verify the results
		Assert.assertEquals(Status.CONFLICT.getStatusCode(),
				secondLoginResponse.getStatus());
	}
}
