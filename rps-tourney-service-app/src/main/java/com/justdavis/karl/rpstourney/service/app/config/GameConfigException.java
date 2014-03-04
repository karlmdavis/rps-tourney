package com.justdavis.karl.rpstourney.service.app.config;

/**
 * This unchecked {@link RuntimeException} indicates that the application's
 * configuration could not be found where expected, parsed, or otherwise loaded.
 */
public final class GameConfigException extends RuntimeException {
	private static final long serialVersionUID = 3353857916686157636L;

	/**
	 * Constructs a new {@link GameConfigException}.
	 * 
	 * @param message
	 *            the value to use for {@link #getMessage()}
	 * @param cause
	 *            the value to use for {@link #getCause()}
	 */
	public GameConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link GameConfigException}.
	 * 
	 * @param message
	 *            the value to use for {@link #getMessage()}
	 */
	public GameConfigException(String message) {
		super(message);
	}
}
