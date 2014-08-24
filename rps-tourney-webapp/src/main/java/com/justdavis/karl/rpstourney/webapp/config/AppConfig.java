package com.justdavis.karl.rpstourney.webapp.config;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the game web application's configuration data. Please note that
 * instances of this class can be marshalled/unmarshalled via JAX-B.
 * 
 * @see IConfigLoader
 */
@XmlRootElement
public final class AppConfig {
	@XmlElement
	private final URI clientServiceRoot;

	/**
	 * This private no-arg constructor is required by JAX-B.
	 */
	@SuppressWarnings("unused")
	private AppConfig() {
		this.clientServiceRoot = null;
	}

	/**
	 * Constructs a new {@link AppConfig} instance.
	 * 
	 * @param clientServiceRoot
	 *            the value to use for {@link #getClientServiceRoot()}
	 */
	public AppConfig(URI clientServiceRoot) {
		this.clientServiceRoot = clientServiceRoot;
	}

	/**
	 * @return the {@link URI} root for the game web service
	 */
	public URI getClientServiceRoot() {
		return clientServiceRoot;
	}
}
