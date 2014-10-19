package com.justdavis.karl.rpstourney.webapp;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import com.justdavis.karl.rpstourney.webapp.config.AppConfig;
import com.justdavis.karl.rpstourney.webapp.config.IConfigLoader;

/**
 * This class is responsible for configuring the application's
 * {@link SessionCookieConfig}. Without this configuration, the
 * <code>JSESSIONID</code> cookies produced by this application will have the
 * wrong <code>Domain</code> and <code>Path</code> attributes when running
 * behind a proxy, which will effectively prevent sessions from working
 * correctly.
 */
public final class SessionCookieConfigurator {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SessionCookieConfigurator.class);

	private final Class<?> springConfig;

	/**
	 * Constructs a new {@link SessionCookieConfigurator} instance.
	 */
	public SessionCookieConfigurator() {
		this(ConfigLoaderBindingForProduction.class);
	}

	/**
	 * <p>
	 * Constructs a new {@link SessionCookieConfigurator} instance.
	 * </p>
	 * <p>
	 * Marked as non-public and deprecated: this constructor is only intended
	 * for use in unit tests.
	 * </p>
	 * 
	 * @param springConfig
	 *            the Spring {@link Configuration} class to locate the
	 *            {@link IConfigLoader} bean from
	 */
	@Deprecated
	SessionCookieConfigurator(Class<?> springConfig) {
		if (springConfig == null)
			throw new IllegalArgumentException();

		this.springConfig = springConfig;
	}

	/**
	 * Properly configure the {@link ServletContext}'s
	 * {@link SessionCookieConfig}.
	 */
	public void applyConfiguration(ServletContext servletContext) {
		/*
		 * We log our entry and exit to this method at 'info', so that the log
		 * makes more sense. The Spring startup involved here will log a bunch
		 * of stuff at 'info', and would be pretty confusing without some
		 * "bookmarks".
		 */

		LOGGER.info("Adjusting SessionCookieConfig...");
		AbstractApplicationContext springContext = getSpringContext();
		try {
			// Pull the AppConfig out of the Spring context.
			IConfigLoader configLoader = springContext
					.getBean(IConfigLoader.class);
			AppConfig appConfig = configLoader.getConfig();

			// Grab the app's base URL and split it into domain and path.
			URL baseUrl = appConfig.getBaseUrl();
			String domain = baseUrl.getHost();
			String path = baseUrl.getPath();

			// Configure the SessionCookieConfig.
			SessionCookieConfig sessionCookieConfig = servletContext
					.getSessionCookieConfig();
			sessionCookieConfig.setDomain(domain);
			sessionCookieConfig.setPath(path);

			LOGGER.warn("Session cookies configured: domain='{}', path='{}'",
					domain, path);
		} finally {
			if (springContext != null)
				springContext.close();
		}
		LOGGER.info("Adjusted SessionCookieConfig...");
	}

	/**
	 * @return a Spring {@link AbstractApplicationContext} that can be used to
	 *         retrieve the application's {@link IConfigLoader}
	 */
	private AbstractApplicationContext getSpringContext() {
		/*
		 * There does not seem to be a point in this web application's lifecycle
		 * where both the Spring context and ServletContext are available.
		 * ServletContextListeners are one likely option, but apparently they
		 * aren't allowed to modify the ServletContext after it's initialized.
		 * So as an alternative, we create a small "bootstrap" Spring context
		 * here, which will only be used to load the AppConfig, and is then
		 * disposed of.
		 */
		AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
		springContext.getEnvironment().setDefaultProfiles(
				SpringProfile.PRODUCTION);

		springContext.register(springConfig);
		springContext.refresh();

		return springContext;
	}
}
