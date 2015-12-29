package org.rps.tourney.service.benchmarks.state;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link IServerManager} implementation is used when the benchmarks should
 * run against an already-running server, such as production.
 */
public final class ExistingServerManager implements IServerManager {
	/**
	 * The {@link System#getProperties()} key that specifies the {@link URL} of
	 * the already-running web service to use.
	 */
	public static final String PROP_SERVICE_URL = "rps.service.url";

	/**
	 * The {@link System#getProperties()} key that specifies the {@link URL} of
	 * the already-running web application to use.
	 */
	public static final String PROP_WEBAPP_URL = "rps.webapp.url";

	/**
	 * The {@link System#getProperties()} key that specifies the email
	 * address/login of the admin account for the already-running web service to
	 * use.
	 */
	public static final String PROP_SERVICE_ADMIN_ADDRESS = "rps.service.admin.address";

	/**
	 * The {@link System#getProperties()} key that specifies the password of the
	 * admin account for the already-running web service to use.
	 */
	public static final String PROP_SERVICE_ADMIN_PASSWORD = "rps.service.admin.password";

	private static final Logger LOGGER = LoggerFactory.getLogger(ExistingServerManager.class);

	private final URL serviceUrl;
	private final URL webappUrl;
	private final InternetAddress adminAddress;
	private final String adminPassword;

	/**
	 * Constructs a new {@link ExistingServerManager} instance. Will attempt to
	 * configure itself based on the current Java system properties. If it
	 * fails, it will throw an {@link IllegalArgumentException}.
	 */
	public ExistingServerManager() {
		String serviceUrlText = System.getProperty(PROP_SERVICE_URL);
		if (serviceUrlText == null)
			throw new IllegalArgumentException();
		try {
			this.serviceUrl = new URL(serviceUrlText);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}

		String webappUrlText = System.getProperty(PROP_WEBAPP_URL);
		if (webappUrlText == null)
			throw new IllegalArgumentException();
		try {
			this.webappUrl = new URL(webappUrlText);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}

		String adminAddressText = System.getProperty(PROP_SERVICE_ADMIN_ADDRESS);
		if (adminAddressText == null)
			throw new IllegalArgumentException();
		try {
			this.adminAddress = new InternetAddress(adminAddressText);
		} catch (AddressException e) {
			throw new IllegalArgumentException(e);
		}

		this.adminPassword = System.getProperty(PROP_SERVICE_ADMIN_PASSWORD);
		if (adminPassword == null)
			throw new IllegalArgumentException();
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#getServiceUrl()
	 */
	@Override
	public URL getServiceUrl() {
		return serviceUrl;
	}
	
	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#getWebAppUrl()
	 */
	@Override
	public URL getWebAppUrl() {
		return webappUrl;
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#getAdminAddress()
	 */
	@Override
	public InternetAddress getAdminAddress() {
		return adminAddress;
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#getAdminPassword()
	 */
	@Override
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#tearDown()
	 */
	@Override
	public void tearDown() {
		// Nothing to do here.
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExistingServerManager [serviceUrl=");
		builder.append(serviceUrl);
		builder.append(", adminAddress=");
		builder.append(adminAddress);
		builder.append(", adminPassword=");
		builder.append(adminPassword);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return <code>true</code> if the necessary {@link System#getProperties()}
	 *         entries have been provided to properly configure an
	 *         {@link ExistingServerManager}, <code>false</code> if they have
	 *         not
	 */
	public static boolean isConfigured() {
		try {
			new ExistingServerManager();
		} catch (IllegalArgumentException e) {
			LOGGER.debug("Did not configure for existing server.", e);
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * Generates the Java System properties that would configure the benchmarks
	 * to run against a local Tomcat instance running out of Eclipse on port
	 * <code>9093</code>.
	 * </p>
	 * <p>
	 * Just a convenience method for use in {@link Benchmark} classes'
	 * <code>main(...)</code> methods. Should be used with
	 * {@link ChainedOptionsBuilder#jvmArgsAppend(String...)}.
	 * </p>
	 */
	public static String[] jvmArgsForTomcatWtp() {
		List<String> jvmArgs = new LinkedList<>();

		jvmArgs.add(String.format("-D%s=%s", PROP_SERVICE_URL, "http://localhost:9093/rps-tourney-service-app"));
		jvmArgs.add(String.format("-D%s=%s", PROP_WEBAPP_URL, "http://localhost:9093/rps-tourney-webapp"));

		/*
		 * The admin login details are specified in
		 * /rps-tourney-webapp/src/test/resources/rps-service-config-dev.xml.
		 */
		jvmArgs.add(String.format("-D%s=%s", PROP_SERVICE_ADMIN_ADDRESS, "admin@example.com"));
		jvmArgs.add(String.format("-D%s=%s", PROP_SERVICE_ADMIN_PASSWORD, "password"));

		return jvmArgs.toArray(new String[jvmArgs.size()]);
	}
}
