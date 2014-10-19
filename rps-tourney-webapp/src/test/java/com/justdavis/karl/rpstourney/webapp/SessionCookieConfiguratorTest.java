package com.justdavis.karl.rpstourney.webapp;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.webapp.config.AppConfig;
import com.justdavis.karl.rpstourney.webapp.config.IConfigLoader;

/**
 * Unit tests for {@link SessionCookieConfigurator}.
 */
public final class SessionCookieConfiguratorTest {
	/**
	 * Tests
	 * {@link SessionCookieConfigurator#applyConfiguration(ServletContext)}.
	 * 
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void createNewGame() throws Exception {
		// Build the mocks that will be needed.
		ServletContext servletContext = new MockServletContext();

		// Create and run the SessionCookieConfigurator.
		@SuppressWarnings("deprecation")
		SessionCookieConfigurator cookieConfigurator = new SessionCookieConfigurator(
				MockConfigLoaderBinding.class);
		cookieConfigurator.applyConfiguration(servletContext);

		// Verify that things were configured, as expected.
		Assert.assertEquals("example.com", servletContext
				.getSessionCookieConfig().getDomain());
		Assert.assertEquals("/foo/bar/", servletContext
				.getSessionCookieConfig().getPath());
	}

	/**
	 * A mock Spring {@link Configuration} for use in
	 * {@link SessionCookieConfiguratorTest}.
	 */
	@Configuration
	static class MockConfigLoaderBinding {
		/**
		 * @return the mock {@link IConfigLoader}
		 */
		@Bean
		IConfigLoader configLoader() {
			return new IConfigLoader() {
				/**
				 * @see com.justdavis.karl.rpstourney.webapp.config.IConfigLoader#getConfig()
				 */
				@Override
				public AppConfig getConfig() {
					try {
						URL baseUrl = new URL("https://example.com/foo/bar/");
						URL clientServiceRoot = new URL("http://example.com");
						return new AppConfig(baseUrl, clientServiceRoot);
					} catch (MalformedURLException e) {
						throw new BadCodeMonkeyException();
					}
				}
			};
		}
	}
}
