package com.justdavis.karl.rpstourney.webapp.config;

/**
 * <p>
 * Implementations of this interface are responsible for loading/providing the
 * application with the {@link AppConfig} data object to use.
 * </p>
 * <p>
 * Implementations of this interface must cache {@link AppConfig} data after it
 * has been loaded once. Accordingly, the only way for changes to the
 * application's configuration to be applied is to restart the application.
 * </p>
 */
public interface IConfigLoader {
	/**
	 * @return the just-loaded or previously-loaded-and-now-cached
	 *         {@link AppConfig} data object for the application
	 */
	AppConfig getConfig();
}
