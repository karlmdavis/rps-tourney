package com.justdavis.karl.rpstourney.app.console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.api.GameSession;
import com.justdavis.karl.rpstourney.api.Throw;
import com.justdavis.karl.rpstourney.api.ai.IAiPlayer;
import com.justdavis.karl.rpstourney.api.ai.ScriptedAiPlayer;

/**
 * Unit tests for {@link ConsoleGameDriver}.
 */
public final class ConsoleGameDriverTest {
	/**
	 * Plays a short game via {@link ConsoleGameDriver}, to ensure that
	 * everything works as expected.
	 * 
	 * @throws UnsupportedEncodingException
	 *             (should not occur)
	 */
	@Test
	public void playOneRoundGame() throws UnsupportedEncodingException {
		String humanInput = "R\n";

		GameSession game = new GameSession(1);
		IAiPlayer computerOpponent = new ScriptedAiPlayer(Throw.PAPER);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				humanInput.getBytes("US-ASCII"));

		ConsoleGameDriver gameDriver = new ConsoleGameDriver();
		gameDriver.playGameSession(game, computerOpponent, new PrintStream(
				outputStream), inputStream);

		String gameOutput = outputStream.toString("US-ASCII");
		Assert.assertNotNull(gameOutput);
		Assert.assertNotEquals(0, gameOutput.length());
		Assert.assertTrue(gameOutput.contains("You lost."));
	}

	/**
	 * Plays a short game via {@link ConsoleGameDriver}, to ensure that
	 * everything works as expected.
	 * 
	 * @throws UnsupportedEncodingException
	 *             (should not occur)
	 */
	@Test
	public void playTwoRoundGame() throws UnsupportedEncodingException {
		String humanInput = "R\nr\n";

		GameSession game = new GameSession(3);
		IAiPlayer computerOpponent = new ScriptedAiPlayer(Throw.PAPER);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				humanInput.getBytes("US-ASCII"));

		ConsoleGameDriver gameDriver = new ConsoleGameDriver();
		gameDriver.playGameSession(game, computerOpponent, new PrintStream(
				outputStream), inputStream);

		String gameOutput = outputStream.toString("US-ASCII");
		Assert.assertNotNull(gameOutput);
		Assert.assertNotEquals(0, gameOutput.length());
		Assert.assertTrue(gameOutput.contains("Round 2!"));
		Assert.assertFalse(gameOutput.contains("You won!"));
		Assert.assertTrue(gameOutput.contains("You lost."));
	}
}
