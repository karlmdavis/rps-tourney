package com.justdavis.karl.rpstourney.service.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.app.config.XmlConfigLoader;

/**
 * The Spring {@link Configuration} for the {@link IConfigLoader} to use in
 * {@link SpringProfile#PRODUCTION}.
 */
@Configuration
@Profile(value = SpringProfile.PRODUCTION)
public class ConfigLoaderBindingForProduction {
	/**
	 * @param dsConnectorsManager
	 *            the injected {@link DataSourceConnectorsManager} for the
	 *            application
	 * @return the {@link IConfigLoader} implementation for the application
	 */
	@Bean
	IConfigLoader configLoader(DataSourceConnectorsManager dsConnectorsManager) {
		return new XmlConfigLoader(dsConnectorsManager);
	}
}
