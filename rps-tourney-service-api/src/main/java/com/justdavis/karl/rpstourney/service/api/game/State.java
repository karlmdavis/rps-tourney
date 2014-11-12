package com.justdavis.karl.rpstourney.service.api.game;

import java.util.List;

/**
 * These values for the {@link Game#getState()} field represent a very
 * simple state machine for the state of each game.
 */
public enum State {
	/**
	 * {@link Game}s in this state are waiting for {@link Game#getPlayer2()}
	 * to be identified. In this {@link State}, the
	 * {@link Game#setMaxRounds(int)} and {@link Game#setPlayer2(Player)}
	 * methods may be used, and {@link Game#getRounds()} will return an
	 * empty {@link List}.
	 */
	WAITING_FOR_PLAYER,

	/**
	 * {@link Game}s in this state are ready to be played, but have not yet
	 * had a call to {@link Game#submitThrow(int, Player, Throw)} complete
	 * yet. In this {@link State}, the {@link Game#setMaxRounds(int)} method
	 * may be used, and {@link Game#getRounds()} will return a {@link List}
	 * with just the first {@link GameRound} in it.
	 */
	WAITING_FOR_FIRST_THROW,

	/**
	 * {@link Game}s in this state represent games that are in-progress.
	 * None of the {@link Game} setters may be used, and
	 * {@link Game#getRounds()} will return a non-<code>null</code>,
	 * non-empty {@link List}.
	 */
	STARTED,

	/**
	 * {@link Game}s in this state represent games that were played and have
	 * completed.
	 */
	FINISHED;
}