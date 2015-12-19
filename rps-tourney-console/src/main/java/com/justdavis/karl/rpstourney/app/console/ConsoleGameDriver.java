package com.justdavis.karl.rpstourney.app.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.api.GameSession;
import com.justdavis.karl.rpstourney.app.console.i18n.IResourceBundleLoader;
import com.justdavis.karl.rpstourney.app.console.localservice.GameBundle;
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.State;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * Allows a "Rock-Paper-Scissors Tourney" game to be played via a text console
 * (or something else connected to an input & output stream).
 */
final class ConsoleGameDriver {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleGameDriver.class);

	private final IResourceBundleLoader resourceBundleLoader;

	/**
	 * Constructs a new {@link ConsoleGameDriver} instance.
	 * 
	 * @param resourceBundleLoader
	 *            the {@link IResourceBundleLoader} to use
	 */
	public ConsoleGameDriver(IResourceBundleLoader resourceBundleLoader) {
		this.resourceBundleLoader = resourceBundleLoader;
	}

	/**
	 * Allows the user at the specified input/output streams to play the
	 * specified {@link GameSession}.
	 * 
	 * @param gameBundle
	 *            the {@link GameBundle} containing the game and the services
	 *            needed to play through it
	 * @param out
	 *            the {@link PrintStream} to display the game on
	 * @param in
	 *            the {@link InputStream} to read the player's input from
	 */
	void playGameSession(GameBundle gameBundle, PrintStream out, InputStream in) {
		// Sanity check: null game.
		if (gameBundle == null)
			throw new IllegalArgumentException();
		// Sanity check: null out stream.
		if (out == null)
			throw new IllegalArgumentException();
		// Sanity check: null in stream.
		if (in == null)
			throw new IllegalArgumentException();

		GameView game = getGame(gameBundle);

		// Print out the intro text.
		out.println("Rock-Paper-Scissors Tourney");
		out.println("===========================");
		out.println(String.format("%nYou are playing against %s. Best out of %d wins!",
				computeOpponentName(resourceBundleLoader, gameBundle), game.getMaxRounds()));

		// Play rounds until the game is won.
		try (Scanner scanner = new Scanner(in);) {
			while ((game = getGame(gameBundle)).getWinner() == null) {
				playGameRound(gameBundle, out, scanner);
			}
		}

		// Print out the game's winner.
		out.println();
		out.println(String.format("Final Score: %s", determineScoreText(game)));
		out.println(determineWonOrLostText(game));
	}

	/**
	 * Plays a full, single round in the specified game.
	 * 
	 * @param gameBundle
	 *            the {@link GameBundle} containing the game and the services
	 *            needed to play through it
	 * @param out
	 *            the {@link PrintStream} to display the game on
	 * @param scanner
	 *            the {@link Scanner} to read the player's input from
	 */
	private static void playGameRound(GameBundle gameBundle, PrintStream out, Scanner scanner) {
		GameView game = getGame(gameBundle);

		int currentRoundIndex = game.getRounds().size() - 1;
		GameRound currentRound = game.getRounds().get(currentRoundIndex);

		// Print out the round intro text.
		out.println(String.format("%nRound %d!", currentRound.getAdjustedRoundIndex() + 1));
		out.println(String.format(" Score: %s", determineScoreText(game)));

		// Wait for the human player to select a valid move and submit it.
		Throw humanMove = requestHumanMove(out, scanner);
		gameBundle.getGameClient().submitThrow(gameBundle.getGameId(), currentRoundIndex, humanMove);

		// Wait for the opponent to make their Throw.
		waitForOpponentThrow(gameBundle, currentRoundIndex);

		// Print out the round results.
		game = getGame(gameBundle);
		currentRound = game.getRounds().get(currentRoundIndex);
		PlayerRole[] playerRoles = game.getPlayerRoles(game.getViewPlayer());
		Throw yourThrow, opponentThrow;
		if (playerRoles.length == 2) {
			yourThrow = currentRound.getThrowForPlayer1();
			opponentThrow = currentRound.getThrowForPlayer2();
		} else if (playerRoles.length == 1 && playerRoles[0].equals(PlayerRole.PLAYER_1)) {
			yourThrow = currentRound.getThrowForPlayer1();
			opponentThrow = currentRound.getThrowForPlayer2();
		} else if (playerRoles.length == 1 && playerRoles[0].equals(PlayerRole.PLAYER_2)) {
			yourThrow = currentRound.getThrowForPlayer2();
			opponentThrow = currentRound.getThrowForPlayer1();
		} else {
			throw new BadCodeMonkeyException();
		}
		out.println(String.format(" You threw %s, computer threw %s.", ThrowToken.match(yourThrow).getFullDisplayText(),
				ThrowToken.match(opponentThrow).getFullDisplayText()));
		String winOrLossText = determineWonOrLostText(game, currentRound);
		out.println(String.format(" %s", winOrLossText));
	}

	/**
	 * @param out
	 *            the {@link PrintStream} to display the game on
	 * @param scanner
	 *            the {@link Scanner} to read the player's input from
	 * @return the {@link Throw} selected by the human player, that they would
	 *         like to next make
	 */
	private static Throw requestHumanMove(PrintStream out, Scanner scanner) {
		Throw humanMove = null;
		while (humanMove == null) {
			// Print out the instructions.
			out.print(String.format(" Select your throw [%s,%s,%s]: ", ThrowToken.ROCK.getToken(),
					ThrowToken.PAPER.getToken(), ThrowToken.SCISSORS.getToken()));

			// Grab the next character entered by the player.
			String tokenEnteredByHuman = scanner.next();

			// Try to match that character against a throw.
			ThrowToken selectedThrowToken = ThrowToken.match(tokenEnteredByHuman);
			if (selectedThrowToken != null)
				humanMove = selectedThrowToken.getMove();
			else
				out.println(" Invalid throw.");

			// Loop until a valid move is selected.
		}
		return humanMove;
	}

	/**
	 * @param gameBundle
	 *            the {@link GameBundle} containing the game and the services
	 *            needed to play through it
	 * @param roundIndex
	 *            the {@link GameRound#getRoundIndex()} of the game round to
	 *            wait for the opponent to make a move in
	 * @return an updated {@link GameView} instance, wherein the opponent has
	 *         now made their move for the specified round
	 */
	private static void waitForOpponentThrow(GameBundle gameBundle, int roundIndex) {
		GameView game = getGame(gameBundle);
		GameRound round = game.getRounds().get(roundIndex);

		while (round.getResult() == null) {
			// Wait a bit, then refresh the game state to check again.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Nothing in this app uses interrupts; safe to log and ignore.
				LOGGER.warn("Unexpected interrupt.", e);
			}
			game = getGame(gameBundle);
			round = game.getRounds().get(roundIndex);
		}
	}

	/**
	 * @param gameBundle
	 *            the {@link GameBundle} for the active game
	 * @return an updated {@link GameView} for the game being played
	 */
	private static GameView getGame(GameBundle gameBundle) {
		return gameBundle.getGameClient().getGame(gameBundle.getGameId());
	}

	/**
	 * @param resourceBundleLoader
	 *            the {@link IResourceBundleLoader} to use
	 * @param gameBundle
	 *            the {@link GameBundle} for the active game
	 * @return the display name to use for the user's opponent in the active
	 *         game
	 */
	private static String computeOpponentName(IResourceBundleLoader resourceBundleLoader, GameBundle gameBundle) {
		GameView game = getGame(gameBundle);
		Player currentPlayer = game.getViewPlayer();

		Player opponentPlayer;
		if (game.getPlayer2() != null && game.getPlayer2().isHuman() && game.getPlayer2().equals(currentPlayer))
			opponentPlayer = game.getPlayer1();
		else
			opponentPlayer = game.getPlayer2();

		if (opponentPlayer == null) {
			return "(waiting for opponent)";
		} else if (opponentPlayer.getName() != null) {
			return opponentPlayer.getName();
		} else {
			String aiNameKey = "players.ai.name." + opponentPlayer.getBuiltInAi().getDisplayNameKey();
			return resourceBundleLoader.getBundle().getString(aiNameKey);
		}
	}

	/**
	 * Determines the "won/lost" text to display for a completed game.
	 * 
	 * @param game
	 *            the {@link GameView} to calculate the result for
	 * @return the text "<code>You won!</code>" or "<code>You lost.</code>", as
	 *         appropriate
	 */
	private static String determineWonOrLostText(GameView game) {
		if (game == null)
			throw new IllegalArgumentException();
		if (game.getState() != State.FINISHED)
			throw new IllegalStateException();

		int scoreForUser, scoreForOpponent;
		List<PlayerRole> userRoles = Arrays.asList(game.getPlayerRoles(game.getViewPlayer()));
		if (userRoles.contains(PlayerRole.PLAYER_1)) {
			scoreForUser = game.getScoreForPlayer1();
			scoreForOpponent = game.getScoreForPlayer2();
		} else {
			scoreForUser = game.getScoreForPlayer2();
			scoreForOpponent = game.getScoreForPlayer1();
		}

		if (scoreForUser > scoreForOpponent)
			return "You won!";
		else if (scoreForUser < scoreForOpponent)
			return "You lost.";
		else
			throw new BadCodeMonkeyException();
	}

	/**
	 * Determines the "won/lost" text to display for a completed game round.
	 * 
	 * @param game
	 *            the {@link GameView} to calculate the result for
	 * @param round
	 *            the {@link GameRound} to calculate the result for
	 * @return the text "<code>You won!</code>", or "<code>You lost.</code>
	 *         ", or "<code>Tied.</code>", as appropriate
	 */
	private static String determineWonOrLostText(GameView game, GameRound round) {
		if (game == null)
			throw new IllegalArgumentException();
		if (round == null)
			throw new IllegalArgumentException();

		Result roundResult = round.getResult();
		Collection<PlayerRole> userRoles = Arrays.asList(game.getPlayerRoles(game.getViewPlayer()));
		if (roundResult == null)
			throw new BadCodeMonkeyException();
		else if (roundResult == Result.TIED)
			return "Tied.";
		else if (userRoles.contains(roundResult.getWinningPlayerRole()))
			return "You won!";
		else
			return "You Lost";
	}

	/**
	 * Determines the score text to display.
	 * 
	 * @param game
	 *            the {@link GameView} to calculate the score from
	 * @return the score so far in the form of "<code>You: X, Opponent: Y</code>
	 *         "
	 */
	private static String determineScoreText(GameView game) {
		if (game == null)
			throw new IllegalArgumentException();

		int scoreForUser, scoreForOpponent;
		List<PlayerRole> userRoles = Arrays.asList(game.getPlayerRoles(game.getViewPlayer()));
		if (userRoles.contains(PlayerRole.PLAYER_1)) {
			scoreForUser = game.getScoreForPlayer1();
			scoreForOpponent = game.getScoreForPlayer2();
		} else {
			scoreForUser = game.getScoreForPlayer2();
			scoreForOpponent = game.getScoreForPlayer1();
		}

		return String.format("You: %d, Computer: %d", scoreForUser, scoreForOpponent);
	}

	/**
	 * Associates each {@link Throw} with a token that can be used to represent
	 * it.
	 */
	private enum ThrowToken {
		ROCK("R", "Rock", Throw.ROCK),

		PAPER("P", "Paper", Throw.PAPER),

		SCISSORS("S", "Scissors", Throw.SCISSORS);

		private final String token;
		private final String fullDisplayText;
		private final Throw move;

		/**
		 * Enum constant constructor.
		 * 
		 * @param token
		 *            the value to use for {@link #getToken()}
		 * @param fullDisplayText
		 *            the value to use for {@link #getFullDisplayText()}
		 * @param move
		 *            the value to use for {@link #getMove()}
		 */
		private ThrowToken(String token, String fullDisplayText, Throw move) {
			this.token = token;
			this.fullDisplayText = fullDisplayText;
			this.move = move;
		}

		/**
		 * @return the short {@link String} to use to represent the
		 *         {@link #getMove()} {@link Throw}
		 */
		public String getToken() {
			return token;
		}

		/**
		 * @return the full display {@link String} to use to represent the
		 *         {@link #getMove()} {@link Throw}
		 */
		public String getFullDisplayText() {
			return fullDisplayText;
		}

		/**
		 * @return the {@link Throw} represented by this {@link ThrowToken}
		 */
		public Throw getMove() {
			return move;
		}

		/**
		 * Checks all of the {@link ThrowToken}s to find the one that matches
		 * the specified token, if any.
		 * 
		 * @param token
		 *            the possible {@link #getToken()} value to match
		 * @return the matching {@link ThrowToken}, or <code>null</code> if no
		 *         match is found
		 */
		public static ThrowToken match(String token) {
			for (ThrowToken throwToken : ThrowToken.values())
				if (throwToken.getToken().equalsIgnoreCase(token))
					return throwToken;

			return null;
		}

		/**
		 * Checks all of the {@link ThrowToken}s to find the one that matches
		 * the specified {@link Throw}.
		 * 
		 * @param move
		 *            the {@link Throw} value to match
		 * @return the matching {@link ThrowToken}
		 */
		public static ThrowToken match(Throw move) {
			for (ThrowToken throwToken : ThrowToken.values())
				if (throwToken.getMove().equals(move))
					return throwToken;

			throw new BadCodeMonkeyException();
		}
	}
}
