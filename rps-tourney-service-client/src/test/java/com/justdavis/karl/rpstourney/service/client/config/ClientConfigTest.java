package com.justdavis.karl.rpstourney.service.client.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ClientConfig}.
 */
public final class ClientConfigTest {
	/**
	 * Tests {@link ClientConfig#getServiceRoot()}.
	 *
	 * @throws URISyntaxException
	 *             (won't happen))
	 */
	@Test
	public void getServiceRoot() throws URISyntaxException {
		URI serviceRoot = new URI("http://example.com/");
		ClientConfig config = new ClientConfig(serviceRoot);
		Assert.assertEquals(serviceRoot, config.getServiceRoot());
	}
}
