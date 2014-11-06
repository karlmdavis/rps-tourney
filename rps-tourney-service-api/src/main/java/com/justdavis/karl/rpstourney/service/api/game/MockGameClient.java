package com.justdavis.karl.rpstourney.service.api.game;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock {@link IGameResource} implementation for use in tests.
 */
public final class MockGameClient implements IGameResource {
	private final GameView game;

	/**
	 * Constructs a new {@link MockGameClient} instance.
	 * 
	 * @param game
	 *            the {@link GameView} instance that will be returned by every
	 *            single method here
	 */
	public MockGameClient(GameView game) {
		this.game = game;
	}

	/**
	 * Constructs a new {@link MockGameClient} instance.
	 * 
	 * @param game
	 *            the {@link Game} instance that will be wrapped in a GameView
	 *            and returned by every single method here
	 */
	public MockGameClient(Game game) {
		this(new GameView(game, null));
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#createGame()
	 */
	@Override
	public GameView createGame() {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGamesForPlayer()
	 */
	@Override
	public List<GameView> getGamesForPlayer() {
		List<GameView> games = new ArrayList<>();
		games.add(game);
		return games;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGame(java.lang.String)
	 */
	@Override
	public GameView getGame(String gameId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public GameView setMaxRounds(String gameId, int oldMaxRoundsValue,
			int newMaxRoundsValue) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#joinGame(java.lang.String)
	 */
	@Override
	public GameView joinGame(String gameId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#prepareRound(java.lang.String)
	 */
	@Override
	public GameView prepareRound(String gameId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@Override
	public GameView submitThrow(String gameId, int roundIndex, Throw throwToPlay) {
		return game;
	}
}