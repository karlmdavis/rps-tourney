package com.justdavis.karl.rpstourney.service.app;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

/**
 * Integration tests to verify that
 * <a href="https://en.wikipedia.org/wiki/HTTP_404">HTTP 404</a> errors are
 * handled properly.
 */
public final class NotFoundErrorsIT {
	/**
	 * Ensures that an HTTP 404 is returned as expected when requesting an
	 * invalid URL. This is a regression test case for
	 * <a href="https://github.com/karlmdavis/rps-tourney/issues/108">Issue
	 * #108: Web service throws 500 errors at AuthenticationFilter:137 if a 404
	 * is encountered</a>.
	 */
	@Test
	public void return404ForBadUrls() {
		TestsConfig config = TestsConfig.createConfigFromSystemProperties();

		// Create the JAX-RS client (manually, for this).
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceUrl().toString()).path("totallydoesnotexist")
				.request(MediaType.TEXT_PLAIN);

		Response response = requestBuilder.get();

		// Test the service.
		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
}
