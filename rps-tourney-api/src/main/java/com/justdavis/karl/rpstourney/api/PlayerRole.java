package com.justdavis.karl.rpstourney.api;

/**
 * Enumerates the various roles available to each player in the game.
 */
public enum PlayerRole {
	/**
	 * The {@link IPlayer} that has been (arbitrarily) designated as "Player 1"
	 * for the game.
	 * 
	 * @see GameSession#getPlayer1()
	 */
	PLAYER_1,

	/**
	 * The {@link IPlayer} that has been (arbitrarily) designated as "Player 2"
	 * for the game.
	 * 
	 * @see GameSession#getPlayer2()
	 */
	PLAYER_2;
}
