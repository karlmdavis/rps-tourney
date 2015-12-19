package com.justdavis.karl.rpstourney.service.app.config;

/**
 * <p>
 * Implementations of this interface are responsible for loading/providing the
 * application with the {@link ServiceConfig} data object to use.
 * </p>
 * <p>
 * Implementations of this interface must cache {@link ServiceConfig} data after
 * it has been loaded once. Accordingly, the only way for changes to the
 * application's configuration to be applied is to restart the application.
 * </p>
 */
public interface IConfigLoader {
	/**
	 * @return the just-loaded or previously-loaded-and-now-cached
	 *         {@link ServiceConfig} data object for the application
	 */
	ServiceConfig getConfig();
}
