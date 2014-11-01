package com.justdavis.karl.rpstourney.service.api.game;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock {@link IGameResource} implementation for use in tests.
 */
public final class MockGameClient implements IGameResource {
	private final Game game;

	/**
	 * Constructs a new {@link MockGameClient} instance.
	 * 
	 * @param game
	 *            the shared, mutable {@link Game} instance that will be
	 *            returned by every single method here
	 */
	public MockGameClient(Game game) {
		this.game = game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#createGame()
	 */
	@Override
	public Game createGame() {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGamesForPlayer()
	 */
	@Override
	public List<Game> getGamesForPlayer() {
		List<Game> games = new ArrayList<>();
		games.add(game);
		return games;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGame(java.lang.String)
	 */
	@Override
	public Game getGame(String gameId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public Game setMaxRounds(String gameId, int oldMaxRoundsValue,
			int newMaxRoundsValue) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#joinGame(java.lang.String)
	 */
	@Override
	public Game joinGame(String gameId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#prepareRound(java.lang.String)
	 */
	@Override
	public Game prepareRound(String gameId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@Override
	public Game submitThrow(String gameId, int roundIndex, Throw throwToPlay) {
		return game;
	}
}