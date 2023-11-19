package com.justdavis.karl.rpstourney.service.app;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.jetty.EmbeddedServer;

/**
 * Integration tests to verify that <a href="https://en.wikipedia.org/wiki/HTTP_404">HTTP 404</a> errors are handled
 * properly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringBindingsForWebServiceITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class NotFoundErrorsIT {
	@Inject
	private EmbeddedServer server;

	/**
	 * Ensures that an HTTP 404 is returned as expected when requesting an invalid URL. This is a regression test case
	 * for <a href="https://github.com/karlmdavis/rps-tourney/issues/108">Issue #108: Web service throws 500 errors at
	 * AuthenticationFilter:137 if a 404 is encountered</a>.
	 */
	@Test
	public void return404ForBadUrls() {
		// Create the JAX-RS client (manually, for this).
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(server.getServerBaseAddress()).path("totallydoesnotexist")
				.request(MediaType.TEXT_PLAIN);

		Response response = requestBuilder.get();

		// Test the service.
		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
}
