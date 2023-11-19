package org.rps.tourney.benchmarks.serverutils;

import java.net.URL;

import javax.mail.internet.InternetAddress;

import org.openjdk.jmh.annotations.TearDown;

/**
 * Encapsulates the information that the application's benchmarks need from {@link ServerState}: where the applications
 * are, logins to use for them, etc.
 */
public interface IServerManager {
	/**
	 * @return the {@link URL} of the game web service being benchmarked
	 */
	URL getServiceUrl();

	/**
	 * @return the {@link URL} of the game web application being benchmarked
	 */
	URL getWebAppUrl();

	/**
	 * @return the web service's admin email address/login
	 */
	InternetAddress getAdminAddress();

	/**
	 * @return the web service's admin password
	 */
	String getAdminPassword();

	/**
	 * Call this method as part of the benchmarks' {@link TearDown} to release any resources that were acquired to start
	 * or interact with this {@link IServerManager}.
	 */
	void tearDown();
}
