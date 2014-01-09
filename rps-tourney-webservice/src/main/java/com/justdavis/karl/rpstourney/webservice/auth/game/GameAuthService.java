package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.UUID;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.AuthToken;
import com.justdavis.karl.rpstourney.webservice.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao;
import com.lambdaworks.crypto.SCryptUtil;

/**
 * This JAX-RS web service allows users to login as a guest. See
 * {@link #loginAsGuest(UriInfo, UUID)} for details.
 */
@Path(GameAuthService.SERVICE_PATH)
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GameAuthService {
	/**
	 * The {@link Path} that this service will be hosted at.
	 */
	public static final String SERVICE_PATH = "/auth/game/";

	/**
	 * The {@link Path} for
	 * {@link #loginWithGameAccount(UriInfo, UUID, InternetAddress, String)}.
	 */
	public static final String SERVICE_PATH_LOGIN = "/login/";

	/**
	 * The {@link Path} for
	 * {@link #createGameLogin(UriInfo, UUID, InternetAddress, String)}.
	 */
	public static final String SERVICE_PATH_CREATE_LOGIN = "/create/";

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

	private AccountSecurityContext securityContext;
	private UriInfo uriInfo;
	private IAccountsDao accountsDao;
	private IGameLoginIndentitiesDao loginsDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public GameAuthService() {
	}

	/**
	 * @param securityContext
	 *            the {@link AccountSecurityContext} for the request that the
	 *            {@link GameAuthService} was instantiated to handle
	 */
	@Context
	public void setAccountSecurityContext(AccountSecurityContext securityContext) {
		// Sanity check: null AccountSecurityContext?
		if (securityContext == null)
			throw new IllegalArgumentException();

		this.securityContext = securityContext;
	}

	/**
	 * @param uriInfo
	 *            the {@link UriInfo} for the request that the
	 *            {@link GameAuthService} was instantiated to handle
	 */
	@Context
	public void setUriInfo(UriInfo uriInfo) {
		// Sanity check: null UriInfo?
		if (uriInfo == null)
			throw new IllegalArgumentException();

		this.uriInfo = uriInfo;
	}

	/**
	 * @param accountsDao
	 *            the injected {@link IAccountsDao} to use
	 */
	@Inject
	public void setAccountDao(IAccountsDao accountsDao) {
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
	 * <p>
	 * Allows clients to login with a {@link GameLoginIdentity}.
	 * </p>
	 * <p>
	 * The account being logged in must already exist. If the user/client
	 * calling this method is already logged in, this method will return an
	 * error, rather than overwriting the existing login (users must manually
	 * log out, first).
	 * </p>
	 * 
	 * @param emailAddress
	 *            the email address to log in as, which must match an existing
	 *            {@link GameLoginIdentity#getEmailAddress()}
	 * @param password
	 *            the password to authenticate with, which must match the
	 *            password hash in {@link GameLoginIdentity#getPasswordHash()}
	 *            for the specified login
	 * @return a {@link Response} containing the logged-in {@link Account}
	 *         instance, along with a
	 *         {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie
	 *         containing {@link Account#getAuthToken()}
	 */
	@POST
	@Path(SERVICE_PATH_LOGIN)
	@Produces(MediaType.TEXT_XML)
	public Response loginWithGameAccount(InternetAddress emailAddress,
			String password) {
		/*
		 * Never, ever allow this method to kill an existing login. If
		 * users/clients want to log out, they must do so explicitly.
		 */
		if (securityContext.getUserPrincipal() != null)
			return Response.status(Status.CONFLICT).build();

		// Search for a matching login.
		GameLoginIdentity login = loginsDao.find(emailAddress);

		// If the login didn't match, return an error.
		if (login == null)
			return Response.status(Status.FORBIDDEN).build();

		// Check the login's password.
		if (!checkPassword(password, login))
			return Response.status(Status.UNAUTHORIZED).build();

		// Create an authentication cookie for the logged-in Account.
		AuthToken authToken = accountsDao.selectOrCreateAuthToken(login
				.getAccount());
		NewCookie authCookie = AuthTokenCookieHelper.createAuthTokenCookie(
				authToken, uriInfo.getRequestUri());

		/*
		 * Return a response with the account and the auth token (as a cookie,
		 * so the login is persisted between requests).
		 */
		return Response.ok().cookie(authCookie).entity(login.getAccount())
				.build();
	}

	/**
	 * <p>
	 * Allows clients to create a new {@link GameLoginIdentity}.
	 * </p>
	 * <p>
	 * If the user/client calling this method is already logged in, this method
	 * will not also create a new {@link Account}, but will instead associate
	 * the new {@link GameLoginIdentity} with the existing {@link Account}. This
	 * can be used to "upgrade" guest accounts to accounts that can be used from
	 * multiple clients/browsers.
	 * </p>
	 * 
	 * @param emailAddress
	 *            the email address to log in as, which must match an existing
	 *            {@link GameLoginIdentity#getEmailAddress()}
	 * @param password
	 *            the password to authenticate with, which must match the
	 *            password hash in {@link GameLoginIdentity#getPasswordHash()}
	 *            for the specified login
	 * @return a {@link Response} containing the new/linked {@link Account}
	 *         instance, along with a
	 *         {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie
	 *         containing {@link Account#getAuthToken()}
	 */
	@POST
	@Path(SERVICE_PATH_CREATE_LOGIN)
	@Produces(MediaType.TEXT_XML)
	@Transactional
	public Response createGameLogin(
			@FormParam("emailAddress") InternetAddress emailAddress,
			@FormParam("password") String password) {
		// Find the existing Account, if any.
		Account account = null;
		if (securityContext.getUserPrincipal() != null)
			account = securityContext.getUserPrincipal();

		// Search for a conflicting login.
		GameLoginIdentity conflictingLogin = loginsDao.find(emailAddress);
		if (conflictingLogin != null)
			return Response.status(Status.CONFLICT).build();

		// Create the new Account (if needed).
		if (account == null) {
			account = new Account();
		}

		// Create and persist the new login.
		GameLoginIdentity login = new GameLoginIdentity(account, emailAddress,
				hashPassword(password));
		loginsDao.save(login);

		// Create an authentication cookie for the logged-in Account.
		AuthToken authToken = accountsDao.selectOrCreateAuthToken(login
				.getAccount());
		NewCookie authCookie = AuthTokenCookieHelper.createAuthTokenCookie(
				authToken, uriInfo.getRequestUri());

		/*
		 * Return a response with the account and the auth token (as a cookie,
		 * so the login is persisted between requests).
		 */
		return Response.ok().cookie(authCookie).entity(login.getAccount())
				.build();
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
