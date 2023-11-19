package com.justdavis.karl.rpstourney.service.api.game;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * This service allows users to create, retrieve, and play games. Please note that all {@link Game} instances returned
 * by this service should be treated as read-only: remote clients wishing to modify game state may only do so through
 * the methods in this interface.
 */
@Path(IGameResource.SERVICE_PATH)
public interface IGameResource {
	/**
	 * The base {@link Path} that this service will be hosted at.
	 */
	public static final String SERVICE_PATH = "/games";

	/**
	 * The {@link Path} for the {@link #createGame()} method.
	 */
	public static final String SERVICE_PATH_NEW = "/new";

	/**
	 * The {@link Path} for the {@link #getGamesForPlayer()} method.
	 */
	public static final String SERVICE_PATH_GAMES_FOR_PLAYER = "/";

	/**
	 * The {@link Path} variable for methods that take in {@link Game#getId()} as a {@link PathParam}.
	 */
	public static final String SERVICE_PATH_GAME_ID = "/{gameId}";

	/**
	 * The {@link Path} for the {@link #setMaxRounds(String, int)} method.
	 */
	public static final String SERVICE_PATH_MAX_ROUNDS = "/maxRounds";

	/**
	 * The {@link Path} for the {@link #inviteOpponent(String, String)} method.
	 */
	public static final String SERVICE_PATH_INVITE_OPPONENT = "/invite";

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
	 * Creates a new game, with the first player set as the user calling this method, and leaving the identity of the
	 * second player to be set later.
	 * </p>
	 *
	 * @return a {@link GameView} of the new {@link Game} instance
	 * @see Game#Game(Player)
	 */
	@POST
	@Path(IGameResource.SERVICE_PATH_NEW)
	@Produces(MediaType.TEXT_XML)
	GameView createGame();

	/**
	 * <p>
	 * Returns all games that the the user who calls this method is a {@link Player} in.
	 * </p>
	 *
	 * @return a {@link List} of {@link GameView}s for the {@link Game}s that the the user who calls this method is a
	 *         {@link Player} in, or an empty {@link List} if there are no such {@link Game}s or if the user is not
	 *         authenticated
	 * @see Game#setPlayer2(Player)
	 */
	@GET
	@Path(IGameResource.SERVICE_PATH_GAMES_FOR_PLAYER)
	@Produces(MediaType.TEXT_XML)
	List<GameView> getGamesForPlayer();

	/**
	 * <p>
	 * Returns a {@link GameView} of the specified {@link Game}.
	 * </p>
	 *
	 * @param gameId
	 *            the {@link Game#getId()} value of the {@link Game} to return
	 * @return a {@link GameView} of the matching {@link Game} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching {@link Game} can be found.
	 */
	@GET
	@Path(IGameResource.SERVICE_PATH_GAME_ID)
	@Produces(MediaType.TEXT_XML)
	GameView getGame(@PathParam("gameId") String gameId) throws NotFoundException;

	/**
	 * <p>
	 * A web service proxy for the {@link Game#setMaxRounds(int)} method.
	 * </p>
	 *
	 * @param gameId
	 *            the {@link Game#getId()} value of the {@link Game} to modify
	 * @param oldMaxRoundsValue
	 *            the previous value of {@link Game#getMaxRounds()} (used to help prevent synchronization issues)
	 * @param newMaxRoundsValue
	 *            the value to use for {@link Game#getMaxRounds()}
	 * @return a {@link GameView} of the modified {@link Game} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching {@link Game} can be found.
	 * @throws GameConflictException
	 *             <p>
	 *             An {@link GameConflictException} will be thrown in the following cases:
	 *             </p>
	 *             <ul>
	 *             <li>If {@link Game#getState()} is not {@link State#WAITING_FOR_PLAYER} or
	 *             {@link State#WAITING_FOR_FIRST_THROW}.</li>
	 *             <li>If the <code>oldMaxRoundsValue</code> does not match the actual, current value of
	 *             {@link Game#getMaxRounds()}.</li>
	 *             </ul>
	 * @see Game#setMaxRounds(int)
	 */
	@POST
	@Path(IGameResource.SERVICE_PATH_GAME_ID + IGameResource.SERVICE_PATH_MAX_ROUNDS)
	@Produces(MediaType.TEXT_XML)
	GameView setMaxRounds(@PathParam("gameId") String gameId, @FormParam("oldMaxRoundsValue") int oldMaxRoundsValue,
			@FormParam("newMaxRoundsValue") int newMaxRoundsValue) throws NotFoundException, GameConflictException;

	/**
	 * <p>
	 * Invites the specified {@link Player} to join the specified {@link Game}.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> Currently, this method may only be used to invite {@link BuiltInAi} players to join a game
	 * created by the human opponent inviting them to it. As this is always allowed, this method will just set the
	 * {@link Game#getPlayer2()} to the requested {@link Player} before returning. Additional uses for this method are
	 * expected in the future, but not yet supported.
	 * </p>
	 *
	 * @param gameId
	 *            the {@link Game#getId()} value of the {@link Game} to modify
	 * @param playerId
	 *            the {@link Player#getId()} value of the {@link Player} to invite to join the specified {@link Game}
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching {@link Game} can be found.
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if {@link Game#getState()} is not
	 *             {@link State#WAITING_FOR_PLAYER}.
	 */
	@POST
	@Path(IGameResource.SERVICE_PATH_GAME_ID + IGameResource.SERVICE_PATH_INVITE_OPPONENT)
	@Produces(MediaType.TEXT_XML)
	void inviteOpponent(@PathParam("gameId") String gameId, @FormParam("playerId") long playerId)
			throws NotFoundException, GameConflictException;

	/**
	 * <p>
	 * Joins the user who calls this method to the specified {@link Game} as {@link Game#getPlayer2()}, if that field is
	 * still <code>null</code>.
	 * </p>
	 *
	 * @param gameId
	 *            the {@link Game#getId()} value of the {@link Game} to modify
	 * @return a {@link GameView} of the modified {@link Game} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching {@link Game} can be found.
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if {@link Game#getState()} is not
	 *             {@link State#WAITING_FOR_PLAYER}.
	 * @see Game#setPlayer2(Player)
	 */
	@POST
	@Path(IGameResource.SERVICE_PATH_GAME_ID + IGameResource.SERVICE_PATH_JOIN)
	@Produces(MediaType.TEXT_XML)
	GameView joinGame(@PathParam("gameId") String gameId) throws NotFoundException, GameConflictException;

	/**
	 * <p>
	 * A web service proxy for several {@link Game} methods, analogous to the following code snippet:
	 * </p>
	 *
	 * <pre>
	 * Game game = ...;
	 *
	 * if(!game.isRoundPrepared()) {
	 *   game.prepareRound();
	 * }
	 *
	 * return game;
	 * </pre>
	 *
	 * @param gameId
	 *            the {@link Game#getId()} value of the {@link Game} to modify
	 * @return a {@link GameView} of the (possibly updated) {@link Game} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching {@link Game} can be found.
	 * @see Game#prepareRound()
	 */
	@POST
	@Path(IGameResource.SERVICE_PATH_GAME_ID + IGameResource.SERVICE_PATH_PREPARE)
	@Produces(MediaType.TEXT_XML)
	GameView prepareRound(@PathParam("gameId") String gameId) throws NotFoundException;

	/**
	 * <p>
	 * A web service proxy for the {@link Game#submitThrow(int, Player, Throw)} method, where the {@link Player} passed
	 * to that call is the user who makes this web service call.
	 * </p>
	 *
	 * @param gameId
	 *            the {@link Game#getId()} value of the {@link Game} to modify
	 * @param roundIndex
	 *            the {@link GameRound#getRoundIndex()} of the current round (used to verify that gameplay is correctly
	 *            synchronized)
	 * @param throwToPlay
	 *            the {@link Throw} to submit for the {@link Player}
	 * @return a {@link GameView} of the modified {@link Game} instance
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching {@link Game} can be found.
	 * @throws GameConflictException
	 *             <p>
	 *             An {@link GameConflictException} will be thrown in the following cases:
	 *             </p>
	 *             <ul>
	 *             <li>If {@link Game#getState()} is not {@link State#STARTED}.</li>
	 *             <li>If the specified <code>roundIndex</code> is not the same as {@link GameRound#getRoundIndex()} in
	 *             the last/current round.</li>
	 *             <li>If the {@link Player} has already submitted a {@link Throw} for the {@link GameRound}.</li>
	 *             </ul>
	 * @see Game#submitThrow(int, Player, Throw)
	 */
	@POST
	@Path(IGameResource.SERVICE_PATH_GAME_ID + IGameResource.SERVICE_PATH_THROW)
	@Produces(MediaType.TEXT_XML)
	GameView submitThrow(@PathParam("gameId") String gameId, @FormParam("roundIndex") int roundIndex,
			@FormParam("throwToPlay") Throw throwToPlay) throws NotFoundException, GameConflictException;

	/**
	 * <p>
	 * Deletes the specified {@link Game} from the service/database. This operation is restricted to
	 * {@link SecurityRole#ADMINS} only.
	 * </p>
	 *
	 * @param gameId
	 *            the {@link Game#getId()} value of the {@link Game} to delete
	 * @throws NotFoundException
	 *             A {@link NotFoundException} will be thrown if no matching {@link Game} can be found.
	 */
	@DELETE
	@Path(IGameResource.SERVICE_PATH_GAME_ID)
	void deleteGame(@PathParam("gameId") String gameId) throws NotFoundException;
}
