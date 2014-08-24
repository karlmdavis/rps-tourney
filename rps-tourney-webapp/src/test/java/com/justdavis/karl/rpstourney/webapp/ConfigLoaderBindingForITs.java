package com.justdavis.karl.rpstourney.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.justdavis.karl.rpstourney.webapp.config.IConfigLoader;
import com.justdavis.karl.rpstourney.webapp.config.MockConfigLoader;

/**
 * The Spring {@link Configuration} for the {@link IConfigLoader} to use in
 * {@link SpringProfile#INTEGRATION_TESTS}.
 */
@Configuration
@Profile({ SpringProfile.INTEGRATION_TESTS })
public class ConfigLoaderBindingForITs {
	/**
	 * @return the {@link IConfigLoader} implementation for the application
	 */
	@Bean
	IConfigLoader configLoader() {
		return new MockConfigLoader();
	}
}
