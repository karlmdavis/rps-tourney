package com.justdavis.karl.rpstourney.app.console.localservice;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * <p>
 * This {@link IGameResource} implementation only supports local games. It's
 * really just used to enable this application to play local games using the
 * same interface as remote games must be played with.
 * </p>
 * <p>
 * Each {@link LocalGameClient} instance is associated with a single new
 * {@link Game} instance; a new {@link LocalGameClient} is needed for each game
 * to be played.
 * </p>
 */
public final class LocalGameClient implements IGameResource {
	private final Game game;
	private final Player localPlayer;

	/**
	 * Constructs a new {@link LocalGameClient} instance.
	 * 
	 * @param game
	 *            the {@link Game} to be played/manipulated
	 * @param localPlayer
	 *            the local current user/{@link Player}
	 */
	public LocalGameClient(Game game, Player localPlayer) {
		this.game = game;
		this.localPlayer = localPlayer;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#createGame()
	 */
	@Override
	public GameView createGame() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGamesForPlayer()
	 */
	@Override
	public List<GameView> getGamesForPlayer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGame(java.lang.String)
	 */
	@Override
	public GameView getGame(String gameId) {
		// The LocalGameClient only supports a single, local Game instance.
		if (gameId == null || !gameId.equals(game.getId()))
			throw new IllegalArgumentException();

		return new GameView(game, localPlayer);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public GameView setMaxRounds(String gameId, int oldMaxRoundsValue,
			int newMaxRoundsValue) {
		game.setMaxRounds(newMaxRoundsValue);
		return getGame(gameId);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#inviteOpponent(java.lang.String,
	 *      long)
	 */
	@Override
	public void inviteOpponent(String gameId, long playerId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#joinGame(java.lang.String)
	 */
	@Override
	public GameView joinGame(String gameId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#prepareRound(java.lang.String)
	 */
	@Override
	public GameView prepareRound(String gameId) {
		game.prepareRound();
		return getGame(gameId);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@Override
	public GameView submitThrow(String gameId, int roundIndex, Throw throwToPlay) {
		game.submitThrow(roundIndex, localPlayer, throwToPlay);

		// Calculate the Throw for the Player 2 AI.
		GameView gameView = new GameView(game, game.getPlayer2());
		Throw aiThrow = game.getPlayer2().getBuiltInAi().getPositronicBrain()
				.calculateNextThrow(gameView, PlayerRole.PLAYER_2);

		// Submit the Throw.
		game.submitThrow(roundIndex, game.getPlayer2(), aiThrow);

		return getGame(gameId);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#deleteGame(java.lang.String)
	 */
	@Override
	public void deleteGame(String gameId) throws NotFoundException {
		throw new UnsupportedOperationException();
	}
}
