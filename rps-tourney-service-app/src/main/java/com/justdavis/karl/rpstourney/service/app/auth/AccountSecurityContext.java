package com.justdavis.karl.rpstourney.service.app.auth;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;

/**
 * This {@link SecurityContext} implementation uses {@link Account}s for {@link #getUserPrincipal()} and
 * {@link #isUserInRole(String)}.
 */
public final class AccountSecurityContext implements SecurityContext {
	private final Account account;
	private final boolean secure;

	/**
	 * Constructs a new {@link AccountSecurityContext} instance.
	 *
	 * @param account
	 *            the {@link Account}/{@link Principal} that has authenticated with the application, or
	 *            <code>null</code> if the user is anonymous
	 * @param secure
	 *            <code>true</code> if the connection for the request is encrypted (e.g. <code>https</code>),
	 *            <code>false</code> if it's not
	 */
	public AccountSecurityContext(Account account, boolean secure) {
		this.account = account;
		this.secure = secure;
	}

	/**
	 * Constructs a new {@link AccountSecurityContext} instance.
	 *
	 * @param account
	 *            the {@link Account}/{@link Principal} that has authenticated with the application, or
	 *            <code>null</code> if the user is anonymous
	 */
	public AccountSecurityContext(Account account) {
		this(account, false);
	}

	/**
	 * Constructs a new {@link AccountSecurityContext} instance.
	 */
	public AccountSecurityContext() {
		this(null, false);
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#getUserPrincipal()
	 */
	@Override
	public Account getUserPrincipal() {
		return account;
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#isUserInRole(java.lang.String)
	 */
	@Override
	public boolean isUserInRole(String role) {
		if (account == null)
			return false;

		for (SecurityRole userRole : account.getRoles())
			if (userRole.getId().equals(role))
				return true;
		return false;
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#isSecure()
	 */
	@Override
	public boolean isSecure() {
		return secure;
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#getAuthenticationScheme()
	 */
	@Override
	public String getAuthenticationScheme() {
		return AuthenticationFilter.AUTH_SCHEME;
	}
}
