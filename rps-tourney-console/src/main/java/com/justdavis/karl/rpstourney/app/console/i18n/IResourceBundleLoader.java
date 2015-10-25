package com.justdavis.karl.rpstourney.app.console.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Implementations of this interface are responsible for retrieving the
 * application's {@link ResourceBundle}s. (This application uses a single,
 * combined bundle.)
 */
public interface IResourceBundleLoader {
	/**
	 * @return the application's {@link ResourceBundle} for the current
	 *         {@link Locale}
	 */
	ResourceBundle getBundle();
}
