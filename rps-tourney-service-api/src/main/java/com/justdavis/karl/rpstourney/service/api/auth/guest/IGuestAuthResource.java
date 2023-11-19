package com.justdavis.karl.rpstourney.service.api.auth.guest;

import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * This service allows users to login as a guest. See {@link #loginAsGuest(UriInfo, UUID)} for details.
 */
@Path(IGuestAuthResource.SERVICE_PATH)
public interface IGuestAuthResource {

	/**
	 * The {@link Path} that this service will be hosted at.
	 */
	public static final String SERVICE_PATH = "/auth/guest/";

	/**
	 * <p>
	 * Allows clients to login as a guest. This guest login will be persistent and will have a "blank" {@link Account}
	 * created for it.
	 * </p>
	 * <p>
	 * If the user/client calling this method is already logged in, this method will return an error, rather than
	 * overwriting the existing login (users must manually log out, first).
	 * </p>
	 *
	 * @return the new {@link Account} instance
	 */
	@POST
	@Produces(MediaType.TEXT_XML)
	Account loginAsGuest();
}
