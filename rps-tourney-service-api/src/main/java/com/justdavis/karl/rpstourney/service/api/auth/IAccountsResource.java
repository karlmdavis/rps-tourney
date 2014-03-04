package com.justdavis.karl.rpstourney.service.api.auth;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

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
	 * The {@link Path} for {@link #validateAuth(UriInfo, UUID)}.
	 */
	public static final String SERVICE_PATH_VALIDATE = "/validate/";

	/**
	 * The {@link Path} for {@link #getAccount(UriInfo, UUID)}.
	 */
	public static final String SERVICE_PATH_GET_ACCOUNT = "";

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
}