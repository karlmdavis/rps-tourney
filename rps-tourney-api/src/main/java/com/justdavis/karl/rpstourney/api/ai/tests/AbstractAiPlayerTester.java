package com.justdavis.karl.rpstourney.api.ai.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.rpstourney.api.GameRound;
import com.justdavis.karl.rpstourney.api.GameSession;
import com.justdavis.karl.rpstourney.api.PlayerRole;
import com.justdavis.karl.rpstourney.api.Throw;
import com.justdavis.karl.rpstourney.api.ai.IAiPlayer;
import com.justdavis.karl.rpstourney.api.ai.RandomAiPlayer;

/**
 * <p>
 * The unit tests for {@link IAiPlayer} implementations should extend this class
 * to ensure that the tests here are also run.
 * </p>
 * <p>
 * These tests rely on JUnit 4 and SLF4J; those dependencies must be available
 * on the test classpath.
 * </p>
 */
public abstract class AbstractAiPlayerTester {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @return a new instance of the {@link IAiPlayer} implementation being
	 *         tested
	 */
	protected abstract IAiPlayer getAiPlayer();

	/**
	 * Verifies that the {@link IAiPlayer} implementation can correctly handle a
	 * call to {@link IAiPlayer#selectThrow(int, java.util.List)} for the first
	 * move in a game.
	 */
	@Test
	public void submitFirstThrow() {
		IAiPlayer aiPlayer = getAiPlayer();

		Throw selectedThrow = aiPlayer.selectThrow(1, new ArrayList<GameRound>());
		Assert.assertNotNull(selectedThrow);
	}

	/**
	 * Verifies that the {@link IAiPlayer} implementation can correctly handle
	 * calls to {@link IAiPlayer#selectThrow(int, java.util.List)} for a short
	 * game, playing against a player that always throws paper.
	 */
	@Test
	public void playShortGame() {
		IAiPlayer aiPlayer = getAiPlayer();
		GameSession game = new GameSession(21);

		while (game.checkForWinner() == null) {
			Throw player1Throw = aiPlayer.selectThrow(game.getMaxRounds(), game.getCompletedRounds());
			Assert.assertNotNull(player1Throw);

			Throw player2Throw = Throw.PAPER;

			game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_1, player1Throw);
			game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_2, player2Throw);
		}
	}

	/**
	 * <p>
	 * Verifies that the {@link IAiPlayer} implementation can correctly handle
	 * 1000 calls to {@link IAiPlayer#selectThrow(int, java.util.List)} in under
	 * 5 seconds.
	 * </p>
	 * <p>
	 * Obviously, success here depends on the speed of the computer it's running
	 * on. That said, the {@link IAiPlayer} implementation should have enough of
	 * a "buffer" in speed that it doesn't really matter.
	 * </p>
	 */
	@Test
	public void select1000ThrowsInUnder5Seconds() {
		// Create the AI player.
		IAiPlayer aiPlayer = getAiPlayer();

		/*
		 * Generate the throws that will be used for the opponent. (It will be
		 * playing against a very random opponent.)
		 */
		IAiPlayer randomOpponent = new RandomAiPlayer();
		List<Throw> randomThrows = new ArrayList<Throw>();
		for (int i = 0; i < 1000; i++)
			randomThrows.add(randomOpponent.selectThrow(1, new ArrayList<GameRound>()));

		// Create the game that will be played partway through.
		GameSession game = new GameSession((1000 * 2) + 1);

		// Start the clock, play through 1000 rounds, stop the clock.
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			// Submit the pre-generated opponent Throw.
			game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_1, randomThrows.get(i));

			// Generate and submit the AI's throw.
			Throw aiPlayerThrow = aiPlayer.selectThrow(game.getMaxRounds(), game.getCompletedRounds());
			game.submitThrow(game.getCurrentRoundIndex(), PlayerRole.PLAYER_2, aiPlayerThrow);
		}
		long endTime = System.currentTimeMillis();

		// Ensure it was fast enough.
		long runTime = endTime - startTime;
		logger.info("The {} AI ran through 1000 rounds in {}ms.", aiPlayer.getClass(), runTime);
		Assert.assertTrue("Clock rollover problem.", runTime > 0);
		Assert.assertTrue((runTime / 1000) <= 5);
	}
}
