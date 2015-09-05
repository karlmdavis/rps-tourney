package com.justdavis.karl.rpstourney.service.api.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.NotFoundException;

import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * A mock {@link IPlayersResource} implementation for use in tests.
 */
public class MockPlayersClient implements IPlayersResource {
	private final Player[] players;

	/**
	 * Constructs a new {@link MockPlayersClient} instance.
	 * 
	 * @param players
	 *            the {@link Player} instances that will be returned by this
	 *            {@link IGameResource}
	 */
	public MockPlayersClient(Player... players) {
		this.players = players;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayersForBuiltInAis()
	 */
	@Override
	public Set<Player> getPlayersForBuiltInAis() {
		return new HashSet<>(Arrays.asList(players));
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayerForBuiltInAi(com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi)
	 */
	@Override
	public Player getPlayerForBuiltInAi(BuiltInAi ai) {
		if (ai == null)
			throw new IllegalArgumentException();

		for (Player player : players)
			if (ai.equals(player.getBuiltInAi()))
				return player;

		throw new NotFoundException();
	}
}