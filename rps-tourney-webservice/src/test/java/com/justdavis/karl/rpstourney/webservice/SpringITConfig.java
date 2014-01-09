package com.justdavis.karl.rpstourney.webservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.rpstourney.webservice.GameApplicationInitializer.AppSpringConfig;
import com.justdavis.karl.rpstourney.webservice.config.IConfigLoader;
import com.justdavis.karl.rpstourney.webservice.config.MockConfigLoader;

/**
 * This Spring {@link Configuration} should be used for integration tests. It
 * imports the application's default {@link Configuration},
 * {@link AppSpringConfig}, and overrides any beans that need to be mocked out.
 */
@Configuration
@Import(AppSpringConfig.class)
public class SpringITConfig {
	/**
	 * @param dsConnectorsManager
	 *            the injected {@link DataSourceConnectorsManager} for the
	 *            application
	 * @return the {@link IConfigLoader} implementation for the application
	 */
	@Bean
	IConfigLoader configLoader(DataSourceConnectorsManager dsConnectorsManager) {
		return new MockConfigLoader(dsConnectorsManager);
	}
}
