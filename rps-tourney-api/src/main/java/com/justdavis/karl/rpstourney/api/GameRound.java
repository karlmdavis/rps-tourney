package com.justdavis.karl.rpstourney.api;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * <p>
 * Represents a completed round of a {@link GameSession}, tracking the moves
 * made by the players, and the outcome.
 * </p>
 * <p>
 * Instances of this class are immutable and thread-safe.
 * </p>
 */
public final class GameRound {
	private final int roundIndex;
	private final Throw throwForPlayer1;
	private final Throw throwForPlayer2;

	/**
	 * Constructs a new {@link GameRound} instance.
	 * 
	 * @param roundIndex
	 *            the value to use for {@link #getRoundIndex()}
	 * @param currentThrowPlayer1
	 *            the value to use for {@link #getThrowForPlayer1()}
	 * @param currentThrowPlayer2
	 *            the value to use for {@link #getThrowForPlayer2()}
	 */
	public GameRound(int roundIndex, Throw currentThrowPlayer1,
			Throw currentThrowPlayer2) {
		this.roundIndex = roundIndex;
		this.throwForPlayer1 = currentThrowPlayer1;
		this.throwForPlayer2 = currentThrowPlayer2;
	}

	/**
	 * @return the index of this {@link GameRound} in the {@link GameSession} 
	 *         it's part of
	 */
	public int getRoundIndex() {
		return roundIndex;
	}

	/**
	 * @return the {@link Throw} that was selected by
	 *         {@link PlayerRole#PLAYER_1} in this {@link GameRound}
	 */
	public Throw getThrowForPlayer1() {
		return throwForPlayer1;
	}

	/**
	 * @return the {@link Throw} that was selected by
	 *         {@link PlayerRole#PLAYER_2} in this {@link GameRound}
	 */
	public Throw getThrowForPlayer2() {
		return throwForPlayer2;
	}

	/**
	 * @return the {@link PlayerRole} for the {@link IPlayer} that won this
	 *         {@link GameRound}, or <code>null</code> if there was a tie
	 */
	public PlayerRole determineWinner() {
		/*
		 * I'm sure that there's a way to simplify this logic, but for now this
		 * is good enough.
		 */

		// Check to see if Player 1 won.
		if (throwForPlayer1 == Throw.ROCK && throwForPlayer2 == Throw.SCISSORS)
			return PlayerRole.PLAYER_1;
		if (throwForPlayer1 == Throw.PAPER && throwForPlayer2 == Throw.ROCK)
			return PlayerRole.PLAYER_1;
		if (throwForPlayer1 == Throw.SCISSORS && throwForPlayer2 == Throw.PAPER)
			return PlayerRole.PLAYER_1;

		// Check to see if Player 2 won.
		if (throwForPlayer2 == Throw.ROCK && throwForPlayer1 == Throw.SCISSORS)
			return PlayerRole.PLAYER_2;
		if (throwForPlayer2 == Throw.PAPER && throwForPlayer1 == Throw.ROCK)
			return PlayerRole.PLAYER_2;
		if (throwForPlayer2 == Throw.SCISSORS && throwForPlayer1 == Throw.PAPER)
			return PlayerRole.PLAYER_2;

		// Must have been a tie.
		if (throwForPlayer1 == throwForPlayer2)
			return null;

		// If we get here, I screwed something up. Go boom.
		throw new BadCodeMonkeyException();
	}
}
