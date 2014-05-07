package com.justdavis.karl.rpstourney.service.app.auth.guest;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.app.JettyBindingsForITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * Integration tests for {@link GuestAuthResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JettyBindingsForITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class GuestAuthResourceImplIT {
	@Inject
	private EmbeddedServer server;

	@Inject
	private IGuestLoginIndentitiesDao loginsDao;

	/**
	 * Ensures that {@link GuestAuthResourceImpl} creates new
	 * {@link GuestLoginIdentity}s as expected.
	 */
	@Test
	public void createLogin() {
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
