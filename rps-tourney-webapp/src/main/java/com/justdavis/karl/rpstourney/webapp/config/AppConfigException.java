package com.justdavis.karl.rpstourney.webapp.config;

/**
 * This unchecked {@link RuntimeException} indicates that the application's configuration could not be found where
 * expected, parsed, or otherwise loaded.
 */
public final class AppConfigException extends RuntimeException {
	private static final long serialVersionUID = 5868164294827440254L;

	/**
	 * Constructs a new {@link AppConfigException}.
	 *
	 * @param message
	 *            the value to use for {@link #getMessage()}
	 * @param cause
	 *            the value to use for {@link #getCause()}
	 */
	public AppConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link AppConfigException}.
	 *
	 * @param message
	 *            the value to use for {@link #getMessage()}
	 */
	public AppConfigException(String message) {
		super(message);
	}
}
