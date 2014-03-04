package com.justdavis.karl.rpstourney.webservice.auth.guest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.MockAccountsDao;

/**
 * Unit tests for {@link GuestAuthService}.
 */
public final class GuestAuthServiceTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * Ensures that {@link GuestAuthService} creates new
	 * {@link GuestLoginIdentity}s as expected.
	 */
	@Test
	public void createLogin() {
		// Create the mock params to pass to the service.
		HttpServletRequest httpRequest = new MockHttpServletRequest();
		AccountSecurityContext securityContext = new AccountSecurityContext();
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGuestLoginIdentitiesDao loginsDao = new MockGuestLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GuestAuthService authService = new GuestAuthService();
		authService.setHttpServletRequest(httpRequest);
		authService.setAccountSecurityContext(securityContext);
		authService.setAccountsDao(accountsDao);
		authService.setGuestLoginIdentitiesDao(loginsDao);

		// Call the service.
		Account loggedInAccount = authService.loginAsGuest();

		// Verify the results
		Assert.assertNotNull(loggedInAccount);
		Assert.assertEquals(1, loginsDao.logins.size());
	}

	/**
	 * Ensures that {@link GuestAuthService#loginAsGuest()} behaves as expected
	 * when the user/client already has an active login.
	 */
	@Test
	public void existingLogin() {
		// Create the mock params to pass to the service.
		HttpServletRequest httpRequest = new MockHttpServletRequest();
		AccountSecurityContext securityContext = new AccountSecurityContext();
		MockAccountsDao accountsDao = new MockAccountsDao();
		MockGuestLoginIdentitiesDao loginsDao = new MockGuestLoginIdentitiesDao(
				accountsDao);

		// Create the service.
		GuestAuthService authService = new GuestAuthService();
		authService.setHttpServletRequest(httpRequest);
		authService.setAccountSecurityContext(securityContext);
		authService.setAccountsDao(accountsDao);
		authService.setGuestLoginIdentitiesDao(loginsDao);

		// Call the service once to login and create an Account.
		authService.loginAsGuest();

		// Call the service a second time logged in as the new Account.
		securityContext = new AccountSecurityContext(
				accountsDao.accounts.get(0));
		authService.setAccountSecurityContext(securityContext);
		expectedException.expect(WebApplicationException.class);
		expectedException.expectMessage("User already logged in.");
		authService.loginAsGuest();
	}
}
