package com.justdavis.karl.rpstourney.service.app.game;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
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
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.service.app.auth.AuthenticationFilter;

/**
 * The web service implementation of {@link IGameSessionResource}, which is the
 * primary service for gameplay interactions.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GameSessionResourceImpl implements IGameSessionResource {
	private AccountSecurityContext securityContext;
	private IPlayersDao playersDao;
	private IGameSessionsDao gamesDao;

	/**
	 * This public, default/no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public GameSessionResourceImpl() {
	}

	/**
	 * @param securityContext
	 *            the {@link AccountSecurityContext} for the request that the
	 *            {@link GameSessionResourceImpl} was instantiated to handle
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
	 *            the injected {@link IGameSessionsDao} to use
	 */
	@Inject
	public void setGamesDao(IGameSessionsDao gamesDao) {
		if (gamesDao == null)
			throw new IllegalArgumentException();

		this.gamesDao = gamesDao;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#createGame()
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameSession createGame() {
		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);

		// Create the new game.
		GameSession game = new GameSession(userPlayer);
		gamesDao.save(game);

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#getGame(java.lang.String)
	 */
	@Override
	public GameSession getGame(String gameSessionId) {
		// Look up the specified game.
		GameSession game = gamesDao.findById(gameSessionId);
		if (game == null)
			throw new WebApplicationException("Game not found.",
					Status.NOT_FOUND);

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameSession setMaxRounds(String gameSessionId,
			int oldMaxRoundsValue, int newMaxRoundsValue) {
		// Look up the specified game.
		GameSession game = gamesDao.findById(gameSessionId);
		if (game == null)
			throw new WebApplicationException("Game not found.",
					Status.NOT_FOUND);

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

		/*
		 * Ensure that the old maxRoundsValue matches the value from the
		 * just-lookup-up game. This will prevent updates to the value from
		 * users that haven't yet seen the latest value.
		 */
		if (oldMaxRoundsValue != game.getMaxRounds())
			throw new IllegalStateException();

		try {
			game.setMaxRounds(newMaxRoundsValue);
		} catch (IllegalArgumentException e) {
			// Invalid rounds value.
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		} catch (IllegalStateException e) {
			// The game has already started.
			throw new WebApplicationException(e, Status.CONFLICT);
		}

		gamesDao.save(game);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#joinGame(java.lang.String)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameSession joinGame(String gameSessionId) {
		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);

		// Look up the specified game.
		GameSession game = gamesDao.findById(gameSessionId);
		if (game == null)
			throw new WebApplicationException("Game not found.",
					Status.NOT_FOUND);

		try {
			game.setPlayer2(userPlayer);
		} catch (IllegalArgumentException e) {
			// Trying to set the same user as both players.
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		} catch (IllegalStateException e) {
			// The game has already started.
			throw new WebApplicationException(e, Status.CONFLICT);
		}

		gamesDao.save(game);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#prepareRound(java.lang.String)
	 */
	@Transactional
	@Override
	public GameSession prepareRound(String gameSessionId) {
		/*
		 * Note: This method is intentionally not marked with @RolesAllowed, as
		 * it doesn't really matter who calls it.
		 */

		// Look up the specified game.
		GameSession game = gamesDao.findById(gameSessionId);
		if (game == null)
			throw new WebApplicationException("Game not found.",
					Status.NOT_FOUND);

		// Prepare the round, if needed.
		if (!game.isRoundPrepared()) {
			game.prepareRound();
		}

		gamesDao.save(game);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameSession submitThrow(String gameSessionId, int roundIndex,
			Throw throwToPlay) {
		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao
				.findOrCreatePlayerForAccount(userAccount);

		// Look up the specified game.
		GameSession game = gamesDao.findById(gameSessionId);
		if (game == null)
			throw new WebApplicationException("Game not found.",
					Status.NOT_FOUND);

		try {
			game.submitThrow(roundIndex, userPlayer, throwToPlay);
		} catch (IllegalArgumentException e) {
			// Trying to set the same user as both players.
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		} catch (IllegalStateException e) {
			// The game has already started.
			throw new WebApplicationException(e, Status.CONFLICT);
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
