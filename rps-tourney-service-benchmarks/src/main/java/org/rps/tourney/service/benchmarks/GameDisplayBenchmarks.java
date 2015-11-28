package org.rps.tourney.service.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.rps.tourney.service.benchmarks.state.ServerState;

import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;

/**
 * Contains {@link Benchmark}s for those web service methods related to display
 * of existing game instances.
 */
public class GameDisplayBenchmarks {
	/**
	 * A {@link Benchmark} for {@link GameClient#getGame(String)}, when the
	 * requesting user is unauthenticated.
	 * 
	 * @param gameInProgressState
	 *            a {@link GameInProgressState} instance that provides the game
	 *            to be requested from the web service
	 */
	@Benchmark
	public void retrieveGameAsUnauthenticatedUser(GameInProgressState gameInProgressState) {
		ClientConfig config = new ClientConfig(
				gameInProgressState.getServerState().getServer().getUrlWithPath("rps-tourney-service-app"));
		CookieStore cookies = new CookieStore();
		GameClient gameClient = new GameClient(config, cookies);

		gameClient.getGame(gameInProgressState.getGameId());
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
		Options benchmarkOptions = new OptionsBuilder().include(GameDisplayBenchmarks.class.getSimpleName())
				.warmupIterations(20).measurementIterations(10).forks(1).threads(10 ^ 2).verbosity(VerboseMode.EXTRA)
				.build();

		new Runner(benchmarkOptions).run();
	}

	/**
	 * Represents a {@link Game} that is already in-progress and can be
	 * retrieved from the web service.
	 */
	@State(Scope.Benchmark)
	public static class GameInProgressState {
		private ServerState serverState;
		private String gameId;

		/**
		 * @return the {@link ServerState} that the web service is running in
		 */
		public ServerState getServerState() {
			return serverState;
		}

		/**
		 * @return the {@link Game#getId()} for the {@link Game} represented by
		 *         this instance
		 */
		public String getGameId() {
			return gameId;
		}

		/**
		 * Initializes {@link GameInProgressState} instances.
		 * 
		 * @param serverState
		 *            the {@link ServerState} that the web service is running in
		 */
		@Setup
		public void setupGameInProgressState(ServerState serverState) {
			this.serverState = serverState;

			ClientConfig config = new ClientConfig(
					serverState.getServer().getUrlWithPath(ServerState.CONTEXT_ROOT_SERVICE));
			CookieStore cookies = new CookieStore();

			GuestAuthClient authClient = new GuestAuthClient(config, cookies);
			authClient.loginAsGuest();

			GameClient gameClient = new GameClient(config, cookies);
			GameView game = gameClient.createGame();
			this.gameId = game.getId();
		}

		/**
		 * Cleans up {@link GameInProgressState} instances.
		 */
		@TearDown
		public void tearDownGameInProgressState() {
			// TODO Will eventually need to implement this.
			// ClientConfig config = new ClientConfig(
			// serverState.getServer().getUrlWithPath(ServerState.CONTEXT_ROOT_SERVICE));
			// CookieStore cookies = new CookieStore();
			// GameClient gameClient = new GameClient(config, cookies);
			// gameClient.deleteGame(gameId);
		}
	}
}
