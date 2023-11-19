package com.justdavis.karl.rpstourney.webapp.config;

import java.net.URL;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the game web application's configuration data. Please note that instances of this class can be
 * marshalled/unmarshalled via JAX-B.
 *
 * @see IConfigLoader
 */
@XmlRootElement
public final class AppConfig {
	@XmlElement
	private final URL baseUrl;

	@XmlElement
	private final URL clientServiceRoot;

	/**
	 * This private no-arg constructor is required by JAX-B.
	 */
	@SuppressWarnings("unused")
	private AppConfig() {
		this.baseUrl = null;
		this.clientServiceRoot = null;
	}

	/**
	 * Constructs a new {@link AppConfig} instance.
	 *
	 * @param baseUrl
	 *            the value to use for {@link #getBaseUrl()}
	 * @param clientServiceRoot
	 *            the value to use for {@link #getClientServiceRoot()}
	 */
	public AppConfig(URL baseUrl, URL clientServiceRoot) {
		if (baseUrl == null)
			throw new IllegalArgumentException();
		if (clientServiceRoot == null)
			throw new IllegalArgumentException();

		this.baseUrl = baseUrl;
		this.clientServiceRoot = clientServiceRoot;
	}

	/**
	 * @return the root {@link URL} that this web application is actually being served from, which all links generated
	 *         by the application will reference
	 * @see BaseUrlInterceptor
	 */
	public URL getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @return the {@link URL} root for the game web service
	 */
	public URL getClientServiceRoot() {
		return clientServiceRoot;
	}
}
