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
 * Unit tests for {@link WinStayLoseShiftBrain}.
 */
public final class WinStayLoseShiftBrainTest {
	/**
	 * Verifies that {@link WinStayLoseShiftBrain} works as expected.
	 */
	@Test
	public void normalUsage() {
		WinStayLoseShiftBrain ai = new WinStayLoseShiftBrain();
		Game game = new Game(new Player(new Account()));
		game.setPlayer2(new Player(BuiltInAi.WIN_STAY_LOSE_SHIFT_V1));

		// "Fix" the first round, to make the tests easier.
		game.submitThrow(0, game.getPlayer1(), Throw.ROCK);
		game.submitThrow(0, game.getPlayer2(), Throw.ROCK);

		/*
		 * The AI didn't win that round, so it should make the "opposite" Throw
		 * this round.
		 */
		game.submitThrow(1, game.getPlayer1(), Throw.ROCK);
		Throw aiThrowSecond = ai.calculateNextThrow(new GameView(game, game.getPlayer2()), PlayerRole.PLAYER_2);
		Assert.assertEquals(Throw.PAPER, aiThrowSecond);
		game.submitThrow(1, game.getPlayer2(), aiThrowSecond);

		/*
		 * The AI did win that round, so it should make the same Throw this
		 * round.
		 */
		Throw aiThrowThird = ai.calculateNextThrow(new GameView(game, game.getPlayer2()), PlayerRole.PLAYER_2);
		Assert.assertEquals(Throw.PAPER, aiThrowThird);
	}
}
