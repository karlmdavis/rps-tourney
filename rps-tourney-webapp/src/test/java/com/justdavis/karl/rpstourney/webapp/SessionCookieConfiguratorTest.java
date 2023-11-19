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
	 * Tests {@link SessionCookieConfigurator#applyConfiguration(ServletContext)} when {@link AppConfig#getBaseUrl()}
	 * has a context path.
	 *
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void configureCookiesWithPath() throws Exception {
		// Build the mocks that will be needed.
		ServletContext servletContext = new MockServletContext();

		// Create and run the SessionCookieConfigurator.
		@SuppressWarnings("deprecation")
		SessionCookieConfigurator cookieConfigurator = new SessionCookieConfigurator(MockConfigLoaderBindingA.class);
		cookieConfigurator.applyConfiguration(servletContext);

		// Verify that things were configured, as expected.
		Assert.assertEquals(".example.com", servletContext.getSessionCookieConfig().getDomain());
		Assert.assertEquals("/foo/bar/", servletContext.getSessionCookieConfig().getPath());
	}

	/**
	 * Tests {@link SessionCookieConfigurator#applyConfiguration(ServletContext)} when {@link AppConfig#getBaseUrl()}
	 * does not have a context path.
	 *
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void configureCookiesWithoutPath() throws Exception {
		// Build the mocks that will be needed.
		ServletContext servletContext = new MockServletContext();

		// Create and run the SessionCookieConfigurator.
		@SuppressWarnings("deprecation")
		SessionCookieConfigurator cookieConfigurator = new SessionCookieConfigurator(MockConfigLoaderBindingB.class);
		cookieConfigurator.applyConfiguration(servletContext);

		// Verify that things were configured, as expected.
		Assert.assertEquals(".example.com", servletContext.getSessionCookieConfig().getDomain());
		Assert.assertEquals("/", servletContext.getSessionCookieConfig().getPath());
	}

	/**
	 * Tests {@link SessionCookieConfigurator#applyConfiguration(ServletContext)} when {@link AppConfig#getBaseUrl()}
	 * has <code>localhost</code> as the domain.
	 *
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void configureCookiesForLocalhost() throws Exception {
		// Build the mocks that will be needed.
		ServletContext servletContext = new MockServletContext();

		// Create and run the SessionCookieConfigurator.
		@SuppressWarnings("deprecation")
		SessionCookieConfigurator cookieConfigurator = new SessionCookieConfigurator(MockConfigLoaderBindingC.class);
		cookieConfigurator.applyConfiguration(servletContext);

		/*
		 * Verify that things were configured, as expected. See the comments for
		 * CookiesUtils.computeCookieDomainProperty(URL), for an explanation of why the domain should be null in this
		 * case.
		 */
		Assert.assertNull(servletContext.getSessionCookieConfig().getDomain());
	}

	/**
	 * A mock Spring {@link Configuration} for use in {@link SessionCookieConfiguratorTest}.
	 */
	@Configuration
	static class MockConfigLoaderBindingA {
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

	/**
	 * A mock Spring {@link Configuration} for use in {@link SessionCookieConfiguratorTest}.
	 */
	@Configuration
	static class MockConfigLoaderBindingB {
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
						URL baseUrl = new URL("https://example.com");
						URL clientServiceRoot = new URL("http://example.com");
						return new AppConfig(baseUrl, clientServiceRoot);
					} catch (MalformedURLException e) {
						throw new BadCodeMonkeyException();
					}
				}
			};
		}
	}

	/**
	 * A mock Spring {@link Configuration} for use in {@link SessionCookieConfiguratorTest}.
	 */
	@Configuration
	static class MockConfigLoaderBindingC {
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
						URL baseUrl = new URL("http://localhost/foo");
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
