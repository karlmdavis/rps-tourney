package com.justdavis.karl.rpstourney.service.app.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.State;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(GameResourceImpl.class);

	private AccountSecurityContext securityContext;
	private IPlayersDao playersDao;
	private IGamesDao gamesDao;
	private AiGameplayHelper aiHelper;

	/**
	 * This public, default/no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public GameResourceImpl() {
	}

	/**
	 * @param securityContext
	 *            the {@link SecurityContext} for the request that the
	 *            {@link GameResourceImpl} was instantiated to handle
	 */
	@Context
	public void setSecurityContext(SecurityContext securityContext) {
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
	 * @param aiHelper
	 *            the injected {@link AiGameplayHelper} to use
	 */
	@Inject
	public void setAiGameplayHelper(AiGameplayHelper aiHelper) {
		if (aiHelper == null)
			throw new IllegalArgumentException();

		this.aiHelper = aiHelper;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#createGame()
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameView createGame() {
		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao.findOrCreatePlayerForAccount(userAccount);

		// Create the new game.
		Game game = new Game(userPlayer);
		gamesDao.save(game);

		// Create and return a GameView for the game.
		GameView gameView = new GameView(game, userPlayer);
		return gameView;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGamesForPlayer()
	 */
	@Override
	public List<GameView> getGamesForPlayer() {
		// Return an empty Set for unauthenticated users.
		if (securityContext.getUserPrincipal() == null) {
			return Collections.emptyList();
		}

		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao.findPlayerForAccount(userAccount);

		// Get the games for that Player.
		List<Game> games;
		if (userPlayer != null)
			games = gamesDao.getGamesForPlayer(userPlayer);
		else
			games = Collections.emptyList();

		// Create and return the GameViews for the Games.
		List<GameView> gameViews = new ArrayList<GameView>(games.size());
		for (Game game : games)
			gameViews.add(new GameView(game, userPlayer));
		return gameViews;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGame(java.lang.String)
	 */
	@Override
	public GameView getGame(String gameId) {
		// Look up the specified game.
		Game game = gamesDao.findById(gameId);
		if (game == null)
			throw new NotFoundException("Game not found: " + gameId);

		// Create and return a GameView for the game.
		Account userAccount = securityContext.getUserPrincipal();
		Player userPlayer = userAccount != null ? playersDao.findPlayerForAccount(userAccount) : null;
		GameView gameView = new GameView(game, userPlayer);
		return gameView;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameView setMaxRounds(String gameId, int oldMaxRoundsValue, int newMaxRoundsValue) {
		Game game = getRawGame(gameId);

		/*
		 * Check to make sure that the requesting user is one of the two
		 * players.
		 */
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao.findOrCreatePlayerForAccount(userAccount);
		if (!userPlayer.equals(game.getPlayer1()) && !userPlayer.equals(game.getPlayer2()))
			throw new IllegalArgumentException();

		game = gamesDao.setMaxRounds(gameId, oldMaxRoundsValue, newMaxRoundsValue);

		// Create and return a GameView for the game.
		GameView gameView = new GameView(game, userPlayer);
		return gameView;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#inviteOpponent(java.lang.String,
	 *      long)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public void inviteOpponent(String gameId, long playerId) throws NotFoundException, GameConflictException {
		Game game = getRawGame(gameId);

		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao.findOrCreatePlayerForAccount(userAccount);

		// Verify that the current user is Player 1.
		if (!game.getPlayer1().equals(userPlayer))
			throw new ForbiddenException();

		// Verify that the invited player is a BuiltInAi.
		Player invitedPlayer = playersDao.getPlayer(playerId);
		if (invitedPlayer.getBuiltInAi() == null)
			throw new ForbiddenException();

		// Join the player to the game.
		game.setPlayer2(invitedPlayer);

		// Advance the game for any AI players.
		aiHelper.advanceGameForAiPlayers(game);

		// Save the resulting game state.
		gamesDao.save(game);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#joinGame(java.lang.String)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameView joinGame(String gameId) {
		Game game = getRawGame(gameId);

		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao.findOrCreatePlayerForAccount(userAccount);

		try {
			game.setPlayer2(userPlayer);
		} catch (IllegalArgumentException e) {
			// Trying to set the same user as both players.
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}

		// Advance the game for any AI players.
		aiHelper.advanceGameForAiPlayers(game);

		// Save the resulting game state.
		gamesDao.save(game);

		// Create and return a GameView for the game.
		GameView gameView = new GameView(game, userPlayer);
		return gameView;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#prepareRound(java.lang.String)
	 */
	@Transactional
	@Override
	public GameView prepareRound(String gameId) {
		/*
		 * Note: This method is intentionally not marked with @RolesAllowed, as
		 * it doesn't really matter who calls it.
		 */

		LOGGER.trace("Prepare round start.");
		Game game = getRawGame(gameId);

		// Prepare the round, if needed.
		if (!game.isRoundPrepared()) {
			game.prepareRound();
		}

		// TODO does AI need to be called here, too?

		gamesDao.save(game);

		// Create and return a GameView for the game.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao.findPlayerForAccount(userAccount);
		GameView gameView = new GameView(game, userPlayer);
		LOGGER.trace("Prepare round end.");
		return gameView;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	@Override
	public GameView submitThrow(String gameId, int roundIndex, Throw throwToPlay) {
		Game game = getRawGame(gameId);

		// Determine the current user/player.
		Account userAccount = getUserAccount();
		Player userPlayer = playersDao.findOrCreatePlayerForAccount(userAccount);

		// Submit the Throw to the game.
		game.submitThrow(roundIndex, userPlayer, throwToPlay);

		// Advance the game for any AI players.
		if (game.getState() != State.FINISHED)
			aiHelper.advanceGameForAiPlayers(game);

		// Save the resulting game state.
		gamesDao.save(game);

		// Create and return a GameView for the game.
		GameView gameView = new GameView(game, userPlayer);
		return gameView;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#deleteGame(java.lang.String)
	 */
	@RolesAllowed({ SecurityRole.ID_ADMINS })
	@Transactional
	@Override
	public void deleteGame(String gameId) throws NotFoundException {
		Game game = getRawGame(gameId);
		if (game == null)
			throw new NotFoundException("Game not found: " + gameId);

		gamesDao.delete(game.getId());
	}

	/**
	 * @param gameId
	 *            the {@link Game#getId()} value to match
	 * @return the specified {@link Game} instance (not wrapped in a
	 *         {@link GameView})
	 */
	private Game getRawGame(String gameId) {
		// Look up the specified game.
		Game game = gamesDao.findById(gameId);
		if (game == null)
			throw new NotFoundException("Game not found: " + gameId);

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
