package com.justdavis.karl.rpstourney.service.api.game;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * <p>
 * A runtime exception indicating that an attempt was made to modify a
 * {@link Game}'s state in an incorrect way. These are typically caused by
 * "mid-air collision" issues, when two users/clients try to update a
 * {@link Game} at the same time.
 * </p>
 * <p>
 * Gets translated to a {@link javax.ws.rs.core.Response.Status#CONFLICT
 * conflict} status type in JAX-RS requests and responses.
 * </p>
 */
public final class GameConflictException extends ClientErrorException {
	private static final long serialVersionUID = 6015922139636721023L;

	private final ConflictType type;

	/**
	 * Construct a new {@link GameConflictException} instance.
	 * 
	 * @param type
	 *            the value to use for {@link #getType()}
	 */
	public GameConflictException(ConflictType type) {
		super(Response.Status.CONFLICT);
		this.type = type;
	}

	/**
	 * Construct a new {@link GameConflictException} instance.
	 * 
	 * @param response
	 *            the {@link Response} that this {@link GameConflictException}
	 *            was indicated/caused by
	 */
	public GameConflictException(Response response) {
		this(extractTypeFromResponse(response));
	}

	/**
	 * Used to extract a {@link GameConflictException} / {@link ConflictType}
	 * from an HTTP {@link Response}.
	 * 
	 * @param response
	 *            the HTTP {@link Response} to translate
	 * @return the {@link ConflictType} (for a {@link GameConflictException})
	 *         that was embedded in the specified {@link Response}, or
	 *         {@link ConflictType#UNKNOWN} if that could not be determined (as
	 *         might happen if the server supports {@link ConflictType}s that
	 *         the client is not familiar with)
	 * @throws IllegalArgumentException
	 *             An {@link IllegalArgumentException} will be thrown if the
	 *             {@link Response#getStatus()} type is not
	 *             {@link Status#CONFLICT}.
	 */
	private static ConflictType extractTypeFromResponse(Response response) {
		if (response == null)
			throw new IllegalArgumentException();
		if (response.getStatus() != Status.CONFLICT.getStatusCode())
			throw new IllegalArgumentException();

		// Try to extract a ConflictType name from the Response.
		String conflictTypeName = response.readEntity(String.class);
		if (conflictTypeName == null)
			return ConflictType.UNKNOWN;
		try {
			ConflictType conflictType = ConflictType.valueOf(conflictTypeName);
			return conflictType;
		} catch (IllegalArgumentException e) {
			return ConflictType.UNKNOWN;
		}
	}

	/**
	 * @return the {@link ConflictType} that indicates the type of problem/issue
	 *         that was encountered
	 */
	public ConflictType getType() {
		return type;
	}

	/**
	 * A JAX-RS {@link ExceptionMapper} for {@link GameConflictException}s.
	 */
	public static final class GameConflictExceptionMapper implements ExceptionMapper<GameConflictException> {
		/**
		 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
		 */
		@Override
		public Response toResponse(GameConflictException exception) {
			return Response.status(Status.CONFLICT).entity(exception.getType().name()).build();
		}
	}

	/**
	 * Enumerates the various types of {@link GameConflictException}s that can
	 * occur.
	 */
	public static enum ConflictType {
		/**
		 * Users cannot call {@link Game#setMaxRounds(int)} after a game has
		 * started.
		 */
		ROUNDS_FINALIZED,

		/**
		 * An invalid value was passed to {@link Game#setMaxRounds(int)}.
		 */
		ROUNDS_INVALID,

		/**
		 * When trying to set the maximum rounds for a game, the user supplied a
		 * stale/incorrect "old" value.
		 */
		ROUNDS_STALE,

		/**
		 * Indicates that a user is trying to call
		 * {@link Game#setPlayer2(Player)} after someone else has already been
		 * set as Player 2.
		 */
		PLAYER_2_FINALIZED,

		/**
		 * Indicates that a user is trying to set a throw before the game has
		 * started (i.e. before Player 2 has joined).
		 */
		THROW_BEFORE_START,

		/**
		 * Indicates that a user is trying to set their throw to an invalid
		 * value.
		 */
		THROW_INVALID,

		/**
		 * Indicates that a user is trying to set a throw for a round other than
		 * the current one.
		 */
		THROW_WRONG_ROUND,

		/**
		 * Indicates that a user is trying to set their throw for the same
		 * {@link GameRound} more than once, which is disallowed.
		 */
		THROW_ALREADY_SET,

		/**
		 * Indicates that a user is trying to set a throw after the game has
		 * finished.
		 */
		THROW_AFTER_FINISH,

		/**
		 * Indicates that some unknown error condition occurred. This should
		 * never be used directly by server-side {@link IGameResource}
		 * implementations.
		 */
		UNKNOWN;
	}
}
