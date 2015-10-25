package com.justdavis.karl.rpstourney.app.console.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This {@link IResourceBundleLoader} implementation just loads from the
 * <code>rps-tourney-console/src/main/resources/i18n/messages.properties</code>
 * file, for the current system {@link Locale}.
 */
public final class DefaultResourceBundleLoader implements IResourceBundleLoader {
	private final Locale locale;

	/**
	 * Constructs a new {@link DefaultResourceBundleLoader}, which will use
	 * {@link Locale#getDefault()}.
	 */
	public DefaultResourceBundleLoader() {
		this(Locale.getDefault());
	}

	/**
	 * <p>
	 * Constructs a new {@link DefaultResourceBundleLoader}.
	 * </p>
	 * <p>
	 * <strong>Warning:</strong> Only intended for external use by tests.
	 * </p>
	 * 
	 * @param locale
	 *            the {@link Locale} to use
	 */
	public DefaultResourceBundleLoader(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.app.console.i18n.IResourceBundleLoader#getBundle()
	 */
	@Override
	public ResourceBundle getBundle() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(
				"i18n/messages", locale);
		return resourceBundle;
	}
}
