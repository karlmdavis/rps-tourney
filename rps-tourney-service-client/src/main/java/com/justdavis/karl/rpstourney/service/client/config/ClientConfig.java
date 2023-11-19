package com.justdavis.karl.rpstourney.service.client.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedUriSyntaxException;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;

/**
 * Models the configuration needed by the various web service client implementations, e.g. {@link GameAuthClient}.
 */
public final class ClientConfig {
	private final URI serviceRoot;

	/**
	 * Constructs a new {@link ClientConfig} instance.
	 *
	 * @param serviceRoot
	 *            the value to use for {@link #getServiceRoot()}
	 */
	public ClientConfig(URI serviceRoot) {
		this.serviceRoot = serviceRoot;
	}

	/**
	 * Constructs a new {@link ClientConfig} instance.
	 *
	 * @param serviceRoot
	 *            the value to use for {@link #getServiceRoot()}
	 */
	public ClientConfig(URL serviceRoot) {
		try {
			this.serviceRoot = serviceRoot.toURI();
		} catch (URISyntaxException e) {
			throw new UncheckedUriSyntaxException(e);
		}
	}

	/**
	 * @return the root {@link URI} that the web service is accessible at, e.g.
	 *         <code>new URI("https://rpstourney.com/service")</code>
	 */
	public URI getServiceRoot() {
		return serviceRoot;
	}
}
