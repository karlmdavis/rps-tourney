package com.justdavis.karl.rpstourney.service.app.auth;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;

/**
 * The JAX-RS server-side implementation of {@link IAccountsResource}.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountsResourceImpl implements IAccountsResource {
	/**
	 * The {@link SecurityContext} of the current request.
	 */
	private SecurityContext securityContext;

	private IAccountsDao accountsDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public AccountsResourceImpl() {
	}

	/**
	 * @param securityContext
	 *            the {@link AccountSecurityContext} for the request that the
	 *            {@link AccountsResourceImpl} was instantiated to handle
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
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#validateAuth()
	 */
	@Override
	@RolesAllowed({ SecurityRole.ID_USERS })
	public Account validateAuth() {
		// Just pass it through to getAccount().
		return getAccount();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#getAccount()
	 */
	@Override
	@RolesAllowed({ SecurityRole.ID_USERS })
	public Account getAccount() {
		/*
		 * Grab the requestor's Account from the SecurityContext. This will have
		 * been set by the AuthenticationFilter.
		 */
		Principal userPrincipal = securityContext.getUserPrincipal();
		if (userPrincipal == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");
		if (!(userPrincipal instanceof Account))
			throw new BadCodeMonkeyException(
					"AuthenticationFilter not working.");
		Account userAccount = (Account) userPrincipal;

		/*
		 * Return a response with the account and the auth token (as a cookie,
		 * so the login is persisted between requests).
		 */
		return userAccount;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#selectOrCreateAuthToken()
	 */
	@Override
	public AuthToken selectOrCreateAuthToken() {
		/*
		 * Grab the requestor's Account from the SecurityContext. This will have
		 * been set by the AuthenticationFilter.
		 */
		Principal userPrincipal = securityContext.getUserPrincipal();
		if (userPrincipal == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");
		if (!(userPrincipal instanceof Account))
			throw new BadCodeMonkeyException(
					"AuthenticationFilter not working.");
		Account userAccount = (Account) userPrincipal;

		// Lookup/create the AuthToken, and return it.
		AuthToken authToken = accountsDao.selectOrCreateAuthToken(userAccount);
		return authToken;
	}
}
