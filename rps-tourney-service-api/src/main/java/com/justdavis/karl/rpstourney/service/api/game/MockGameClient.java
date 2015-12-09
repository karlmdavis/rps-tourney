package com.justdavis.karl.rpstourney.service.api.game;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.NotFoundException;

/**
 * A mock {@link IGameResource} implementation for use in tests.
 */
public class MockGameClient implements IGameResource {
	private final GameView[] games;

	/**
	 * Constructs a new {@link MockGameClient} instance.
	 * 
	 * @param games
	 *            the {@link GameView} instances that will be returned by this
	 *            {@link IGameResource}
	 */
	public MockGameClient(GameView... games) {
		this.games = games;
	}

	/**
	 * Constructs a new {@link MockGameClient} instance.
	 * 
	 * @param games
	 *            the {@link Game} instances that will be wrapped in
	 *            {@link GameView}s and returned by this {@link IGameResource}
	 */
	public MockGameClient(Game... games) {
		this(wrapInGameViews(games));
	}

	/**
	 * @param games
	 *            the {@link Game}s to wrap
	 * @return an array of {@link GameView}s wrapping each of the specified
	 *         {@link Game}s
	 */
	private static GameView[] wrapInGameViews(Game[] games) {
		if (games == null)
			return new GameView[] {};

		GameView[] wrappedGames = new GameView[games.length];
		for (int i = 0; i < games.length; i++) {
			if (games[i] == null)
				throw new IllegalArgumentException();

			wrappedGames[i] = new GameView(games[i], null);
		}

		return wrappedGames;
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
		return Arrays.asList(games);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGame(java.lang.String)
	 */
	@Override
	public GameView getGame(String gameId) {
		for (GameView game : games)
			if (game.getId().equals(gameId))
				return game;

		throw new NotFoundException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public GameView setMaxRounds(String gameId, int oldMaxRoundsValue,
			int newMaxRoundsValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#inviteOpponent(java.lang.String,
	 *      long)
	 */
	@Override
	public void inviteOpponent(String gameId, long playerId)
			throws NotFoundException, GameConflictException {
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
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@Override
	public GameView submitThrow(String gameId, int roundIndex, Throw throwToPlay) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#deleteGame(java.lang.String)
	 */
	@Override
	public void deleteGame(String gameId) throws NotFoundException {
		throw new UnsupportedOperationException();
	}
}