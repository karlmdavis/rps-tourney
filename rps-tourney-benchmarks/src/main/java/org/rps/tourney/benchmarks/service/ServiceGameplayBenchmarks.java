package org.rps.tourney.benchmarks.service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.ws.rs.core.Response.Status;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.rps.tourney.benchmarks.BenchmarkUser;
import org.rps.tourney.benchmarks.serverutils.ServerState;
import org.rps.tourney.benchmarks.service.ServiceGameDisplayBenchmarks.GameInProgressState;

import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;
import com.justdavis.karl.rpstourney.service.client.game.PlayersClient;

/**
 * Contains {@link Benchmark}s for those web service methods related to playing
 * games.
 */
public class ServiceGameplayBenchmarks {
	/**
	 * A {@link Benchmark} for {@link GameClient#getGame(String)}, when the
	 * requesting user is unauthenticated.
	 * 
	 * @param gameInProgressState
	 *            a {@link GameInProgressState} instance that provides the game
	 *            to be requested from the web service
	 */
	@Benchmark
	public void playScriptedFiveRoundGame(GameplayState gameplayState) {
		ClientConfig config = new ClientConfig(gameplayState.getServerState().getServerManager().getServiceUrl());

		// Initialize the clients for player 'A'.
		CookieStore cookiesA = new CookieStore();
		GameAuthClient authClientA = new GameAuthClient(config, cookiesA);
		authClientA.loginWithGameAccount(BenchmarkUser.USER_A.getAddress(), BenchmarkUser.USER_A.getPassword());
		GameClient gameClientA = new GameClient(config, cookiesA);

		// Initialize the clients for player 'B'.
		CookieStore cookiesB = new CookieStore();
		GameAuthClient authClientB = new GameAuthClient(config, cookiesB);
		authClientB.loginWithGameAccount(BenchmarkUser.USER_B.getAddress(), BenchmarkUser.USER_B.getPassword());
		GameClient gameClientB = new GameClient(config, cookiesB);

		// Create the game to be played.
		GameView game = gameClientA.createGame();
		gameplayState.addGameForCleanup(game);
		gameClientB.joinGame(game.getId());

		/*
		 * Play through the game.
		 */

		// Round 0
		gameClientA.submitThrow(game.getId(), 0, Throw.ROCK);
		gameClientB.submitThrow(game.getId(), 0, Throw.PAPER);
		// Score: A: 0, B: 1

		// Round 1
		gameClientA.submitThrow(game.getId(), 1, Throw.ROCK);
		gameClientB.submitThrow(game.getId(), 1, Throw.ROCK);
		// Score: A: 0, B: 1

		// Round 2
		gameClientA.submitThrow(game.getId(), 2, Throw.SCISSORS);
		gameClientB.submitThrow(game.getId(), 2, Throw.PAPER);
		// Score: A: 1, B: 1

		// Round 3
		gameClientA.submitThrow(game.getId(), 3, Throw.SCISSORS);
		gameClientB.submitThrow(game.getId(), 3, Throw.SCISSORS);
		// Score: A: 1, B: 1

		// Round 4
		gameClientA.submitThrow(game.getId(), 4, Throw.SCISSORS);
		gameClientB.submitThrow(game.getId(), 4, Throw.PAPER);
		// Score: A: 2, B: 1

		// Verify the final state, just as a sanity check:
		game = gameClientA.getGame(game.getId());
		if (game.getScoreForPlayer1() != 2)
			throw new IllegalStateException();
		if (game.getScoreForPlayer2() != 1)
			throw new IllegalStateException();
	}

	/**
	 * This method is only here to allow this {@link Benchmark} class to be run
	 * inside Eclipse. These configuration settings specified in here are only
	 * applied within Eclipse.
	 * 
	 * @param args
	 *            (not used)
	 * @throws RunnerException
	 *             Any failures in the benchmarks will be wrapped and rethrown
	 *             as {@link RunnerException}s.
	 */
	public static void main(String[] args) throws RunnerException {
		ChainedOptionsBuilder benchmarkOptions = new OptionsBuilder().include(ServiceGameplayBenchmarks.class.getSimpleName())
				.warmupIterations(20).measurementIterations(10).forks(1).threads(10 ^ 2).verbosity(VerboseMode.EXTRA);
		// benchmarkOptions.addProfiler(StackProfiler.class);
		// benchmarkOptions.jvmArgsAppend(ExistingServerManager.jvmArgsForTomcatWtp());

		new Runner(benchmarkOptions.build()).run();
	}

	/**
	 * <p>
	 * Manages the state required for {@link ServiceGameplayBenchmarks}.
	 * </p>
	 * <p>
	 * Also tracks the {@link Game}s that were created during the benchmark and
	 * cleans them up afterwards in a {@link TearDown}.
	 * </p>
	 */
	@State(Scope.Benchmark)
	public static class GameplayState {
		private static final Queue<String> gameIds = new ConcurrentLinkedQueue<>();
		private ServerState serverState;

		/**
		 * @return the {@link ServerState} that the web service is running in
		 */
		public ServerState getServerState() {
			return serverState;
		}

		/**
		 * @param gameToCleanup
		 *            the {@link GameView} for the game to be cleaned up at the
		 *            end of the benchmarks
		 */
		public void addGameForCleanup(GameView gameToCleanup) {
			gameIds.add(gameToCleanup.getId());
		}

		/**
		 * Initializes {@link GameplayState} instances.
		 * 
		 * @param serverState
		 *            the {@link ServerState} that the web service is running in
		 */
		@Setup
		public void setup(ServerState serverState) {
			this.serverState = serverState;
			ClientConfig config = new ClientConfig(serverState.getServerManager().getServiceUrl());

			// Ensure that the benchmark user and player A exists.
			CookieStore cookiesA = new CookieStore();
			GameAuthClient authClientA = new GameAuthClient(config, cookiesA);
			try {
				authClientA.createGameLogin(BenchmarkUser.USER_A.getAddress(), BenchmarkUser.USER_A.getPassword());
			} catch (HttpClientException e) {
				if (e.getStatus().getStatusCode() != Status.CONFLICT.getStatusCode())
					throw new IllegalStateException(e);
				// Ignore the error if it's just that the user already exists.

				// Login, instead.
				authClientA.loginWithGameAccount(BenchmarkUser.USER_A.getAddress(), BenchmarkUser.USER_A.getPassword());
			}
			PlayersClient playersClientA = new PlayersClient(config, cookiesA);
			playersClientA.findOrCreatePlayer();

			// Ensure that the benchmark user and player B exists.
			CookieStore cookiesB = new CookieStore();
			GameAuthClient authClientB = new GameAuthClient(config, cookiesB);
			try {
				authClientB.createGameLogin(BenchmarkUser.USER_B.getAddress(), BenchmarkUser.USER_B.getPassword());
			} catch (HttpClientException e) {
				if (e.getStatus().getStatusCode() != Status.CONFLICT.getStatusCode())
					throw new IllegalStateException(e);
				// Ignore the error if it's just that the user already exists.

				// Login, instead.
				authClientB.loginWithGameAccount(BenchmarkUser.USER_B.getAddress(), BenchmarkUser.USER_B.getPassword());
			}
			PlayersClient playersClientB = new PlayersClient(config, cookiesB);
			playersClientB.findOrCreatePlayer();
		}

		/**
		 * Cleans up all of the games passed to
		 * {@link #addGameForCleanup(GameView)} over the course of the
		 * benchmarks.
		 */
		@TearDown
		public void tearDown() {
			ClientConfig config = new ClientConfig(serverState.getServerManager().getServiceUrl());
			CookieStore cookies = new CookieStore();
			GameAuthClient loginClient = new GameAuthClient(config, cookies);
			loginClient.loginWithGameAccount(serverState.getServerManager().getAdminAddress(),
					serverState.getServerManager().getAdminPassword());
			GameClient gameClient = new GameClient(config, cookies);

			while (!gameIds.isEmpty())
				gameClient.deleteGame(gameIds.poll());
		}
	}
}
