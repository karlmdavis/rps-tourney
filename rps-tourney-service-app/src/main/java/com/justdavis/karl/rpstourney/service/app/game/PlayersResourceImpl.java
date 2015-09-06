package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

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
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayersForBuiltInAis(java.util.List)
	 */
	@Override
	public Set<Player> getPlayersForBuiltInAis(List<BuiltInAi> ais) {
		Set<Player> aiPlayers = playersDao.findPlayerForBuiltInAi(ais
				.toArray(new BuiltInAi[ais.size()]));
		if (aiPlayers.size() != ais.size())
			throw new IllegalStateException(String.format(
					"Active AIs are %s, but retrieved AIs are %s.", ais,
					aiPlayers));
		return aiPlayers;
	}
}
