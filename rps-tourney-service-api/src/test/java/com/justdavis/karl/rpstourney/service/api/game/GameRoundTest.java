package com.justdavis.karl.rpstourney.service.api.game;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;

/**
 * Unit tests for {@link GameRound}.
 */
public final class GameRoundTest {
	/**
	 * Tests {@link GameRound}.
	 */
	@Test
	public void basicUsage() {
		GameSession game = new GameSession(new Player(new Account()));
		GameRound round = new GameRound(game, 42);

		Assert.assertEquals(42, round.getRoundIndex());
		Assert.assertNull(round.getThrowForPlayer1());
		Assert.assertNull(round.getThrowForPlayer1Timestamp());
		Assert.assertNull(round.getThrowForPlayer2());
		Assert.assertNull(round.getThrowForPlayer2Timestamp());

		round.setThrowForPlayer1(Throw.ROCK);
		round.setThrowForPlayer2(Throw.PAPER);
		Assert.assertEquals(Throw.ROCK, round.getThrowForPlayer1());
		Assert.assertNotNull(round.getThrowForPlayer1Timestamp());
		Assert.assertEquals(Throw.PAPER, round.getThrowForPlayer2());
		Assert.assertNotNull(round.getThrowForPlayer2Timestamp());
		Assert.assertEquals(Result.PLAYER_2_WON, round.getResult());
	}
}
