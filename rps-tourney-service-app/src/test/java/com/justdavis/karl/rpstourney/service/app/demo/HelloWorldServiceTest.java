package com.justdavis.karl.rpstourney.service.app.demo;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link HelloWorldServiceImpl}.
 */
public final class HelloWorldServiceTest {
	/**
	 * Tests {@link HelloWorldServiceImpl#getHelloWorld()}.
	 */
	@Test
	public void getHelloWorld() {
		IHelloWorldService helloService = new HelloWorldServiceImpl();
		Assert.assertEquals("Hello World!", helloService.getHelloWorld());
	}

	/**
	 * Tests {@link HelloWorldServiceImpl#echo(String)}.
	 */
	@Test
	public void echo() {
		IHelloWorldService helloService = new HelloWorldServiceImpl();
		Assert.assertEquals("foo", helloService.echo("foo"));
	}
}
