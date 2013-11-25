package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountService;
import com.justdavis.karl.rpstourney.webservice.auth.AuthTokenCookieHelper;
import com.lambdaworks.crypto.SCryptUtil;

/**
 * This JAX-RS web service allows users to login as a guest. See
 * {@link #loginAsGuest(UriInfo, UUID)} for details.
 */
@Path(GameAuthService.SERVICE_PATH)
public final class GameAuthService {
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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GameAuthService.class);

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

	/**
	 * The in-memory store used to track existing {@link GameLoginIdentity}
	 * instances. FIXME Should be replaced with actual persistence.
	 */
	public static List<GameLoginIdentity> existingLogins = new LinkedList<>();

	/**
	 * Constructs a new {@link GameAuthService} instance.
	 */
	public GameAuthService() {
		if (existingLogins == null)
			existingLogins = new LinkedList<>();
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
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authToken
	 *            the value of the
	 *            {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie,
	 *            or <code>null</code> if the client/user is not already logged
	 *            in
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
	public Response loginWithGameAccount(
			@Context UriInfo uriInfo,
			@CookieParam(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN) UUID authToken,
			InternetAddress emailAddress, String password) {
		/*
		 * Never, ever allow this method to kill an existing login. If
		 * users/clients want to log out, they must do so explicitly.
		 */
		if (authToken != null)
			return Response.status(Status.CONFLICT).build();

		// Search for a matching login.
		GameLoginIdentity login = matchLogin(emailAddress, password);

		// If the login didn't match, return an error.
		if (login == null)
			return Response.status(Status.FORBIDDEN).build();

		// Create an authentication cookie for the logged-in Account.
		NewCookie authCookie = AuthTokenCookieHelper.createAuthTokenCookie(
				login.getAccount(), uriInfo.getRequestUri());

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
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authToken
	 *            the value of the
	 *            {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie,
	 *            or <code>null</code> if the client/user is not already logged
	 *            in
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
	public Response createGameLogin(
			@Context UriInfo uriInfo,
			@CookieParam(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN) UUID authToken,
			InternetAddress emailAddress, String password) {
		// Find the existing Account, if any.
		Account account = null;
		if (authToken != null)
			account = getAccount(authToken);

		// Search for a conflicting login.
		GameLoginIdentity conflictingLogin = matchLogin(emailAddress);
		if (conflictingLogin != null)
			return Response.status(Status.CONFLICT).build();

		// Create the new Account (if needed).
		if (account == null) {
			UUID randomAuthToken = UUID.randomUUID();
			account = new Account(randomAuthToken);

			AccountService.existingAccounts.add(account);
		}

		// Create the new login.
		GameLoginIdentity login = new GameLoginIdentity(account, emailAddress,
				hashPassword(password));
		existingLogins.add(login);

		// Create an authentication cookie for the logged-in Account.
		NewCookie authCookie = AuthTokenCookieHelper.createAuthTokenCookie(
				login.getAccount(), uriInfo.getRequestUri());

		/*
		 * Return a response with the account and the auth token (as a cookie,
		 * so the login is persisted between requests).
		 */
		return Response.ok().cookie(authCookie).entity(login.getAccount())
				.build();
	}

	/**
	 * @param emailAddress
	 *            the email address to match against
	 *            {@link GameLoginIdentity#getEmailAddress()}
	 * @return the pre-existing {@link GameLoginIdentity} that matches the
	 *         specified parameters, or <code>null</code> if no match was found
	 */
	private GameLoginIdentity matchLogin(InternetAddress emailAddress) {
		for (GameLoginIdentity existingLogin : existingLogins) {
			if (existingLogin.getEmailAddress().equals(emailAddress))
				return existingLogin;
		}

		// No match found.
		return null;
	}

	/**
	 * @param emailAddress
	 *            the email address to match against
	 *            {@link GameLoginIdentity#getEmailAddress()}
	 * @param password
	 *            the password to hash and then match against
	 *            {@link GameLoginIdentity#getPasswordHash()}
	 * @return the pre-existing {@link GameLoginIdentity} that matches the
	 *         specified parameters, or <code>null</code> if no match was found
	 */
	private GameLoginIdentity matchLogin(InternetAddress emailAddress,
			String password) {
		for (GameLoginIdentity existingLogin : existingLogins) {
			if (existingLogin.getEmailAddress().equals(emailAddress)
					&& checkPassword(password, existingLogin))
				return existingLogin;
		}

		// No match found.
		return null;
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

	/**
	 * @param authToken
	 *            the value to match against {@link Account#getAuthToken()}
	 * @return the {@link Account} instance with the specified
	 *         {@link Account#getAuthToken()} value, or <code>null</code> if no
	 *         match was found
	 */
	private Account getAccount(UUID authToken) {
		// Search for the Account.
		Account account = null;
		for (Account existingAccount : AccountService.existingAccounts)
			if (existingAccount.getAuthToken().equals(authToken))
				account = existingAccount;

		if (authToken != null && account == null) {
			/*
			 * If there was an auth token, a match for it wasn't found. Either
			 * someone's trying to hack, the Account has been deleted, or
			 * something's gone fairly badly wrong.
			 */
			LOGGER.warn(
					"Unable to find an existing account for auth token: {}",
					authToken);
		}

		return account;
	}
}
