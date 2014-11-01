package com.justdavis.karl.rpstourney.service.api.game;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * <p>
 * A runtime exception indicating that an attempt was made to modify a
 * {@link Game}'s state in an incorrect way. These are typically caused
 * by "mid-air collision" issues, when two users/clients try to update a
 * {@link Game} at the same time.
 * </p>
 * <p>
 * Gets translated to a {@link javax.ws.rs.core.Response.Status#CONFLICT
 * conflict} status code in JAX-RS requests and responses.
 * </p>
 */
public final class GameConflictException extends ClientErrorException {
	private static final long serialVersionUID = 6015922139636721023L;

	/**
	 * Construct a new {@link GameConflictException} instance.
	 */
	public GameConflictException() {
		super(Response.Status.BAD_REQUEST);
	}

	/**
	 * Construct a new {@link GameConflictException} instance.
	 * 
	 * @param message
	 *            the value to use for {@link #getMessage()}
	 */
	public GameConflictException(String message) {
		super(message, Response.Status.CONFLICT);
	}

	/**
	 * Construct a new {@link GameConflictException} instance.
	 * 
	 * @param response
	 *            the {@link Response} that this {@link GameConflictException}
	 *            was indicated/caused by
	 * @throws IllegalArgumentException
	 *             An {@link IllegalArgumentException} will be thrown if the
	 *             {@link Response#getStatus()} code is not the same as
	 *             {@link Status#CONFLICT}.
	 */
	public GameConflictException(Response response) {
		super(response.readEntity(String.class), validateStatusCode(response,
				Response.Status.CONFLICT));
	}

	/**
	 * @param response
	 *            the {@link Response} to validate
	 * @param requiredStatus
	 *            the {@link Status} to check for
	 * @return the passed-in {@link Response} (unchanged)
	 * @throws IllegalArgumentException
	 *             An {@link IllegalArgumentException} will be thrown if the
	 *             {@link Response#getStatus()} code is not the same as
	 *             {@link Status#CONFLICT}.
	 */
	private static Response validateStatusCode(Response response,
			Status requiredStatus) {
		if (response == null)
			throw new IllegalArgumentException();
		if (requiredStatus == null)
			throw new IllegalArgumentException();

		if (response.getStatus() != requiredStatus.getStatusCode())
			throw new IllegalArgumentException();

		return response;
	}

	/**
	 * A JAX-RS {@link ExceptionMapper} for {@link GameConflictException}.
	 */
	public static final class GameConflictExceptionMapper implements
			ExceptionMapper<GameConflictException> {
		/**
		 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
		 */
		@Override
		public Response toResponse(GameConflictException exception) {
			return Response.status(Status.CONFLICT)
					.entity(exception.getMessage()).build();
		}
	}
}
