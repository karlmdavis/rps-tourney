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
		Assert.assertEquals(PlayerRole.PLAYER_2, game.getCompletedRounds().get(0).determineWinner());
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
		Assert.assertEquals(PlayerRole.PLAYER_2, game.getCompletedRounds().get(0).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the second round.
		game.submitThrow(1, PlayerRole.PLAYER_2, Throw.ROCK);
		game.submitThrow(1, PlayerRole.PLAYER_1, Throw.PAPER);
		Assert.assertEquals(2, game.getCompletedRounds().size());
		Assert.assertEquals(2, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_1, game.getCompletedRounds().get(1).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the third round.
		game.submitThrow(2, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(2, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(3, game.getCompletedRounds().size());
		Assert.assertEquals(2, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.getCompletedRounds().get(2).determineWinner());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.checkForWinner());
	}

	/**
	 * Tests a game with a tied round.
	 */
	@Test
	public void multipleRoundsWithTie() {
		// Create the game.
		GameSession game = new GameSession(3);
		Assert.assertNotNull(game.getCompletedRounds());
		Assert.assertEquals(0, game.getCompletedRounds().size());
		Assert.assertEquals(null, game.checkForWinner());
		Assert.assertEquals(0, game.getCurrentRoundIndex());

		// Play the first round. Winner: Player 2.
		game.submitThrow(0, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(0, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(1, game.getCompletedRounds().size());
		Assert.assertEquals(1, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.getCompletedRounds().get(0).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the second round. Winner: Player 1.
		game.submitThrow(1, PlayerRole.PLAYER_2, Throw.ROCK);
		game.submitThrow(1, PlayerRole.PLAYER_1, Throw.PAPER);
		Assert.assertEquals(2, game.getCompletedRounds().size());
		Assert.assertEquals(2, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_1, game.getCompletedRounds().get(1).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the third round. Winner: (none/tie)
		game.submitThrow(2, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(2, PlayerRole.PLAYER_2, Throw.ROCK);
		Assert.assertEquals(3, game.getCompletedRounds().size());
		Assert.assertEquals(3, game.getCurrentRoundIndex());
		Assert.assertEquals(null, game.getCompletedRounds().get(2).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the fourth round. Winner: Player 1.
		game.submitThrow(3, PlayerRole.PLAYER_1, Throw.SCISSORS);
		game.submitThrow(3, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(4, game.getCompletedRounds().size());
		Assert.assertEquals(3, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_1, game.getCompletedRounds().get(3).determineWinner());
		Assert.assertEquals(PlayerRole.PLAYER_1, game.checkForWinner());
	}

	/**
	 * Tests a game where a player wins "early" (in less than the maximum amount
	 * of rounds). The game should end as soon as it becomes impossible for the
	 * other player to win, regardless of the number of rounds left.
	 */
	@Test
	public void earlyWinner() {
		// Create the game.
		GameSession game = new GameSession(3);
		Assert.assertNotNull(game.getCompletedRounds());
		Assert.assertEquals(0, game.getCompletedRounds().size());
		Assert.assertEquals(null, game.checkForWinner());
		Assert.assertEquals(0, game.getCurrentRoundIndex());

		// Play the first round. Winner: Player 2.
		game.submitThrow(0, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(0, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(1, game.getCompletedRounds().size());
		Assert.assertEquals(1, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.getCompletedRounds().get(0).determineWinner());
		Assert.assertEquals(null, game.checkForWinner());

		// Play the second round. Winner: Player 2.
		game.submitThrow(1, PlayerRole.PLAYER_1, Throw.ROCK);
		game.submitThrow(1, PlayerRole.PLAYER_2, Throw.PAPER);
		Assert.assertEquals(2, game.getCompletedRounds().size());
		Assert.assertEquals(1, game.getCurrentRoundIndex());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.getCompletedRounds().get(1).determineWinner());
		Assert.assertEquals(PlayerRole.PLAYER_2, game.checkForWinner());
	}
}
