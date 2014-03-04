package com.justdavis.karl.rpstourney.service.api.auth.game;

import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * Implementations of this service allows users to login as a guest. See
 * {@link #loginAsGuest(UriInfo, UUID)} for details.
 */
@Path(IGameAuthResource.SERVICE_PATH)
public interface IGameAuthResource {
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
	 * <p>
	 * Allows clients to login with a {@link GameLoginIdentity}. This login will
	 * be persistent.
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
	 * @return the logged-in {@link Account} instance
	 */
	@POST
	@Path(SERVICE_PATH_LOGIN)
	@Produces(MediaType.TEXT_XML)
	Account loginWithGameAccount(InternetAddress emailAddress, String password);

	/**
	 * <p>
	 * Allows clients to create a new {@link GameLoginIdentity}. This login will
	 * be persistent.
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
	 * @return the new/linked {@link Account} instance
	 */
	@POST
	@Path(SERVICE_PATH_CREATE_LOGIN)
	@Produces(MediaType.TEXT_XML)
	Account createGameLogin(
			@FormParam("emailAddress") InternetAddress emailAddress,
			@FormParam("password") String password);
}