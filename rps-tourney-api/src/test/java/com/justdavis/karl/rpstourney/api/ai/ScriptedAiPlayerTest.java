package com.justdavis.karl.rpstourney.api.ai;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.api.GameSession;
import com.justdavis.karl.rpstourney.api.PlayerRole;
import com.justdavis.karl.rpstourney.api.Throw;
import com.justdavis.karl.rpstourney.api.ai.tests.AbstractAiPlayerTester;

/**
 * Unit tests for {@link ScriptedAiPlayer}.
 */
public final class ScriptedAiPlayerTest extends AbstractAiPlayerTester {
	/**
	 * @see com.justdavis.karl.rpstourney.api.ai.tests.AbstractAiPlayerTester#getAiPlayer()
	 */
	@Override
	protected IAiPlayer getAiPlayer() {
		return new ScriptedAiPlayer(Throw.ROCK, Throw.PAPER, Throw.SCISSORS);
	}

	/**
	 * Verifies that {@link ScriptedAiPlayer} can be constructed without going
	 * boom.
	 */
	@Test
	public void constructor() {
		// Shouldn't throw any exceptions:
		new ScriptedAiPlayer(Throw.ROCK, Throw.PAPER, Throw.SCISSORS);
	}

	/**
	 * Verifies that {@link ScriptedAiPlayer} works as expected with a script
	 * that only has one entry.
	 */
	@Test
	public void scriptWithOneEntry() {
		ScriptedAiPlayer aiPlayer = new ScriptedAiPlayer(Throw.ROCK);
		GameSession game = new GameSession(5);

		/*
		 * The AI being tested will play as Player 1. Player 2 will always throw
		 * paper.
		 */

		Throw aiThrow1 = aiPlayer.selectThrow(game.getMaxRounds(), game.getCompletedRounds());
		Assert.assertEquals(Throw.ROCK, aiThrow1);
		game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_1, aiThrow1);
		game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_2, Throw.PAPER);

		Throw aiThrow2 = aiPlayer.selectThrow(game.getCurrentRoundIndex(), game.getCompletedRounds());
		Assert.assertEquals(Throw.ROCK, aiThrow2);
	}

	/**
	 * Verifies that {@link ScriptedAiPlayer} works as expected with a script
	 * that only has two entries.
	 */
	@Test
	public void scriptWithTwoEntries() {
		ScriptedAiPlayer aiPlayer = new ScriptedAiPlayer(Throw.ROCK, Throw.PAPER);
		GameSession game = new GameSession(11);

		/*
		 * The AI being tested will play as Player 1. Player 2 will always throw
		 * scissors.
		 */

		Throw aiThrow1 = aiPlayer.selectThrow(game.getMaxRounds(), game.getCompletedRounds());
		Assert.assertEquals(Throw.ROCK, aiThrow1);
		game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_1, aiThrow1);
		game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_2, Throw.SCISSORS);

		Throw aiThrow2 = aiPlayer.selectThrow(game.getMaxRounds(), game.getCompletedRounds());
		Assert.assertEquals(Throw.PAPER, aiThrow2);
		game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_1, aiThrow2);
		game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_2, Throw.SCISSORS);

		Throw aiThrow3 = aiPlayer.selectThrow(game.getMaxRounds(), game.getCompletedRounds());
		Assert.assertEquals(Throw.ROCK, aiThrow3);
	}
}
