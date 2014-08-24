package com.justdavis.karl.rpstourney.webapp;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.env.AbstractEnvironment;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedUriSyntaxException;
import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.webapp.config.XmlConfigLoader;

/**
 * Launches a Jetty {@link EmbeddedServer} to host this project's web
 * application, running with the {@link SpringProfile#INTEGRATION_TESTS}
 * configuration.
 */
public class WebAppJettyLauncher {
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

		// Activate the Spring DEVELOPMENT profile.
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,
				SpringProfile.DEVELOPMENT);

		// Specify the config file location.
		URL configUrl = Thread.currentThread().getContextClassLoader()
				.getResource("rps-webapp-config-dev.xml");
		try {
			System.setProperty(XmlConfigLoader.CONFIG_PROP,
					new File(configUrl.toURI()).getAbsolutePath());
		} catch (URISyntaxException e) {
			throw new UncheckedUriSyntaxException(e);
		}

		/*
		 * Start up an embedded Jetty instance, which will look for all of the
		 * annotated Servlets, etc. available on the classpath (e.g.
		 * GameWebApplicationInitializer), and run them.
		 */
		EmbeddedServer app = new EmbeddedServer(8089, false, null);
		app.startServer();
	}
}
