package com.justdavis.karl.rpstourney.webservice.auth;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.webservice.auth.game.GameAuthService;
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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AccountService.class);

	/**
	 * The in-memory store used to track existing {@link Account} instances.
	 * FIXME Should be replaced with actual persistence.
	 */
	public static List<Account> existingAccounts = new LinkedList<>();

	private final SecurityContext securityContext;

	/**
	 * Constructs a new {@link GameAuthService} instance.
	 * 
	 * @param securityContext
	 *            the {@link SecurityContext} for the request that the
	 *            {@link AccountService} was instantiated to handle
	 */
	public AccountService(@Context AccountSecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	/**
	 * Allows users to validate that their existing logins (as represented by
	 * the <code>{@value AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN}</code>
	 * cookie) are valid.
	 * 
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authToken
	 *            the value of the
	 *            {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie,
	 *            or <code>null</code> to create a new guest login
	 * @return a {@link Response} containing the user's/client's {@link Account}
	 *         , along with a
	 *         {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie
	 *         containing {@link GuestLoginIdentity#getAuthToken()} (if a valid
	 *         authentication token was provided)
	 */
	@GET
	@Path(SERVICE_PATH_VALIDATE)
	@Produces(MediaType.TEXT_XML)
	@RolesAllowed({ SecurityRole.ID_USERS })
	public Response validateAuth() {
		// Just pass it through to getAccount().
		return getAccount();
	}

	/**
	 * Returns the {@link Account} for the requesting user/client.
	 * 
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authToken
	 *            the value of the
	 *            {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie,
	 *            or <code>null</code> to create a new guest login
	 * @return a {@link Response} containing a new {@link GuestLoginIdentity}
	 *         instance (or the pre-existing one, if a valid authentication
	 *         token was provided), along with a
	 *         {@link AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN} cookie
	 *         containing {@link GuestLoginIdentity#getAuthToken()}
	 */
	@GET
	@Path(SERVICE_PATH_GET_ACCOUNT)
	@Produces(MediaType.TEXT_XML)
	@RolesAllowed({ SecurityRole.ID_USERS })
	public Response getAccount() {
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
		return Response.ok().entity(userAccount).build();
	}

	/**
	 * @param authToken
	 *            the value to match against {@link Account#getAuthToken()}
	 * @return the {@link Account} instance with the specified
	 *         {@link Account#getAuthToken()} value, or <code>null</code> if no
	 *         match was found
	 */
	static Account getAccount(UUID authToken) {
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
