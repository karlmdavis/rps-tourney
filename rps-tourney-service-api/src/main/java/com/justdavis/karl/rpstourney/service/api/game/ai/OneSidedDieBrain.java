package com.justdavis.karl.rpstourney.service.api.game.ai;

import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * This {@link IPositronicBrain} rolls a "one-sided die" to determine which
 * {@link Throw} to make (that's a joke: it always throws the same thing). As
 * might be expected, this is really only useful in tests.
 */
public final class OneSidedDieBrain implements IPositronicBrain {
	private final Throw throwToMakeOverAndOverAndOverEtc;

	/**
	 * Constructs a new {@link OneSidedDieBrain} instance.
	 * 
	 * @param throwToMakeOverAndOverAndOverEtc
	 *            the {@link Throw} that will always be returned by
	 *            {@link #calculateNextThrow(GameView, PlayerRole)}
	 */
	public OneSidedDieBrain(Throw throwToMakeOverAndOverAndOverEtc) {
		this.throwToMakeOverAndOverAndOverEtc = throwToMakeOverAndOverAndOverEtc;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.ai.IPositronicBrain#calculateNextThrow(com.justdavis.karl.rpstourney.service.api.game.GameView,
	 *      com.justdavis.karl.rpstourney.service.api.game.PlayerRole)
	 */
	@Override
	public Throw calculateNextThrow(GameView game, PlayerRole role) {
		return throwToMakeOverAndOverAndOverEtc;
	}
}
