package com.justdavis.karl.rpstourney.webservice;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * A mock implementation of {@link UriInfo} for use in tests. Any methods needed
 * in a given test should be overridden.
 */
public class MockUriInfo implements UriInfo {
	/**
	 * @see javax.ws.rs.core.UriInfo#getPath()
	 */
	@Override
	public String getPath() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getPath(boolean)
	 */
	@Override
	public String getPath(boolean decode) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getPathSegments()
	 */
	@Override
	public List<PathSegment> getPathSegments() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getPathSegments(boolean)
	 */
	@Override
	public List<PathSegment> getPathSegments(boolean decode) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getRequestUri()
	 */
	@Override
	public URI getRequestUri() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getRequestUriBuilder()
	 */
	@Override
	public UriBuilder getRequestUriBuilder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getAbsolutePath()
	 */
	@Override
	public URI getAbsolutePath() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getAbsolutePathBuilder()
	 */
	@Override
	public UriBuilder getAbsolutePathBuilder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getBaseUri()
	 */
	@Override
	public URI getBaseUri() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getBaseUriBuilder()
	 */
	@Override
	public UriBuilder getBaseUriBuilder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getPathParameters()
	 */
	@Override
	public MultivaluedMap<String, String> getPathParameters() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getPathParameters(boolean)
	 */
	@Override
	public MultivaluedMap<String, String> getPathParameters(boolean decode) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getQueryParameters()
	 */
	@Override
	public MultivaluedMap<String, String> getQueryParameters() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getQueryParameters(boolean)
	 */
	@Override
	public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getMatchedURIs()
	 */
	@Override
	public List<String> getMatchedURIs() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getMatchedURIs(boolean)
	 */
	@Override
	public List<String> getMatchedURIs(boolean decode) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#getMatchedResources()
	 */
	@Override
	public List<Object> getMatchedResources() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#resolve(java.net.URI)
	 */
	@Override
	public URI resolve(URI uri) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.ws.rs.core.UriInfo#relativize(java.net.URI)
	 */
	@Override
	public URI relativize(URI uri) {
		throw new UnsupportedOperationException();
	}
}
