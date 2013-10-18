package com.justdavis.karl.rpstourney.api;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link GameSession}.
 */
public final class GameSessionTest {
	/**
	 * Tests a simple one-round game.
	 */
	@Test
	public void simpleGame() {
		// Create the game.
		GameSession game = new GameSession(1);
		Assert.assertNotNull(game.getCompletedRounds());
		Assert.assertEquals(0, game.getCompletedRounds().size());
		Assert.assertEquals(null, game.checkForWinner());
		Assert.assertEquals(0, game.getCurrentRoundIndex());

		// Play the first (and only) round.
		game.submitThrow(0, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(0, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(1, game.getCompletedRounds().size());
		Assert.assertEquals(0, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_2,
				game.getCompletedRounds().get(0).determineWinner());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.checkForWinner());
	}

	/**
	 * Tests a simple three-round game.
	 */
	@Test
	public void multipleRounds() {
		// Create the game.
		GameSession game = new GameSession(3);
		Assert.assertNotNull(game.getCompletedRounds());
		Assert.assertEquals(0, game.getCompletedRounds().size());
		Assert.assertEquals(null, game.checkForWinner());
		Assert.assertEquals(0, game.getCurrentRoundIndex());

		// Play the first round.
		game.submitThrow(0, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(0, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(1, game.getCompletedRounds().size());
		Assert.assertEquals(1, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_2,
				game.getCompletedRounds().get(0).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the second round.
		game.submitThrow(1, PlayerRole.PLAYER_2, Throw.ROCK);
		game.submitThrow(1, PlayerRole.PLAYER_1, Throw.PAPER);
		Assert.assertEquals(2, game.getCompletedRounds().size());
		Assert.assertEquals(2, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_1,
				game.getCompletedRounds().get(1).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the third round.
		game.submitThrow(2, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(2, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(3, game.getCompletedRounds().size());
		Assert.assertEquals(2, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_2,
				game.getCompletedRounds().get(2).determineWinner());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.checkForWinner());
	}
}
