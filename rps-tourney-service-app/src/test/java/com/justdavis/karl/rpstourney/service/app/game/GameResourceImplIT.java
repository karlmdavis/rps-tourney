package com.justdavis.karl.rpstourney.service.app.game;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.State;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForWebServiceITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;
import com.justdavis.karl.rpstourney.service.client.game.PlayersClient;

/**
 * Integration tests for {@link GameResourceImpl} and {@link GameClient}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringBindingsForWebServiceITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class GameResourceImplIT {
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
	 * <p>
	 * Ensures that {@link GameResourceImpl#getGamesForPlayer()} works correctly
	 * when the requesting client has an {@link Account}, but no associated
	 * {@link Player}.
	 * </p>
	 * <p>
	 * This is a regression test case for an issue encountered during
	 * development.
	 * </p>
	 */
	@Test
	public void getGamesForPlayerWithNewAccount() {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();

		// Login the player.
		GuestAuthClient authClientForPlayer1 = new GuestAuthClient(
				clientConfig, cookiesForPlayer1);
		authClientForPlayer1.loginAsGuest();

		// Try to get the list of games, which should be empty.
		GameClient gameClientForPlayer1 = new GameClient(clientConfig,
				cookiesForPlayer1);
		Assert.assertEquals(0, gameClientForPlayer1.getGamesForPlayer().size());
	}

	/**
	 * Ensures that the client and server {@link IGameResource#createGame()} and
	 * {@link IGameResource#getGame(String)} implementations work correctly.
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
		GameClient gameClientForPlayer1 = new GameClient(clientConfig,
				cookiesForPlayer1);
		GameView gameFromCreate = gameClientForPlayer1.createGame();
		Assert.assertNotNull(gameFromCreate);
		Assert.assertNotNull(gameFromCreate.getId());
		Assert.assertNotNull(gameFromCreate.getPlayer1());
		Assert.assertEquals(accountForPlayer1, gameFromCreate.getPlayer1()
				.getHumanAccount());

		// Retrieve the game.
		GameView gameFromGet = gameClientForPlayer1.getGame(gameFromCreate
				.getId());
		Assert.assertNotNull(gameFromGet);
		Assert.assertNotNull(gameFromGet.getId());
		Assert.assertEquals(gameFromCreate.getId(), gameFromGet.getId());
		Assert.assertEquals(accountForPlayer1, gameFromGet.getPlayer1()
				.getHumanAccount());
	}

	/**
	 * Ensures that the client and server {@link IGameResource} implementations
	 * work correctly for a simple 1-round game.
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
		GameClient gameClientForPlayer1 = new GameClient(clientConfig,
				cookiesForPlayer1);
		GameClient gameClientForPlayer2 = new GameClient(clientConfig,
				cookiesForPlayer2);
		GameView game = gameClientForPlayer1.createGame();
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
	 * Ensures that the client and server {@link IGameResource} implementations
	 * work correctly for a simple game with a human and an AI player.
	 * 
	 * @throws AddressException
	 *             (won't be thrown; address is correct and static)
	 */
	@Test
	public void playGameWithAi() throws AddressException {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookiesForPlayer1 = new CookieStore();

		// Login the human player.
		GuestAuthClient authClientForPlayer1 = new GuestAuthClient(
				clientConfig, cookiesForPlayer1);
		authClientForPlayer1.loginAsGuest();

		// Create the game and request an AI opponent.
		GameClient gameClientForPlayer1 = new GameClient(clientConfig,
				cookiesForPlayer1);
		PlayersClient playersClientForPlayer1 = new PlayersClient(clientConfig,
				cookiesForPlayer1);
		aiPlayerInitializer.initializeAiPlayers(BuiltInAi.ONE_SIDED_DIE_ROCK);
		Player aiPlayer = playersClientForPlayer1
				.getPlayersForBuiltInAis(
						Arrays.asList(BuiltInAi.ONE_SIDED_DIE_ROCK)).iterator()
				.next();
		GameView game = gameClientForPlayer1.createGame();
		gameClientForPlayer1.inviteOpponent(game.getId(), aiPlayer.getId());

		// Play the game.
		gameClientForPlayer1.submitThrow(game.getId(), 0, Throw.ROCK);
		gameClientForPlayer1.submitThrow(game.getId(), 1, Throw.PAPER);
		gameClientForPlayer1.submitThrow(game.getId(), 2, Throw.PAPER);

		// Verify the game's results.
		game = gameClientForPlayer1.getGame(game.getId());
		Assert.assertNotNull(game);
		Assert.assertEquals(aiPlayer, game.getPlayer2());
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(game.getPlayer1(), game.getWinner());
	}

	/**
	 * Ensures that {@link GameResourceImpl#inviteOpponent(String, long)}
	 * correctly handles security: only player 1 should be able to invite
	 * opponents into their game.
	 * 
	 * @throws AddressException
	 *             (won't be thrown; address is correct and static)
	 */
	@Test
	public void inviteOpponentSecurity() throws AddressException {
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());

		// Login the human player.
		CookieStore cookiesForPlayer1 = new CookieStore();
		GuestAuthClient authClientForPlayer1 = new GuestAuthClient(
				clientConfig, cookiesForPlayer1);
		authClientForPlayer1.loginAsGuest();

		// Create the game and request an AI opponent.
		GameClient gameClientForPlayer1 = new GameClient(clientConfig,
				cookiesForPlayer1);
		GameView game = gameClientForPlayer1.createGame();

		// Login a "villian".
		CookieStore cookiesForVillian = new CookieStore();
		GuestAuthClient authClientForVillain = new GuestAuthClient(
				clientConfig, cookiesForVillian);
		authClientForVillain.loginAsGuest();
		GameClient gameClientForVillian = new GameClient(clientConfig,
				cookiesForVillian);

		// Have the villian try to invite an AI opponent.
		PlayersClient playersClientForVillian = new PlayersClient(clientConfig,
				cookiesForVillian);
		aiPlayerInitializer.initializeAiPlayers(BuiltInAi.ONE_SIDED_DIE_ROCK);
		Player aiPlayer = playersClientForVillian
				.getPlayersForBuiltInAis(
						Arrays.asList(BuiltInAi.ONE_SIDED_DIE_ROCK)).iterator()
				.next();
		HttpClientException inviteError = null;
		try {
			gameClientForVillian.inviteOpponent(game.getId(), aiPlayer.getId());
		} catch (HttpClientException e) {
			inviteError = e;
		}

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), inviteError
				.getStatus().getStatusCode());
	}

	/**
	 * Ensures that {@link GameResourceImpl} doesn't reveal moves made in the
	 * current round, except to the player that made them. If this were to
	 * happen, it would allow players to cheat, as they would be able to see
	 * their opponent's move before making their own.
	 */
	@Test
	public void opponentsMoveNotRevealed() throws AddressException {
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

		// Create and configure the game.
		GameClient gameClientForPlayer1 = new GameClient(clientConfig,
				cookiesForPlayer1);
		GameClient gameClientForPlayer2 = new GameClient(clientConfig,
				cookiesForPlayer2);
		GameView game = gameClientForPlayer1.createGame();
		gameClientForPlayer1.setMaxRounds(game.getId(), game.getMaxRounds(), 1);
		gameClientForPlayer2.joinGame(game.getId());

		// Player 1: Make a move.
		gameClientForPlayer1.submitThrow(game.getId(), 0, Throw.ROCK);

		// Verify that the move is visible to Player 1.
		game = gameClientForPlayer1.getGame(game.getId());
		Assert.assertEquals(Throw.ROCK, game.getRounds().get(0)
				.getThrowForPlayer1());

		// Verify that the move isn't visible to Player 2.
		game = gameClientForPlayer2.getGame(game.getId());
		Assert.assertNull(game.getRounds().get(0).getThrowForPlayer1());

		// Verify that the move isn't visible to a non-player.
		CookieStore cookiesForNonPlayer = new CookieStore();
		GameClient gameClientForNonPlayer = new GameClient(clientConfig,
				cookiesForNonPlayer);
		game = gameClientForNonPlayer.getGame(game.getId());
		Assert.assertNull(game.getRounds().get(0).getThrowForPlayer1());
	}

	/**
	 * Ensures that the server
	 * {@link IGameResource#setMaxRounds(String, int, int)} implementation works
	 * correctly in the face of concurrent calls.
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
		final GameClient gameClientForPlayer1 = new GameClient(clientConfig,
				cookiesForPlayer1);
		final GameView game = gameClientForPlayer1.createGame();
		final GameClient gameClientForPlayer2 = new GameClient(clientConfig,
				cookiesForPlayer2);
		gameClientForPlayer2.joinGame(game.getId());

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
		Callable<GameView> requestA = new Callable<GameView>() {
			@Override
			public GameView call() throws Exception {
				try {
					GameView gameAfterModification = gameClientForPlayer1
							.setMaxRounds(game.getId(), 3, 5);

					/*
					 * We'll use a non-null result to signal that the request
					 * succeeded.
					 */
					return gameAfterModification;
				} catch (GameConflictException t) {
					/*
					 * We'll use a null result to signal that the request
					 * failed.
					 */
					return null;
				}
			}
		};

		// Request 'B' will always try to set the rounds from 3 to 7.
		Callable<GameView> requestB = new Callable<GameView>() {
			@Override
			public GameView call() throws Exception {
				try {
					GameView gameAfterModification = gameClientForPlayer2
							.setMaxRounds(game.getId(), 3, 7);

					/*
					 * We'll use a non-null result to signal that the request
					 * succeeded.
					 */
					return gameAfterModification;
				} catch (GameConflictException t) {
					/*
					 * We'll use a null result to signal that the request
					 * failed.
					 */
					return null;
				}
			}
		};

		// Create the executor that will run the requests.
		ExecutorService executorService = Executors.newFixedThreadPool(2);

		// Sanity checks: make sure both requests /can/ work.
		Assert.assertNotNull(executorService.submit(requestA).get());
		gameClientForPlayer1.setMaxRounds(game.getId(), 5, 3);
		Assert.assertNotNull(executorService.submit(requestB).get());
		gameClientForPlayer1.setMaxRounds(game.getId(), 7, 3);

		// Try to break this over and over.
		for (int i = 0; i < 100; i++) {
			Future<GameView> futureA = executorService.submit(requestA);
			Future<GameView> futureB = executorService.submit(requestB);
			GameView resultA = futureA.get();
			GameView resultB = futureB.get();

			// Check the results: exactly one should have succeeded.
			Assert.assertFalse("Both failed on attempt " + (i + 1),
					resultA == null && resultB == null);
			Assert.assertFalse("Both succeeded on attempt " + (i + 1),
					resultA != null && resultB != null);

			// Reset the number of rounds back to 3.
			GameView currentGame = resultA != null ? resultA : resultB;
			gameClientForPlayer1.setMaxRounds(game.getId(),
					currentGame.getMaxRounds(), 3);
		}

		// If we got here: Success!
	}

	/**
	 * Ensures that the server
	 * {@link IGameResource#submitThrow(String, int, Throw)} implementation
	 * works correctly in the face of concurrent calls.
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
		final GameClient gameClientForPlayer1a = new GameClient(clientConfig,
				cookiesForPlayer1a);
		GameView game = gameClientForPlayer1a.createGame();
		final String gameId = game.getId();
		final GameClient gameClientForPlayer2 = new GameClient(clientConfig,
				cookiesForPlayer2);
		gameClientForPlayer2.joinGame(gameId);
		gameClientForPlayer2.setMaxRounds(gameId, game.getMaxRounds(), 999999);

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
		final GameClient gameClientForPlayer1b = new GameClient(clientConfig,
				cookiesForPlayer1b);

		final AtomicInteger currentRound = new AtomicInteger(0);

		// Request 'A' will always be player 1 throwing ROCK.
		Callable<GameView> requestA = new Callable<GameView>() {
			@Override
			public GameView call() throws Exception {
				try {
					GameView gameAfterModification = gameClientForPlayer1a
							.submitThrow(gameId, currentRound.get(), Throw.ROCK);

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
		Callable<GameView> requestB = new Callable<GameView>() {
			@Override
			public GameView call() throws Exception {
				try {
					GameView gameAfterModification = gameClientForPlayer1b
							.submitThrow(gameId, currentRound.get(),
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
		Callable<GameView> requestC = new Callable<GameView>() {
			@Override
			public GameView call() throws Exception {
				try {
					GameView gameAfterModification = gameClientForPlayer2
							.submitThrow(gameId, currentRound.get(),
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
			Future<GameView> futureA = executorService.submit(requestA);
			Future<GameView> futureB = executorService.submit(requestB);
			Future<GameView> futureC = executorService.submit(requestC);
			GameView resultA = futureA.get();
			GameView resultB = futureB.get();
			GameView resultC = futureC.get();

			// Check the results: exactly one of A and B should have succeeded.
			Assert.assertFalse("Both failed on attempt " + (i + 1),
					resultA == null && resultB == null);
			Assert.assertFalse("Both succeeded on attempt " + (i + 1),
					resultA != null && resultB != null);

			// Check the results: C should always succeed.
			Assert.assertTrue("Failed on attempt " + (i + 1), resultC != null);

			// Prepare for the next round.
			GameView gameThusFar = gameClientForPlayer2.prepareRound(gameId);

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
