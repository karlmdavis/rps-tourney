package com.justdavis.karl.rpstourney.webapp.security;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;

/**
 * An {@link Authentication} implementation that can be used with the web
 * service's {@link Account} class.
 */
public final class WebServiceAccountAuthentication extends
		AbstractAuthenticationToken {
	private static final long serialVersionUID = 5599413980514620207L;

	private final Account authenticatedAccount;

	/**
	 * Constructs a new {@link WebServiceAccountAuthentication} instance.
	 * 
	 * @param authenticatedAccount
	 *            the already-authenticated guest {@link Account} that this new
	 *            {@link WebServiceAccountAuthentication} instance will
	 *            represent
	 */
	public WebServiceAccountAuthentication(Account authenticatedAccount) {
		super(buildAuthoritiesList(authenticatedAccount));

		this.authenticatedAccount = authenticatedAccount;
		setAuthenticated(true);
	}

	/**
	 * @param authenticatedAccount
	 *            the already-authenticated guest {@link Account} that this new
	 *            {@link WebServiceAccountAuthentication} instance will
	 *            represent
	 * @return a {@link List} of {@link GrantedAuthority}s built from the
	 *         specified {@link Account}'s {@link Account#getRoles()}
	 */
	private static Collection<? extends GrantedAuthority> buildAuthoritiesList(
			Account authenticatedAccount) {
		if (authenticatedAccount == null)
			throw new IllegalArgumentException();

		List<SimpleGrantedAuthority> grantedAuthorities = new LinkedList<>();
		for (SecurityRole role : authenticatedAccount.getRoles())
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getId()));
		return grantedAuthorities;
	}

	/**
	 * @see org.springframework.security.core.Authentication#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		return authenticatedAccount;
	}

	/**
	 * @see org.springframework.security.core.Authentication#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		/*
		 * We could maybe also collect an ILoginIdentity in this class'
		 * constructor and return that here. However, there isn't really any
		 * need for that now (many other Authentication implementations just
		 * return null here), so we don't bother.
		 */
		return null;
	}
}
