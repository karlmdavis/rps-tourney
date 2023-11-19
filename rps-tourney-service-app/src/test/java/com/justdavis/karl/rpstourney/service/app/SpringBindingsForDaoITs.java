package com.justdavis.karl.rpstourney.service.app;

import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.provisioners.DataSourceProvisionersManager;
import com.justdavis.karl.misc.datasources.provisioners.IProvisioningTargetsProvider;
import com.justdavis.karl.misc.datasources.provisioners.XmlProvisioningTargetsProvider;

/**
 * The Spring {@link Configuration} for the Spring bindings to use in {@link SpringProfile#INTEGRATION_TESTS} (and also
 * the {@link SpringProfile#INTEGRATION_TESTS_WITH_JETTY}).
 */
@Configuration
@Import(SpringBindingsForDaos.class)
@Profile({ SpringProfile.INTEGRATION_TESTS, SpringProfile.INTEGRATION_TESTS_WITH_JETTY })
public class SpringBindingsForDaoITs {
	/**
	 * @param provisionersManager
	 *            the injected {@link DataSourceConnectorsManager} for the application
	 * @return the {@link IProvisioningTargetsProvider} for the application
	 */
	@Bean
	IProvisioningTargetsProvider targetsProvider(DataSourceProvisionersManager provisionersManager) {
		/*
		 * The src/test/resources/datasource-provisioning-targets.xml file contains the location of the database servers
		 * that we can provision databases onto as part of our tests.
		 */
		URL availableTargetsUrl = Thread.currentThread().getContextClassLoader()
				.getResource("datasource-provisioning-targets.xml");
		IProvisioningTargetsProvider targetsProvider = new XmlProvisioningTargetsProvider(provisionersManager,
				availableTargetsUrl);

		return targetsProvider;
	}
}
