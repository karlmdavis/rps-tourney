package com.justdavis.karl.rpstourney.service.app.auth;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.SecurityContext;

import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Clock;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.service.app.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.service.app.auth.AuthenticationFilter;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext.AccountSecurityContextProvider;

/**
 * Unit tests for {@link AuthenticationFilter}.
 */
public class AuthenticationFilterTest {
	/**
	 * Ensures that {@link AuthenticationFilter} sets the
	 * {@link SecurityContext} and {@link AccountSecurityContext} as expected
	 * when an authentication token is provided.
	 * 
	 * @throws IOException
	 *             (not expected to be thrown)
	 */
	@Test
	public void filterRequestForAuth() throws IOException {
		// Create the mock request to use.
		ContainerRequestContext requestContext = new MockContainerRequestContext();
		MockAccountsDao accountsDao = new MockAccountsDao();
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);
		accountsDao.save(account);
		requestContext.getCookies().put(
				AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN,
				AuthTokenCookieHelper.createAuthTokenCookie(authToken,
						requestContext.getUriInfo().getRequestUri()));

		// Run the auth filter.
		AuthenticationFilter authFilter = new AuthenticationFilter();
		authFilter.setAccountDao(accountsDao);
		authFilter.filter(requestContext);

		// Verify that the SecurityContext is set.
		Assert.assertSame(account, requestContext.getSecurityContext()
				.getUserPrincipal());

		// Verify that the AccountSecurityContext is set correctly.
		AccountSecurityContext securityContext = (AccountSecurityContext) requestContext
				.getProperty(AccountSecurityContextProvider.PROP_SECURITY_CONTEXT);
		Assert.assertNotNull(securityContext);
		Assert.assertSame(account, securityContext.getUserPrincipal());
	}

	/**
	 * Ensures that {@link AuthenticationFilter} sets the
	 * {@link SecurityContext} and {@link AccountSecurityContext} as expected
	 * when no authentication token is provided.
	 * 
	 * @throws IOException
	 *             (not expected to be thrown)
	 */
	@Test
	public void filterRequestForNoAuth() throws IOException {
		// Create the mock request to use.
		ContainerRequestContext requestContext = new MockContainerRequestContext();

		// Run the auth filter.
		AuthenticationFilter authFilter = new AuthenticationFilter();
		authFilter.setAccountDao(new MockAccountsDao());
		authFilter.filter(requestContext);

		// Verify that the SecurityContext is set correctly.
		Assert.assertNull(requestContext.getSecurityContext()
				.getUserPrincipal());

		// Verify that the AccountSecurityContext is set correctly.
		AccountSecurityContext securityContext = (AccountSecurityContext) requestContext
				.getProperty(AccountSecurityContextProvider.PROP_SECURITY_CONTEXT);
		Assert.assertNotNull(securityContext);
		Assert.assertNull(securityContext.getUserPrincipal());
	}

	/**
	 * Ensures that {@link AuthenticationFilter} updates the auth token cookie
	 * in the response when an {@link Account} is authenticated for the request.
	 * 
	 * @throws IOException
	 *             (not expected to be thrown)
	 */
	@Test
	public void filterResponseForAuth() throws IOException {
		// Create the auth filter.
		MockAccountsDao accountsDao = new MockAccountsDao();
		AuthenticationFilter authFilter = new AuthenticationFilter();
		authFilter.setAccountDao(accountsDao);

		// Create the mock request to use (run the filter on the request).
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);
		accountsDao.save(account);
		ContainerRequestContext requestContext = new MockContainerRequestContext();
		requestContext.getCookies().put(
				AuthTokenCookieHelper.COOKIE_NAME_AUTH_TOKEN,
				AuthTokenCookieHelper.createAuthTokenCookie(authToken,
						requestContext.getUriInfo().getRequestUri()));
		authFilter.filter(requestContext);

		// Create the mock response to use.
		ContainerResponseContext responseContext = new MockContainerResponseContext();

		// Run the auth filter on the response.
		authFilter.filter(requestContext, responseContext);

		// Ensure that the auth token cookie was set.
		Assert.assertEquals(1, responseContext.getHeaders().size());
		NewCookie authTokenCookie = (NewCookie) responseContext.getHeaders()
				.getFirst(HttpHeaders.SET_COOKIE);
		Assert.assertNotNull(authTokenCookie);
	}

	/**
	 * Ensures that {@link AuthenticationFilter} updates the auth token cookie
	 * in the response when no {@link Account} is authenticated for the request.
	 * 
	 * @throws IOException
	 *             (not expected to be thrown)
	 */
	@Test
	public void filterResponseForNoAuth() throws IOException {
		// Create the auth filter.
		MockAccountsDao accountsDao = new MockAccountsDao();
		AuthenticationFilter authFilter = new AuthenticationFilter();
		authFilter.setAccountDao(accountsDao);

		// Create the mock request to use (run the filter on the request).
		ContainerRequestContext requestContext = new MockContainerRequestContext();
		authFilter.filter(requestContext);

		// Create the mock response to use.
		ContainerResponseContext responseContext = new MockContainerResponseContext();

		// Run the auth filter on the response.
		authFilter.filter(requestContext, responseContext);

		// Ensure that no cookies were set (as expected).
		Assert.assertEquals(0, responseContext.getHeaders().size());
	}

	/**
	 * A mock {@link ContainerResponseContext} for use in tests.
	 */
	private static final class MockContainerResponseContext implements
			ContainerResponseContext {
		private final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getStatus()
		 */
		@Override
		public int getStatus() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#setStatus(int)
		 */
		@Override
		public void setStatus(int code) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getStatusInfo()
		 */
		@Override
		public StatusType getStatusInfo() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#setStatusInfo(javax.ws.rs.core.Response.StatusType)
		 */
		@Override
		public void setStatusInfo(StatusType statusInfo) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getHeaders()
		 */
		@Override
		public MultivaluedMap<String, Object> getHeaders() {
			return headers;
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getStringHeaders()
		 */
		@Override
		public MultivaluedMap<String, String> getStringHeaders() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getHeaderString(java.lang.String)
		 */
		@Override
		public String getHeaderString(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getAllowedMethods()
		 */
		@Override
		public Set<String> getAllowedMethods() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getDate()
		 */
		@Override
		public Date getDate() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getLanguage()
		 */
		@Override
		public Locale getLanguage() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getLength()
		 */
		@Override
		public int getLength() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getMediaType()
		 */
		@Override
		public MediaType getMediaType() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getCookies()
		 */
		@Override
		public Map<String, NewCookie> getCookies() {
			return new HashMap<>();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getEntityTag()
		 */
		@Override
		public EntityTag getEntityTag() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getLastModified()
		 */
		@Override
		public Date getLastModified() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getLocation()
		 */
		@Override
		public URI getLocation() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getLinks()
		 */
		@Override
		public Set<Link> getLinks() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#hasLink(java.lang.String)
		 */
		@Override
		public boolean hasLink(String relation) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getLink(java.lang.String)
		 */
		@Override
		public Link getLink(String relation) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getLinkBuilder(java.lang.String)
		 */
		@Override
		public Builder getLinkBuilder(String relation) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#hasEntity()
		 */
		@Override
		public boolean hasEntity() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getEntity()
		 */
		@Override
		public Object getEntity() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getEntityClass()
		 */
		@Override
		public Class<?> getEntityClass() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getEntityType()
		 */
		@Override
		public Type getEntityType() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#setEntity(java.lang.Object,
		 *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
		 */
		@Override
		public void setEntity(Object entity, Annotation[] annotations,
				MediaType mediaType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#setEntity(java.lang.Object)
		 */
		@Override
		public void setEntity(Object entity) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getEntityAnnotations()
		 */
		@Override
		public Annotation[] getEntityAnnotations() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#getEntityStream()
		 */
		@Override
		public OutputStream getEntityStream() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.container.ContainerResponseContext#setEntityStream(java.io.OutputStream)
		 */
		@Override
		public void setEntityStream(OutputStream outputStream) {
			throw new UnsupportedOperationException();
		}
	}
}
