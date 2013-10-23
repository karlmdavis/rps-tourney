package com.justdavis.karl.rpstourney.app.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Properties;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.misc.resources.ResourcePath;
import com.justdavis.karl.rpstourney.api.GameSession;

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

	/**
	 * The application entry point for {@link ConsoleApp}.
	 * 
	 * @param args
	 *            the command line arguments passed to the application when it
	 *            was launched. See {@link #parseCommandLineOptions(String[])}
	 *            for details on how these are parsed.
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
	}

	/**
	 * Runs the game application.
	 * 
	 * @param args
	 *            the command line arguments passed to the application when it
	 *            was launched. See {@link #parseCommandLineOptions(String[])}
	 *            for details on how these are parsed.
	 * @param out
	 *            the {@link PrintStream} to display the game on
	 * @param in
	 *            the {@link InputStream} to read the player's input from
	 * @return the exit code that the application should return, e.g.
	 *         {@link #EXIT_CODE_OK}
	 */
	int runApp(String[] args, PrintStream out, InputStream in) {
		// Parse the command line options.
		Options options = parseCommandLineOptions(args);
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
			printUsage(out);
			return EXIT_CODE_OK;
		}

		// Play the game.
		GameSession game = new GameSession(options.getNumRounds());
		ConsoleGameDriver gameDriver = new ConsoleGameDriver();
		gameDriver.playGameSession(game, out, in);

		return EXIT_CODE_OK;
	}

	/**
	 * <p>
	 * Parses the specified {@link String} array of command line options and
	 * returns an {@link Options} instance representing them, or
	 * <code>null</code> if they could not be parsed.
	 * </p>
	 * <p>
	 * <strong>Side Effects:</strong> If the command line arguments could not be
	 * parsed successfully, this miethod will also print out a (hopefully)
	 * helpful error message and the application usage text, all to
	 * {@link System#err}.
	 * </p>
	 * 
	 * @param args
	 *            the command line arguments passed to the application when it
	 *            was launched
	 * @return an {@link Options} instance representing the command line
	 *         arguments passed to the application when it was launched, or
	 *         <code>null</code> if they could not be parsed
	 */
	private static Options parseCommandLineOptions(String[] args) {
		Options options = new Options();
		CmdLineParser optionsParser = new CmdLineParser(options);
		try {
			optionsParser.parseArgument(args);
		} catch (CmdLineException e) {
			/*
			 * This exception will be thrown if the command line options cannot
			 * be parsed correctly.
			 */

			// Print the error and usage.
			System.err.println(e.getMessage());
			printUsage(System.err);
		}

		return options;
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

	/**
	 * Prints out help text regarding the application's command line options.
	 * 
	 * @param outputStream
	 *            the {@link PrintStream} to write to, typically
	 *            {@link System#out} or {@link System#err}
	 */
	private static void printUsage(PrintStream outputStream) {
		// Create a throw-away parser to use here.
		CmdLineParser optionsParser = new CmdLineParser(new Options());

		// Print out the usage.
		outputStream.print("Usage: java -jar ");
		outputStream.print(getExpectedOutputJarName());
		outputStream.print(" [OPTION]...\n\n");
		outputStream.println("Options:");
		optionsParser.printUsage(outputStream);
		outputStream.flush();
	}

	/**
	 * @return the name of the JAR that this project is expected to produce
	 */
	private static String getExpectedOutputJarName() {
		/*
		 * To pull this off, I've created a .properties resource file that Maven
		 * should filter, dropping the output JAR's name into it. This method
		 * just loads that properties file and pulls the filtered value out from
		 * it.
		 */

		// Get the path to the resource.
		ResourcePath jarDetailsPath = new ResourcePath(ConsoleApp.class,
				"jar-details.properties");

		// Load & parse the resource.
		Properties jarDetailsProps = new Properties();
		try {
			jarDetailsProps.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(jarDetailsPath.getPath()));
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}

		return jarDetailsProps.getProperty("project.build.finalName");
	}
}
