package com.justdavis.karl.rpstourney.webservice;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration tests for {@link HelloWorldService}.
 */
public final class HelloWorldServiceIT {
	private static final String SERVER_ADDRESS = "http://localhost:8087/";

	private static EmbeddedServer server;

	/**
	 * Starts an {@link EmbeddedServer}, running {@link GameApplication}.
	 */
	@BeforeClass
	public static void startEmbeddedServer() {
		if (server != null)
			throw new IllegalStateException();

		server = new EmbeddedServer();
		server.startServer();
	}

	/**
	 * Stop the {@link EmbeddedServer}.
	 */
	@AfterClass
	public static void stopEmbeddedServer() throws Exception {
		if (server == null)
			return;

		server.stopServer();
		server = null;
	}

	/**
	 * Tests {@link HelloWorldService#getHelloWorld()}.
	 */
	@Test
	public void getHelloWorld() {
		WebClient client = WebClient.create(SERVER_ADDRESS);

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
		WebClient client = WebClient.create(SERVER_ADDRESS);

		client.accept(MediaType.TEXT_PLAIN);
		client.path("helloworld/echo/foo");
		String response = client.get(String.class);

		Assert.assertEquals("foo", response);
	}
}
