package com.justdavis.karl.rpstourney.webservice.auth;

import java.io.IOException;
import java.util.ListIterator;
import java.util.UUID;

import javax.ws.rs.BindingPriority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.SecurityContext;

import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext.AccountSecurityContextProvider;

/**
 * <p>
 * {@link AuthenticationFilter} will filter both requests and responses, to
 * apply the application's authentication scheme to them.
 * </p>
 * <p>
 * For requests, it checks for authentication token cookies, and if found, sets
 * the {@link SecurityContext} for the rest of the request to use the logged-in
 * {@link Account}. For responses, it sets/refreshes those authentication token
 * cookies, to ensure that the expiration date and other properties of those
 * cookies are updated every time a client makes a webservice call.
 * </p>
 */
@BindingPriority(BindingPriority.AUTHENTICATION)
public final class AuthenticationFilter implements ContainerRequestFilter,
		ContainerResponseFilter {
	/**
	 * The custom authentication scheme ID used by the application.
	 * 
	 * @see SecurityContext#getAuthenticationScheme()
	 */
	public static String AUTH_SCHEME = "GameAuth";

	/**
	 * This callback will be passed each request towards the start of the
	 * processing chain for it. It checks the request for a cookie containing an
	 * authentication token. If such a cookie is found, it sets the
	 * {@link SecurityContext} for the request to reflect the {@link Account} of
	 * the user/client making the request.
	 * 
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		// Is there an auth token cookie? If so, grab the Account for it.
		Cookie authTokenCookie = requestContext.getCookies().get(
				AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN);
		Account userAccount = null;
		if (authTokenCookie != null) {
			UUID authTokenUuid = UUID.fromString(authTokenCookie.getValue());
			userAccount = AccountService.getAccount(authTokenUuid);
		}

		// Was the request made over SSL?
		boolean secure = requestContext.getUriInfo().getRequestUri()
				.getScheme().equals("https");

		// Set the SecurityContext for the rest of the request.
		AccountSecurityContext securityContext = new AccountSecurityContext(
				userAccount, secure);
		requestContext.setSecurityContext(securityContext);

		/*
		 * FIXME The getSecurityContext() method is broken in CXF 2.7. From
		 * looking at the patches, it seems that it is fixed in 3.0 (whenever
		 * that'll be released). In the meantime, we'll also save the
		 * AccountSecurityContext this way. See AccountSecurityContextProvider
		 * for instructions on accessing this.
		 */
		requestContext.setProperty(
				AccountSecurityContextProvider.PROP_SECURITY_CONTEXT,
				securityContext);
	}

	/**
	 * This callback will be passed each request and response towards the end of
	 * the processing chain for it. It checks the {@link SecurityContext} for
	 * the request, and if it contains a valid {@link Account} for the
	 * user/client that made the request, it will set/refresh a cookie
	 * containing the authentication token for that {@link Account}.
	 * 
	 * @see javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.ContainerRequestContext,
	 *      javax.ws.rs.container.ContainerResponseContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		// Pull the AccountSecurityContext for the request that's wrapping up.
		AccountSecurityContext securityContext = (AccountSecurityContext) requestContext
				.getProperty(AccountSecurityContextProvider.PROP_SECURITY_CONTEXT);

		Account userAccount = securityContext.getUserPrincipal();
		if (userAccount != null) {
			// Create the Cookie we'll drop in to the response.
			NewCookie authTokenCookie = AuthTokenCookieHelper
					.createAuthTokenCookie(userAccount, requestContext
							.getUriInfo().getRequestUri());

			// Are there any cookies already in the response?
			if (responseContext.getHeaders()
					.containsKey(HttpHeaders.SET_COOKIE)) {
				ListIterator<Object> cookies = responseContext.getHeaders()
						.get(HttpHeaders.SET_COOKIE).listIterator();
				while (cookies.hasNext()) {
					String cookieBody = cookies.next().toString();
					if (cookieBody.startsWith(authTokenCookie.getName()))
						cookies.set(authTokenCookie);
				}
			} else {
				responseContext.getHeaders().add(HttpHeaders.SET_COOKIE,
						authTokenCookie);
			}
		}

		/*
		 * Note that we're not explicitly clearing the cookie. If the request
		 * provided an invalid one, we'll leave it be. It'll probably keep on
		 * providing it in future requests, too. Perhaps we should? Not sure.
		 */
	}
}
