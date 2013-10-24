package com.justdavis.karl.rpstourney.app.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.justdavis.karl.rpstourney.api.GameSession;
import com.justdavis.karl.rpstourney.api.ai.RandomAiPlayer;

/**
 * <p>
 * The main entry point/driver for the text console version of the
 * "Rock-Paper-Scissors Tourney" game. When run, it will allow a human to play
 * the game using the text console (or whatever is connected to
 * {@link System#out} and {@link System#in}). The player's opponent will be a
 * computer/AI player.
 * </p>
 * <p>
 * This class is pretty much only responsible for application initialization. It
 * delegates the user interface and gameplay to {@link ConsoleGameDriver}.
 * </p>
 */
public final class ConsoleApp {
	/**
	 * By convention, applications return this value as an exit code to indicate
	 * that they ran successfully and are exiting normally.
	 */
	private static final int EXIT_CODE_OK = 0;

	/**
	 * This is the exit code that the application will return if the command
	 * line options cannot be parsed correctly.
	 */
	private static final int EXIT_CODE_BAD_ARGS = 1;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConsoleApp.class);

	private final OptionsParser optionsParser;

	/**
	 * The application entry point for {@link ConsoleApp}.
	 * 
	 * @param args
	 *            the command line arguments passed to the application when it
	 *            was launched. See
	 *            {@link OptionsParser#parseCommandLineOptions(String[])} for
	 *            details on how these are parsed.
	 */
	public static void main(String[] args) {
		// Create and run the app.
		ConsoleApp app = new ConsoleApp();
		int exitCode = app.runApp(args, System.out, System.in);

		/*
		 * This application doesn't have any expected error conditions, aside
		 * from unanticipated (and unhandled) runtime exceptions. Accordingly,
		 * we'll always return an "all clear" exit code if we make it this far.
		 * We'll let the JVM deal with any unhandled exceptions, presumably by
		 * dumping a stack trace and returning an error code.
		 */
		System.exit(exitCode);
	}

	/**
	 * Constructs a new {@link ConsoleApp} instance.
	 */
	public ConsoleApp() {
		this.optionsParser = new OptionsParser();
	}

	/**
	 * <p>
	 * Runs the game application.
	 * </p>
	 * <p>
	 * The method is only in the default/package scope to enable unit testing.
	 * </p>
	 * 
	 * @param args
	 *            the command line arguments passed to the application when it
	 *            was launched. See
	 *            {@link OptionsParser#parseCommandLineOptions(String[])} for
	 *            details on how these are parsed.
	 * @param out
	 *            the {@link PrintStream} to display the game on
	 * @param in
	 *            the {@link InputStream} to read the player's input from
	 * @return the exit code that the application should return, e.g.
	 *         {@link #EXIT_CODE_OK}
	 */
	int runApp(String[] args, PrintStream out, InputStream in) {
		// Parse the command line options.
		Options options = optionsParser.parseCommandLineOptions(args);
		if (options == null) {
			return EXIT_CODE_BAD_ARGS;
		}

		/*
		 * Enable debug logging, if requested. We must be careful not to
		 * accidentally log anything before now, as any such log events will
		 * never be written out.
		 */
		if (options.isDebugEnabled())
			enableDebugLogging();
		LOGGER.debug("Command line arguments: {}", Arrays.toString(args));

		/*
		 * Did the user request the command line help? If so, display it and
		 * exit early.
		 */
		if (options.isHelpRequested()) {
			optionsParser.printUsage(out);
			return EXIT_CODE_OK;
		}

		// Play the game.
		GameSession game = new GameSession(options.getNumRounds());
		ConsoleGameDriver gameDriver = new ConsoleGameDriver();
		RandomAiPlayer computerPlayer = new RandomAiPlayer();
		gameDriver.playGameSession(game, computerPlayer, out, in);

		return EXIT_CODE_OK;
	}

	/**
	 * Enables output of debug/logging events to <code>STDERR</code>.
	 */
	private static void enableDebugLogging() {
		// Grab the root Logback logger.
		LoggerContext loggerContext = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		ch.qos.logback.classic.Logger rootLogbackLogger = loggerContext
				.getLogger(Logger.ROOT_LOGGER_NAME);

		/*
		 * Enable all logging levels at the root logger, which will in turn
		 * enable them for all child loggers.
		 */
		rootLogbackLogger.setLevel(Level.ALL);
	}
}
