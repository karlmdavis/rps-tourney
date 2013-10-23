package com.justdavis.karl.rpstourney.app.console;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * <p>
 * Models the command line options that the application can be launched with.
 * </p>
 * <p>
 * The fields here are all annotated to parsing via {@link CmdLineParser}.
 * </p>
 */
final class Options {
	/*
	 * In order to support parsing via CmdLineParser, the following is required:
	 * 1) a non-private default constructor must be present, and 2) none of the
	 * fields here may be final.
	 */

	@Option(name = "--numRounds", aliases = { "-r" }, required = false, usage = "the maximum number of rounds to play (excluding ties), defaults to 3")
	private int numRounds;

	@Option(name = "--debug", aliases = { "-d" }, required = false, usage = "enables debug/logging output to STDERR")
	private boolean debugEnabled;

	@Option(name = "--help", aliases = { "-h" }, required = false, usage = "displays this help text")
	private boolean helpRequested;

	/**
	 * Default constructor.
	 */
	Options() {
		// Set default values.
		this.numRounds = 3;
		this.debugEnabled = false;
		this.helpRequested = false;
	}

	/**
	 * @return the maximum number of rounds to play, excluding tied rounds
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * @param numRounds
	 *            the value for {@link #getNumRounds()}
	 */
	void setNumRounds(int numRounds) {
		if (numRounds < 1)
			throw new IllegalArgumentException();

		this.numRounds = numRounds;
	}

	/**
	 * @return <code>true</code> if the application should write debug/logging
	 *         output to <code>STDERR</code>, <code>false</code> if all such
	 *         output should be suppressed
	 */
	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * @param debugEnabled
	 *            the value for {@link #isDebugEnabled()}
	 */
	void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

	/**
	 * @return <code>true</code> if the application should write the command
	 *         line help text to <code>STDOUT</code> and then exit,
	 *         <code>false</code> if the application should attempt to just run
	 *         normally
	 */
	public boolean isHelpRequested() {
		return helpRequested;
	}

	/**
	 * @param helpRequested
	 *            the value for {@link #isDebugEnabled()}
	 */
	void setHelpRequested(boolean helpRequested) {
		this.helpRequested = helpRequested;
	}
}
