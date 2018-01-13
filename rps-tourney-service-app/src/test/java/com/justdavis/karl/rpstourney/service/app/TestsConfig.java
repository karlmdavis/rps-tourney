package com.justdavis.karl.rpstourney.service.app;

import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedMalformedUrlException;
import com.justdavis.karl.rpstourney.service.api.exceptions.UncheckedAddressException;

/**
 * Contains the configuration data needed by this project's integration tests.
 */
public final class TestsConfig {
	/**
	 * The {@link System#getProperties()} key for the {@link #getAdminAddress()}
	 * value to use.
	 */
	public static final String PROP_SERVICE_URL = "rps.service.url";

	/**
	 * The default value for {@link #getServiceUrl()}.
	 */
	public static final String DEFAULT_SERVICE_URL = "http://localhost:9093/rps-tourney-service-app";

	/**
	 * The {@link System#getProperties()} key for the {@link #getAdminAddress()}
	 * value to use.
	 */
	public static final String PROP_SERVICE_ADMIN_ADDRESS = "rps.service.admin.address";

	/**
	 * The default value for {@link #getAdminAddress()}.
	 */
	public static final String DEFAULT_SERVICE_ADMIN_ADDRESS = "admin@example.com";

	/**
	 * The {@link System#getProperties()} key for the
	 * {@link #getAdminPassword()} value to use.
	 */
	public static final String PROP_SERVICE_ADMIN_PASSWORD = "rps.service.admin.password";

	/**
	 * The default value for {@link #getAdminPassword()}.
	 */
	public static final String DEFAULT_SERVICE_ADMIN_PASSWORD = "password";

	private final URL serviceUrl;
	private final InternetAddress adminAddress;
	private final String adminPassword;

	/**
	 * Constructs a new {@link TestsConfig} instance.
	 * 
	 * @param serviceUrl
	 *            the value to use for {@link #getServiceUrl()}
	 * @param adminAddress
	 *            the value to use for {@link #getAdminAddress()}
	 * @param adminPassword
	 *            the value to use for {@link #getAdminPassword()}
	 */
	private TestsConfig(URL serviceUrl, InternetAddress adminAddress, String adminPassword) {
		this.serviceUrl = serviceUrl;
		this.adminAddress = adminAddress;
		this.adminPassword = adminPassword;
	}

	/**
	 * @return the {@link URL} of the web service to use
	 */
	public URL getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * @return the email address/login of the admin account to use for the web
	 *         service
	 */
	public InternetAddress getAdminAddress() {
		return adminAddress;
	}

	/**
	 * @return the password of the admin account to use for the web service
	 */
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * <p>
	 * Returns a {@link TestsConfig} with its properties configured for use with
	 * local development/test environments, or configured as specified in the
	 * relevant Java {@link System#getProperties()} entries:
	 * </p>
	 * <ul>
	 * <li>{@link #PROP_SERVICE_URL}</li>
	 * <li>{@link #PROP_SERVICE_ADMIN_ADDRESS}</li>
	 * <li>{@link #PROP_SERVICE_ADMIN_PASSWORD}</li>
	 * </ul>
	 * 
	 * @return a {@link TestsConfig} with its properties configured for use with
	 *         local development/test environments, or configured as specified
	 *         in the relevant Java {@link System#getProperties()} entries
	 */
	public static TestsConfig createConfigFromSystemProperties() {
		try {
			URL serviceRoot = new URL(System.getProperty(PROP_SERVICE_URL, DEFAULT_SERVICE_URL));
			InternetAddress adminAddress = new InternetAddress(
					System.getProperty(PROP_SERVICE_ADMIN_ADDRESS, DEFAULT_SERVICE_ADMIN_ADDRESS));
			String adminPassword = System.getProperty(PROP_SERVICE_ADMIN_PASSWORD, DEFAULT_SERVICE_ADMIN_PASSWORD);
			return new TestsConfig(serviceRoot, adminAddress, adminPassword);
		} catch (MalformedURLException e) {
			throw new UncheckedMalformedUrlException(e);
		} catch (AddressException e) {
			throw new UncheckedAddressException(e);
		}
	}
}
