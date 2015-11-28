package org.rps.tourney.service.benchmarks.state;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.tomcat.ITomcatServer;
import com.justdavis.karl.tomcat.TomcatServerHelper;

/**
 * Stores the {@link ITomcatServer} instance representing the application
 * server/container that is running the web service, along with related objects.
 */
@State(Scope.Benchmark)
public class ServerState {
	public static final String CONTEXT_ROOT_SERVICE = "rps-tourney-service-app";

	private ITomcatServer server;

	/**
	 * @return the {@link ITomcatServer} instance that the web service is
	 *         running in
	 */
	public ITomcatServer getServer() {
		return server;
	}

	/**
	 * Initializes {@link ServerState} instances.
	 */
	@Setup
	public void setupServerState() {
		// Get the path to the config file for the web service to use.
		InputStream configUrl = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("rps-service-config-benchmarks.xml");
		Path configPath;
		try {
			configPath = Files.createTempFile("rps-service-config-benchmarks", ".xml");
			configPath.toFile().deleteOnExit();
			Files.copy(configUrl, configPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}

		// Create, configure, and start Tomcat to run the web service.
		TomcatServerHelper benchmarksServerHelper = new TomcatServerHelper();
		Path tomcatDir = Paths.get(".", "target", "tomcat").toAbsolutePath();
		this.server = benchmarksServerHelper.createLocallyInstalledServer(tomcatDir)
				.addWar(CONTEXT_ROOT_SERVICE, Paths.get(".."),
						FileSystems.getDefault()
								.getPathMatcher("glob:../rps-tourney-service-app/target/rps-tourney-service-app-*.war"))
				.setJavaSystemProperty("rps.service.config.path", configPath.toString())
				.setJavaSystemProperty("rps.service.logs.path", tomcatDir.toString()).start();
	}

	/**
	 * Cleans up {@link ServerState} instances.
	 */
	@TearDown
	public void tearDownServerState() {
		if (this.server != null)
			this.server.release();
	}
}
