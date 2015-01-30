package com.justdavis.karl.rpstourney.service.api.auth;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Implementations of this service allow users to manage their {@link Account}.
 */
@Path(IAccountsResource.SERVICE_PATH)
public interface IAccountsResource {

	/**
	 * The {@link Path} that this this resource class' methods will be hosted
	 * at.
	 */
	public static final String SERVICE_PATH = "/account/";

	/**
	 * The {@link Path} for {@link #validateAuth()}.
	 */
	public static final String SERVICE_PATH_VALIDATE = "/validate/";

	/**
	 * The {@link Path} for {@link #getAccount()}.
	 */
	public static final String SERVICE_PATH_GET_ACCOUNT = "";

	/**
	 * The {@link Path} for {@link #updateAccount(Account)}.
	 */
	public static final String SERVICE_PATH_UPDATE_ACCOUNT = "";

	/**
	 * The {@link Path} for {@link #selectOrCreateAuthToken()}.
	 */
	public static final String SERVICE_PATH_AUTH_TOKEN = "selectOrCreateAuthToken";

	/**
	 * The {@link Path} for {@link #getLogins()}.
	 */
	public static final String SERVICE_PATH_GET_LOGINS = "logins";

	/**
	 * Allows users to validate that their existing logins (as represented by
	 * the <code>{@value AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN}</code>
	 * cookie) are valid.
	 * 
	 * @return the user's/client's {@link Account}
	 */
	@GET
	@Path(SERVICE_PATH_VALIDATE)
	@Produces(MediaType.TEXT_XML)
	Account validateAuth();

	/**
	 * Returns the {@link Account} for the requesting user/client.
	 * 
	 * @return the user's/client's {@link Account}
	 */
	@GET
	@Path(SERVICE_PATH_GET_ACCOUNT)
	@Produces(MediaType.TEXT_XML)
	Account getAccount();

	/**
	 * Updates the specified {@link Account}. Users/clients may only update the
	 * {@link Account} that they are authenticated as, unless they have the
	 * {@link SecurityRole#ADMINS} privilege.
	 * 
	 * @param accountToUpdate
	 *            the {@link Account} to be updated, with the updated state
	 *            modified in it (e.g. if you want to change
	 *            {@link Account#getName()}, get the {@link Account}, call
	 *            {@link Account#setName(String)}, and then pass that modified
	 *            instance to this method)
	 * @return the updated user's/client's {@link Account}
	 * @throws BadRequestException
	 *             A {@link BadRequestException} will be thrown if the specified
	 *             {@link Account} is invalid.
	 * @throws ForbiddenException
	 *             A {@link ForbiddenException} will be thrown if a non-admin
	 *             attempts to modify another user's account or attempts to
	 *             adjust their permissions.
	 */
	@POST
	@Path(SERVICE_PATH_GET_ACCOUNT)
	@Produces(MediaType.TEXT_XML)
	Account updateAccount(Account accountToUpdate);

	/**
	 * Selects the most recent active {@link AuthToken} from the requesting
	 * user/client's {@link Account} or, if no such {@link AuthToken} exists,
	 * creates a new one, persists it, and returns that.
	 * 
	 * @return the most recent active {@link AuthToken} from the requesting
	 *         user/client's {@link Account} (or a new one)
	 */
	@GET
	@Path(SERVICE_PATH_AUTH_TOKEN)
	@Produces(MediaType.TEXT_XML)
	AuthToken selectOrCreateAuthToken();

	/**
	 * @return the {@ink ILoginIdentity}s associated with the requesting
	 *         user/client's {@link Account}, ordered by their creation date,
	 *         ascending
	 */
	@GET
	@Path(SERVICE_PATH_GET_LOGINS)
	@Produces(MediaType.TEXT_XML)
	LoginIdentities getLogins();
}