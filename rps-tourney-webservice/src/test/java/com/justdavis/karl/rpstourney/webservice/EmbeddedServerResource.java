package com.justdavis.karl.rpstourney.webservice;

import java.net.URI;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;

/**
 * <p>
 * {@link EmbeddedServerResource} can be used via JUnit's {@link Rule} or
 * {@link ClassRule} annotations, and allows for test cases to easily make use
 * of an {@link EmbeddedServer} instance, e.g.:
 * </p>
 * <blockquote>
 * 
 * <pre>
 * public static class SomeTest {
 * 	&#064;ClassRule
 * 	public static EmbeddedServerResource server = new EmbeddedServerResource();
 * 
 * 	&#064;Test
 * 	public void testUsingServer() throws IOException {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 */
public final class EmbeddedServerResource extends ExternalResource {
	private EmbeddedServer server = null;

	/**
	 * @see org.junit.rules.ExternalResource#before()
	 */
	@Override
	protected void before() throws Throwable {
		if (server != null)
			throw new IllegalStateException();

		server = new EmbeddedServer();
		server.startServer();
	}

	/**
	 * @see org.junit.rules.ExternalResource#after()
	 */
	@Override
	protected void after() {
		if (server == null)
			return;

		server.stopServer();
		server = null;
	}

	/**
	 * @return the value of {@link EmbeddedServer#getServerBaseAddress()}
	 */
	public URI getServerBaseAddress() {
		return server.getServerBaseAddress();
	}
}
