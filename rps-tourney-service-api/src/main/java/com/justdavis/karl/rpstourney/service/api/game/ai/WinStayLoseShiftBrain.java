package com.justdavis.karl.rpstourney.service.api.game.ai;

import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * This {@link IPositronicBrain} employs a "win-stay, lose-shift" strategy, per
 * the following article: <a href=
 * "http://arstechnica.com/science/2014/05/win-at-rock-paper-scissors-by-knowing-thy-opponent/"
 * >Scientists find a winning strategy for rock-paper-scissors</a>. Note that
 * this <strong>isn't</strong> the strategy that is referred to as
 * "the best way to win"; it's the strategy that other, best way is designed to
 * beat. Instead, this strategy is analogous to how most humans play.
 */
public final class WinStayLoseShiftBrain implements IPositronicBrain {
	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.ai.IPositronicBrain#calculateNextThrow(com.justdavis.karl.rpstourney.service.api.game.GameView,
	 *      com.justdavis.karl.rpstourney.service.api.game.PlayerRole)
	 */
	@Override
	public Throw calculateNextThrow(GameView game, PlayerRole role) {
		int currentRoundIndex = game.getRounds().size() - 1;
		return calculateThrowForRound(game, role, currentRoundIndex);
	}

	/**
	 * This has been extracted into a static method for the convenience of other
	 * {@link IPositronicBrain} implementations, as some of them will want to
	 * "borrow" this strategy. As an added convenience, it also lets users
	 * specify which round to calculate a Throw for.
	 *
	 * @param game
	 *            (see {@link #calculateNextThrow(GameView, PlayerRole)})
	 * @param role
	 *            (see {@link #calculateNextThrow(GameView, PlayerRole)})
	 * @param roundIndex
	 *            the {@link GameRound#getRoundIndex()} of the {@link GameRound}
	 *            to calculate a Throw for (must be less than or equal to the
	 *            actual current round in the specified {@link GameView})
	 * @return the {@link Throw} to make
	 */
	static Throw calculateThrowForRound(GameView game, PlayerRole role, int roundIndex) {
		// Select a random Throw for the first round.
		if (roundIndex < 1)
			return ThreeSidedDieBrain.calculateRandomThrow();

		/*
		 * The strategy for other rounds is dependent on whether or not the last
		 * round's Throw won.
		 */
		GameRound previousRound = game.getRounds().get(roundIndex - 1);
		boolean wonPreviousRound = role.equals(previousRound.getResult().getWinningPlayerRole());
		if (wonPreviousRound) {
			// Stay with the Throw from last round.
			return previousRound.getThrowForPlayer(role);
		} else {
			// Go with the Throw that could have won last round.
			Throw previousWinningThrow = previousRound.getThrowForPlayer(role.getOpponentRole());
			return previousWinningThrow.getOppositeThrow();
		}
	}
}
