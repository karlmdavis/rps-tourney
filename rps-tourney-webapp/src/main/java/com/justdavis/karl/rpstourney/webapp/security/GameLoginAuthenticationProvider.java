package com.justdavis.karl.rpstourney.webapp.security;

import java.security.AuthProvider;
import java.util.LinkedList;
import java.util.List;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.api.exceptions.UncheckedAddressException;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;

/**
 * A Spring Security {@link AuthProvider} implementation for
 * {@link IGameAuthResource} logins.
 */
@Component
public final class GameLoginAuthenticationProvider implements
		AuthenticationProvider {
	private final IGameAuthResource gameAuthClient;

	/**
	 * Constructs a new {@link GameLoginAuthenticationProvider} instance.
	 * 
	 * @param gameAuthClient
	 *            the {@link IGameAuthResource} client to use
	 */
	@Inject
	public GameLoginAuthenticationProvider(IGameAuthResource gameAuthClient) {
		this.gameAuthClient = gameAuthClient;
	}

	/**
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		// Return null to let another AuthenticationProvider handle this.
		if (!supports(authentication.getClass()))
			return null;

		// Extract the username & password that were supplied.
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		/*
		 * Call the IGameAuthResource web service to perform the actual
		 * authentication.
		 */
		Account authenticatedAccount = null;
		try {
			InternetAddress emailAddress = new InternetAddress(name);
			authenticatedAccount = gameAuthClient.loginWithGameAccount(
					emailAddress, password);
		} catch (HttpClientException e) {
			/*
			 * These two status codes indicate a legitimate authentication
			 * failure.
			 */
			if (e.getStatus() == Status.FORBIDDEN
					|| e.getStatus() == Status.UNAUTHORIZED) {
				throw new BadCredentialsException("Authentication failed.", e);
			}

			// Otherwise, we'll assume the service is down.
			throw new AuthenticationServiceException(
					"Authentication service failed.", e);
		} catch (AddressException e) {
			throw new UncheckedAddressException(e);
		}

		/*
		 * Login was successful, so return a new token with the user's
		 * permissions (GrantedAuthoritys) included.
		 */
		List<SimpleGrantedAuthority> grantedAuthorities = new LinkedList<>();
		for (SecurityRole role : authenticatedAccount.getRoles())
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getId()));
		return new UsernamePasswordAuthenticationToken(
				authenticatedAccount.getName(), null, grantedAuthorities);
	}

	/**
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authentication);
	}
}
