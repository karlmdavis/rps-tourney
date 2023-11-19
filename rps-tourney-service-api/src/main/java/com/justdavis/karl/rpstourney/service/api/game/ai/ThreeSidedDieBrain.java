package com.justdavis.karl.rpstourney.service.api.game.ai;

import java.util.concurrent.ThreadLocalRandom;

import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * This {@link IPositronicBrain} rolls a "three-sided die" to determine which {@link Throw} to make. It's just random;
 * history and tactics are completely ignored.
 */
public final class ThreeSidedDieBrain implements IPositronicBrain {
	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.ai.IPositronicBrain#calculateNextThrow(com.justdavis.karl.rpstourney.service.api.game.GameView,
	 *      com.justdavis.karl.rpstourney.service.api.game.PlayerRole)
	 */
	@Override
	public Throw calculateNextThrow(GameView game, PlayerRole role) {
		return calculateRandomThrow();
	}

	/**
	 * This has been extracted into a static method for the convenience of other {@link IPositronicBrain}
	 * implementations, as many of them will sometimes need to select a random {@link Throw}.
	 *
	 * @return a random {@link Throw}, where all possibilities are weighted equally
	 */
	static Throw calculateRandomThrow() {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		int dieRoll = rng.nextInt(3);

		if (dieRoll == 0)
			return Throw.ROCK;
		else if (dieRoll == 1)
			return Throw.PAPER;
		else
			return Throw.SCISSORS;
	}
}
