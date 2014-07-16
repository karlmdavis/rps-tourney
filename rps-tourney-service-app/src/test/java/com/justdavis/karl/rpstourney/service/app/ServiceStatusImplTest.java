package com.justdavis.karl.rpstourney.service.app;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.IServiceStatusResource;

/**
 * Unit tests for {@link ServiceStatusResourceImpl}.
 */
public final class ServiceStatusImplTest {
	/**
	 * Ensures that {@link ServiceStatusResourceImpl} works as expected.
	 */
	@Test
	public void normalUsage() {
		ServiceStatusResourceImpl serviceStatus = new ServiceStatusResourceImpl();
		Assert.assertEquals(IServiceStatusResource.PONG, serviceStatus.ping());
		Assert.assertEquals("foo", serviceStatus.echo("foo"));
		Assert.assertNotNull(serviceStatus.getVersion());
		Assert.assertFalse(serviceStatus.getVersion().trim().isEmpty());
	}
}
