package com.justdavis.karl.rpstourney.webservice;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * <p>
 * Hosts the {@link GameApplication} JAX-RS app via an embedded Jetty instance.
 * Uses the configuration in <code>src/main/webapp/WEB-INF/web.xml</code> to
 * configure Jetty.
 * </p>
 * <p>
 * Not designed for production use; should only be used for development/testing.
 * </p>
 */
public final class EmbeddedServer {
	/**
	 * The default port that Jetty will run on.
	 */
	public static final int DEFAULT_PORT = 8087;

	private final int port;
	private Server server;

	/**
	 * Constructs a new {@link EmbeddedServer} instance. Does not start the
	 * server; see {@link #startServer()} for that.
	 * 
	 * @param port
	 *            the port to host Jetty on
	 */
	public EmbeddedServer(int port) {
		this.port = port;
	}

	/**
	 * Constructs a new {@link EmbeddedServer} instance. Does not start the
	 * server; see {@link #startServer()} for that.
	 */
	public EmbeddedServer() {
		this(DEFAULT_PORT);
	}

	/**
	 * Launches Jetty, configuring it to run the web application configured in
	 * <code>rps-tourney-webservice/src/main/webapp/WEB-INF/web.xml</code>.
	 */
	public synchronized void startServer() {
		if (this.server != null)
			throw new IllegalStateException();

		// Create the Jetty Server instance.
		this.server = new Server(this.port);
		this.server.setStopAtShutdown(true);

		// Use the web.xml to configure things.
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");
		this.server.setHandler(webapp);
		/*
		 * NOTE: the above is not robust enough to handle running things if this
		 * code is packaged in an unexploded WAR. It would probably need to try
		 * and load the web.xml as a classpath resource, or something like that.
		 */

		// Start up Jetty.
		try {
			this.server.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stops Jetty, if it has been started via {@link #startServer()}.
	 */
	public synchronized void stopServer() {
		if (this.server == null)
			return;

		try {
			this.server.stop();
			this.server.join();
		} catch (Exception e) {
			/*
			 * If this fails, we're screwed. If this code was going to be used
			 * in production, it'd probably best to log the error and call
			 * System.exit(...) here.
			 */
			throw new RuntimeException("Unable to stop Jetty", e);
		}
	}

	/**
	 * Creates and starts a {@link EmbeddedServer}. Will run until forcefully
	 * stopped (e.g. <code>ctrl+c</code>).
	 * 
	 * @param args
	 *            (not used)
	 */
	public static void main(String[] args) {
		EmbeddedServer app = new EmbeddedServer();
		app.startServer();
	}
}
