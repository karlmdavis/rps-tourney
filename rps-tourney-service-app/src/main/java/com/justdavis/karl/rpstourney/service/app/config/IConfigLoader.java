package com.justdavis.karl.rpstourney.service.app.config;

/**
 * <p>
 * Implementations of this interface are responsible for loading/providing the
 * application with the {@link GameConfig} data object to use.
 * </p>
 * <p>
 * Implementations of this interface must cache {@link GameConfig} data after it
 * has been loaded once. Accordingly, the only way for changes to the
 * application's configuration to be applied is to restart the application.
 * </p>
 */
public interface IConfigLoader {
	/**
	 * @return the just-loaded or previously-loaded-and-now-cached
	 *         {@link GameConfig} data object for the application
	 */
	GameConfig getConfig();
}
