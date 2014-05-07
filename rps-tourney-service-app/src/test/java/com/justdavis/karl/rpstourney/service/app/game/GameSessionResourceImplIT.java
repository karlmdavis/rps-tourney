package com.justdavis.karl.rpstourney.service.app.game;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.GameSession.State;
import com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.app.JettyBindingsForITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameSessionClient;

/**
 * Integration tests for {@link GameSessionResourceImpl} and
 * {@link GameSessionClient}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JettyBindingsForITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class GameSessionResourceImplIT {
	@Inject
	private EmbeddedServer server;

	@Inject
	private IDataSourceSchemaManager schemaManager;

	@Inject
	private IConfigLoader configLoader;

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
	 * Ensures that the client and server
	 * {@link IGameSessionResource#createGame()} and
	 * {@link IGameSessionResource#getGame(String)} implementations work
	 * correctly.
	 */
	@Test
	public void createAndGet() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();

		// Login the player.
		GuestAuthClient authClientForPlayer1 = new GuestAuthClient(
				clientConfig, cookiesForPlayer1);
		Account accountForPlayer1 = authClientForPlayer1.loginAsGuest();

		// Create the game.
		GameSessionClient gameClientForPlayer1 = new GameSessionClient(
				clientConfig, cookiesForPlayer1);
		GameSession gameFromCreate = gameClientForPlayer1.createGame();
		Assert.assertNotNull(gameFromCreate);
		Assert.assertNotNull(gameFromCreate.getId());
		Assert.assertNotNull(gameFromCreate.getPlayer1());
		Assert.assertEquals(accountForPlayer1, gameFromCreate.getPlayer1()
				.getHumanAccount());

		// Retrieve the game.
		GameSession gameFromGet = gameClientForPlayer1.getGame(gameFromCreate
				.getId());
		Assert.assertNotNull(gameFromGet);
		Assert.assertNotNull(gameFromGet.getId());
		Assert.assertEquals(gameFromCreate.getId(), gameFromGet.getId());
		Assert.assertEquals(accountForPlayer1, gameFromGet.getPlayer1()
				.getHumanAccount());
	}

	/**
	 * Ensures that the client and server {@link IGameSessionResource}
	 * implementations work correctly for a simple 1-round game.
	 * 
	 * @throws AddressException
	 *             (won't be thrown; address is correct and static)
	 */
	@Test
	public void playSimpleGame() throws AddressException {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();
		CookieStore cookiesForPlayer2 = new CookieStore();

		// Login the players.
		GameAuthClient authClientForPlayer1 = new GameAuthClient(clientConfig,
				cookiesForPlayer1);
		authClientForPlayer1.createGameLogin(new InternetAddress(
				"foo@example.com"), "s,e%c&r et");
		GuestAuthClient authClientForPlayer2 = new GuestAuthClient(
				clientConfig, cookiesForPlayer2);
		Account accountForPlayer2 = authClientForPlayer2.loginAsGuest();

		// Create and configure the game.
		GameSessionClient gameClientForPlayer1 = new GameSessionClient(
				clientConfig, cookiesForPlayer1);
		GameSessionClient gameClientForPlayer2 = new GameSessionClient(
				clientConfig, cookiesForPlayer2);
		GameSession game = gameClientForPlayer1.createGame();
		gameClientForPlayer1.setMaxRounds(game.getId(), game.getMaxRounds(), 1);
		gameClientForPlayer2.joinGame(game.getId());

		// Play the game.
		gameClientForPlayer1.submitThrow(game.getId(), 0, Throw.ROCK);
		gameClientForPlayer2.submitThrow(game.getId(), 0, Throw.ROCK);
		gameClientForPlayer1.submitThrow(game.getId(), 1, Throw.ROCK);
		gameClientForPlayer2.submitThrow(game.getId(), 1, Throw.PAPER);

		// Verify the game's results.
		game = gameClientForPlayer1.getGame(game.getId());
		Assert.assertNotNull(game);
		Assert.assertEquals(accountForPlayer2, game.getPlayer2()
				.getHumanAccount());
		Assert.assertEquals(accountForPlayer2, game.getPlayer2()
				.getHumanAccount());
		Assert.assertEquals(1, game.getMaxRounds());
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(accountForPlayer2, game.getWinner()
				.getHumanAccount());
	}

	/**
	 * Ensures that the server
	 * {@link IGameSessionResource#setMaxRounds(String, int, int)}
	 * implementation works correctly in the face of concurrent calls.
	 * 
	 * @throws ExecutionException
	 *             (should never happen, as we never cancel tasks)
	 * @throws InterruptedException
	 *             (should never happen, as we never cancel tasks)
	 */
	@Test
	public void setMaxRoundsConcurrency() throws InterruptedException,
			ExecutionException {
		/*
		 * Note to self: On 2014-04-27, I saw this test fail one of its
		 * assertions but was unable to reproduce (can't recall which
		 * assertion). I'd guess there's still an intermittent concurrency bug
		 * lurking.
		 */

		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();
		CookieStore cookiesForPlayer2 = new CookieStore();

		// Login the players.
		GuestAuthClient authClientForPlayer1 = new GuestAuthClient(
				clientConfig, cookiesForPlayer1);
		authClientForPlayer1.loginAsGuest();
		GuestAuthClient authClientForPlayer2 = new GuestAuthClient(
				clientConfig, cookiesForPlayer2);
		authClientForPlayer2.loginAsGuest();

		// Create the game.
		final GameSessionClient gameClientForPlayer1 = new GameSessionClient(
				clientConfig, cookiesForPlayer1);
		final GameSession game = gameClientForPlayer1.createGame();

		/*
		 * What we need to ensure can't happen is this: two or more simultaneous
		 * requests that both succeed. Because of the guard "oldMaxRounds"
		 * parameter required, only one concurrent call to this method should
		 * ever be told it succeeded. The others should all fail because they
		 * pass in an out-of-date "oldMaxRounds" value. To test this, we'll run
		 * pairs of these requests concurrently and ensure that only one ever
		 * succeeds.
		 */

		// Request 'A' will always try to set the rounds from 3 to 5.
		Callable<GameSession> requestA = new Callable<GameSession>() {
			@Override
			public GameSession call() throws Exception {
				try {
					GameSession gameAfterModification = gameClientForPlayer1
							.setMaxRounds(game.getId(), 3, 5);

					/*
					 * We'll use a non-null result to signal that the request
					 * succeeded.
					 */
					return gameAfterModification;
				} catch (Throwable t) {
					/*
					 * We'll use a null result to signal that the request
					 * failed.
					 */
					return null;
				}
			}
		};

		// Request 'B' will always try to set the rounds from 3 to 7.
		final GameSessionClient gameClientForPlayer2 = new GameSessionClient(
				clientConfig, cookiesForPlayer2);
		Callable<GameSession> requestB = new Callable<GameSession>() {
			@Override
			public GameSession call() throws Exception {
				try {
					GameSession gameAfterModification = gameClientForPlayer2
							.setMaxRounds(game.getId(), 3, 7);

					/*
					 * We'll use a non-null result to signal that the request
					 * succeeded.
					 */
					return gameAfterModification;
				} catch (Throwable t) {
					/*
					 * We'll use a null result to signal that the request
					 * failed.
					 */
					return null;
				}
			}
		};

		// Try to break this over and over.
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		for (int i = 0; i < 100; i++) {
			Future<GameSession> futureA = executorService.submit(requestA);
			Future<GameSession> futureB = executorService.submit(requestB);
			GameSession resultA = futureA.get();
			GameSession resultB = futureB.get();

			// Check the results: exactly one should have succeeded.
			Assert.assertFalse("Both failed on attempt " + (i + 1),
					resultA == null && resultB == null);
			Assert.assertFalse("Both succeeded on attempt " + (i + 1),
					resultA != null && resultB != null);

			// Reset the number of rounds back to 3.
			GameSession currentGame = resultA != null ? resultA : resultB;
			gameClientForPlayer1.setMaxRounds(game.getId(),
					currentGame.getMaxRounds(), 3);
		}

		// If we got here: Success!
	}

	/**
	 * Ensures that the server
	 * {@link IGameSessionResource#submitThrow(String, int, Throw)}
	 * implementation works correctly in the face of concurrent calls.
	 * 
	 * @throws ExecutionException
	 *             (should never happen, as we never cancel tasks)
	 * @throws InterruptedException
	 *             (should never happen, as we never cancel tasks)
	 * @throws AddressException
	 *             (should never happen, as we only use static, valid addresses
	 *             here)
	 */
	@Test
	public void submitThrowConcurrency() throws InterruptedException,
			ExecutionException, AddressException {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1a = new CookieStore();
		CookieStore cookiesForPlayer2 = new CookieStore();

		// Login the players.
		GameAuthClient authClientForPlayer1a = new GameAuthClient(clientConfig,
				cookiesForPlayer1a);
		authClientForPlayer1a.createGameLogin(new InternetAddress(
				"player1@example.com"), "secret");
		GuestAuthClient authClientForPlayer2 = new GuestAuthClient(
				clientConfig, cookiesForPlayer2);
		authClientForPlayer2.loginAsGuest();

		// Create the game.
		final GameSessionClient gameClientForPlayer1a = new GameSessionClient(
				clientConfig, cookiesForPlayer1a);
		GameSession game = gameClientForPlayer1a.createGame();
		final String gameSessionId = game.getId();
		final GameSessionClient gameClientForPlayer2 = new GameSessionClient(
				clientConfig, cookiesForPlayer2);
		gameClientForPlayer2.joinGame(gameSessionId);
		gameClientForPlayer2.setMaxRounds(gameSessionId, game.getMaxRounds(),
				999999);

		/*
		 * What we want to ensure here is that the following scenario works
		 * correctly: three concurrent calls from three separate clients. The
		 * first player is submitting two separate throws at once. The second
		 * player is also submitting its throw. We want to ensure that, for the
		 * first player, only one of the two calls succeeds.
		 */

		CookieStore cookiesForPlayer1b = new CookieStore();
		GameAuthClient authClientForPlayer1b = new GameAuthClient(clientConfig,
				cookiesForPlayer1b);
		authClientForPlayer1b.loginWithGameAccount(new InternetAddress(
				"player1@example.com"), "secret");
		final GameSessionClient gameClientForPlayer1b = new GameSessionClient(
				clientConfig, cookiesForPlayer1b);

		final AtomicInteger currentRound = new AtomicInteger(0);

		// Request 'A' will always be player 1 throwing ROCK.
		Callable<GameSession> requestA = new Callable<GameSession>() {
			@Override
			public GameSession call() throws Exception {
				try {
					GameSession gameAfterModification = gameClientForPlayer1a
							.submitThrow(gameSessionId, currentRound.get(),
									Throw.ROCK);

					/*
					 * We'll use a non-null result to signal that the request
					 * succeeded.
					 */
					return gameAfterModification;
				} catch (Throwable t) {
					/*
					 * We'll use a null result to signal that the request
					 * failed.
					 */
					return null;
				}
			}
		};

		// Request 'B' will always be player 1 throwing PAPER.
		Callable<GameSession> requestB = new Callable<GameSession>() {
			@Override
			public GameSession call() throws Exception {
				try {
					GameSession gameAfterModification = gameClientForPlayer1b
							.submitThrow(gameSessionId, currentRound.get(),
									Throw.PAPER);

					/*
					 * We'll use a non-null result to signal that the request
					 * succeeded.
					 */
					return gameAfterModification;
				} catch (Throwable t) {
					/*
					 * We'll use a null result to signal that the request
					 * failed.
					 */
					return null;
				}
			}
		};

		// Request 'C' will always be player 2 throwing SCISSORS.
		Callable<GameSession> requestC = new Callable<GameSession>() {
			@Override
			public GameSession call() throws Exception {
				try {
					GameSession gameAfterModification = gameClientForPlayer2
							.submitThrow(gameSessionId, currentRound.get(),
									Throw.SCISSORS);

					/*
					 * We'll use a non-null result to signal that the request
					 * succeeded.
					 */
					return gameAfterModification;
				} catch (Throwable t) {
					/*
					 * We'll use a null result to signal that the request
					 * failed.
					 */
					return null;
				}
			}
		};

		// Try to break this over and over.
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 100; i++) {
			Future<GameSession> futureA = executorService.submit(requestA);
			Future<GameSession> futureB = executorService.submit(requestB);
			Future<GameSession> futureC = executorService.submit(requestC);
			GameSession resultA = futureA.get();
			GameSession resultB = futureB.get();
			GameSession resultC = futureC.get();

			// Check the results: exactly one of A and B should have succeeded.
			Assert.assertFalse("Both failed on attempt " + (i + 1),
					resultA == null && resultB == null);
			Assert.assertFalse("Both succeeded on attempt " + (i + 1),
					resultA != null && resultB != null);

			// Check the results: C should always succeed.
			Assert.assertTrue("Failed on attempt " + (i + 1), resultC != null);

			// Prepare for the next round.
			GameSession gameThusFar = gameClientForPlayer2
					.prepareRound(gameSessionId);

			/*
			 * Sanity check: ensure that the round's result is correct. (While
			 * developing this test, intermittent failures were seen where
			 * player 2's throw wasn't actually showing up.)
			 */
			Assert.assertEquals("Game state is off: " + gameThusFar, i + 2,
					gameThusFar.getRounds().size());

			// Bump the round counter.
			currentRound.incrementAndGet();
		}

		// If we got here: Success!
	}
}
