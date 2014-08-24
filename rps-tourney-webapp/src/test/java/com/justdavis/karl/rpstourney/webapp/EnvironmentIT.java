package com.justdavis.karl.rpstourney.webapp;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.IServiceStatusResource;
import com.justdavis.karl.rpstourney.service.client.ServiceStatusClient;

/**
 * Some basic integration tests that ensure that Jetty and everything else seem
 * to be working as expected by the other ITs.
 */
public final class EnvironmentIT {
	/**
	 * Verify that the web service looks to be up & running.
	 */
	@Test
	public void checkWebService() {
		// Call /status/ping to make sure things are up.
		ServiceStatusClient statusClient = new ServiceStatusClient(
				ITUtils.createClientConfig());
		Assert.assertEquals(IServiceStatusResource.PONG, statusClient.ping());
	}

	/**
	 * Verify that the web application looks to be up & running.
	 */
	@Test
	public void checkWebApp() {
		// TODO
	}
}
