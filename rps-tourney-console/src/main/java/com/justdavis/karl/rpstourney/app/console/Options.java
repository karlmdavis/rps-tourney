package com.justdavis.karl.rpstourney.app.console;

import java.net.URI;
import java.net.URISyntaxException;

import javax.mail.internet.InternetAddress;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

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

	static final String DEFAULT_SERVER = "https://rpstourney.com/api";

	@Option(name = "--online", aliases = { "-o" }, required = false, usage = "enables network "
			+ "play (and saving of history), defaults to false")
	private boolean online;

	@Option(name = "--server", aliases = { "-s" }, required = false, usage = "specifies the URL "
			+ "of the RPS Tourney service to play via (if network play is enabled), defaults to "
			+ DEFAULT_SERVER)
	private URI serverUri;

	@Option(name = "--user", aliases = { "-u" }, required = false, usage = "the email address "
			+ "of the user to login as (if network play is enabled), if not set, an anonymous "
			+ "account will be created and used")
	private InternetAddress emailAddress;

	@Option(name = "--password", aliases = { "-p" }, required = false, usage = "the password to "
			+ "authenticate with (if a user is specified)")
	private String password;

	@Option(name = "--game", aliases = { "-g" }, required = false, usage = "specifies the URL "
			+ "of the RPS Tourney game to join (instead of starting a new game)")
	private URI gameUri;

	@Option(name = "--ai", aliases = { "-a" }, required = false, handler = BuiltInAiOptionHandler.class, usage = "specify the AI "
			+ "to play against: 'Easy', 'Medium', or 'Hard', defaults to 'Easy'")
	private BuiltInAi aiOpponent;

	@Option(name = "--numRounds", aliases = { "-r" }, required = false, usage = "the maximum number "
			+ "of rounds to play (excluding ties), defaults to 3")
	private int numRounds;

	@Option(name = "--debug", aliases = { "-d" }, required = false, usage = "enables debug/logging "
			+ "output to STDERR")
	private boolean debugEnabled;

	@Option(name = "--help", aliases = { "-h" }, required = false, usage = "displays this help text")
	private boolean helpRequested;

	/**
	 * Default constructor.
	 */
	Options() {
		// Set default values.
		this.online = false;
		try {
			this.serverUri = new URI(DEFAULT_SERVER);
		} catch (URISyntaxException e) {
			throw new BadCodeMonkeyException(e);
		}
		this.emailAddress = null;
		this.password = null;
		this.gameUri = null;
		this.aiOpponent = BuiltInAi.THREE_SIDED_DIE_V1;
		this.numRounds = 3;
		this.debugEnabled = false;
		this.helpRequested = false;
	}

	/**
	 * @return <code>true</code> if network play (and saving history) are
	 *         disabled, <code>false</code> if they are not
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * @param online
	 *            the value for {@link #isOnline()}
	 */
	void setOnline(boolean local) {
		this.online = local;
	}

	/**
	 * @return the {@link URI} of the RPS Tourney service to play via
	 */
	public URI getServerUri() {
		return serverUri;
	}

	/**
	 * @param serverUri
	 *            the value for {@link #getServerUri()}
	 */
	void setServerUri(URI serverUri) {
		this.serverUri = serverUri;
	}

	/**
	 * @return the email address of the user to login as (if {@link #isOnline()}
	 *         is <code>false</code>), if not set, an anonymous account will be
	 *         created and used
	 */
	public InternetAddress getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the value for {@link #getEmailAddress()}
	 */
	void setEmailAddress(InternetAddress emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the password to authenticate with (if a user is specified)
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the value for {@link #getPassword()}
	 */
	void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the URL of the RPS Tourney game to join (instead of starting a
	 *         new game)
	 */
	public URI getGameUri() {
		return gameUri;
	}

	/**
	 * @param gameUri
	 *            the value for {@link #getGameUri()}
	 */
	void setGameUri(URI gameUri) {
		this.gameUri = gameUri;
	}

	/**
	 * @return the {@link BuiltInAi} to play against
	 */
	public BuiltInAi getAiOpponent() {
		return aiOpponent;
	}

	/**
	 * @param aiOpponent
	 *            the value for {@link #getAiOpponent()}
	 */
	void setAiOpponent(BuiltInAi aiOpponent) {
		this.aiOpponent = aiOpponent;
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
