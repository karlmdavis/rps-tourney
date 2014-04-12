package com.justdavis.karl.rpstourney.service.app.auth;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.app.SpringITConfigWithJetty;
import com.justdavis.karl.rpstourney.service.app.auth.guest.GuestAuthResourceImpl;
import com.justdavis.karl.rpstourney.service.app.auth.guest.IGuestLoginIndentitiesDao;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * Integration tests for {@link AccountsResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringITConfigWithJetty.class })
@WebAppConfiguration
public final class AccountsResourceImplIT {
	@Inject
	private EmbeddedServer server;

	@Inject
	private IGuestLoginIndentitiesDao loginsDao;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * Ensures that {@link AccountsResourceImpl#validateAuth()} returns
	 * {@link Status#UNAUTHORIZED} as expected when called without
	 * authentication.
	 */
	@Test
	public void validateGuestLoginDenied() {
		/*
		 * Just a note: the AccountsResourceImpl will never even be run if
		 * everything is working correctly. Instead, the AuthorizationFilter
		 * will handle this.
		 */

		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();

		/*
		 * Attempt to validate the login, which should fail with an HTTP 401
		 * Unauthorized error.
		 */
		AccountsClient accountsClient = new AccountsClient(clientConfig,
				cookieStore);
		expectedException.expect(HttpClientException.class);
		expectedException.expectMessage(StringContains.containsString("401"));
		accountsClient.validateAuth();
	}

	/**
	 * Ensures that {@link AccountsResourceImpl#validateAuth()} works as
	 * expected when used with an {@link Account} created via
	 * {@link GuestAuthResourceImpl#loginAsGuest()}.
	 */
	@Test
	public void createAndValidateGuestLogin() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();

		// Create the login and account.
		GuestAuthClient guestAuthClient = new GuestAuthClient(clientConfig,
				cookieStore);
		Account createdAccount = guestAuthClient.loginAsGuest();

		// Verify the create results.
		Assert.assertNotNull(createdAccount);
		Assert.assertEquals(1, loginsDao.getLogins().size());

		// Validate the login.
		AccountsClient accountsClient = new AccountsClient(clientConfig,
				cookieStore);
		Account validatedAccount = accountsClient.validateAuth();

		// Verify the validate results.
		Assert.assertNotNull(validatedAccount);
		Assert.assertEquals(createdAccount.getId(), validatedAccount.getId());
	}
}
