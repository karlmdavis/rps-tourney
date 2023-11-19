package com.justdavis.karl.rpstourney.service.app.config;

import javax.inject.Inject;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.hsql.HsqlCoordinates;
import com.justdavis.karl.misc.datasources.provisioners.hsql.HsqlProvisioner;
import com.justdavis.karl.misc.datasources.provisioners.hsql.HsqlProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.hsql.HsqlProvisioningTarget;

/**
 * An {@link IConfigLoader} that is intended for use with the application's integration tests.
 */
public final class MockConfigLoader implements IConfigLoader {
	private final ServiceConfig config;

	/**
	 * Constructs a new {@link MockConfigLoader} instance.
	 *
	 * @param dsConnectorsManager
	 *            the application's {@link DataSourceConnectorsManager}
	 */
	@Inject
	public MockConfigLoader(DataSourceConnectorsManager dsConnectorsManager) {
		/*
		 * Once ServiceConfig has more to it than just a set of DB coords, I'd recommend loading the rest of the config
		 * from an XML file using a separate (customized) XmlConfigLoader instance.
		 */

		HsqlProvisioningRequest hsqlProvisioningRequest = HsqlProvisioningRequest
				.requestForRandomDatabase("integrationTests");
		HsqlProvisioner hsqlProvisioner = new HsqlProvisioner();
		HsqlCoordinates coords = hsqlProvisioner.provision(new HsqlProvisioningTarget(), hsqlProvisioningRequest);
		AdminAccountConfig adminAccountConfig = new AdminAccountConfig("admin@example.com", "password");
		ServiceConfig actualConfig = new ServiceConfig(coords, adminAccountConfig);

		this.config = actualConfig;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.config.IConfigLoader#getConfig()
	 */
	@Override
	public ServiceConfig getConfig() {
		return config;
	}
}
