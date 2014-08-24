package com.justdavis.karl.rpstourney.webapp.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * An {@link IConfigLoader} that is intended for use with the application's
 * integration tests.
 */
public final class MockConfigLoader implements IConfigLoader {
	private final AppConfig config;

	/**
	 * Constructs a new {@link MockConfigLoader} instance. This constructor is
	 * intended for use in ITs, as they can pass in the appropriate parameters
	 * manually.
	 * 
	 * @param clientServiceRoot
	 *            the value to use for {@link AppConfig#getClientServiceRoot()}
	 */
	public MockConfigLoader(URI clientServiceRoot) {
		AppConfig actualConfig = new AppConfig(clientServiceRoot);
		this.config = actualConfig;
	}

	/**
	 * Constructs a new {@link MockConfigLoader} instance.
	 */
	@Inject
	public MockConfigLoader() {
		/*
		 * Once AppConfig has more to it than just a single URI, I'd recommend
		 * loading the rest of the config from an XML file using a separate
		 * (customized) XmlConfigLoader instance.
		 */

		this(getServiceUriForManualTesting());
	}

	/**
	 * @return the {@link AppConfig#getClientServiceRoot()} that will work when
	 *         things are being tested manually via
	 *         <code>WebAppJettyLauncher</code>
	 */
	private static URI getServiceUriForManualTesting() {
		URI clientServiceRoot;
		try {
			// This is the address that WebAppJettyLauncher will run at.
			clientServiceRoot = new URI("http://localhost:8088/");
		} catch (URISyntaxException e) {
			// Won't happen, as the URI is static.
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
