package com.justdavis.karl.rpstourney.app.console;

/**
 * This checked exception is used to signal that an error condition has occurred that should cause the game application
 * to exit with the code specified in {@link #getExitCode()}.
 */
public final class ConsoleGameExitException extends Exception {
	private static final long serialVersionUID = 7946240691200608017L;

	private final int exitCode;

	/**
	 * Constructs a new {@link ConsoleGameExitException} instance.
	 *
	 * @param exitCode
	 */
	public ConsoleGameExitException(int exitCode) {
		this.exitCode = exitCode;
	}

	/**
	 * @return the {@link System#exit(int)} code that the game application should exit with
	 */
	public int getExitCode() {
		return exitCode;
	}
}
