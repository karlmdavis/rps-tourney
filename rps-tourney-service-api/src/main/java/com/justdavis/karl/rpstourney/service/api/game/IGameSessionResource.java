package com.justdavis.karl.rpstourney.service.api.game;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.justdavis.karl.rpstourney.service.api.game.GameSession.State;

/**
 * This service allows users to create, retrieve, and play games. Please note
 * that all {@link GameSession} instances returned by this service should be
 * treated as read-only: remote clients wishing to modify game state may only do
 * so through the methods in this interface.
 */
@Path(IGameSessionResource.SERVICE_PATH)
public interface IGameSessionResource {
	/**
	 * The base {@link Path} that this service will be hosted at.
	 */
	public static final String SERVICE_PATH = "/game/session";

	/**
	 * The {@link Path} for the {@link #createGame()} method.
	 */
	public static final String SERVICE_PATH_NEW = "/new";

	/**
	 * The {@link Path} variable for methods that take in
	 * {@link GameSession#getId()} as a {@link PathParam}.
	 */
	public static final String SERVICE_PATH_GAME_ID = "/{gameSessionId}";

	/**
	 * The {@link Path} for the {@link #setMaxRounds(String, int)} method.
	 */
	public static final String SERVICE_PATH_MAX_ROUNDS = "/maxRounds";

	/**
	 * The {@link Path} for the {@link #joinGame(String)} method.
	 */
	public static final String SERVICE_PATH_JOIN = "/join";

	/**
	 * The {@link Path} for the {@link #prepareRound(String)} method.
	 */
	public static final String SERVICE_PATH_PREPARE = "/prepareRound";

	/**
	 * The {@link Path} for the {@link #submitThrow(String)} method.
	 */
	public static final String SERVICE_PATH_THROW = "/throw";

	/**
	 * <p>
	 * Creates a new {@link GameSession}, with the first player set as the user
	 * calling this method, and leaving the identity of the second player to be
	 * set later.
	 * </p>
	 * 
	 * @return the new {@link GameSession} instance
	 * @see GameSession#GameSession(Player)
	 */
	@POST
	@Path(IGameSessionResource.SERVICE_PATH_NEW)
	@Produces(MediaType.TEXT_XML)
	GameSession createGame();

	/**
	 * <p>
	 * Returns the specified {@link GameSession}.
	 * </p>
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} value of the
	 *            {@link GameSession} to return
	 * @return the matching {@link GameSession} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching
	 *             {@link GameSession} can be found.
	 */
	@GET
	@Path(IGameSessionResource.SERVICE_PATH_GAME_ID)
	@Produces(MediaType.TEXT_XML)
	GameSession getGame(@PathParam("gameSessionId") String gameSessionId);

	/**
	 * <p>
	 * A web service proxy for the {@link GameSession#setMaxRounds(int)} method.
	 * </p>
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} value of the
	 *            {@link GameSession} to modify
	 * @param oldMaxRoundsValue
	 *            the previous value of {@link GameSession#getMaxRounds()} (used
	 *            to help prevent synchronization issues)
	 * @param newMaxRoundsValue
	 *            the value to use for {@link GameSession#getMaxRounds()}
	 * @return the modified {@link GameSession} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching
	 *             {@link GameSession} can be found.
	 * @throws GameConflictException
	 *             <p>
	 *             An {@link GameConflictException} will be thrown in the
	 *             following cases:
	 *             </p>
	 *             <ul>
	 *             <li>If {@link GameSession#getState()} is not
	 *             {@link State#WAITING_FOR_PLAYER} or
	 *             {@link State#WAITING_FOR_FIRST_THROW}.</li>
	 *             <li>If the <code>oldMaxRoundsValue</code> does not match the
	 *             actual, current value of {@link GameSession#getMaxRounds()}.</li>
	 *             </ul>
	 * @see GameSession#setMaxRounds(int)
	 */
	@POST
	@Path(IGameSessionResource.SERVICE_PATH_GAME_ID
			+ IGameSessionResource.SERVICE_PATH_MAX_ROUNDS)
	@Produces(MediaType.TEXT_XML)
	GameSession setMaxRounds(@PathParam("gameSessionId") String gameSessionId,
			@FormParam("oldMaxRoundsValue") int oldMaxRoundsValue,
			@FormParam("newMaxRoundsValue") int newMaxRoundsValue);

	/**
	 * <p>
	 * Joins the user who calls this method to the specified {@link GameSession}
	 * as {@link GameSession#getPlayer2()}, if that field is still
	 * <code>null</code>.
	 * </p>
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} value of the
	 *            {@link GameSession} to modify
	 * @return the modified {@link GameSession} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching
	 *             {@link GameSession} can be found.
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if
	 *             {@link GameSession#getState()} is not
	 *             {@link State#WAITING_FOR_PLAYER}.
	 * @see GameSession#setPlayer2(Player)
	 */
	@POST
	@Path(IGameSessionResource.SERVICE_PATH_GAME_ID
			+ IGameSessionResource.SERVICE_PATH_JOIN)
	@Produces(MediaType.TEXT_XML)
	GameSession joinGame(@PathParam("gameSessionId") String gameSessionId);

	/**
	 * <p>
	 * A web service proxy for several {@link GameSession} methods, analogous to
	 * the following code snippet:
	 * </p>
	 * 
	 * <pre>
	 * GameSession game = ...;
	 * 
	 * if(!game.isRoundPrepared()) {
	 *   game.prepareRound();
	 * }
	 * 
	 * return game;
	 * </pre>
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} value of the
	 *            {@link GameSession} to modify
	 * @return the (possibly updated) {@link GameSession} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching
	 *             {@link GameSession} can be found.
	 * @see GameSession#prepareRound()
	 */
	@POST
	@Path(IGameSessionResource.SERVICE_PATH_GAME_ID
			+ IGameSessionResource.SERVICE_PATH_PREPARE)
	@Produces(MediaType.TEXT_XML)
	GameSession prepareRound(@PathParam("gameSessionId") String gameSessionId);

	/**
	 * <p>
	 * A web service proxy for the
	 * {@link GameSession#submitThrow(int, Player, Throw)} method, where the
	 * {@link Player} passed to that call is the user who makes this web service
	 * call.
	 * </p>
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} value of the
	 *            {@link GameSession} to modify
	 * @param roundIndex
	 *            the {@link GameRound#getRoundIndex()} of the current round
	 *            (used to verify that gameplay is correctly synchronized)
	 * @param throwToPlay
	 *            the {@link Throw} to submit for the {@link Player}
	 * @return the modified {@link GameSession} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching
	 *             {@link GameSession} can be found.
	 * @throws GameConflictException
	 *             <p>
	 *             An {@link GameConflictException} will be thrown in the
	 *             following cases:
	 *             </p>
	 *             <ul>
	 *             <li>If {@link GameSession#getState()} is not
	 *             {@link State#STARTED}.</li>
	 *             <li>If the specified <code>roundIndex</code> is not the same
	 *             as {@link GameRound#getRoundIndex()} in the last/current
	 *             round.</li>
	 *             <li>If the {@link Player} has already submitted a
	 *             {@link Throw} for the {@link GameRound}.</li>
	 *             </ul>
	 * @see GameSession#submitThrow(int, Player, Throw)
	 */
	@POST
	@Path(IGameSessionResource.SERVICE_PATH_GAME_ID
			+ IGameSessionResource.SERVICE_PATH_THROW)
	@Produces(MediaType.TEXT_XML)
	GameSession submitThrow(@PathParam("gameSessionId") String gameSessionId,
			@FormParam("roundIndex") int roundIndex,
			@FormParam("throwToPlay") Throw throwToPlay);
}