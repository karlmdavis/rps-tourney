package com.justdavis.karl.rpstourney.service.app.game;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

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
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.service.app.JettyBindingsForITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.PlayersClient;

/**
 * Integration tests for {@link PlayersResourceImpl} and {@link PlayersClient}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JettyBindingsForITs.class })
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
		schemaManager.wipeSchema(configLoader.getConfig()
				.getDataSourceCoordinates());
		schemaManager.createOrUpgradeSchema(configLoader.getConfig()
				.getDataSourceCoordinates());
	}

	/**
	 * Ensures that {@link PlayersResourceImpl#getPlayersForBuiltInAis()} works
	 * correctly.
	 */
	@Test
	public void getPlayersForBuiltInAis() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();

		// Create some AI Players.
		aiPlayerInitializer.initializeAiPlayers(BuiltInAi.THREE_SIDED_DIE_V1);

		// Try to get the list of Players.
		PlayersClient playersClient = new PlayersClient(clientConfig,
				cookiesForPlayer1);
		Set<Player> aiPlayers = playersClient.getPlayersForBuiltInAis();
		Assert.assertEquals(1, aiPlayers.size());
	}

	/**
	 * Ensures that {@link PlayersResourceImpl#getPlayerForBuiltInAi(BuiltInAi)}
	 * works correctly.
	 */
	@Test
	public void getPlayerForBuiltInAi() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();
		PlayersClient playersClient = new PlayersClient(clientConfig,
				cookiesForPlayer1);

		// Create some AI Players.
		aiPlayerInitializer.initializeAiPlayers(BuiltInAi.ONE_SIDED_DIE_PAPER);

		// Try to retrieve a Player that's not there.
		HttpClientException playerNotFoundError = null;
		try {
			playersClient.getPlayerForBuiltInAi(BuiltInAi.ONE_SIDED_DIE_ROCK);
		} catch (HttpClientException e) {
			playerNotFoundError = e;
		}
		Assert.assertNotNull(playerNotFoundError);
		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(),
				playerNotFoundError.getStatus().getStatusCode());

		// Try to retrieve a Player that is there.
		Assert.assertNotNull(playersClient
				.getPlayerForBuiltInAi(BuiltInAi.ONE_SIDED_DIE_PAPER));
	}
}
