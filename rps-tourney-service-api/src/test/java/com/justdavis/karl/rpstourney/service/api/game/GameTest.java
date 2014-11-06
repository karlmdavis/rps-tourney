package com.justdavis.karl.rpstourney.service.api.game;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game.State;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;

/**
 * Unit tests for {@link Game}. The JAXB tests here also cover {@link Player}
 * and {@link GameRound}.
 */
public final class GameTest {
	/**
	 * Ensures that {@link Game} instances' state transitions correctly at the
	 * start of a game.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void gameStartStateTransitions() throws JAXBException,
			XPathExpressionException {
		// Create the initial instances.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		Game game = new Game(player1);
		game.setMaxRounds(41);

		// Verify the initial state.
		Assert.assertEquals(State.WAITING_FOR_PLAYER, game.getState());
		Assert.assertEquals(41, game.getMaxRounds());
		Assert.assertNotNull(game.getPlayer1());
		Assert.assertNull(game.getPlayer2());
		Assert.assertNotNull(game.getRounds());
		Assert.assertEquals(0, game.getRounds().size());

		// Set the second player.
		Account player2Account = new Account();
		Player player2 = new Player(player2Account);
		game.setPlayer2(player2);
		Assert.assertEquals(State.WAITING_FOR_FIRST_THROW, game.getState());
		Assert.assertEquals(41, game.getMaxRounds());
		Assert.assertNotNull(game.getPlayer1());
		Assert.assertNotNull(game.getPlayer2());
		Assert.assertNotNull(game.getRounds());
		Assert.assertEquals(1, game.getRounds().size());

		// Submit the first throw.
		game.submitThrow(0, player1, Throw.ROCK);
		Assert.assertEquals(State.STARTED, game.getState());
	}

	/**
	 * Ensures that {@link Game} instances' state transitions correctly as
	 * rounds are completed.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void gameRoundTransitions() throws JAXBException,
			XPathExpressionException {
		// Create the initial instances.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		Game game = new Game(player1);
		game.setMaxRounds(41);

		// Set the second player, which should start the game automatically.
		Account player2Account = new Account();
		Player player2 = new Player(player2Account);
		game.setPlayer2(player2);

		// Play the first round.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();

		// Verify that there's now a second round.
		Assert.assertEquals(State.STARTED, game.getState());
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
	}

	/**
	 * Tests a simple one-round game.
	 */
	@Test
	public void simpleGame() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game game = new Game(player1);
		game.setMaxRounds(1);
		game.setPlayer2(player2);

		// Play the first (and only) round.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(1, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player2, game.getWinner());
	}

	/**
	 * Tests a simple three-round game.
	 */
	@Test
	public void multipleRounds() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game game = new Game(player1);
		game.setMaxRounds(3);
		game.setPlayer2(player2);

		// Play the first round.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(1, game.getRounds().get(1).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the second round.
		game.submitThrow(1, player1, Throw.PAPER);
		game.submitThrow(1, player2, Throw.ROCK);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(2, game.getRounds().get(2).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_1_WON, game.getRounds().get(1)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the third round.
		game.submitThrow(2, player1, Throw.ROCK);
		game.submitThrow(2, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(2)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player2, game.getWinner());
	}

	/**
	 * Tests a game with a tied round.
	 */
	@Test
	public void multipleRoundsWithTie() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game game = new Game(player1);
		game.setMaxRounds(3);
		game.setPlayer2(player2);

		// Play the first round. Winner: Player 2.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(1, game.getRounds().get(1).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the second round. Winner: Player 1.
		game.submitThrow(1, player1, Throw.PAPER);
		game.submitThrow(1, player2, Throw.ROCK);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(2, game.getRounds().get(2).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_1_WON, game.getRounds().get(1)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the third round. Winner: (none/tie)
		game.submitThrow(2, player1, Throw.ROCK);
		game.submitThrow(2, player2, Throw.ROCK);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(4, game.getRounds().size());
		Assert.assertEquals(3, game.getRounds().get(3).getRoundIndex());
		Assert.assertEquals(Result.TIED, game.getRounds().get(2).getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the fourth round. Winner: Player 1.
		game.submitThrow(3, player1, Throw.SCISSORS);
		game.submitThrow(3, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(4, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_1_WON, game.getRounds().get(3)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player1, game.getWinner());
	}

	/**
	 * Tests a game where a player wins "early" (in less than the maximum amount
	 * of rounds). The game should end as soon as it becomes impossible for the
	 * other player to win, regardless of the number of rounds left.
	 */
	@Test
	public void earlyWinner() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game game = new Game(player1);
		game.setMaxRounds(3);
		game.setPlayer2(player2);

		// Play the first round. Winner: Player 2.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(1, game.getRounds().get(1).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the second round. Winner: Player 2.
		game.submitThrow(1, player1, Throw.ROCK);
		game.submitThrow(1, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player2, game.getWinner());
	}

	/**
	 * Tests that {@link Game#setMaxRounds(int)} throws a
	 * {@link GameConflictException} when it should.
	 */
	@Test(expected = GameConflictException.class)
	public void setMaxRounds_conflicts() {
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game game = new Game(player1);
		game.setPlayer2(player2);
		game.submitThrow(0, player1, Throw.ROCK);

		// Should blow up:
		game.setMaxRounds(1);
	}

	/**
	 * Tests that {@link Game#submitThrow(int, Player, Throw)} throws a
	 * {@link GameConflictException} when the wrong round is specified.
	 */
	@Test(expected = GameConflictException.class)
	public void submitThrow_conflictingRound() {
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game game = new Game(player1);
		game.setPlayer2(player2);

		// Should blow up:
		game.submitThrow(1, player1, Throw.ROCK);
	}

	/**
	 * Tests {@link Game#getLastThrowTimestamp()}.
	 */
	@Test
	public void getLastThrowTime() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game game = new Game(player1);
		game.setMaxRounds(3);
		game.setPlayer2(player2);

		// Play the first round. Winner: Player 2.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();

		// Play part of the second round.
		game.submitThrow(1, player1, Throw.ROCK);
		if (!game.isRoundPrepared())
			game.prepareRound();

		// Verify that the method works as expected.
		Assert.assertEquals(game.getRounds().get(1)
				.getThrowForPlayer1Timestamp(), game.getLastThrowTimestamp());
	}
}
