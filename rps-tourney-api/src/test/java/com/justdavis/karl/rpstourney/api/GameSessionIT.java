package com.justdavis.karl.rpstourney.api;

import org.junit.Assert;
import org.junit.Test;

/**
 * Integration tests for {@link GameSession} and related classes.
 */
public final class GameSessionIT {
	@Test
	public void simpleGame() {
		// Create the players.
		IPlayer player1 = null; // TODO
		IPlayer player2 = null; // TODO

		// Create the game.
		GameSession game = new GameSession(1, player1, player2);
		Assert.assertNotNull(game.getCompletedRounds());
		Assert.assertEquals(0, game.getCompletedRounds().size());
		Assert.assertEquals(null, game.checkForWinner());
		Assert.assertEquals(0, game.getCurrentRoundIndex());

		// Play the first (and only) round.
		game.submitThrow(0, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(0, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(0, game.getCurrentRoundIndex());
		Assert.assertEquals(1, game.getCompletedRounds().size());
		Assert.assertEquals(PlayerRole.PLAYER_2,
				game.getCompletedRounds().get(0).determineWinner());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.checkForWinner());
	}
}
