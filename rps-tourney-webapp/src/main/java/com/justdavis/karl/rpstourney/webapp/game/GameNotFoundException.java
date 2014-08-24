package com.justdavis.karl.rpstourney.webapp.game;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.justdavis.karl.rpstourney.service.api.game.GameSession;

/**
 * This exception can be thrown to indicate that a {@link GameSession} was
 * requested that doesn't actually exist.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public final class GameNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1108767824650492144L;

	/**
	 * Constructs a new {@link GameNotFoundException}.
	 * 
	 * @param cause
	 *            the value to use for {@link #getCause()}
	 */
	public GameNotFoundException(Throwable cause) {
		super(cause);
	}
}
