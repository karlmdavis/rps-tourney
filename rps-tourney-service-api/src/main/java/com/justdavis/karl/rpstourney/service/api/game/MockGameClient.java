package com.justdavis.karl.rpstourney.service.api.game;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock {@link IGameSessionResource} implementation for use in tests.
 */
public final class MockGameClient implements IGameSessionResource {
	private final GameSession game;

	/**
	 * Constructs a new {@link MockGameClient} instance.
	 * 
	 * @param game
	 *            the shared, mutable {@link GameSession} instance that will be
	 *            returned by every single method here
	 */
	public MockGameClient(GameSession game) {
		this.game = game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#createGame()
	 */
	@Override
	public GameSession createGame() {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#getGamesForPlayer()
	 */
	@Override
	public List<GameSession> getGamesForPlayer() {
		List<GameSession> games = new ArrayList<>();
		games.add(game);
		return games;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#getGame(java.lang.String)
	 */
	@Override
	public GameSession getGame(String gameSessionId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public GameSession setMaxRounds(String gameSessionId,
			int oldMaxRoundsValue, int newMaxRoundsValue) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#joinGame(java.lang.String)
	 */
	@Override
	public GameSession joinGame(String gameSessionId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#prepareRound(java.lang.String)
	 */
	@Override
	public GameSession prepareRound(String gameSessionId) {
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@Override
	public GameSession submitThrow(String gameSessionId, int roundIndex,
			Throw throwToPlay) {
		return game;
	}
}