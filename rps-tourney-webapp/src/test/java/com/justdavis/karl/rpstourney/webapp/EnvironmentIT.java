package com.justdavis.karl.rpstourney.webapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
	 * 
	 * @throws IOException
	 *             An {@link IOException} will be thrown if an error occurs
	 *             trying to access the web application. Indicates that things
	 *             aren't running correctly.
	 */
	@Test
	public void checkWebApp() throws IOException {
		URL webAppHomePageUrl = new URL(ITUtils.buildWebAppUrl());
		HttpURLConnection webAppHomePageConnection = (HttpURLConnection) webAppHomePageUrl
				.openConnection();

		Assert.assertEquals(200, webAppHomePageConnection.getResponseCode());
	}
}
