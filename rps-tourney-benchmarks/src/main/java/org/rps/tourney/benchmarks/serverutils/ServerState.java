package org.rps.tourney.benchmarks.serverutils;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.tomcat.ITomcatServer;

/**
 * Stores the {@link ITomcatServer} instance representing the application server/container that is running the web
 * applications, along with related objects.
 */
@State(Scope.Benchmark)
public class ServerState {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerState.class);

	private IServerManager serverManager;

	/**
	 * @return the {@link IServerManager} for the application server being benchmarked
	 */
	public IServerManager getServerManager() {
		return serverManager;
	}

	/**
	 * Initializes this {@link ServerState} instance. Will be called by the benchmark framework after the instance has
	 * been constructed, but before it is used.
	 */
	@Setup
	public void setup() {
		if (ExistingServerManager.isConfigured()) {
			this.serverManager = new ExistingServerManager();
		} else {
			this.serverManager = new LocalServerManager();
		}
		LOGGER.info("Running benchmarks against server: {}", this.serverManager);
	}

	/**
	 * Cleans up {@link ServerState} instances.
	 */
	@TearDown
	public void tearDown() {
		if (this.serverManager != null)
			this.serverManager.tearDown();
	}
}
