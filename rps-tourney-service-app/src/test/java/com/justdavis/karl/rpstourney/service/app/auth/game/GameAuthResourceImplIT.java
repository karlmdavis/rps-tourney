package com.justdavis.karl.rpstourney.service.app.auth.game;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForWebServiceITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * Integration tests for {@link GameAuthResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringBindingsForWebServiceITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class GameAuthResourceImplIT {
	@Inject
	private EmbeddedServer server;

	@Inject
	private IGameLoginIndentitiesDao loginsDao;

	@Inject
	private IDataSourceSchemaManager schemaManager;

	@Inject
	private IConfigLoader configLoader;

	/**
	 * Wipes and repopulates the data source schema between tests.
	 */
	@After
	public void wipeSchema() {
		schemaManager.wipeSchema(configLoader.getConfig().getDataSourceCoordinates());
		schemaManager.createOrUpgradeSchema(configLoader.getConfig().getDataSourceCoordinates());
	}

	/**
	 * Ensures that {@link GameAuthResourceImpl} creates new {@link GameLoginIdentity}s as expected.
	 *
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void createAndValidateLogin() throws AddressException {
		ClientConfig clientConfig = new ClientConfig(server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();

		// Create the login and account.
		GameAuthClient gameAuthClient = new GameAuthClient(clientConfig, cookieStore);
		InternetAddress loginAddress = new InternetAddress("foo@example.com");
		Account createdAccount = gameAuthClient.createGameLogin(loginAddress, "secret");

		// Verify the create results.
		Assert.assertNotNull(createdAccount);
		GameLoginIdentity loginFromDb = loginsDao.find(loginAddress);
		Assert.assertNotNull(loginFromDb);
		Assert.assertEquals(loginAddress, loginFromDb.getEmailAddress());

		// Validate the login.
		AccountsClient accountsClient = new AccountsClient(clientConfig, cookieStore);
		Account validatedAccount = accountsClient.validateAuth();

		// Verify the validate results.
		Assert.assertNotNull(validatedAccount);
		Assert.assertEquals(createdAccount.getId(), validatedAccount.getId());
	}

	/**
	 * Ensures that {@link GameAuthResourceImpl} creates new {@link GameLoginIdentity}s as expected when they're being
	 * added to an existing account.
	 *
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void createLoginForExistingAccount() throws AddressException {
		ClientConfig clientConfig = new ClientConfig(server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();

		// Create a guest login and account.
		GuestAuthClient guestAuthClient = new GuestAuthClient(clientConfig, cookieStore);
		Account createdAccount = guestAuthClient.loginAsGuest();
		Assert.assertNotNull(createdAccount);

		// Create a game login for the same account.
		GameAuthClient gameAuthClient = new GameAuthClient(clientConfig, cookieStore);
		Account updatedAccount = gameAuthClient.createGameLogin(new InternetAddress("foo@example.com"), "secret");
		Assert.assertNotNull(updatedAccount);
		Assert.assertEquals(1, loginsDao.getLogins().size());
		Assert.assertEquals(createdAccount.getId(), updatedAccount.getId());
	}

	/**
	 * Ensures that {@link GameAuthResourceImpl#loginWithGameAccount(InternetAddress, String)} works as expected.
	 *
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void loginWithGameAccount() throws AddressException {
		ClientConfig clientConfig = new ClientConfig(server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();

		// Create the login and account.
		GameAuthClient gameAuthClient = new GameAuthClient(clientConfig, cookieStore);
		Account createdAccount = gameAuthClient.createGameLogin(new InternetAddress("foo@example.com"), "secret");

		// Make sure the create response looks correct.
		Assert.assertNotNull(createdAccount);

		// Reset the client's login/auth cookies.
		cookieStore.clear();

		// Try to login.
		Account loggedInAccount = gameAuthClient.loginWithGameAccount(new InternetAddress("foo@example.com"), "secret");

		// Make sure the login response looks correct.
		Assert.assertNotNull(loggedInAccount);
		Assert.assertEquals(createdAccount.getId(), loggedInAccount.getId());

		// Validate the login.
		AccountsClient accountsClient = new AccountsClient(clientConfig, cookieStore);
		Account validatedAccount = accountsClient.validateAuth();

		// Verify the validate results.
		Assert.assertNotNull(validatedAccount);
		Assert.assertEquals(createdAccount.getId(), validatedAccount.getId());
	}
}
