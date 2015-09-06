package com.justdavis.karl.rpstourney.service.api.game;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * Enumerates the identifiers for the two players within each {@link Game}.
 */
public enum PlayerRole {
	PLAYER_1,

	PLAYER_2;

	/**
	 * @return the opponent or "opposite" of this {@link PlayerRole}
	 */
	public PlayerRole getOpponentRole() {
		if (this.equals(PLAYER_1))
			return PLAYER_2;
		if (this.equals(PLAYER_2))
			return PLAYER_1;

		throw new BadCodeMonkeyException();
	}
}
