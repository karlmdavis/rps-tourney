package com.justdavis.karl.rpstourney.webapp.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * An {@link IConfigLoader} that is intended for use with the application's integration tests.
 */
public final class MockConfigLoader implements IConfigLoader {
	private final AppConfig config;

	/**
	 * Constructs a new {@link MockConfigLoader} instance. This constructor is intended for use in unit tests, as they
	 * can pass in the appropriate parameters manually.
	 *
	 * @param baseUrl
	 *            the value to use for {@link AppConfig#getBaseUrl()}
	 * @param clientServiceRoot
	 *            the value to use for {@link AppConfig#getClientServiceRoot()}
	 */
	public MockConfigLoader(URL baseUrl, URL clientServiceRoot) {
		AppConfig actualConfig = new AppConfig(baseUrl, clientServiceRoot);
		this.config = actualConfig;
	}

	/**
	 * Constructs a new {@link MockConfigLoader} instance.
	 */
	@Inject
	public MockConfigLoader() {
		/*
		 * Once AppConfig has more to it than just a couple of fields, I'd recommend loading the rest of the config from
		 * an XML file using a separate (customized) XmlConfigLoader instance.
		 */

		this(getBaseUrlForManualTesting(), getServiceUrlForManualTesting());
	}

	/**
	 * @return the {@link AppConfig#getClientServiceRoot()} that will work when things are being tested manually via
	 *         <code>WebAppJettyLauncher</code>
	 */
	private static URL getBaseUrlForManualTesting() {
		URL clientServiceRoot;
		try {
			// This is the address that WebAppJettyLauncher will run at.
			clientServiceRoot = new URL("http://localhost:8089/");
		} catch (MalformedURLException e) {
			// Won't happen, as the URL is static.
			throw new BadCodeMonkeyException(e);
		}
		return clientServiceRoot;
	}

	/**
	 * @return the {@link AppConfig#getClientServiceRoot()} that will work when things are being tested manually via
	 *         <code>ServiceAppJettyLauncher</code> and <code>WebAppJettyLauncher</code>
	 */
	private static URL getServiceUrlForManualTesting() {
		URL clientServiceRoot;
		try {
			// This is the address that ServiceAppJettyLauncher will run at.
			clientServiceRoot = new URL("http://localhost:8088/");
		} catch (MalformedURLException e) {
			// Won't happen, as the URL is static.
			throw new BadCodeMonkeyException(e);
		}
		return clientServiceRoot;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webapp.config.IConfigLoader#getConfig()
	 */
	@Override
	public AppConfig getConfig() {
		return config;
	}
}
