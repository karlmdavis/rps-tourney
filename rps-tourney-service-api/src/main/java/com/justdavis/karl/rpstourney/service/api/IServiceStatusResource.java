package com.justdavis.karl.rpstourney.service.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * Implementations of this service allow clients to check the web service's
 * status.
 */
@Path(IServiceStatusResource.SERVICE_PATH)
public interface IServiceStatusResource {
	/**
	 * The {@link Path} that this this resource class' methods will be hosted
	 * at.
	 */
	public static final String SERVICE_PATH = "/status/";

	/**
	 * The {@link Path} for {@link #ping()}.
	 */
	public static final String SERVICE_PATH_PING = "/ping";

	/**
	 * The {@link Path} for {@link #echo(String)}.
	 */
	public static final String SERVICE_PATH_ECHO = "/echo";

	/**
	 * The {@link Path} for {@link #getVersion()}.
	 */
	public static final String SERVICE_PATH_VERSION = "/version";

	/**
	 * The value that will always be returned by the {@link #ping()} method.
	 */
	public static final String PONG = "pong";

	/**
	 * Returns a simple response ({@link #PONG}) to indicate that the service is
	 * up and running.
	 * 
	 * @return the user's/client's {@link Account}
	 */
	@GET
	@Path(SERVICE_PATH_PING)
	@Produces(MediaType.TEXT_PLAIN)
	String ping();

	/**
	 * Returns the specified text back to verify that form submission is
	 * working.
	 * 
	 * @param text
	 *            the value to echo back
	 * @return the value that was passed in via the <code>text</code> parameter
	 */
	@POST
	@Path(SERVICE_PATH_ECHO)
	@Produces(MediaType.TEXT_PLAIN)
	String echo(@FormParam("text") String text);

	/**
	 * Returns the version number of the web service.
	 * 
	 * @return the version number of the web service
	 */
	@GET
	@Path(SERVICE_PATH_VERSION)
	@Produces(MediaType.TEXT_PLAIN)
	String getVersion();
}