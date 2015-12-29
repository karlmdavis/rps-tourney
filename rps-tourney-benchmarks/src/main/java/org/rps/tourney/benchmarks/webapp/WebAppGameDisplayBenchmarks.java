package org.rps.tourney.benchmarks.webapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.misc.exceptions.unchecked.UncheckedMalformedUrlException;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;

/**
 * Contains {@link Benchmark}s for those web app resources related to display of
 * existing game instances.
 */
public class WebAppGameDisplayBenchmarks {
	/**
	 * A {@link Benchmark} for server side rendering of the game page and its
	 * associated resources, with an unauthenticated user.
	 * 
	 * @param gameInProgressState
	 *            a {@link GameInProgressState} instance that provides the game
	 *            to be requested from the web service
	 */
	@Benchmark
	public void viewGameAsUnauthenticatedUser(GameInProgressState gameInProgressState) {
		// Build the URL to the game in the webapp.
		URL webAppUrl = gameInProgressState.getServerState().getServerManager().getWebAppUrl();
		URL gameUrl = resolve(webAppUrl, "/game/" + gameInProgressState.getGameId());

		/*
		 * Specify the resources to be loaded, in groups. Each top-level element
		 * here is a group of resources that will be loaded concurrently.
		 */
		URL[] resources1 = new URL[] { gameUrl };
		URL[] resources2 = new URL[] { resolve(webAppUrl, "/js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"),
				resolve(webAppUrl, "/css/rps.css"), resolve(webAppUrl, "/js/rps.js"),
				resolve(webAppUrl, "/js/game.min.js") };
		URL[] resources3 = new URL[] { resolve(webAppUrl, "/i18n/messages.properties"), resolve(gameUrl, "/data") };
		URL[][] resourcesToLoad = new URL[][] { resources1, resources2, resources3 };

		ExecutorService executorService = Executors.newFixedThreadPool(8);
		List<ResourceResult> failures = new LinkedList<>();
		for (URL[] resourceGroup : resourcesToLoad) {
			// Submit tasks to load each of the resources.
			Set<Future<ResourceResult>> resourceFutures = new HashSet<>(8);
			for (URL resource : resourceGroup) {
				ResourceLoader loader = new ResourceLoader(resource);
				Future<ResourceResult> resourceFuture = executorService.submit(loader);
				resourceFutures.add(resourceFuture);
			}

			// Block until the entire group is finished.
			for (Future<ResourceResult> resourceFuture : resourceFutures) {
				ResourceResult resourceResult;
				try {
					resourceResult = resourceFuture.get();
				} catch (InterruptedException | ExecutionException e) {
					throw new IllegalStateException(e);
				}

				if (resourceResult.getResponseCode() != 200)
					failures.add(resourceResult);
			}
		}

		// Must cleanup the executor's threads, else JMH gets unhappy.
		executorService.shutdown();

		if (!failures.isEmpty())
			throw new IllegalStateException("Not all resources loaded correctly: " + failures);
	}

	/**
	 * A {@link Benchmark} for server side rendering of the game
	 * update/refresh/status JSON, as an unauthenticated user.
	 * 
	 * @param gameInProgressState
	 *            a {@link GameInProgressState} instance that provides the game
	 *            to be requested from the web service
	 */
	@Benchmark
	public void refreshGameAsUnauthenticatedUser(GameInProgressState gameInProgressState) {
		// Build the URL to the game in the webapp.
		URL webAppUrl = gameInProgressState.getServerState().getServerManager().getWebAppUrl();
		URL gameUrl = resolve(webAppUrl, "/game/" + gameInProgressState.getGameId());
		URL gameJsonUrl = resolve(gameUrl, "/data");

		try {
			HttpURLConnection jsonConnection = (HttpURLConnection) gameJsonUrl.openConnection();
			jsonConnection.disconnect();

			if (jsonConnection.getResponseCode() != 200)
				throw new IllegalStateException("JSON didn't load correctly: " + jsonConnection.getResponseCode());
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}

	/**
	 * @param baseUrl
	 *            the base URL to be added to
	 * @param additionalUrl
	 *            the path to add to the end of the base {@link URL}
	 * @return a combined {@link URL}
	 */
	private static final URL resolve(URL baseUrl, String additionalUrl) {
		try {
			return new URL(baseUrl.getProtocol(), baseUrl.getHost(), baseUrl.getPort(),
					baseUrl.getFile() + additionalUrl);
		} catch (MalformedURLException e) {
			throw new UncheckedMalformedUrlException(e);
		}
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
		ChainedOptionsBuilder benchmarkOptions = new OptionsBuilder()
				.include(WebAppGameDisplayBenchmarks.class.getSimpleName()).warmupIterations(20)
				.measurementIterations(10).forks(1).threads(10 ^ 2).verbosity(VerboseMode.EXTRA);
		// benchmarkOptions.addProfiler(StackProfiler.class);
		// benchmarkOptions.jvmArgsAppend(ExistingServerManager.jvmArgsForTomcatWtp());

		new Runner(benchmarkOptions.build()).run();
	}

	/**
	 * A {@link Callable} that can be used in
	 * {@link WebAppGameDisplayBenchmarks#retrieveGameHtmlOnlyAsUnauthenticatedUser(GameInProgressState)}
	 * .
	 */
	private static final class ResourceLoader implements Callable<ResourceResult> {
		private final URL resource;

		/**
		 * Constructs a new {@link ResourceLoader}.
		 * 
		 * @param resource
		 *            the {@link URL} of the resource to be loaded
		 */
		public ResourceLoader(URL resource) {
			this.resource = resource;
		}

		/**
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public ResourceResult call() throws Exception {
			HttpURLConnection resourceConnection = (HttpURLConnection) resource.openConnection();
			resourceConnection.disconnect();
			return new ResourceResult(resource, resourceConnection.getResponseCode());
		}
	}

	/**
	 * Represents the results of {@link ResourceLoader} operations.
	 */
	private static final class ResourceResult {
		private final URL resource;
		private final int responseCode;

		/**
		 * Constructs a new {@link ResourceResult}.
		 * 
		 * @param resource
		 *            the value to use for {@link #getResource()}
		 * @param responseCode
		 *            the value to use for {@link #getResponseCode()}
		 */
		public ResourceResult(URL resource, int responseCode) {
			this.resource = resource;
			this.responseCode = responseCode;
		}

		/**
		 * @return the resource that was loaded
		 */
		public URL getResource() {
			return resource;
		}

		/**
		 * @return the HTTP response/status code that was encountered while
		 *         loading the resource
		 */
		public int getResponseCode() {
			return responseCode;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ResourceResult [getResource()=");
			builder.append(getResource());
			builder.append(", getResponseCode()=");
			builder.append(getResponseCode());
			builder.append("]");
			return builder.toString();
		}
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

			ClientConfig config = new ClientConfig(serverState.getServerManager().getServiceUrl());
			CookieStore cookies = new CookieStore();

			GameAuthClient authClient = new GameAuthClient(config, cookies);
			authClient.createGameLogin(BenchmarkUser.USER_A.getAddress(), BenchmarkUser.USER_A.getPassword());

			GameClient gameClient = new GameClient(config, cookies);
			GameView game = gameClient.createGame();
			this.gameId = game.getId();
		}

		/**
		 * Cleans up {@link GameInProgressState} instances.
		 */
		@TearDown
		public void tearDownGameInProgressState() {
			ClientConfig config = new ClientConfig(serverState.getServerManager().getServiceUrl());
			CookieStore cookies = new CookieStore();
			GameAuthClient loginClient = new GameAuthClient(config, cookies);
			loginClient.loginWithGameAccount(serverState.getServerManager().getAdminAddress(),
					serverState.getServerManager().getAdminPassword());
			GameClient gameClient = new GameClient(config, cookies);
			gameClient.deleteGame(gameId);
		}
	}
}
