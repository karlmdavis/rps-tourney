package com.justdavis.karl.rpstourney.webapp;

import javax.ws.rs.core.UriBuilder;

import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * <p>
 * Contains constants and methods for use by the project's integration tests.
 * </p>
 * <p>
 * The code here relies on and interacts with the embedded Jetty instance that
 * is used for this project's integration tests. See
 * <code>cargo-maven2-plugin</code> in the project's <code>pom.xml</code> for
 * details.
 * </p>
 */
public final class ITUtils {
	/**
	 * The port that Jetty will run on, for the embedded Jetty instance used in
	 * the project's integration tests.
	 */
	private static final int JETTY_PORT = 9093;

	/**
	 * The context path that this project's WAR will be hosted at, for the
	 * embedded Jetty instance used in this project's integration tests.
	 */
	private static final String CONTEXT_WEBAPP = "/rps-tourney-webapp";

	/**
	 * The context path that the <code>rps-tourney-service-app</code> project's
	 * WAR will be hosted at, for the embedded Jetty instance used in this
	 * project's integration tests.
	 */
	private static final String CONTEXT_WEB_SERVICE = "/rps-tourney-service-app";

	/**
	 * @return a {@link ClientConfig} instance that can be used to access the
	 *         <code>rps-tourney-service-app</code> WAR's web services, for the
	 *         embedded Jetty instance used in this project's integration tests
	 */
	public static final ClientConfig createClientConfig() {
		UriBuilder serviceRootUri = UriBuilder.fromPath(String.format(
				"http://localhost:%d/%s", JETTY_PORT, CONTEXT_WEB_SERVICE));
		ClientConfig clientConfig = new ClientConfig(serviceRootUri.build());
		return clientConfig;
	}
}
