package com.justdavis.karl.rpstourney.api;

/**
 * <p>
 * Each {@link IPlayer} implementation represents a <em>type</em> of player in
 * the game, e.g. there might be an implementation for a "local human player"
 * and another implementation for a "local AI player". Each {@link IPlayer}
 * instance, in turn, represents a specific player in the game. These
 * {@link IPlayer} instances are not persistent between games, any sort of user
 * metadata is stored elsewhere.
 * </p>
 */
public interface IPlayer {
	/**
	 * @return the user-displayable name for this player
	 */
	String getName();
}
