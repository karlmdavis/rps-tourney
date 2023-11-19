package com.justdavis.karl.rpstourney.webapp.security;

import java.security.AuthProvider;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Response.Status;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.api.exceptions.UncheckedAddressException;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;

/**
 * A Spring Security {@link AuthProvider} implementation for {@link IGameAuthResource} logins.
 */
@Component
public final class GameLoginAuthenticationProvider implements AuthenticationProvider {
	private final CookieStore sessionCookies;
	private final IGameAuthResource gameAuthClient;

	/**
	 * Constructs a new {@link GameLoginAuthenticationProvider} instance.
	 *
	 * @param sessionCookies
	 *            the {@link CookieStore} for the current client/session
	 * @param gameAuthClient
	 *            the {@link IGameAuthResource} client to use
	 */
	@Inject
	public GameLoginAuthenticationProvider(CookieStore sessionCookies, IGameAuthResource gameAuthClient) {
		this.sessionCookies = sessionCookies;
		this.gameAuthClient = gameAuthClient;
	}

	/**
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// Returning null lets another AuthenticationProvider handle this.
		if (!supports(authentication.getClass()))
			return null;

		// Extract the username & password that were supplied.
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		/*
		 * Call the IGameAuthResource web service to perform the actual authentication.
		 */
		Account authenticatedAccount = null;
		try {
			/*
			 * The web service doesn't allow login attempts if the client is already authenticated. We must explicitly
			 * ensure that the web client is logged out, first.
			 */
			sessionCookies.forget(CustomRememberMeServices.COOKIE_NAME);

			// Now login.
			InternetAddress emailAddress = new InternetAddress(name);
			authenticatedAccount = gameAuthClient.loginWithGameAccount(emailAddress, password);
		} catch (HttpClientException e) {
			/*
			 * These two status codes indicate a legitimate authentication failure.
			 */
			if (e.getStatus() == Status.FORBIDDEN || e.getStatus() == Status.UNAUTHORIZED) {
				throw new BadCredentialsException("Authentication failed.", e);
			}

			// Otherwise, we'll assume the service is down.
			throw new AuthenticationServiceException("Authentication service failed.", e);
		} catch (AddressException e) {
			throw new UncheckedAddressException(e);
		}

		/*
		 * Note: If we got this far, the web service authentication was successful. As part of that authentication, the
		 * web service set/updated the AuthToken in the response, which the client will have automatically applied to
		 * its CookieStore. All future web service calls from here on out will be as the newly authenticated user.
		 * HOWEVER: Because we don't have access to the web application's cookies here, we can't adjust the web
		 * application's 'AuthToken' cookie. That has to be handled by the GameLoginSuccessHandler, instead.
		 */

		return new WebServiceAccountAuthentication(authenticatedAccount);
	}

	/**
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
