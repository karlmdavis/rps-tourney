package com.justdavis.karl.rpstourney.webapp.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;

/**
 * <p>
 * Implementations of this interface allow the web application to manage logins
 * for users/clients via the web service's {@link IGuestAuthResource} facility.
 * </p>
 */
public interface IGuestLoginManager {
	/**
	 * Authenticate the user/client that issued the specified request as a
	 * guest. Implementations must ensure that the login is set for the rest of
	 * the request. In addition, implementations should attempt to ensure that
	 * future requests are also authenticated by setting any authentication
	 * cookies, etc. in the specified response.
	 * 
	 * @param request
	 *            the {@link HttpServletRequest} being processed
	 * @param response
	 *            the {@link HttpServletResponse} being generated for the
	 *            request
	 */
	void loginClientAsGuest(HttpServletRequest request, HttpServletResponse response);
}
