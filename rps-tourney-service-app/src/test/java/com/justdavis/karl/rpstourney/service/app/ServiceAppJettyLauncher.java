package com.justdavis.karl.rpstourney.service.app;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.env.AbstractEnvironment;

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.exceptions.UncheckedAddressException;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

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
		EmbeddedServer server = new EmbeddedServer(8088, false, null);
		server.startServer();

		createDevUsers(server);
	}

	/**
	 * Creates {@link Account}s, etc. for use in manual development testing.
	 * 
	 * @param server
	 *            the {@link EmbeddedServer} that the web service is running in
	 */
	private static void createDevUsers(EmbeddedServer server) {
		if (server == null)
			throw new IllegalArgumentException();

		// Create the web service to be used.
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		CookieStore cookieStore = new CookieStore();
		GameAuthClient gameAuthClient = new GameAuthClient(clientConfig,
				cookieStore);

		// Use the client to create a login.
		try {
			gameAuthClient.createGameLogin(new InternetAddress(
					"dev@rpstourney.com"), "secret");
		} catch (AddressException e) {
			throw new UncheckedAddressException(e);
		}
	}
}
