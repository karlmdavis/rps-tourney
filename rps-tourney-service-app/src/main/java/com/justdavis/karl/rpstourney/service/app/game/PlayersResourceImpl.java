package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.game.IPlayersResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.service.app.auth.AuthenticationFilter;

/**
 * The JAX-RS server-side implementation of {@link IPlayersResource}.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PlayersResourceImpl implements IPlayersResource {
	private AccountSecurityContext securityContext;
	private IPlayersDao playersDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public PlayersResourceImpl() {
	}

	/**
	 * @param securityContext
	 *            the {@link SecurityContext} for the request that the
	 *            {@link PlayersResourceImpl} was instantiated to handle
	 */
	@Context
	public void setSecurityContext(SecurityContext securityContext) {
		// Sanity check: null AccountSecurityContext?
		if (securityContext == null)
			throw new IllegalArgumentException();

		this.securityContext = (AccountSecurityContext) securityContext;
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
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#findOrCreatePlayer()
	 */
	@Override
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	public Player findOrCreatePlayer() {
		return playersDao.findOrCreatePlayerForAccount(getUserAccount());
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayersForBuiltInAis(java.util.List)
	 */
	@Override
	public Set<Player> getPlayersForBuiltInAis(List<BuiltInAi> ais) {
		Set<Player> aiPlayers = playersDao.findPlayerForBuiltInAi(ais.toArray(new BuiltInAi[ais.size()]));
		if (aiPlayers.size() != ais.size())
			throw new IllegalStateException(
					String.format("Active AIs are %s, but retrieved AIs are %s.", ais, aiPlayers));
		return aiPlayers;
	}

	/**
	 * This method should only be used on web service requests annotated with
	 * <code>@RolesAllowed({ SecurityRole.ID_USERS })</code>, as it assumes that
	 * the request currently being processed is authenticated.
	 * 
	 * @return the requestor's Account from {@link #securityContext}, which will
	 *         have been set by the {@link AuthenticationFilter}
	 */
	private Account getUserAccount() {
		Account userAccount = securityContext.getUserPrincipal();
		if (userAccount == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");

		return userAccount;
	}
}
