package com.justdavis.karl.rpstourney.api;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link GameRound}.
 */
public final class GameRoundTest {
	/**
	 * Tests {@link GameRound}.
	 */
	@Test
	public void basicUsage() {
		GameRound round = new GameRound(42, Throw.ROCK, Throw.PAPER);
		Assert.assertEquals(42, round.getRoundIndex());
		Assert.assertEquals(Throw.ROCK, round.getThrowForPlayer1());
		Assert.assertEquals(Throw.PAPER, round.getThrowForPlayer2());
	}
}
