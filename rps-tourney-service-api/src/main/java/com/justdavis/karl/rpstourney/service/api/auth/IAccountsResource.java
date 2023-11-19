package com.justdavis.karl.rpstourney.service.api.auth;

import java.util.UUID;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.game.Game;

/**
 * Implementations of this service allow users to manage their {@link Account}.
 */
@Path(IAccountsResource.SERVICE_PATH)
public interface IAccountsResource {
	/**
	 * The {@link Path} that this this resource class' methods will be hosted at.
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
	 * The {@link Path} for {@link #mergeFromDifferentAccount(Account)}.
	 */
	public static final String SERVICE_PATH_MERGE = "merge";

	/**
	 * The {@link FormParam#value()} / name of the first {@link #mergeFromDifferentAccount(Account)} param.
	 */
	public static final String SERVICE_PARAM_MERGE_TARGET = "targetAccountId";

	/**
	 * The {@link FormParam#value()} / name of the second {@link #mergeFromDifferentAccount(Account)} param.
	 */
	public static final String SERVICE_PARAM_MERGE_SOURCE = "sourceAccountAuthTokenValue";

	/**
	 * Allows users to validate that their existing logins (as represented by the
	 * <code>{@value AuthTokenCookieHelper#COOKIE_NAME_AUTH_TOKEN}</code> cookie) are valid.
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
	 * Updates the specified {@link Account}. Users/clients may only update the {@link Account} that they are
	 * authenticated as, unless they have the {@link SecurityRole#ADMINS} privilege.
	 *
	 * @param accountToUpdate
	 *            the {@link Account} to be updated, with the updated state modified in it (e.g. if you want to change
	 *            {@link Account#getName()}, get the {@link Account}, call {@link Account#setName(String)}, and then
	 *            pass that modified instance to this method)
	 * @return the updated user's/client's {@link Account}
	 * @throws BadRequestException
	 *             A {@link BadRequestException} will be thrown if the specified {@link Account} is invalid.
	 * @throws ForbiddenException
	 *             A {@link ForbiddenException} will be thrown if a non-admin attempts to modify another user's account
	 *             or attempts to adjust their permissions.
	 */
	@POST
	@Path(SERVICE_PATH_GET_ACCOUNT)
	@Produces(MediaType.TEXT_XML)
	Account updateAccount(@Valid Account accountToUpdate);

	/**
	 * Selects the most recent active {@link AuthToken} from the requesting user/client's {@link Account} or, if no such
	 * {@link AuthToken} exists, creates a new one, persists it, and returns that.
	 *
	 * @return the most recent active {@link AuthToken} from the requesting user/client's {@link Account} (or a new one)
	 */
	@GET
	@Path(SERVICE_PATH_AUTH_TOKEN)
	@Produces(MediaType.TEXT_XML)
	AuthToken selectOrCreateAuthToken();

	/**
	 * <p>
	 * Merges all of the {@link Game}s and {@link ILoginIdentity}s associated with the specified "source"
	 * {@link Account} into the specified "target" {@link Account}, and then deletes the now-empty "source"
	 * {@link Account}. In addition, an {@link AuditAccountMerge} record will be saved to the database recording the
	 * operation.
	 * </p>
	 * <p>
	 * Security Considerations: Unless this method is being called by an admin, the following restrictions will be
	 * enforced for this method:
	 * </p>
	 * <ul>
	 * <li>The current user, as returned by {@link #getAccount()}, must be the "target" {@link Account}.</li>
	 * <li>The "source" {@link Account} must be anonymous; it must only have {@link GuestLoginIdentity}s associated with
	 * it.</li>
	 * </ul>
	 * <p>
	 * These restrictions are intended to allow non-admins to merge anonymous {@link Account}s that they have access to,
	 * while preventing users from "poisoning" the history of {@link Account}s they don't control.
	 * </p>
	 *
	 * @param targetAccountId
	 *            the {@link Account#getId()} of the {@link Account} to merge to, which will end up acquiring all of the
	 *            game history, etc. from the other {@link Account}
	 * @param sourceAccountId
	 *            a valid {@link AuthToken#getToken()} associated with the {@link Account} to merge from, which will be
	 *            deleted from the database as part of this operation
	 */
	@POST
	@Path(SERVICE_PATH_MERGE)
	void mergeAccount(@FormParam(SERVICE_PARAM_MERGE_TARGET) long targetAccountId,
			@FormParam(SERVICE_PARAM_MERGE_SOURCE) UUID sourceAccountAuthTokenValue);
}
