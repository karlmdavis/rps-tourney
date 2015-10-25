package com.justdavis.karl.rpstourney.app.console.localservice;

import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;

/**
 * Encapsulates the model and service objects that will be used to play a game.
 */
public final class GameBundle {
	private final IGameResource gameClient;
	private final String gameId;

	/**
	 * Constructs a new {@link GameBundle} instance.
	 * 
	 * @param gameClient
	 *            the value to use for {@link #getGameClient()}
	 * @param gameId
	 *            the value to use for {@link #getGameId()}
	 */
	public GameBundle(IGameResource gameClient, String gameId) {
		this.gameClient = gameClient;
		this.gameId = gameId;
	}

	/**
	 * @return the {@link IGameResource} that will be used to interact with the
	 *         game being played
	 */
	public IGameResource getGameClient() {
		return gameClient;
	}

	/**
	 * @return the {@link Game#getId()} value of the game being played
	 */
	public String getGameId() {
		return gameId;
	}
}
