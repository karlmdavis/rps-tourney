package com.justdavis.karl.rpstourney.service.app;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.env.AbstractEnvironment;

import com.justdavis.karl.misc.jetty.EmbeddedServer;

/**
 * Launches a Jetty {@link EmbeddedServer} to host this project's service web
 * application, running with the {@link SpringProfile#INTEGRATION_TESTS}
 * configuration.
 */
public class ServiceAppJettyLauncher {
	/**
	 * Creates and starts a {@link EmbeddedServer}. Will run until forcefully
	 * stopped (e.g. <code>ctrl+c</code>).
	 * 
	 * @param args
	 *            (not used)
	 */
	public static void main(String[] args) {
		// Re-route Java's built-in logging (JUL) to SLF4J.
		SLF4JBridgeHandler.install();

		// Activate the Spring INTEGRATION_TESTS profile.
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,
				SpringProfile.INTEGRATION_TESTS);

		/*
		 * Start up an embedded Jetty instance, which will look for all of the
		 * annotated Servlets, etc. available on the classpath (e.g.
		 * GameWebApplicationInitializer), and run them.
		 */
		EmbeddedServer app = new EmbeddedServer(8088, false, null);
		app.startServer();
	}
}
