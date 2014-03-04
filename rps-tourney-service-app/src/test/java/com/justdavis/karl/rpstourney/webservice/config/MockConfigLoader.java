package com.justdavis.karl.rpstourney.webservice.config;

import java.io.InputStream;

import javax.inject.Inject;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;

/**
 * An {@link IConfigLoader} that is intended for use with the application's
 * integration tests.
 */
public final class MockConfigLoader extends XmlConfigLoader {
	/**
	 * Constructs a new {@link XmlConfigLoader} instance.
	 * 
	 * @param dsConnectorsManager
	 *            the application's {@link DataSourceConnectorsManager}
	 */
	@Inject
	public MockConfigLoader(DataSourceConnectorsManager dsConnectorsManager) {
		super(dsConnectorsManager);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.config.XmlConfigLoader#retrieveConfigFile()
	 */
	@Override
	protected InputStream retrieveConfigFile() {
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("game-config-its.xml");
	}
}
