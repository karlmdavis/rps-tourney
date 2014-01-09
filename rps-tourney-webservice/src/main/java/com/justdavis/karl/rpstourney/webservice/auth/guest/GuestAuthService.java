package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.webservice.auth.AuthToken;
import com.justdavis.karl.rpstourney.webservice.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao;
import com.justdavis.karl.rpstourney.webservice.auth.game.GameAuthService;

/**
 * This JAX-RS web service allows users to login as a guest. See
 * {@link #loginAsGuest(UriInfo, UUID)} for details.
 */
@Path(GuestAuthService.SERVICE_PATH)
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuestAuthService {
	/**
	 * The {@link Path} that this service will be hosted at.
	 */
	public static final String SERVICE_PATH = "/auth/guest/";

	private AccountSecurityContext securityContext;
	private UriInfo uriInfo;
	private IAccountsDao accountsDao;
	private IGuestLoginIndentitiesDao loginsDao;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public GuestAuthService() {
	}

	/**
	 * @param securityContext
	 *            the {@link AccountSecurityContext} for the request that the
	 *            {@link GameAuthService} was instantiated to handle
	 */
	@Context
	public void setAccountSecurityContext(AccountSecurityContext securityContext) {
		// Sanity check: null SecurityContext?
		if (securityContext == null)
			throw new IllegalArgumentException();

		this.securityContext = securityContext;
	}

	/**
	 * @param uriInfo
	 *            the {@link UriInfo} for the request that the
	 *            {@link GameAuthService} was instantiated to handle
	 */
	@Context
	public void setUriInfo(UriInfo uriInfo) {
		// Sanity check: null UriInfo?
		if (uriInfo == null)
			throw new IllegalArgumentException();

		this.uriInfo = uriInfo;
	}

	/**
	 * @param accountsDao
	 *            the injected {@link IAccountsDao} to use
	 */
	@Inject
	public void setAccountDao(IAccountsDao accountsDao) {
		// Sanity check: null IAccountsDao?
		if (accountsDao == null)
			throw new IllegalArgumentException();

		this.accountsDao = accountsDao;
	}

	/**
	 * @param loginsDao
	 *            the injected {@link IGuestLoginIndentitiesDao} to use
	 */
	@Inject
	public void setGuestLoginIdentitiesDao(IGuestLoginIndentitiesDao loginsDao) {
		// Sanity check: null IGuestLoginIndentitiesDao?
		if (loginsDao == null)
			throw new IllegalArgumentException();

		this.loginsDao = loginsDao;
	}

	/**
	 * Allows clients to login as a guest. This guest login will be persistent
	 * and will have a "blank" {@link Account} created for it. If the
	 * user/client calling this method is already logged in, this method will
	 * return an error, rather than overwriting the existing login (users must
	 * manually log out, first).
	 * 
	 * @return a {@link Response} containing the new {@link Account} instance,
	 *         along with a {@link #COOKIE_NAME_AUTH_TOKEN} cookie containing
	 *         {@link Account#getAuthToken()}
	 */
	@POST
	@Produces(MediaType.TEXT_XML)
	@Transactional
	public Response loginAsGuest() {
		/*
		 * Never, ever allow this method to kill an existing login. If
		 * users/clients want to log out, they must do so explicitly.
		 */
		if (securityContext.getUserPrincipal() != null)
			return Response.status(Status.CONFLICT).build();

		// Create the new login.
		GuestLoginIdentity login = createLogin();

		// Create an authentication cookie for the new login.
		AuthToken authToken = accountsDao.selectOrCreateAuthToken(login
				.getAccount());
		NewCookie authCookie = AuthTokenCookieHelper.createAuthTokenCookie(
				authToken, uriInfo.getRequestUri());

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
		// Create a new blank account to associate the login with.
		Account blankAccount = new Account();

		// Create and persist the new login.
		GuestLoginIdentity newLogin = new GuestLoginIdentity(blankAccount);
		loginsDao.save(newLogin);

		return newLogin;
	}
}
