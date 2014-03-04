package com.justdavis.karl.rpstourney.webservice.auth.guest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.AuthenticationFilter;
import com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao;

/**
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuestAuthService implements IGuestAuthResource {
	private HttpServletRequest httpRequest;
	private AccountSecurityContext securityContext;
	private IAccountsDao accountsDao;
	private IGuestLoginIndentitiesDao loginsDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public GuestAuthService() {
	}

	/**
	 * @param httpRequest
	 *            the {@link HttpServletRequest} that the
	 *            {@link GuestAuthService} was instantiated to handle
	 */
	@Context
	public void setHttpServletRequest(HttpServletRequest httpRequest) {
		// Sanity check: null HttpServletRequest?
		if (httpRequest == null)
			throw new IllegalArgumentException();

		this.httpRequest = httpRequest;
	}

	/**
	 * @param securityContext
	 *            the {@link AccountSecurityContext} for the request that the
	 *            {@link GuestAuthService} was instantiated to handle
	 */
	@Context
	public void setAccountSecurityContext(AccountSecurityContext securityContext) {
		// Sanity check: null SecurityContext?
		if (securityContext == null)
			throw new IllegalArgumentException();

		this.securityContext = securityContext;
	}

	/**
	 * @param accountsDao
	 *            the injected {@link IAccountsDao} to use
	 */
	@Inject
	public void setAccountsDao(IAccountsDao accountsDao) {
		// Sanity check: null IAccountsDao?
		if (accountsDao == null)
			throw new IllegalArgumentException();

		this.accountsDao = accountsDao;
	}

	/**
	 * @param loginsDao
	 *            the injected {@link IGuestLoginIndentitiesDao} to use
	 */
	@Inject
	public void setGuestLoginIdentitiesDao(IGuestLoginIndentitiesDao loginsDao) {
		// Sanity check: null IGuestLoginIndentitiesDao?
		if (loginsDao == null)
			throw new IllegalArgumentException();

		this.loginsDao = loginsDao;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource#loginAsGuest()
	 */
	@Override
	@Transactional
	public Account loginAsGuest() {
		/*
		 * Never, ever allow this method to kill an existing login. If
		 * users/clients want to log out, they must do so explicitly.
		 */
		if (securityContext.getUserPrincipal() != null)
			throw new WebApplicationException("User already logged in.",
					Status.CONFLICT);

		// Create the new login and auth token.
		GuestLoginIdentity login = createLogin();
		AuthToken authTokenForLogin = accountsDao.selectOrCreateAuthToken(login
				.getAccount());

		/*
		 * Store the new login's auth token in the HTTP request, so the response
		 * AuthenticationFilter can record it in a cookie.
		 */
		httpRequest.setAttribute(AuthenticationFilter.LOGIN_PROPERTY,
				authTokenForLogin);

		/*
		 * Return a response with the new account that's associated with the
		 * login.
		 */
		return login.getAccount();
	}

	/**
	 * @return a new {@link GuestLoginIdentity} (and associated objects)
	 */
	private GuestLoginIdentity createLogin() {
		// Create a new blank account to associate the login with.
		Account blankAccount = new Account();

		// Create and persist the new login.
		GuestLoginIdentity newLogin = new GuestLoginIdentity(blankAccount);
		loginsDao.save(newLogin);

		return newLogin;
	}
}
