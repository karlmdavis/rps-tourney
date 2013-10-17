package com.justdavis.karl.rpstourney.api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
	private final IPlayer player1;
	private final IPlayer player2;

	/**
	 * This field is mutable. It will be updated by the {@link GameSession}
	 * itself as play progresses.
	 */
	private final List<GameRound> completedRounds;

	/**
	 * <p>
	 * Used to temporarily store the {@link Throw} submitted by {@link #player1}
	 * for the round that is currently in-progress. If there is no round
	 * in-progress, or if the {@link IPlayer} has not yet submitted a
	 * {@link Throw} via {@link #submitThrow(int, IPlayer, Throw)}, this field
	 * will be <code>null</code>.
	 * </p>
	 * <p>
	 * This field is mutable.
	 * </p>
	 */
	private Throw currentThrowPlayer1;

	/**
	 * <p>
	 * Used to temporarily store the {@link Throw} submitted by {@link #player2}
	 * for the round that is currently in-progress. If there is no round
	 * in-progress, or if the {@link IPlayer} has not yet submitted a
	 * {@link Throw} via {@link #submitThrow(int, IPlayer, Throw)}, this field
	 * will be <code>null</code>.
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
	 * @param player1
	 *            the value to use for {@link #getPlayer1()}
	 * @param player2
	 *            the value to use for {@link #getPlayer2()}
	 */
	public GameSession(int maxRounds, IPlayer player1, IPlayer player2) {
		this.maxRounds = maxRounds;
		this.player1 = player1;
		this.player2 = player2;

		this.completedRounds = new LinkedList<GameRound>();
		this.currentThrowPlayer1 = null;
		this.currentThrowPlayer2 = null;
	}

	/**
	 * @return the maximum numbers of (non-tied) {@link GameRound}s that will be
	 *         played
	 */
	public int getMaxRounds() {
		return maxRounds;
	}

	/**
	 * @return one of the players participating in the game, arbitrarily
	 *         designated as {@link PlayerRole#PLAYER_1}
	 */
	public IPlayer getPlayer1() {
		return player1;
	}

	/**
	 * @return one of the players participating in the game, arbitrarily
	 *         designated as {@link PlayerRole#PLAYER_2}
	 */
	public IPlayer getPlayer2() {
		return player2;
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
		return Math.min(getCompletedRounds().size(), maxRounds);
	}

	/**
	 * This method should be called once for each player each round, to register
	 * that player's selected {@link Throw} for the round.
	 * 
	 * @param roundIndex
	 *            the index of the round that a {@link Throw} is being submitted
	 *            for. This is included as a parameter to help ensure that the
	 *            client &amp; server are staying in sync.
	 * @param player
	 *            the {@link PlayerRole} (which must be either
	 *            {@link PlayerRole#PLAYER_1} or {@link PlayerRole#PLAYER_2})
	 *            that is submitting the {@link Throw}
	 * @param move
	 *            the {@link Throw} that the {@link IPlayer} has selected for
	 *            the round
	 */
	public void submitThrow(int roundIndex, PlayerRole player, Throw move) {
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
		if (!isPlayer1 || isPlayer2)
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
			GameRound completedRound = new GameRound(roundIndex,
					currentThrowPlayer1, currentThrowPlayer2);
			completedRounds.add(completedRound);
		}
	}

	/**
	 * @return the {@link PlayerRole} for the {@link IPlayer} that won this
	 *         {@link GameRound}, or <code>null</code> if the game is still
	 *         in-progress
	 */
	public PlayerRole checkForWinner() {
		// TODO Auto-generated method stub
		return null;
	}
}
