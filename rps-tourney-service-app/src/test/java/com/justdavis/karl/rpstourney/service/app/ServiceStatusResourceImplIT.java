package com.justdavis.karl.rpstourney.service.app;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.IServiceStatusResource;
import com.justdavis.karl.rpstourney.service.client.ServiceStatusClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * Integration tests for {@link ServiceStatusResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringBindingsForWebServiceITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class ServiceStatusResourceImplIT {
	@Inject
	private EmbeddedServer server;

	/**
	 * Ensures that {@link ServiceStatusResourceImpl} works as expected.
	 */
	@Test
	public void normalUsage() {
		// Create the client.
		ClientConfig clientConfig = new ClientConfig(
				server.getServerBaseAddress());
		ServiceStatusClient statusClient = new ServiceStatusClient(clientConfig);

		// Test the service.
		Assert.assertEquals(IServiceStatusResource.PONG, statusClient.ping());
		Assert.assertEquals("foo", statusClient.echo("foo"));
		Assert.assertNotNull(statusClient.getVersion());
		Assert.assertFalse(statusClient.getVersion().trim().isEmpty());
	}
}
