package com.justdavis.karl.rpstourney.service.app.auth.game;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
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
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.service.app.auth.AuthenticationFilter;
import com.justdavis.karl.rpstourney.service.app.auth.IAccountsDao;
import com.lambdaworks.crypto.SCryptUtil;

/**
 * The JAX-RS server-side implementation of {@link IGameAuthResource}.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GameAuthResourceImpl implements IGameAuthResource {
	/**
	 * The CPU cost factor (<code>"N"</code>) that will be passed to
	 * {@link SCryptUtil#scrypt(String, int, int, int)} when new passwords are
	 * first converted to hashes.
	 * 
	 * @see http://stackoverflow.com/a/12581268/1851299
	 */
	private static final int SCRYPT_CPU_COST = (int) Math.pow(2, 14);

	/**
	 * The memory cost factor (<code>"r"</code>) that will be passed to
	 * {@link SCryptUtil#scrypt(String, int, int, int)} when new passwords are
	 * first converted to hashes.
	 * 
	 * @see http://stackoverflow.com/a/12581268/1851299
	 */
	private static final int SCRYPT_MEMORY_COST = 8;

	/**
	 * The parallelization factor (<code>"p"</code>) that will be passed to
	 * {@link SCryptUtil#scrypt(String, int, int, int)} when new passwords are
	 * first converted to hashes.
	 * 
	 * @see http://stackoverflow.com/a/12581268/1851299
	 */
	private static final int SCRYPT_PARALLELIZATION = 1;

	private HttpServletRequest httpRequest;
	private IAccountsDao accountsDao;
	private AccountSecurityContext securityContext;
	private IGameLoginIndentitiesDao loginsDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public GameAuthResourceImpl() {
	}

	/**
	 * @param httpRequest
	 *            the {@link HttpServletRequest} that the
	 *            {@link GameAuthResourceImpl} was instantiated to handle
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
	 *            {@link GameAuthResourceImpl} was instantiated to handle
	 */
	@Context
	public void setAccountSecurityContext(AccountSecurityContext securityContext) {
		// Sanity check: null AccountSecurityContext?
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
	 *            the injected {@link IGameLoginIndentitiesDao} to use
	 */
	@Inject
	public void setGameLoginIdentitiesDao(IGameLoginIndentitiesDao loginsDao) {
		// Sanity check: null IGameLoginIndentitiesDao?
		if (loginsDao == null)
			throw new IllegalArgumentException();

		this.loginsDao = loginsDao;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource#loginWithGameAccount(javax.mail.internet.InternetAddress,
	 *      java.lang.String)
	 */
	@Override
	public Account loginWithGameAccount(InternetAddress emailAddress,
			String password) {
		/*
		 * Never, ever allow this method to kill an existing login. If
		 * users/clients want to log out, they must do so explicitly.
		 */
		if (securityContext.getUserPrincipal() != null)
			throw new WebApplicationException("User already logged in.",
					Status.CONFLICT);

		// Search for a matching login.
		GameLoginIdentity login = loginsDao.find(emailAddress);

		// If the login didn't match, return an error.
		if (login == null)
			throw new WebApplicationException("Authentication failed.",
					Status.FORBIDDEN);

		// Check the login's password.
		if (!checkPassword(password, login))
			throw new WebApplicationException("Authentication failed.",
					Status.UNAUTHORIZED);

		// Pull (or create) an auth token for the login.
		AuthToken authTokenForLogin = accountsDao.selectOrCreateAuthToken(login
				.getAccount());

		/*
		 * Store the login in the HTTP request, so the response
		 * AuthenticationFilter can record it in a cookie.
		 */
		httpRequest.setAttribute(AuthenticationFilter.LOGIN_PROPERTY,
				authTokenForLogin);

		/*
		 * Return the account that was logged in.
		 */
		return login.getAccount();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource#createGameLogin(javax.mail.internet.InternetAddress,
	 *      java.lang.String)
	 */
	@Override
	@Transactional
	public Account createGameLogin(InternetAddress emailAddress, String password) {
		// Find the existing Account, if any.
		Account account = null;
		if (securityContext.getUserPrincipal() != null)
			account = securityContext.getUserPrincipal();

		// Search for a conflicting login.
		GameLoginIdentity conflictingLogin = loginsDao.find(emailAddress);
		if (conflictingLogin != null)
			throw new WebApplicationException(
					"Login already exists for that email address.",
					Status.CONFLICT);

		// Create the new Account (if needed).
		if (account == null) {
			account = new Account();
		}

		// Create and persist the new login.
		GameLoginIdentity login = new GameLoginIdentity(account, emailAddress,
				hashPassword(password));
		loginsDao.save(login);

		// Pull (or create) an auth token for the login.
		AuthToken authTokenForLogin = accountsDao.selectOrCreateAuthToken(login
				.getAccount());

		/*
		 * Store the new login in the HTTP request, so the response
		 * AuthenticationFilter can record it in a cookie.
		 */
		httpRequest.setAttribute(AuthenticationFilter.LOGIN_PROPERTY,
				authTokenForLogin);

		/*
		 * Return the logged-in account.
		 */
		return login.getAccount();
	}

	/**
	 * @param password
	 *            the password to be hashed
	 * @return an scrypt password hash of the specified password
	 */
	static String hashPassword(String password) {
		String passwordHash = SCryptUtil.scrypt(password, SCRYPT_CPU_COST,
				SCRYPT_MEMORY_COST, SCRYPT_PARALLELIZATION);
		return passwordHash;
	}

	/**
	 * @param password
	 *            the password to compare against the hash in
	 *            {@link GameLoginIdentity#getPasswordHash()}
	 * @param login
	 *            the {@link GameLoginIdentity} to pull the password hash from
	 * @return <code>true</code> if the the specified password matches the
	 *         specified password hash (as created by
	 *         {@link #hashPassword(String)}), <code>false</code> if it does not
	 */
	static boolean checkPassword(String password, GameLoginIdentity login) {
		return SCryptUtil.check(password, login.getPasswordHash());
	}
}
