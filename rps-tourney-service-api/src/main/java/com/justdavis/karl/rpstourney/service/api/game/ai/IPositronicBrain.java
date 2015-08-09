package com.justdavis.karl.rpstourney.service.api.game.ai;

import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * Implementations of {@link IPositronicBrain} provide the logic used by the
 * game's AI players, capable of analyzing game history and deciding which
 * {@link Throw} to make next.
 */
public interface IPositronicBrain {
	/**
	 * @param game
	 *            A {@link GameView} of the game to calculate a {@link Throw}
	 *            for the current {@link GameRound} of. Implementations may
	 *            assume that this method will only be called when there is a
	 *            valid {@link Throw} to be made (i.e. not before the game has
	 *            started or after it has finished).
	 * @param role
	 *            the {@link PlayerRole} that the {@link Throw} should be
	 *            calculated for
	 * @return the {@link Throw} chosen by this {@link IPositronicBrain}
	 */
	Throw calculateNextThrow(GameView game, PlayerRole role);
}
