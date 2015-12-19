package com.justdavis.karl.rpstourney.service.app.auth;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.AbstractLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuditAccountGameMerge;
import com.justdavis.karl.rpstourney.service.api.auth.AuditAccountMerge;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;
import com.justdavis.karl.rpstourney.service.app.game.IGamesDao;
import com.justdavis.karl.rpstourney.service.app.game.IPlayersDao;

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
	private IPlayersDao playersDao;
	private IGamesDao gamesDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public AccountsResourceImpl() {
	}

	/**
	 * @param securityContext
	 *            the {@link SecurityContext} for the request that the
	 *            {@link AccountsResourceImpl} was instantiated to handle
	 */
	@Context
	public void setSecurityContext(SecurityContext securityContext) {
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
	 * @param playersDao
	 *            the injected {@link IPlayersDao} to use
	 */
	@Inject
	public void setPlayersDao(IPlayersDao playersDao) {
		// Sanity check: null IAccountsDao?
		if (playersDao == null)
			throw new IllegalArgumentException();

		this.playersDao = playersDao;
	}

	/**
	 * @param gamesDao
	 *            the injected {@link IGamesDao} to use
	 */
	@Inject
	public void setGamesDao(IGamesDao gamesDao) {
		// Sanity check: null IAccountsDao?
		if (gamesDao == null)
			throw new IllegalArgumentException();

		this.gamesDao = gamesDao;
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
	@Transactional
	public Account updateAccount(Account accountToUpdate) {
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");

		// Verify that the Account to be modified is legit.
		if (!accountToUpdate.hasId())
			throw new BadRequestException();

		// Only admins may modify others' Accounts.
		boolean userIsAdmin = authenticatedAccount.hasRole(SecurityRole.ADMINS);
		boolean accountsAreSame = authenticatedAccount.getId() == accountToUpdate.getId();
		if (!userIsAdmin && !accountsAreSame)
			throw new ForbiddenException();

		// Only admins may modify security.
		boolean rolesAreEqual = accountToUpdate.getRoles() != null
				&& authenticatedAccount.getRoles().equals(accountToUpdate.getRoles());
		if (!userIsAdmin && !rolesAreEqual)
			throw new ForbiddenException();

		// Does the specified account already exist?
		Account existingAccount = accountsDao.getAccountById(accountToUpdate.getId());

		// This method doesn't allow for creating new accounts.
		if (existingAccount == null)
			throw new ForbiddenException();

		/*
		 * Prevent AuthTokens and logins from being affected by the merge. All
		 * or some of the fields in these objects are excluded from JAXB, so the
		 * passed in Account will be incomplete for these fields.
		 */
		accountToUpdate.getAuthTokens().addAll(existingAccount.getAuthTokens());
		accountToUpdate.getLogins().clear();
		for (AbstractLoginIdentity login : existingAccount.getLogins()) {
			login.setAccount(accountToUpdate);
			accountToUpdate.getLogins().add(login);
		}

		// Save the modified Account to the database, and echo it back.
		Account mergedAccount = accountsDao.merge(accountToUpdate);
		return mergedAccount;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#selectOrCreateAuthToken()
	 */
	@Override
	@RolesAllowed({ SecurityRole.ID_USERS })
	public AuthToken selectOrCreateAuthToken() {
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");

		// Lookup/create the AuthToken, and return it.
		AuthToken authToken = accountsDao.selectOrCreateAuthToken(authenticatedAccount);
		return authToken;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#mergeAccount(long,
	 *      java.util.UUID)
	 */
	@Override
	@RolesAllowed({ SecurityRole.ID_USERS })
	@Transactional
	public void mergeAccount(long targetAccountId, UUID sourceAccountAuthTokenValue) {
		// Who's calling the method?
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			throw new BadCodeMonkeyException("RolesAllowed not working.");
		boolean userIsAdmin = authenticatedAccount.hasRole(SecurityRole.ADMINS);

		// Find the target Account.
		Account targetAccount = accountsDao.getAccountById(targetAccountId);
		if (targetAccount == null)
			throw new IllegalArgumentException("Unable to find target account with specified ID: " + targetAccountId);

		// Find the source Account.
		Account sourceAccount = accountsDao.getAccountByAuthToken(sourceAccountAuthTokenValue);
		if (sourceAccount == null)
			throw new IllegalArgumentException("Unable to find source account with specified AuthToken");

		/*
		 * Verify that the target Account is the current user (unless the
		 * current user is an admin).
		 */
		boolean userIsTarget = authenticatedAccount.getId() == targetAccountId;
		if (!userIsTarget && !userIsAdmin)
			throw new ForbiddenException();

		/*
		 * Verify that the source Account is anonymous (unless the current user
		 * is an admin).
		 */
		boolean sourceIsAnon = sourceAccount.isAnonymous();
		if (!sourceIsAnon && !userIsAdmin)
			throw new ForbiddenException();

		// Create the root audit entry that will track what's merged.
		AuditAccountMerge auditAccountEntry = new AuditAccountMerge(targetAccount,
				new HashSet<AbstractLoginIdentity>());

		// Merge the names (target wins, if set).
		if (targetAccount.getName() == null && sourceAccount.getName() != null)
			targetAccount.setName(sourceAccount.getName());

		// Merge the AuthTokens.
		targetAccount.getAuthTokens().addAll(sourceAccount.getAuthTokens());

		// Find and merge the logins.
		for (ILoginIdentity loginToMerge : sourceAccount.getLogins()) {
			// This cast should always be safe.
			AbstractLoginIdentity loginToMergeEntity = (AbstractLoginIdentity) loginToMerge;

			loginToMergeEntity.setAccount(targetAccount);
			auditAccountEntry.getMergedLogins().add(loginToMergeEntity);
		}

		// Find the source Player (if one exists).
		Player sourcePlayer = playersDao.findPlayerForAccount(sourceAccount);
		if (sourcePlayer != null) {
			// We'll need a target Player to replace the source with.
			Player targetPlayer = playersDao.findOrCreatePlayerForAccount(targetAccount);

			// Find all of the games for the source Player.
			List<Game> gamesToMerge = gamesDao.getGamesForPlayer(sourcePlayer);
			for (Game gameToMerge : gamesToMerge) {
				if (sourcePlayer.equals(gameToMerge.getPlayer1())) {
					gameToMerge.replacePlayer1(targetPlayer);
					auditAccountEntry.getMergedGames()
							.add(new AuditAccountGameMerge(auditAccountEntry, gameToMerge, PlayerRole.PLAYER_1));
				}
				if (sourcePlayer.equals(gameToMerge.getPlayer2())) {
					gameToMerge.replacePlayer2(targetPlayer);
					auditAccountEntry.getMergedGames()
							.add(new AuditAccountGameMerge(auditAccountEntry, gameToMerge, PlayerRole.PLAYER_2));
				}
			}
		}

		// Merge any previous merge audit entries targeting the source Account.
		List<AuditAccountMerge> previousMergeEntries = accountsDao.getAccountAuditEntries(sourceAccount);
		for (AuditAccountMerge previousMergeEntry : previousMergeEntries) {
			previousMergeEntry.setTargetAccount(targetAccount);
		}

		/*
		 * Save the audit entries and the target Account, then delete the (now
		 * empty) source Account.
		 */
		accountsDao.save(targetAccount);
		accountsDao.save(auditAccountEntry);
		accountsDao.save(previousMergeEntries.toArray(new AuditAccountMerge[previousMergeEntries.size()]));
		playersDao.delete(sourcePlayer);
		accountsDao.delete(sourceAccount);
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
			throw new BadCodeMonkeyException("AuthenticationFilter not working.");

		Account authenticatedAccount = (Account) userPrincipal;
		return authenticatedAccount;
	}
}
