package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.rpstourney.webservice.auth.Account;

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
	 * The name of the {@link Cookie} used to track the
	 * {@link GuestLoginIdentity#getAuthToken()} value.
	 */
	public static final String COOKIE_NAME_AUTH_TOKEN = "guestAuthToken";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GuestAuthService.class);

	/**
	 * The in-memory store used to track existing {@link GuestLoginIdentity}
	 * instances. FIXME Should be replaced with actual persistence.
	 */
	private static Map<UUID, GuestLoginIdentity> existingLogins;

	/**
	 * Constructs a new {@link GuestAuthService} instance.
	 */
	public GuestAuthService() {
		if (existingLogins == null)
			existingLogins = new HashMap<>();
	}

	/**
	 * Allows clients to login with as a guest. This guest login will be
	 * persistent and will have a blank {@link Account} created for it. The
	 * login will be tracked in the {@link #COOKIE_NAME_AUTH_TOKEN} cookie
	 * returned as part of the response. If this cookie is already present and
	 * identifies an existing login, this method won't do anything.
	 * 
	 * @param uriInfo
	 *            the {@link UriInfo} of the client request
	 * @param authTokenUuid
	 *            the value of the {@link #COOKIE_NAME_AUTH_TOKEN} cookie, or
	 *            <code>null</code> to create a new guest login
	 * @return a {@link Response} containing a new {@link GuestLoginIdentity}
	 *         instance (or the pre-existing one, if a valid authentication
	 *         token was provided), along with a {@link #COOKIE_NAME_AUTH_TOKEN}
	 *         cookie containing {@link GuestLoginIdentity#getAuthToken()}
	 */
	@POST
	@Produces(MediaType.TEXT_XML)
	public Response loginAsGuest(@Context UriInfo uriInfo,
			@CookieParam(COOKIE_NAME_AUTH_TOKEN) UUID authTokenUuid) {
		// Try to retrieve the existing login (if any) for the auth token.
		GuestLoginIdentity guestLogin = getLogin(authTokenUuid);

		// No existing login was found, so create a new one.
		if (guestLogin == null)
			guestLogin = createLogin();

		// Grab the auth token for that login record and create a cookie for it.
		String authTokenString = guestLogin.getAuthToken().toString();
		NewCookie authCookie = new NewCookie(COOKIE_NAME_AUTH_TOKEN,
				authTokenString, "/", uriInfo.getBaseUri().toString(),
				Cookie.DEFAULT_VERSION, "", 60 * 60 * 24 * 365 * 1, true);

		/*
		 * JAX-RS doesn't have support for the "HttpOnly" flag until the full
		 * 2.0 release. This is a hack to work around that.
		 */
		String authCookieValue = authCookie.toString() + ";HttpOnly";

		/*
		 * Return a response with the login record, the associated account, and
		 * the auth token (as a cookie, so the login is persisted between
		 * requests).
		 */
		return Response.ok().header("Set-Cookie", authCookieValue)
				.entity(guestLogin).build();
	}

	/**
	 * @param authTokenUuid
	 *            the value to match against
	 *            {@link GuestLoginIdentity#getAuthToken()}
	 * @return the {@link GuestLoginIdentity} instance with the specified
	 *         {@link GuestLoginIdentity#getAuthToken()} value, or
	 *         <code>null</code> if no match was found
	 */
	private GuestLoginIdentity getLogin(UUID authTokenUuid) {
		GuestLoginIdentity existingLogin = existingLogins.get(authTokenUuid);
		if (authTokenUuid != null && existingLogin == null) {
			/*
			 * If there was an auth token, a match for it wasn't found. Either
			 * someone's trying to hack, or something's gone fairly badly wrong.
			 */
			LOGGER.warn(
					"Unable to find an existing guest login for auth token: {}",
					authTokenUuid);
		}

		return existingLogin;
	}

	/**
	 * @return a new {@link GuestLoginIdentity} (and associated objects)
	 */
	private GuestLoginIdentity createLogin() {
		// Create a new blank account to associate the login with.
		Account blankAccount = new Account();

		// Create a random UUID.
		UUID randomAuthToken = UUID.randomUUID();

		/*
		 * Sanity check: does the random UUID already exist? I know this is a
		 * bit stupid, but some internet searches for ways to create UUIDs in
		 * Java indicate that the JDK's UUID.randomUuid() might lead to
		 * collisions. I'll believe it when I see it, but it seems prudent to
		 * check.
		 */
		if (existingLogins.containsKey(randomAuthToken))
			throw new IllegalStateException("Random UUID collision: "
					+ randomAuthToken);

		// Create the new login.
		GuestLoginIdentity newLogin = new GuestLoginIdentity(blankAccount,
				randomAuthToken);

		// Persist the new login.
		existingLogins.put(randomAuthToken, newLogin);

		return newLogin;
	}
}
