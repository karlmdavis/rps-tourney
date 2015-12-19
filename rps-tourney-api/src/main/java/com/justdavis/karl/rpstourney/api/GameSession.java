package com.justdavis.karl.rpstourney.api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * <p>
 * Models a given play session of the game: tracking the moves that have been
 * made, determining the winner (if any), etc.
 * </p>
 * <p>
 * Here are the rules:
 * </p>
 * <ul>
 * <li>At the start of the game, both players must agree on the number of rounds
 * to play. This number must be odd. If the players cannot agree on the number
 * of rounds, a default of 3 will be used.</li>
 * <li>Each round of the game, players select a {@link Throw}.</li>
 * <li>If the two throws are different, one of the players will have won the
 * round. If the two throws are the same, the players have tied. Tied rounds do
 * not count towards the agreed-upon total number of rounds.</li>
 * <li>{@link Throw#ROCK} beats {@link Throw#SCISSORS}, {@link Throw#SCISSORS}
 * beats {@link Throw#PAPER}, and {@link Throw#PAPER} beats {@link Throw#ROCK}.
 * (This logic is captured in {@link GameRound#determineWinner()}.)</li>
 * <li>The player that wins the most rounds wins the game. (The game stops at
 * any point it becomes impossible for one player to win.)</li>
 * </ul>
 * <p>
 * TODO This class is not at all thread-safe.
 * </p>
 */
public final class GameSession {
	private final int maxRounds;

	/**
	 * <p>
	 * Stores the {@link GameRound}s that have been completed. Gets updated by
	 * {@link #submitThrow(int, PlayerRole, Throw)} as play progresses.
	 * </p>
	 * <p>
	 * This field is mutable.
	 * </p>
	 */
	private final List<GameRound> completedRounds;

	/**
	 * <p>
	 * Used to temporarily store the {@link Throw} submitted by
	 * {@link PlayerRole#PLAYER_1} for the round that is currently in-progress.
	 * If there is no round in-progress, or if the player has not yet submitted
	 * a {@link Throw} via {@link #submitThrow(int, PlayerRole, Throw)}, this
	 * field will be <code>null</code>.
	 * </p>
	 * <p>
	 * This field is mutable.
	 * </p>
	 */
	private Throw currentThrowPlayer1;

	/**
	 * <p>
	 * Used to temporarily store the {@link Throw} submitted by
	 * {@link PlayerRole#PLAYER_2} for the round that is currently in-progress.
	 * If there is no round in-progress, or if the player has not yet submitted
	 * a {@link Throw} via {@link #submitThrow(int, PlayerRole, Throw)}, this
	 * field will be <code>null</code>.
	 * </p>
	 * <p>
	 * This field is mutable.
	 * </p>
	 */
	private Throw currentThrowPlayer2;

	/**
	 * <p>
	 * Constructs a new {@link GameSession} instance, and marks the start of
	 * play.
	 * </p>
	 * <p>
	 * Note: The order of the player parameters passed in here does not affect
	 * game play at all.
	 * </p>
	 * 
	 * @param maxRounds
	 *            the value to use for {@link #getMaxRounds()}
	 */
	public GameSession(int maxRounds) {
		// Sanity check: valid numbers of rounds.
		if (maxRounds < 1 || maxRounds % 2 == 0)
			throw new IllegalArgumentException();

		this.maxRounds = maxRounds;

		this.completedRounds = new LinkedList<GameRound>();
		this.currentThrowPlayer1 = null;
		this.currentThrowPlayer2 = null;
	}

	/**
	 * @return the maximum number of (non-tied) {@link GameRound}s that will be
	 *         played
	 */
	public int getMaxRounds() {
		return maxRounds;
	}

	/**
	 * @return an unmodifiable view of the {@link GameRound}s that have been
	 *         completed so far
	 */
	public List<GameRound> getCompletedRounds() {
		return Collections.unmodifiableList(completedRounds);
	}

	/**
	 * @return The (zero-based) index of the game round that's currently
	 *         in-progress. Once the game has completed, this will always return
	 *         the index of the last round that was played.
	 */
	public int getCurrentRoundIndex() {
		/*
		 * Use the getCompletedRounds() accessor here (rather than the field) to
		 * leverage any locking it performs.
		 */
		if (checkForWinner() == null)
			return getCompletedRounds().size();
		else
			return getCompletedRounds().size() - 1;
	}

	/**
	 * This method should be called once for each player each round, to register
	 * that player's selected {@link Throw} for the round.
	 * 
	 * @param roundIndex
	 *            the index of the round that a {@link Throw} is being submitted
	 *            for. This is included as a parameter to help ensure that the
	 *            client &amp; server are staying in sync. See
	 *            {@link #getCurrentRoundIndex()}.
	 * @param player
	 *            the {@link PlayerRole} (which must be either
	 *            {@link PlayerRole#PLAYER_1} or {@link PlayerRole#PLAYER_2})
	 *            that is submitting the {@link Throw}
	 * @param move
	 *            the {@link Throw} that the player has selected for the round
	 */
	public void submitThrow(int roundIndex, PlayerRole player, Throw move) {
		/*
		 * Use the getCompletedRounds() accessor here (rather than the field) to
		 * leverage any locking it performs.
		 */

		// Sanity check: game still in progress?
		if (checkForWinner() != null)
			throw new IllegalStateException();
		// Sanity check: correct round?
		if (roundIndex != getCurrentRoundIndex())
			throw new IllegalArgumentException();
		// Sanity check: legit throw?
		if (move == null)
			throw new IllegalArgumentException();

		// Which player is this?
		boolean isPlayer1 = (player == PlayerRole.PLAYER_1);
		boolean isPlayer2 = (player == PlayerRole.PLAYER_2);

		// Sanity check: legit player?
		if (!isPlayer1 && !isPlayer2)
			throw new IllegalArgumentException();
		// Sanity check: player 1 has already submitted throw?
		if (isPlayer1 && currentThrowPlayer1 != null)
			throw new IllegalStateException();
		// Sanity check: player 1 has already submitted throw?
		if (isPlayer2 && currentThrowPlayer2 != null)
			throw new IllegalStateException();

		// Store the throw.
		if (isPlayer1)
			currentThrowPlayer1 = move;
		else
			currentThrowPlayer2 = move;

		// Is the round now over?
		if (currentThrowPlayer1 != null && currentThrowPlayer2 != null) {
			// Record the completed round.
			GameRound completedRound = new GameRound(roundIndex, currentThrowPlayer1, currentThrowPlayer2);
			completedRounds.add(completedRound);

			// Clean things up for the next round.
			currentThrowPlayer1 = null;
			currentThrowPlayer2 = null;
		}
	}

	/**
	 * @return the {@link PlayerRole} that won this {@link GameSession}, or
	 *         <code>null</code> if the game is still in-progress
	 */
	public PlayerRole checkForWinner() {
		// Count the number of rounds won by each player.
		int player1Wins = 0;
		int player2Wins = 0;
		for (GameRound completedRound : getCompletedRounds()) {
			if (completedRound.determineWinner() == PlayerRole.PLAYER_1)
				player1Wins++;
			if (completedRound.determineWinner() == PlayerRole.PLAYER_2)
				player2Wins++;
		}

		// Is the game still in progress?
		int numWinsNeeded = (maxRounds / 2) + 1;
		if (player1Wins < numWinsNeeded && player2Wins < numWinsNeeded)
			return null;

		// Determine who won.
		if (player1Wins > player2Wins)
			return PlayerRole.PLAYER_1;
		else if (player2Wins > player1Wins)
			return PlayerRole.PLAYER_2;
		else
			throw new BadCodeMonkeyException();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameSession [maxRounds=");
		builder.append(maxRounds);
		builder.append(", completedRounds=");
		builder.append(completedRounds);
		builder.append(", currentThrowPlayer1=");
		builder.append(currentThrowPlayer1);
		builder.append(", currentThrowPlayer2=");
		builder.append(currentThrowPlayer2);
		builder.append("]");
		return builder.toString();
	}
}
