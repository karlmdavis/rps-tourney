package com.justdavis.karl.rpstourney.webservice;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link HelloWorldService}.
 */
public final class HelloWorldServiceTest {
	/**
	 * Tests {@link HelloWorldService#getHelloWorld()}.
	 */
	@Test
	public void getHelloWorld() {
		HelloWorldService helloService = new HelloWorldService();
		Assert.assertEquals("Hello World!", helloService.getHelloWorld());
	}

	/**
	 * Tests {@link HelloWorldService#echo(String)}.
	 */
	@Test
	public void echo() {
		HelloWorldService helloService = new HelloWorldService();
		Assert.assertEquals("foo", helloService.echo("foo"));
	}
}
