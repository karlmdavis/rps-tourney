package com.justdavis.karl.rpstourney.service.app.auth;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
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
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");

		/*
		 * Return a response with the account and the auth token (as a cookie,
		 * so the login is persisted between requests).
		 */
		return authenticatedAccount;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#updateAccount(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	@RolesAllowed({ SecurityRole.ID_USERS })
	public Account updateAccount(Account accountToUpdate) {
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");

		// Verify that the Account to be modified is legit.
		if (!accountToUpdate.hasId())
			throw new BadRequestException();

		// Only admins may modify others' Accounts.
		boolean userIsAdmin = authenticatedAccount.hasRole(SecurityRole.ADMINS);
		boolean accountsAreSame = authenticatedAccount.getId() == accountToUpdate
				.getId();
		if (!userIsAdmin && !accountsAreSame)
			throw new ForbiddenException();

		// Only admins may modify security.
		boolean rolesAreEqual = accountToUpdate.getRoles() != null
				&& authenticatedAccount.getRoles().equals(
						accountToUpdate.getRoles());
		if (!userIsAdmin && !rolesAreEqual)
			throw new ForbiddenException();

		// Does the specified account already exist?
		Account existingAccount = accountsDao.getAccountById(accountToUpdate
				.getId());

		// This method doesn't allow for creating new accounts.
		if (existingAccount == null)
			throw new ForbiddenException();

		// Save the modified Account to the database, and echo it back.
		Account mergedAccount = accountsDao.merge(accountToUpdate);
		return mergedAccount;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#selectOrCreateAuthToken()
	 */
	@Override
	public AuthToken selectOrCreateAuthToken() {
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");

		// Lookup/create the AuthToken, and return it.
		AuthToken authToken = accountsDao
				.selectOrCreateAuthToken(authenticatedAccount);
		return authToken;
	}

	/**
	 * @return the {@link Account} of the currently-authenticated user/client,
	 *         or <code>null</code> if the user/client is not authenticated
	 */
	private Account getAuthenticatedAccount() {
		/*
		 * Grab the requestor's Account from the SecurityContext. This will have
		 * been set by the AuthenticationFilter.
		 */
		Principal userPrincipal = securityContext.getUserPrincipal();
		if (userPrincipal == null)
			return null;

		// Sanity check: our security filter uses Accounts as Principals.
		if (!(userPrincipal instanceof Account))
			throw new BadCodeMonkeyException(
					"AuthenticationFilter not working.");

		Account authenticatedAccount = (Account) userPrincipal;
		return authenticatedAccount;
	}
}
