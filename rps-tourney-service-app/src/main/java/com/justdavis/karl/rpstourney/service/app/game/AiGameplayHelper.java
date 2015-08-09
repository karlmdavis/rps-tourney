package com.justdavis.karl.rpstourney.service.app.game;

import org.springframework.stereotype.Component;

import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.State;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * <p>
 * This class contains helper utilities to advance play in {@link Game}s with AI
 * {@link Player}s.
 * </p>
 * <p>
 * Design Note: This has been pulled out into a separate class because the
 * implementation is likely to change wildly over time. Separating it will make
 * tracking those changes easier.
 * </p>
 */
@Component
public class AiGameplayHelper {
	/**
	 * Evaluates the specified {@link Game} to see if it has an AI
	 * {@link Player} or {@link Player}s that can currently make a move. If so,
	 * it will invoke the AI player(s) logic to do so, and then loop until it
	 * fails to make a move for either {@link Player}. However, if the specified
	 * {@link Game} has no AI players or if any AI players in it cannot
	 * currently make a move, this method will do nothing.
	 * 
	 * @param game
	 *            the {@link Game} to (possibly) manipulate
	 */
	public void advanceGameForAiPlayers(Game game) {
		if (game == null)
			throw new IllegalArgumentException();
		if (game.getState() == State.WAITING_FOR_PLAYER)
			throw new IllegalArgumentException();
		if (game.getState() == State.FINISHED)
			throw new IllegalArgumentException();

		/*
		 * Loop forever, until the AI was unable to make a move for either
		 * player in the game. Will ignore non-AI players, so if neither player
		 * is an AI, this will do nothing and not loop.
		 */
		boolean player1AiMadeMove = false;
		boolean player2AiMadeMove = false;
		do {
			if (!game.getPlayer1().isHuman())
				player1AiMadeMove = advanceGame(game, PlayerRole.PLAYER_1);

			if (!game.getPlayer2().isHuman())
				player2AiMadeMove = advanceGame(game, PlayerRole.PLAYER_2);
		} while (player1AiMadeMove || player2AiMadeMove);
	}

	/**
	 * Evaluates the specified {@link Game} to see if the specified player is an
	 * AI and if it can currently make a move. If so, it will invoke that AI
	 * player's logic to do so, and then return.
	 * 
	 * @param game
	 *            the {@link Game} to (possibly) manipulate
	 * @param playerRole
	 *            the {@link PlayerRole} of the {@link Player} to (possibly)
	 *            make a move for
	 * @return <code>true</code> if a move was made, <code>false</code> if not
	 */
	private boolean advanceGame(Game game, PlayerRole playerRole) {
		// Grab the specified Player.
		Player player = playerRole == PlayerRole.PLAYER_1 ? game.getPlayer1()
				: game.getPlayer2();

		// Sanity check: make sure the Player is a legit AI.
		if (player == null)
			throw new IllegalArgumentException();
		if (player.isHuman())
			throw new IllegalArgumentException();

		// Is it time for the AI player to make a throw?
		int currentRoundIndex = game.getRounds().size() - 1;
		GameRound currentRound = game.getRounds().get(currentRoundIndex);
		if (playerRole == PlayerRole.PLAYER_1
				&& currentRound.getThrowForPlayer1() != null)
			return false;
		if (playerRole == PlayerRole.PLAYER_2
				&& currentRound.getThrowForPlayer2() != null)
			return false;

		// Calculate the Throw that the AI would like to make.
		GameView gameView = new GameView(game, /* FIXME */null);
		Throw aiThrow = player.getBuiltInAi().getPositronicBrain()
				.calculateNextThrow(gameView, playerRole);

		// Submit the Throw.
		game.submitThrow(currentRoundIndex, player, aiThrow);
		return true;
	}
}
