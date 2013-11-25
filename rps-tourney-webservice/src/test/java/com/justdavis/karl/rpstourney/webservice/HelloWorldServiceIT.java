package com.justdavis.karl.rpstourney.webservice;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Integration tests for {@link HelloWorldService}.
 */
public final class HelloWorldServiceIT {
	@ClassRule
	public static EmbeddedServerResource server = new EmbeddedServerResource();

	/**
	 * Tests {@link HelloWorldService#getHelloWorld()}.
	 */
	@Test
	public void getHelloWorld() {
		WebClient client = WebClient.create(server.getServerBaseAddress());

		client.accept(MediaType.TEXT_PLAIN);
		client.path("helloworld");
		String response = client.get(String.class);

		Assert.assertEquals("Hello World!", response);
	}

	/**
	 * Tests {@link HelloWorldService#echo(String)}.
	 */
	@Test
	public void echo() {
		WebClient client = WebClient.create(server.getServerBaseAddress());

		client.accept(MediaType.TEXT_PLAIN);
		client.path("helloworld/echo/foo");
		String response = client.get(String.class);

		Assert.assertEquals("foo", response);
	}
}
