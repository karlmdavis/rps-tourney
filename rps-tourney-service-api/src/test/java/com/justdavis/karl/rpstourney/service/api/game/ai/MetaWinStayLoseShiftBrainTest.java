package com.justdavis.karl.rpstourney.service.api.game.ai;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * Unit tests for {@link MetaWinStayLoseShiftBrain}.
 */
public final class MetaWinStayLoseShiftBrainTest {
	/**
	 * Verifies that {@link MetaWinStayLoseShiftBrain} works as expected.
	 */
	@Test
	public void normalUsage() {
		MetaWinStayLoseShiftBrain ai = new MetaWinStayLoseShiftBrain();
		Game game = new Game(new Player(new Account()));
		game.setPlayer2(new Player(BuiltInAi.META_WIN_STAY_LOSE_SHIFT_V1));

		// "Fix" the first round, to make the tests easier.
		game.submitThrow(0, game.getPlayer1(), Throw.PAPER);
		game.submitThrow(0, game.getPlayer2(), Throw.PAPER);

		/*
		 * The AI didn't win that round, so it should throw the opposite of the other player's move from it.
		 */
		game.submitThrow(1, game.getPlayer1(), Throw.PAPER);
		Throw aiThrowSecond = ai.calculateNextThrow(new GameView(game, game.getPlayer2()), PlayerRole.PLAYER_2);
		Assert.assertEquals(Throw.SCISSORS, aiThrowSecond);
		game.submitThrow(1, game.getPlayer2(), aiThrowSecond);

		/*
		 * The AI did win that round, so it should throw the thing that would beat the one it just played.
		 */
		Throw aiThrowThird = ai.calculateNextThrow(new GameView(game, game.getPlayer2()), PlayerRole.PLAYER_2);
		Assert.assertEquals(Throw.ROCK, aiThrowThird);
	}
}
