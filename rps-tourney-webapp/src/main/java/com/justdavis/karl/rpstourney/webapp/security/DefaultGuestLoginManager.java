package com.justdavis.karl.rpstourney.webapp.security;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;

/**
 * The default {@link IGuestLoginManager} implementation.
 */
@Component
public class DefaultGuestLoginManager implements IGuestLoginManager {
	private final SecurityContextHolderStrategy securityContextStrategy;
	private final IGuestAuthResource guestAuthClient;
	private final RememberMeServices rememberMeServices;

	/**
	 * Constructs a new {@link DefaultGuestLoginManager} instance.
	 * 
	 * @param securityContextStrategy
	 *            the {@link SecurityContextHolderStrategy} for the application
	 * @param guestAuthClient
	 *            the {@link IGuestAuthResource} web service client to use
	 * @param rememberMeServices
	 *            the {@link RememberMeServices} for the application
	 */
	@Inject
	public DefaultGuestLoginManager(
			SecurityContextHolderStrategy securityContextStrategy,
			IGuestAuthResource guestAuthClient,
			RememberMeServices rememberMeServices) {
		this.securityContextStrategy = securityContextStrategy;
		this.guestAuthClient = guestAuthClient;
		this.rememberMeServices = rememberMeServices;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webapp.security.IGuestLoginManager#loginClientAsGuest(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void loginClientAsGuest(HttpServletRequest request,
			HttpServletResponse response) {
		/*
		 * This method shouldn't be called if the request/client is already
		 * authenticated. If it is, whatever called this method screwed up.
		 */
		Authentication existingAuth = securityContextStrategy.getContext()
				.getAuthentication();
		if (existingAuth != null)
			throw new BadCodeMonkeyException("Already authenticated.");

		// Login via the web service using the client.
		Account guestAccount = guestAuthClient.loginAsGuest();
		Authentication guestAuth = new WebServiceAccountAuthentication(
				guestAccount);

		/*
		 * Take the login and use it to "turn on" the remember-me feature of
		 * Spring Security.
		 */
		rememberMeServices.loginSuccess(request, response, guestAuth);
	}
}
