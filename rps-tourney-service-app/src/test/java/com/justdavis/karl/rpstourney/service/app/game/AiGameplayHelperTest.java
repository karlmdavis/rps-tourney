package com.justdavis.karl.rpstourney.service.app.game;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * Unit tests for {@link AiGameplayHelper}.
 */
public final class AiGameplayHelperTest {
	/**
	 * Verifies that {@link AiGameplayHelper} works as expected when only one of the players in a {@link Game} is AI.
	 */
	@Test
	public void singleAiPlayer() {
		AiGameplayHelper aiHelper = new AiGameplayHelper();
		Game game = new Game(new Player(new Account()));
		game.setPlayer2(new Player(BuiltInAi.ONE_SIDED_DIE_ROCK));

		/*
		 * Walk through a game, using the AiGameplayHelper, and verifying it does what it's supposed to.
		 */
		game.submitThrow(0, game.getPlayer1(), Throw.ROCK);
		aiHelper.advanceGameForAiPlayers(game);
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(Throw.ROCK, game.getRounds().get(0).getThrowForPlayer2());
		Assert.assertEquals(Throw.ROCK, game.getRounds().get(1).getThrowForPlayer2());
		game.submitThrow(1, game.getPlayer1(), Throw.PAPER);
		aiHelper.advanceGameForAiPlayers(game);
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(Throw.ROCK, game.getRounds().get(2).getThrowForPlayer2());
	}

	/**
	 * Verifies that {@link AiGameplayHelper} works as expected when both of the players in a {@link Game} are AI.
	 */
	@Test
	public void twoAiPlayers() {
		AiGameplayHelper aiHelper = new AiGameplayHelper();
		Game game = new Game(new Player(BuiltInAi.ONE_SIDED_DIE_ROCK));
		game.setPlayer2(new Player(BuiltInAi.ONE_SIDED_DIE_PAPER));

		/*
		 * Start the AiGameplayHelper on the game, which should cause it to advance all the way through until player 2
		 * wins. Verify that went as expected.
		 */
		aiHelper.advanceGameForAiPlayers(game);
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(2, game.getScoreForPlayer2());
		Assert.assertEquals(game.getPlayer2(), game.getWinner());
	}
}
