package com.justdavis.karl.rpstourney.service.api.game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayersForBuiltInAis(java.util.List)
	 */
	@Override
	public Set<Player> getPlayersForBuiltInAis(List<BuiltInAi> ais) {
		if (ais == null)
			throw new IllegalArgumentException();

		Set<Player> results = new HashSet<>();
		for (Player player : players)
			if (ais.contains(player.getBuiltInAi()))
				results.add(player);

		return results;
	}
}