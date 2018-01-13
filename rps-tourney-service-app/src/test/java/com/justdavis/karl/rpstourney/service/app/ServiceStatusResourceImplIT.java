package com.justdavis.karl.rpstourney.service.app;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.IServiceStatusResource;
import com.justdavis.karl.rpstourney.service.client.ServiceStatusClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * Integration tests for {@link ServiceStatusResourceImpl}.
 */
public final class ServiceStatusResourceImplIT {
	/**
	 * Ensures that {@link ServiceStatusResourceImpl} works as expected.
	 */
	@Test
	public void normalUsage() {
		TestsConfig config = TestsConfig.createConfigFromSystemProperties();

		// Create the client.
		ClientConfig clientConfig = new ClientConfig(config.getServiceUrl());
		ServiceStatusClient statusClient = new ServiceStatusClient(clientConfig);

		// Test the service.
		Assert.assertEquals(IServiceStatusResource.PONG, statusClient.ping());
		Assert.assertEquals("foo", statusClient.echo("foo"));
		Assert.assertNotNull(statusClient.getVersion());
		Assert.assertFalse(statusClient.getVersion().trim().isEmpty());
	}
}
