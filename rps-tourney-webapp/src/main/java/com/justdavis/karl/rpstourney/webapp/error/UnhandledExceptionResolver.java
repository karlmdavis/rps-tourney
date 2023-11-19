package com.justdavis.karl.rpstourney.webapp.error;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * The custom {@link HandlerExceptionResolver} for the application.
 */
public final class UnhandledExceptionResolver extends SimpleMappingExceptionResolver {
	private final Logger LOGGER = LoggerFactory.getLogger(UnhandledExceptionResolver.class);

	/**
	 * Constructs a new {@link UnhandledExceptionResolver} instance.
	 */
	public UnhandledExceptionResolver() {
		/*
		 * This isn't set by default, so without it Spring MVC generates an ugly page with the full stack trace on it,
		 * which is a bad idea. Instead, we point it at the error-default.jsp view.
		 */
		setDefaultErrorView("error-default");
		setDefaultStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	/**
	 * @see org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver#logException(java.lang.Exception,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void logException(Exception ex, HttpServletRequest request) {
		/*
		 * Overriding this superclass method accomplishes two things, 1) configures Spring MVC to actually log errors
		 * (it doesn't by default), and 2) lets us customize how those errors are logged.
		 */

		LOGGER.error("Unhandled error encountered for request: '{}'", getFullRequestUrl(request), ex);
	}

	/**
	 * @param request
	 *            the {@link HttpServletRequest} to get the full URL of
	 * @return the full request URL, including {@link HttpServletRequest#getRequestURL()} and
	 *         {@link HttpServletRequest#getQueryString()} (if any)
	 */
	private String getFullRequestUrl(HttpServletRequest request) {
		StringBuffer fullRequestUrl = request.getRequestURL();
		if (request.getQueryString() != null) {
			fullRequestUrl.append('?');
			fullRequestUrl.append(request.getQueryString());
		}

		return fullRequestUrl.toString();
	}
}
