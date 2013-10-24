package com.justdavis.karl.rpstourney.api.ai;

import java.util.List;
import java.util.Random;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.api.GameRound;
import com.justdavis.karl.rpstourney.api.Throw;

/**
 * This {@link IAiPlayer} just rolls a "three-sided die" to select each
 * {@link Throw}.
 */
public final class RandomAiPlayer implements IAiPlayer {
	private final Random rng;

	/**
	 * Constructs a new {@link RandomAiPlayer} instance.
	 */
	public RandomAiPlayer() {
		this.rng = new Random();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.api.ai.IAiPlayer#selectThrow(int,
	 *      java.util.List)
	 */
	@Override
	public Throw selectThrow(int maxRounds, List<GameRound> completedRounds) {
		// Sanity check: valid max rounds.
		if (maxRounds < 1)
			throw new IllegalArgumentException();
		// Sanity check: valid max rounds.
		if (maxRounds % 2 == 0)
			throw new IllegalArgumentException();
		// Sanity check: null history.
		if (completedRounds == null)
			throw new IllegalArgumentException();

		int threeSidedDieRoll = rng.nextInt(3);
		if (threeSidedDieRoll == 0)
			return Throw.ROCK;
		else if (threeSidedDieRoll == 1)
			return Throw.PAPER;
		else if (threeSidedDieRoll == 2)
			return Throw.SCISSORS;
		else
			throw new BadCodeMonkeyException();
	}
}
