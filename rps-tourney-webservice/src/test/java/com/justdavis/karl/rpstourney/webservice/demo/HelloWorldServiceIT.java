package com.justdavis.karl.rpstourney.webservice.demo;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.EmbeddedServerResource;
import com.justdavis.karl.rpstourney.webservice.demo.HelloWorldServiceImpl;

/**
 * Integration tests for {@link HelloWorldServiceImpl}.
 */
public final class HelloWorldServiceIT {
	@ClassRule
	public static EmbeddedServerResource server = new EmbeddedServerResource();

	/**
	 * Tests {@link HelloWorldServiceImpl#getHelloWorld()}.
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
	 * Tests {@link HelloWorldServiceImpl#echo(String)}.
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
