package com.justdavis.karl.rpstourney.webservice.auth;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestLoginIdentity;

/**
 * This JAX-RS web service allows users to manage their {@link Account}.
 */
@Path(AccountService.SERVICE_PATH)
public final class AccountService {
	/**
	 * The {@link Path} that this service will be hosted at.
	 */
	public static final String SERVICE_PATH = "/account/";

	/**
	 * The {@link Path} for {@link #validateAuth(UriInfo, UUID)}.
	 */
	public static final String SERVICE_PATH_VALIDATE = "/validate/";

	/**
	 * The {@link Path} for {@link #getAccount(UriInfo, UUID)}.
	 */
	public static final String SERVICE_PATH_GET_ACCOUNT = "";

	/**
	 * The name of the {@link Cookie} used to track the
	 * {@link Account#getAuthToken()} value.
	 */
	public static final String COOKIE_NAME_AUTH_TOKEN = "authToken";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AccountService.class);

	/**
	 * The in-memory store used to track existing {@link Account} instances.
	 * FIXME Should be replaced with actual persistence.
	 */
	public static List<Account> existingAccounts = new LinkedList<>();

	/**
	 * Constructs a new {@link GameAuthService} instance.
	 */
	public AccountService() {
	}

	/**
	 * Allows users to validate that their existing logins (as represented by
	 * the <code>{@value #COOKIE_NAME_AUTH_TOKEN}</code> cookie) are valid.
	 * 
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authToken
	 *            the value of the {@link #COOKIE_NAME_AUTH_TOKEN} cookie, or
	 *            <code>null</code> to create a new guest login
	 * @return a {@link Response} containing the user's/client's {@link Account}
	 *         , along with a {@link #COOKIE_NAME_AUTH_TOKEN} cookie containing
	 *         {@link GuestLoginIdentity#getAuthToken()} (if a valid
	 *         authentication token was provided)
	 */
	@GET
	@Path(SERVICE_PATH_VALIDATE)
	@Produces(MediaType.TEXT_XML)
	public Response validateAuth(@Context UriInfo uriInfo,
			@CookieParam(COOKIE_NAME_AUTH_TOKEN) UUID authToken) {
		// Just pass it through to getAccount(...).
		return getAccount(uriInfo, authToken);
	}

	/**
	 * Returns the {@link Account} for the requesting user/client.
	 * 
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authToken
	 *            the value of the {@link #COOKIE_NAME_AUTH_TOKEN} cookie, or
	 *            <code>null</code> to create a new guest login
	 * @return a {@link Response} containing a new {@link GuestLoginIdentity}
	 *         instance (or the pre-existing one, if a valid authentication
	 *         token was provided), along with a {@link #COOKIE_NAME_AUTH_TOKEN}
	 *         cookie containing {@link GuestLoginIdentity#getAuthToken()}
	 */
	@GET
	@Path(SERVICE_PATH_GET_ACCOUNT)
	@Produces(MediaType.TEXT_XML)
	public Response getAccount(@Context UriInfo uriInfo,
			@CookieParam(COOKIE_NAME_AUTH_TOKEN) UUID authToken) {
		// Try to retrieve the existing account (if any) for the auth token.
		Account account = getAccount(authToken);

		/*
		 * If no Account was found, return an error.
		 */
		if (account == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		/*
		 * Create a new Cookie for the auth token. This will update its
		 * properties (e.g. expiration date), if necessary.
		 */
		// TODO This should probably be done in some app-wide filter.
		String authTokenString = account.getAuthToken().toString();
		NewCookie authCookie = new NewCookie(COOKIE_NAME_AUTH_TOKEN,
				authTokenString, "/", uriInfo.getBaseUri().toString(),
				Cookie.DEFAULT_VERSION, "",
				60 * 60 * 24 * 365 * 1 /* 1 year */, true);

		/*
		 * JAX-RS doesn't have support for the "HttpOnly" flag until the full
		 * 2.0 release. This is a hack to work around that.
		 */
		String authCookieValue = authCookie.toString() + ";HttpOnly";

		/*
		 * Return a response with the account and the auth token (as a cookie,
		 * so the login is persisted between requests).
		 */
		return Response.ok().header("Set-Cookie", authCookieValue)
				.entity(account).build();
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
		for (Account existingAccount : existingAccounts)
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
