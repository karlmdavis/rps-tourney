package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountService;
import com.justdavis.karl.rpstourney.webservice.auth.AuthTokenCookieHelper;

/**
 * This JAX-RS web service allows users to login as a guest. See
 * {@link #loginAsGuest(UriInfo, UUID)} for details.
 */
@Path(GuestAuthService.SERVICE_PATH)
public final class GuestAuthService {
	/**
	 * The {@link Path} that this service will be hosted at.
	 */
	public static final String SERVICE_PATH = "/auth/guest/";

	/**
	 * The in-memory store used to track existing {@link GuestLoginIdentity}
	 * instances. FIXME Should be replaced with actual persistence.
	 */
	public static List<GuestLoginIdentity> existingLogins = new LinkedList<>();

	/**
	 * Constructs a new {@link GuestAuthService} instance.
	 */
	public GuestAuthService() {
	}

	/**
	 * Allows clients to login as a guest. This guest login will be persistent
	 * and will have a "blank" {@link Account} created for it. If the
	 * user/client calling this method is already logged in, this method will
	 * return an error, rather than overwriting the existing login (users must
	 * manually log out, first).
	 * 
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authToken
	 *            the value of the {@link #COOKIE_NAME_AUTH_TOKEN} cookie, or
	 *            <code>null</code> to create a new guest login
	 * @return a {@link Response} containing the new {@link Account} instance,
	 *         along with a {@link #COOKIE_NAME_AUTH_TOKEN} cookie containing
	 *         {@link Account#getAuthToken()}
	 */
	@POST
	@Produces(MediaType.TEXT_XML)
	public Response loginAsGuest(
			@Context UriInfo uriInfo,
			@CookieParam(AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN) UUID authToken) {
		/*
		 * Never, ever allow this method to kill an existing login. If
		 * users/clients want to log out, they must do so explicitly.
		 */
		if (authToken != null)
			return Response.status(Status.CONFLICT).build();

		// Create the new login.
		GuestLoginIdentity login = createLogin();

		// Create an authentication cookie for the new login.
		NewCookie authCookie = AuthTokenCookieHelper.createAuthTokenCookie(
				login.getAccount(), uriInfo.getRequestUri());

		/*
		 * Return a response with the new account that's associated with the
		 * login, and the auth token (as a cookie, so the login is persisted
		 * between requests).
		 */
		return Response.ok().cookie(authCookie).entity(login.getAccount())
				.build();
	}

	/**
	 * @return a new {@link GuestLoginIdentity} (and associated objects)
	 */
	private GuestLoginIdentity createLogin() {
		// Create a random UUID.
		UUID randomAuthToken = UUID.randomUUID();

		// Create a new blank account to associate the login with.
		Account blankAccount = new Account(randomAuthToken);

		/*
		 * Sanity check: does the random UUID already exist? I know this is a
		 * bit stupid, but some internet searches for ways to create UUIDs in
		 * Java indicate that the JDK's UUID.randomUuid() might lead to
		 * collisions. I'll believe it when I see it, but it seems prudent to
		 * check.
		 */
		for (GuestLoginIdentity existingLogin : existingLogins)
			if (existingLogin.getAccount().getAuthToken()
					.equals(randomAuthToken))
				throw new IllegalStateException("Random UUID collision: "
						+ randomAuthToken);

		// Create the new login.
		GuestLoginIdentity newLogin = new GuestLoginIdentity(blankAccount);

		// Persist the new login and account.
		existingLogins.add(newLogin);
		AccountService.existingAccounts.add(newLogin.getAccount());

		return newLogin;
	}
}
