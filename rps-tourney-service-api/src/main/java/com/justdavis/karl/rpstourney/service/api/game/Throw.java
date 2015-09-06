package com.justdavis.karl.rpstourney.service.api.game;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * The various throws a player can select in each {@link GameRound}.
 */
public enum Throw {
	ROCK,

	PAPER,

	SCISSORS;

	/**
	 * @return the {@link Throw} that can beat this one
	 */
	public Throw getOppositeThrow() {
		if (this.equals(Throw.ROCK))
			return Throw.PAPER;
		if (this.equals(Throw.PAPER))
			return Throw.SCISSORS;
		if (this.equals(Throw.SCISSORS))
			return Throw.ROCK;

		// Must be missing a case.
		throw new BadCodeMonkeyException();
	}
}
