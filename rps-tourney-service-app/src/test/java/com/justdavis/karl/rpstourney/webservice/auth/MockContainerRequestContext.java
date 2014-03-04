package com.justdavis.karl.rpstourney.webservice.auth;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.justdavis.karl.rpstourney.webservice.MockUriInfo;

/**
 * A mock {@link ContainerRequestContext} for use in tests.
 */
final class MockContainerRequestContext implements ContainerRequestContext {
	private final Map<String, Object> properties = new HashMap<>();
	private final Map<String, Cookie> cookies = new HashMap<>();
	private SecurityContext securityContext = null;
	private Response abortResponse = null;

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getProperty(java.lang.String)
	 */
	@Override
	public Object getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getPropertyNames()
	 */
	@Override
	public Collection<String> getPropertyNames() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#setProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setProperty(String name, Object object) {
		this.properties.put(name, object);
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#removeProperty(java.lang.String)
	 */
	@Override
	public void removeProperty(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getUriInfo()
	 */
	@Override
	public UriInfo getUriInfo() {
		return new MockUriInfo() {
			/**
			 * @see com.justdavis.karl.rpstourney.webservice.MockUriInfo#getRequestUri()
			 */
			@Override
			public URI getRequestUri() {
				return URI.create("http://localhost/");
			}
		};
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#setRequestUri(java.net.URI)
	 */
	@Override
	public void setRequestUri(URI requestUri) throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#setRequestUri(java.net.URI,
	 *      java.net.URI)
	 */
	@Override
	public void setRequestUri(URI baseUri, URI requestUri)
			throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getRequest()
	 */
	@Override
	public Request getRequest() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getMethod()
	 */
	@Override
	public String getMethod() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#setMethod(java.lang.String)
	 */
	@Override
	public void setMethod(String method) throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getHeaders()
	 */
	@Override
	public MultivaluedMap<String, String> getHeaders() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getHeaderString(java.lang.String)
	 */
	@Override
	public String getHeaderString(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getDate()
	 */
	@Override
	public Date getDate() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getLanguage()
	 */
	@Override
	public Locale getLanguage() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getLength()
	 */
	@Override
	public int getLength() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getMediaType()
	 */
	@Override
	public MediaType getMediaType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getAcceptableMediaTypes()
	 */
	@Override
	public List<MediaType> getAcceptableMediaTypes() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getAcceptableLanguages()
	 */
	@Override
	public List<Locale> getAcceptableLanguages() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getCookies()
	 */
	@Override
	public Map<String, Cookie> getCookies() {
		return cookies;
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#hasEntity()
	 */
	@Override
	public boolean hasEntity() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getEntityStream()
	 */
	@Override
	public InputStream getEntityStream() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#setEntityStream(java.io.InputStream)
	 */
	@Override
	public void setEntityStream(InputStream input) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#getSecurityContext()
	 */
	@Override
	public SecurityContext getSecurityContext() {
		return securityContext;
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#setSecurityContext(javax.ws.rs.core.SecurityContext)
	 */
	@Override
	public void setSecurityContext(SecurityContext context) {
		this.securityContext = context;
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestContext#abortWith(javax.ws.rs.core.Response)
	 */
	@Override
	public void abortWith(Response response) {
		this.abortResponse = response;
	}

	/**
	 * @return the {@link Response} passed to {@link #abortWith(Response)}, or
	 *         <code>null</code> if that hasn't been called at all
	 */
	Response getAbortResponse() {
		return abortResponse;
	}
}