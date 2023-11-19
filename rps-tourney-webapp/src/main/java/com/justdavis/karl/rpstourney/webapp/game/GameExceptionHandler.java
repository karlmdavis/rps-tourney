package com.justdavis.karl.rpstourney.webapp.game;

import javax.ws.rs.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This {@link ControllerAdvice} helper maps some specific application exceptions to HTTP response codes.
 */
@ControllerAdvice
public final class GameExceptionHandler {
	/**
	 * Maps {@link NotFoundException}s from the web service client to 404s.
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public void handleNotFoundExceptions() {
		// Nothing to do here; the annotations cover it.
	}
}
