package com.justdavis.karl.rpstourney.service.app.auth;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.app.JettyBindingsForITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.auth.guest.GuestAuthResourceImpl;
import com.justdavis.karl.rpstourney.service.app.auth.guest.IGuestLoginIndentitiesDao;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;

/**
 * Integration tests for {@link AccountsResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JettyBindingsForITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class AccountsResourceImplIT {
	@Inject
	private EmbeddedServer server;

	@Inject
	private IGuestLoginIndentitiesDao loginsDao;

	@Inject
	private IDataSourceSchemaManager schemaManager;

	@Inject
	private IConfigLoader configLoader;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * Wipes and repopulates the data source schema between tests.
	 */
	@After
	public void wipeSchema() {
		schemaManager.wipeSchema(configLoader.getConfig()
				.getDataSourceCoordinates());
		schemaManager.createOrUpgradeSchema(configLoader.getConfig()
				.getDataSourceCoordinates());
	}

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

	/**
	 * Ensures that {@link AccountsResourceImpl#updateAccount(Account)} works as
	 * expected to modify an already-existing {@link Account}.
	 */
	@Test
	public void updateAccount() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();
		AccountsClient accountsClient = new AccountsClient(clientConfig,
				cookieStore);

		// Create the login and account.
		GuestAuthClient guestAuthClient = new GuestAuthClient(clientConfig,
				cookieStore);
		Account createdAccount = guestAuthClient.loginAsGuest();

		// Create the AuthToken and grab a copy of it.
		AuthToken originalAuthToken = accountsClient.selectOrCreateAuthToken();

		// Modify the Account and attempt to save those changes.
		createdAccount.setName("foo");
		Account updatedAccount = accountsClient.updateAccount(createdAccount);

		// Verify the update results.
		Assert.assertNotNull(updatedAccount);
		Assert.assertEquals(createdAccount.getId(), updatedAccount.getId());
		Assert.assertEquals(createdAccount.getName(), updatedAccount.getName());

		// Make sure that the AuthToken is the same.
		AuthToken copyOfAuthToken = accountsClient.selectOrCreateAuthToken();
		Assert.assertEquals(originalAuthToken.getToken(),
				copyOfAuthToken.getToken());

		/*
		 * Pull the Account again and verify that the changes are still there.
		 * (Basically making sure that the TX was committed, which I had screwed
		 * up at one point.)
		 */
		Account copyOfAccount = accountsClient.getAccount();
		Assert.assertEquals(createdAccount.getId(), copyOfAccount.getId());
		Assert.assertEquals(createdAccount.getName(), copyOfAccount.getName());
	}

	/**
	 * Ensures that the bean validation on
	 * {@link AccountsResourceImpl#updateAccount(Account)} is working as
	 * expected.
	 */
	@Test
	public void updateAccountValidation() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();
		AccountsClient accountsClient = new AccountsClient(clientConfig,
				cookieStore);

		// Create the login and account.
		GuestAuthClient guestAuthClient = new GuestAuthClient(clientConfig,
				cookieStore);
		Account createdAccount = guestAuthClient.loginAsGuest();

		/*
		 * Set the Account.name to something invalid and try to apply that. This
		 * should go boom with an HTTP 400.
		 */
		createdAccount.setName("<script>alert(\"pwned!\");</script>");
		BadRequestException error = null;
		try {
			accountsClient.updateAccount(createdAccount);
		} catch (BadRequestException e) {
			error = e;
		}

		Assert.assertNotNull(error);
	}

	/**
	 * Ensures that {@link AccountsResourceImpl#selectOrCreateAuthToken()} works
	 * as expected when used with an {@link Account} created via
	 * {@link GuestAuthResourceImpl#loginAsGuest()}.
	 */
	@Test
	public void selectOrCreateAuthToken() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();

		// Create the login and account.
		GuestAuthClient guestAuthClient = new GuestAuthClient(clientConfig,
				cookieStore);
		guestAuthClient.loginAsGuest();

		// Get an AuthToken.
		AccountsClient accountsClient = new AccountsClient(clientConfig,
				cookieStore);
		AuthToken authToken = accountsClient.selectOrCreateAuthToken();

		// Verify the validate results.
		Assert.assertNotNull(authToken);
	}

	/**
	 * Ensures that {@link AccountsResourceImpl#mergeAccount(Account)} works as
	 * expected.
	 */
	@Test
	public void mergeAccount() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());

		// Create the source account and associated entities to be merged.
		CookieStore sourceCookies = new CookieStore();
		AccountsClient sourceAccountsClient = new AccountsClient(clientConfig,
				sourceCookies);
		GuestAuthClient sourceGuestAuthClient = new GuestAuthClient(
				clientConfig, sourceCookies);
		GameClient sourceGameClient = new GameClient(clientConfig,
				sourceCookies);
		Account sourceAccount = sourceGuestAuthClient.loginAsGuest();
		sourceAccount.setName("foo");
		sourceAccountsClient.updateAccount(sourceAccount);
		AuthToken sourceAuthToken = sourceAccountsClient
				.selectOrCreateAuthToken();
		sourceGameClient.createGame();

		// Create the target account to merge into.
		CookieStore targetCookies = new CookieStore();
		AccountsClient targetAccountsClient = new AccountsClient(clientConfig,
				targetCookies);
		GuestAuthClient targetGuestAuthClient = new GuestAuthClient(
				clientConfig, targetCookies);
		GameClient targetGameClient = new GameClient(clientConfig,
				targetCookies);
		Account targetAccount = targetGuestAuthClient.loginAsGuest();

		/*
		 * Merge the source Account and associated objects to the target
		 * Account.
		 */
		targetAccountsClient.mergeAccount(targetAccount.getId(),
				sourceAuthToken.getToken());

		// Verify the results.
		Assert.assertEquals(2, targetAccountsClient.getAccount().getLogins()
				.size());
		Assert.assertEquals(sourceAccount.getName(), targetAccountsClient
				.getAccount().getName());
		Assert.assertEquals(1, targetGameClient.getGamesForPlayer().size());
	}
}
