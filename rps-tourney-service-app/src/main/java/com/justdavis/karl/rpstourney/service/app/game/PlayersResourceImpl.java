package com.justdavis.karl.rpstourney.service.app.game;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.rpstourney.service.api.game.IPlayersResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * The JAX-RS server-side implementation of {@link IPlayersResource}.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PlayersResourceImpl implements IPlayersResource {
	private IPlayersDao playersDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public PlayersResourceImpl() {
	}

	/**
	 * @param playersDao
	 *            the injected {@link IPlayersDao} to use
	 */
	@Inject
	public void setPlayersDao(IPlayersDao playersDao) {
		// Sanity check: null IAccountsDao?
		if (playersDao == null)
			throw new IllegalArgumentException();

		this.playersDao = playersDao;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayersForBuiltInAis()
	 */
	@Override
	public Set<Player> getPlayersForBuiltInAis() {
		// First, figure out which AIs we're looking for.
		Set<BuiltInAi> activeAis = new HashSet<>();
		for (BuiltInAi ai : BuiltInAi.values()) {
			if (!ai.isRetired())
				activeAis.add(ai);
		}

		Set<Player> aiPlayers = playersDao.findPlayerForBuiltInAi(activeAis
				.toArray(new BuiltInAi[activeAis.size()]));
		if (aiPlayers.size() != activeAis.size())
			throw new IllegalStateException();
		return aiPlayers;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayerForBuiltInAi(com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi)
	 */
	@Override
	public Player getPlayerForBuiltInAi(BuiltInAi ai) {
		Set<Player> aiPlayers = playersDao.findPlayerForBuiltInAi(ai);
		if (aiPlayers.size() > 1)
			throw new IllegalStateException();
		else if (aiPlayers.isEmpty())
			throw new NotFoundException();
		return aiPlayers.iterator().next();
	}
}
