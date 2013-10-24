package com.justdavis.karl.rpstourney.app.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.api.GameRound;
import com.justdavis.karl.rpstourney.api.GameSession;
import com.justdavis.karl.rpstourney.api.PlayerRole;
import com.justdavis.karl.rpstourney.api.Throw;
import com.justdavis.karl.rpstourney.api.ai.IAiPlayer;

/**
 * Allows a "Rock-Paper-Scissors Tourney" game to be played via a text console
 * (or something else connected to an input & output stream).
 */
final class ConsoleGameDriver {
	/**
	 * Allows the user at the specified input/output streams to play the
	 * specified {@link GameSession}.
	 * 
	 * @param game
	 *            the {@link GameSession} to play through
	 * @param computerPlayer
	 *            the {@link IAiPlayer} to use as the computer opponent
	 * @param out
	 *            the {@link PrintStream} to display the game on
	 * @param in
	 *            the {@link InputStream} to read the player's input from
	 */
	void playGameSession(GameSession game, IAiPlayer computerPlayer,
			PrintStream out, InputStream in) {
		// Sanity check: null game.
		if (game == null)
			throw new IllegalArgumentException();
		// Sanity check: new game.
		if (game.getCurrentRoundIndex() != 0)
			throw new IllegalArgumentException();
		// Sanity check: null out stream.
		if (out == null)
			throw new IllegalArgumentException();
		// Sanity check: null in stream.
		if (in == null)
			throw new IllegalArgumentException();

		// Print out the intro text.
		out.println("Rock-Paper-Scissors Tourney");
		out.println("===========================");
		out.println(String.format(
				"\nYou are playing the computer. Best out of %d wins!",
				game.getMaxRounds()));

		// Play rounds until the game is won.
		Scanner scanner = new Scanner(in);
		while (game.checkForWinner() == null) {
			// Print out the round intro text.
			out.println(String.format("\nRound %d! %s",
					game.getCurrentRoundIndex() + 1,
					determineScoreText(game.getCompletedRounds())));

			// Wait for the human player to select a valid move.
			Throw humanMove = null;
			while (humanMove == null) {
				// Print out the instructions.
				out.print(String.format("Select your throw [%s,%s,%s]: ",
						ThrowToken.ROCK.getToken(),
						ThrowToken.PAPER.getToken(),
						ThrowToken.SCISSORS.getToken()));

				// Grab the next character entered by the player.
				String tokenEnteredByHuman = scanner.next();

				// Try to match that character against a throw.
				ThrowToken selectedThrowToken = ThrowToken
						.match(tokenEnteredByHuman);
				if (selectedThrowToken != null)
					humanMove = selectedThrowToken.getMove();
				else
					out.println("Invalid throw.");

				// Loop until a valid move is selected.
			}

			// Submit the human player's Throw.
			game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_1,
					humanMove);

			// Select a Throw for the computer opponent and submit that.
			Throw computerMove = computerPlayer.selectThrow(
					game.getMaxRounds(), game.getCompletedRounds());
			game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_2,
					computerMove);

			// Print out the round results.
			List<GameRound> completedRounds = game.getCompletedRounds();
			GameRound thisRound = completedRounds
					.get(completedRounds.size() - 1);
			PlayerRole roundWinner = thisRound.determineWinner();
			String winOrLossText = determineWinOrLossText(roundWinner);
			out.println(String.format("You threw %s, computer threw %s. %s",
					ThrowToken.match(thisRound.getThrowForPlayer1())
							.getFullDisplayText(),
					ThrowToken.match(thisRound.getThrowForPlayer2())
							.getFullDisplayText(), winOrLossText));
		}

		// Print out the game's winner.
		PlayerRole winner = game.checkForWinner();
		out.println();
		out.println(String.format("%s %s", determineWinOrLossText(winner),
				determineScoreText(game.getCompletedRounds())));
	}

	/**
	 * Determines the "win/loss" text to display, based on the fact that
	 * {@link PlayerRole#PLAYER_1} is always the human player and
	 * {@link PlayerRole#PLAYER_2} is always the computer player.
	 * 
	 * @param winningPlayerRole
	 *            the {@link PlayerRole} that won the round or game, or
	 *            <code>null</code> if no {@link PlayerRole} won (as in a tie)
	 * @return the text "<code>You won!</code>" if {@link PlayerRole#PLAYER_1}
	 *         won, or "<code>You lost.</code>" if {@link PlayerRole#PLAYER_2}
	 *         won, or "Tied." if no {@link PlayerRole} won
	 */
	private static String determineWinOrLossText(PlayerRole winningPlayerRole) {
		if (winningPlayerRole == PlayerRole.PLAYER_1)
			return "You won!";
		else if (winningPlayerRole == PlayerRole.PLAYER_2)
			return "You lost.";
		else if (winningPlayerRole == null)
			return "Tied.";
		else
			throw new BadCodeMonkeyException();
	}

	/**
	 * Determines the score text to display.
	 * 
	 * @param completedRounds
	 *            the {@link List} of completed {@link GameRound}s
	 * @return the score so far in the form of "
	 *         <code>You: X, Computer: Y, Ties: Z</code>", where
	 *         {@link PlayerRole#PLAYER_1} is the human player and
	 *         {@link PlayerRole#PLAYER_2} is the computer opponent
	 */
	private static String determineScoreText(List<GameRound> completedRounds) {
		if (completedRounds == null)
			throw new IllegalArgumentException();

		int player = 0, computer = 0, ties = 0;
		for (GameRound completedRound : completedRounds) {
			PlayerRole roundWinner = completedRound.determineWinner();
			if (roundWinner == PlayerRole.PLAYER_1)
				player++;
			else if (roundWinner == PlayerRole.PLAYER_2)
				computer++;
			else if (roundWinner == null)
				ties++;
			else
				throw new BadCodeMonkeyException();
		}

		return String.format("You: %d, Computer: %d, Ties: %d", player,
				computer, ties);
	}

	/**
	 * Associates each {@link Throw} with a token that can be used to represent
	 * it.
	 */
	private static enum ThrowToken {
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
