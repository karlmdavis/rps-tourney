package com.justdavis.karl.rpstourney.service.app.game;

import java.util.Collections;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.service.app.auth.AuthenticationFilter;

/**
 * The web service implementation of {@link IGameResource}, which is the primary
 * service for gameplay interactions.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GameResourceImpl implements IGameResource {
	private AccountSecurityContext securityContext;
	private IPlayersDao playersDao;
	private IGamesDao gamesDao;

	/**
	 * This public, default/no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public GameResourceImpl() {
	}

	/**
	 * @param securityContext
	 *            the {@link AccountSecurityContext} for the request that the
	 *            {@link GameResourceImpl} was instantiated to handle
	 */
	@Context
	public void setAccountSecurityContext(AccountSecurityContext securityContext) {
		if (securityContext == null)
			throw new IllegalArgumentException();

		this.securityContext = securityContext;
	}

	/**
	 * @param playersDao
	 *            the injected {@link IPlayersDao} to use
	 */
	@Inject
	public void setPlayersDao(IPlayersDao playersDao) {
		if (playersDao == null)
			throw new IllegalArgumentException();

		this.playersDao = playersDao;
	}

	/**
	 * @param gamesDao
	 *            the injected {@link IGamesDao} to use
	 */
	@Inject
	public void setGamesDao(IGamesDao gamesDao) {
		if (gamesDao == null)
			throw new IllegalArgumentException();

		this.gamesDao = gamesDao;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#createGame()
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public Game createGame() {
		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);

		// Create the new game.
		Game game = new Game(userPlayer);
		gamesDao.save(game);

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGamesForPlayer()
	 */
	@Override
	public List<Game> getGamesForPlayer() {
		// Return an empty Set for unauthenticated users.
		if (securityContext.getUserPrincipal() == null) {
			return Collections.emptyList();
		}

		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);

		// Get the games for that Player.
		List<Game> games = gamesDao.getGamesForPlayer(userPlayer);
		return games;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGame(java.lang.String)
	 */
	@Override
	public Game getGame(String gameId) {
		// Look up the specified game.
		Game game = gamesDao.findById(gameId);
		if (game == null)
			throw new NotFoundException("Game not found: " + gameId);

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public Game setMaxRounds(String gameId, int oldMaxRoundsValue,
			int newMaxRoundsValue) {
		Game game = getGame(gameId);

		/*
		 * Check to make sure that the requesting user is one of the two
		 * players.
		 */
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);
		if (!userPlayer.equals(game.getPlayer1())
				&& !userPlayer.equals(game.getPlayer2()))
			throw new IllegalArgumentException();

		try {
			game = gamesDao.setMaxRounds(gameId, oldMaxRoundsValue,
					newMaxRoundsValue);
		} catch (IllegalArgumentException e) {
			// Invalid rounds value.
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#joinGame(java.lang.String)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public Game joinGame(String gameId) {
		Game game = getGame(gameId);

		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);

		try {
			game.setPlayer2(userPlayer);
		} catch (IllegalArgumentException e) {
			// Trying to set the same user as both players.
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}

		gamesDao.save(game);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#prepareRound(java.lang.String)
	 */
	@Transactional
	@Override
	public Game prepareRound(String gameId) {
		/*
		 * Note: This method is intentionally not marked with @RolesAllowed, as
		 * it doesn't really matter who calls it.
		 */

		Game game = getGame(gameId);

		// Prepare the round, if needed.
		if (!game.isRoundPrepared()) {
			game.prepareRound();
		}

		gamesDao.save(game);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public Game submitThrow(String gameId, int roundIndex, Throw throwToPlay) {
		Game game = getGame(gameId);

		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);

		try {
			game.submitThrow(roundIndex, userPlayer, throwToPlay);
		} catch (IllegalArgumentException e) {
			// Trying to set the same user as both players.
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}

		gamesDao.save(game);
		return game;
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
