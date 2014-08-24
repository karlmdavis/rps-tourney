package com.justdavis.karl.rpstourney.webapp;

import org.springframework.context.annotation.Profile;

/**
 * Enumerates the various Spring {@link Profile}s supported in the application's
 * Spring configuration. (Not an actual enum, as the values need to be
 * referenced within annotations.)
 */
public final class SpringProfile {
	/**
	 * The default {@link Profile}. Contains the configuration for use when the
	 * application is deployed in production.
	 * 
	 * @see GameWebApplicationInitializer#onStartup(javax.servlet.ServletContext)
	 */
	public static final String PRODUCTION = "production";

	/**
	 * The default {@link Profile}. Contains the configuration for use when the
	 * application is running inside Eclipse (generally via
	 * <code>WebAppJettyLauncher</code>).
	 */
	public static final String DEVELOPMENT = "development";

	/**
	 * Contains the configuration for use when the application is running in
	 * integration tests.
	 */
	public static final String INTEGRATION_TESTS = "integration-tests";

	/**
	 * Private constructor; class not intended to be instantiated.
	 */
	private SpringProfile() {
	}
}
