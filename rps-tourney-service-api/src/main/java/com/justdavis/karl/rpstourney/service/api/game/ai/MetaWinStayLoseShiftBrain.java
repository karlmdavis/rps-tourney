package com.justdavis.karl.rpstourney.service.api.game.ai;

import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * This {@link IPositronicBrain} is referred to as "the best way to win", per the following article:
 * <a href= "http://arstechnica.com/science/2014/05/win-at-rock-paper-scissors-by-knowing-thy-opponent/" >Scientists
 * find a winning strategy for rock-paper-scissors</a>. Note that this <strong>isn't</strong> the "win-stay, lose-shift"
 * strategy that is referred to there; it's the strategy designed to beat that one.
 */
public final class MetaWinStayLoseShiftBrain implements IPositronicBrain {
	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.ai.IPositronicBrain#calculateNextThrow(com.justdavis.karl.rpstourney.service.api.game.GameView,
	 *      com.justdavis.karl.rpstourney.service.api.game.PlayerRole)
	 */
	@Override
	public Throw calculateNextThrow(GameView game, PlayerRole role) {
		int currentRoundIndex = game.getRounds().size() - 1;

		// Select a random Throw for the first round.
		if (currentRoundIndex <= 0)
			return ThreeSidedDieBrain.calculateRandomThrow();

		/*
		 * "... if you lose the first round, switch to the thing that beats the thing your opponent just played. If you
		 * win, don't keep playing the same thing, but instead switch to the thing that would beat the thing that you
		 * just played."
		 */
		GameRound previousRound = game.getRounds().get(currentRoundIndex - 1);
		boolean wonPreviousRound = role.equals(previousRound.getResult().getWinningPlayerRole());
		if (wonPreviousRound) {
			Throw previousThrow = previousRound.getThrowForPlayer(role);
			return previousThrow.getOppositeThrow();
		} else {
			Throw previousOpponentThrow = previousRound.getThrowForPlayer(role.getOpponentRole());
			return previousOpponentThrow.getOppositeThrow();
		}
	}
}
