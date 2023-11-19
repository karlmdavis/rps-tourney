package com.justdavis.karl.rpstourney.app.console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.app.console.i18n.DefaultResourceBundleLoader;
import com.justdavis.karl.rpstourney.app.console.localservice.GameBundle;
import com.justdavis.karl.rpstourney.app.console.localservice.LocalGameClient;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * Unit tests for {@link ConsoleGameDriver}.
 */
public final class ConsoleGameDriverTest {
	/**
	 * Plays a short game via {@link ConsoleGameDriver}, to ensure that everything works as expected.
	 *
	 * @throws UnsupportedEncodingException
	 *             (should not occur)
	 */
	@Test
	public void playOneRoundGame() throws UnsupportedEncodingException {
		String humanInput = "R\n";

		Game game = new Game(new Player(new Account()));
		game.setMaxRounds(1);
		game.setPlayer2(new Player(BuiltInAi.ONE_SIDED_DIE_PAPER));
		LocalGameClient gameClient = new LocalGameClient(game, game.getPlayer1());
		GameBundle gameBundle = new GameBundle(gameClient, game.getId());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(humanInput.getBytes("US-ASCII"));

		ConsoleGameDriver gameDriver = new ConsoleGameDriver(new DefaultResourceBundleLoader(Locale.ENGLISH));
		gameDriver.playGame(gameBundle, new PrintStream(outputStream), inputStream);

		String gameOutput = outputStream.toString("US-ASCII");
		Assert.assertNotNull(gameOutput);
		Assert.assertNotEquals(0, gameOutput.length());
		Assert.assertTrue(gameOutput.contains("You lost."));
	}

	/**
	 * Plays a short game via {@link ConsoleGameDriver}, to ensure that everything works as expected.
	 *
	 * @throws UnsupportedEncodingException
	 *             (should not occur)
	 */
	@Test
	public void playTwoRoundGame() throws UnsupportedEncodingException {
		String humanInput = "R\nr\n";

		Game game = new Game(new Player(new Account()));
		game.setMaxRounds(3);
		game.setPlayer2(new Player(BuiltInAi.ONE_SIDED_DIE_PAPER));
		LocalGameClient gameClient = new LocalGameClient(game, game.getPlayer1());
		GameBundle gameBundle = new GameBundle(gameClient, game.getId());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(humanInput.getBytes("US-ASCII"));

		ConsoleGameDriver gameDriver = new ConsoleGameDriver(new DefaultResourceBundleLoader(Locale.ENGLISH));
		gameDriver.playGame(gameBundle, new PrintStream(outputStream), inputStream);

		String gameOutput = outputStream.toString("US-ASCII");
		Assert.assertNotNull(gameOutput);
		Assert.assertNotEquals(0, gameOutput.length());
		Assert.assertTrue(gameOutput.contains("Round 2!"));
		Assert.assertFalse(gameOutput.contains("You won!"));
		Assert.assertTrue(gameOutput.contains("You lost."));
	}
}
