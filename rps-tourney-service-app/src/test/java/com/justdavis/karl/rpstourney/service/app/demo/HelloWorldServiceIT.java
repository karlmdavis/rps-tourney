package com.justdavis.karl.rpstourney.service.app.demo;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.app.SpringITConfigWithJetty;
import com.justdavis.karl.rpstourney.service.app.demo.HelloWorldServiceImpl;

/**
 * Integration tests for {@link HelloWorldServiceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringITConfigWithJetty.class })
@WebAppConfiguration
public final class HelloWorldServiceIT {
	@Inject
	private EmbeddedServer server;

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
