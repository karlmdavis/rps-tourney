package com.justdavis.karl.rpstourney.app.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.justdavis.karl.rpstourney.app.console.i18n.DefaultResourceBundleLoader;
import com.justdavis.karl.rpstourney.app.console.localservice.GameBundle;
import com.justdavis.karl.rpstourney.app.console.localservice.LocalGameClient;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.IPlayersResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;
import com.justdavis.karl.rpstourney.service.client.game.PlayersClient;

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

	/**
	 * The {@link Pattern} that will be used to extract {@link Game#getId()}
	 * values from {@link Options#getGameUri()}. The group at index
	 * <code>1</code> will contain the game ID.
	 */
	private static final Pattern GAME_URI_PATTERN = Pattern
			.compile("^https?://.+/(" + Game.ID_PATTERN.pattern() + ")$");

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

		// Create/join the game, per the options.
		GameBundle gameBundle;
		if (options.isOnline()) {
			try {
				gameBundle = createOnlineGame(options);
			} catch (ConsoleGameExitException e) {
				return e.getExitCode();
			}
		} else {
			gameBundle = createLocalGame(options);
		}

		// Play the game.
		ConsoleGameDriver gameDriver = new ConsoleGameDriver(
				new DefaultResourceBundleLoader());
		gameDriver.playGameSession(gameBundle, out, in);

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

	/**
	 * @param options
	 *            the {@link Options} specified for the new game
	 * @return a {@link GameBundle} instance for playing a new local game
	 */
	private static GameBundle createLocalGame(Options options) {
		Game game = new Game(new Player(new Account()));
		game.setMaxRounds(options.getNumRounds());
		game.setPlayer2(new Player(options.getAiOpponent()));
		LocalGameClient gameClient = new LocalGameClient(game,
				game.getPlayer1());

		return new GameBundle(gameClient, game.getId());
	}

	/**
	 * @param options
	 *            the {@link Options} specified for the game
	 * @return a {@link GameBundle} for playing an online game using the web
	 *         service
	 * @throws ConsoleGameExitException
	 *             A {@link GameConflictException} may be thrown if a problem is
	 *             found with the provided {@link Options}.
	 */
	private static GameBundle createOnlineGame(Options options)
			throws ConsoleGameExitException {
		ClientConfig config = new ClientConfig(options.getServerUri());
		CookieStore cookieStore = new CookieStore();

		IGameResource gameClient = new GameClient(config, cookieStore);
		authenticateToWebService(options, config, cookieStore);

		if (options.getGameUri() == null) {
			GameView game = gameClient.createGame();
			gameClient.setMaxRounds(game.getId(), game.getMaxRounds(),
					options.getNumRounds());
			IPlayersResource playersClient = new PlayersClient(config,
					cookieStore);
			Set<Player> aiPlayers = playersClient
					.getPlayersForBuiltInAis(Arrays.asList(options
							.getAiOpponent()));
			if (aiPlayers.size() != 1)
				throw new IllegalStateException("Unexpected AI lookup result: "
						+ aiPlayers);
			Player aiPlayer = aiPlayers.iterator().next();
			gameClient.inviteOpponent(game.getId(), aiPlayer.getId());

			return new GameBundle(gameClient, game.getId());
		} else {
			Matcher gameUriMatcher = GAME_URI_PATTERN.matcher(options
					.getGameUri().toString());
			if (!gameUriMatcher.matches()) {
				LOGGER.error("Unable to parse game URL: "
						+ options.getGameUri());
				throw new ConsoleGameExitException(EXIT_CODE_BAD_ARGS);
			}
			String gameId = gameUriMatcher.group(1);

			return new GameBundle(gameClient, gameId);
		}
	}

	/**
	 * @param options
	 *            the {@link Options} specified for the game
	 * @param config
	 *            the web service {@link ClientConfig} to use
	 * @param cookieStore
	 *            the web service client {@link CookieStore} to use
	 */
	private static void authenticateToWebService(Options options,
			ClientConfig config, CookieStore cookieStore) {
		if (options.getEmailAddress() != null) {
			IGameAuthResource gameAuthClient = new GameAuthClient(config,
					cookieStore);
			gameAuthClient.loginWithGameAccount(options.getEmailAddress(),
					options.getPassword());
		} else {
			IGuestAuthResource guestAuthClient = new GuestAuthClient(config,
					cookieStore);
			guestAuthClient.loginAsGuest();
		}
	}
}