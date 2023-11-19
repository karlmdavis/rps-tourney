package com.justdavis.karl.rpstourney.service.app.game;

import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.misc.junit.JulLoggingToSlf4jBinder;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForWebServiceITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.PlayersClient;

/**
 * Integration tests for {@link PlayersResourceImpl} and {@link PlayersClient}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringBindingsForWebServiceITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class PlayersResourceImplIT {
	@Rule
	public JulLoggingToSlf4jBinder julBinder = new JulLoggingToSlf4jBinder();

	@Inject
	private EmbeddedServer server;

	@Inject
	private IDataSourceSchemaManager schemaManager;

	@Inject
	private IConfigLoader configLoader;

	@Inject
	private AiPlayerInitializer aiPlayerInitializer;

	/**
	 * Wipes and repopulates the data source schema between tests.
	 */
	@After
	public void wipeSchema() {
		schemaManager.wipeSchema(configLoader.getConfig().getDataSourceCoordinates());
		schemaManager.createOrUpgradeSchema(configLoader.getConfig().getDataSourceCoordinates());
	}

	/**
	 * Ensures that {@link PlayersResourceImpl#findOrCreatePlayer()} works correctly.
	 */
	@Test
	public void findOrCreatePlayer() {
		ClientConfig clientConfig = new ClientConfig(server.getServerBaseAddress());
		CookieStore cookies = new CookieStore();

		// Create an Account.
		GuestAuthClient authClient = new GuestAuthClient(clientConfig, cookies);
		Account accountFromLogin = authClient.loginAsGuest();

		// Try to create a Player for the Account.
		PlayersClient playersClient = new PlayersClient(clientConfig, cookies);
		Player playerFromFirstCall = playersClient.findOrCreatePlayer();
		Assert.assertNotNull(playerFromFirstCall);
		Assert.assertEquals(accountFromLogin, playerFromFirstCall.getHumanAccount());

		// Verify that a second call returns the same Player.
		Player playerFromSecondCall = playersClient.findOrCreatePlayer();
		Assert.assertNotNull(playerFromSecondCall);
		Assert.assertEquals(playerFromFirstCall.getId(), playerFromSecondCall.getId());
	}

	/**
	 * Ensures that {@link PlayersResourceImpl#getPlayersForBuiltInAis(java.util.List)} works correctly.
	 */
	@Test
	public void getPlayersForBuiltInAis() {
		ClientConfig clientConfig = new ClientConfig(server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();

		// Create some AI Players.
		aiPlayerInitializer.initializeAiPlayers(BuiltInAi.THREE_SIDED_DIE_V1);
		aiPlayerInitializer.initializeAiPlayers(BuiltInAi.WIN_STAY_LOSE_SHIFT_V1);

		// Try to get the list of Players.
		PlayersClient playersClient = new PlayersClient(clientConfig, cookiesForPlayer1);
		Set<Player> aiPlayers = playersClient
				.getPlayersForBuiltInAis(Arrays.asList(BuiltInAi.THREE_SIDED_DIE_V1, BuiltInAi.WIN_STAY_LOSE_SHIFT_V1));
		Assert.assertEquals(2, aiPlayers.size());
	}
}
