package com.justdavis.karl.rpstourney.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.justdavis.karl.rpstourney.webapp.config.IConfigLoader;
import com.justdavis.karl.rpstourney.webapp.config.XmlConfigLoader;

/**
 * The Spring {@link Configuration} for the {@link IConfigLoader} to use in {@link SpringProfile#PRODUCTION} and
 * {@link SpringProfile#DEVELOPMENT}.
 */
@Configuration
@Profile(value = { SpringProfile.PRODUCTION, SpringProfile.DEVELOPMENT })
public class ConfigLoaderBindingForProduction {
	/**
	 * @return the {@link IConfigLoader} implementation for the application
	 */
	@Bean
	IConfigLoader configLoader() {
		return new XmlConfigLoader();
	}
}
