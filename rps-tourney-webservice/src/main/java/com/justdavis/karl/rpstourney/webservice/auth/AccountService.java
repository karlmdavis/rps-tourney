package com.justdavis.karl.rpstourney.webservice.auth;

import java.security.Principal;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * This JAX-RS web service allows users to manage their {@link Account}.
 */
@Path(AccountService.SERVICE_PATH)
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService {
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
	 * The {@link SecurityContext} of the current request.
	 */
	private SecurityContext securityContext;

	/**
	 * This public, default, no-arg constructor is required by Spring (for
	 * request-scoped beans).
	 */
	public AccountService() {
	}

	/**
	 * @param securityContext
	 *            the {@link AccountSecurityContext} for the request that the
	 *            {@link AccountService} was instantiated to handle
	 */
	@Context
	public void setAccountSecurityContext(AccountSecurityContext securityContext) {
		// Sanity check: null SecurityContext?
		if (securityContext == null)
			throw new IllegalArgumentException();

		this.securityContext = securityContext;
	}

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
	@RolesAllowed({ SecurityRole.ID_USERS })
	public Account validateAuth() {
		// Just pass it through to getAccount().
		return getAccount();
	}

	/**
	 * Returns the {@link Account} for the requesting user/client.
	 * 
	 * @return the user's/client's {@link Account}
	 */
	@GET
	@Path(SERVICE_PATH_GET_ACCOUNT)
	@Produces(MediaType.TEXT_XML)
	@RolesAllowed({ SecurityRole.ID_USERS })
	public Account getAccount() {
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
		return userAccount;
	}
}
