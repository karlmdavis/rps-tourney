package com.justdavis.karl.rpstourney.service.app.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.justdavis.karl.misc.datasources.IDataSourceCoordinates;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.app.auth.AdminAccountInitializer;

/**
 * Represents the game web service application's configuration data. Please note
 * that instances of this class can be marshalled/unmarshalled via JAX-B.
 * 
 * @see IConfigLoader
 */
@XmlRootElement
public final class ServiceConfig {
	@XmlElementRef
	private final IDataSourceCoordinates dataSourceCoordinates;

	@XmlElement(name = "admin")
	private final AdminAccountConfig adminAccountConfig;

	/**
	 * This private no-arg constructor is required by JAX-B.
	 */
	@SuppressWarnings("unused")
	private ServiceConfig() {
		this.dataSourceCoordinates = null;
		this.adminAccountConfig = null;
	}

	/**
	 * Constructs a new {@link ServiceConfig} instance.
	 * 
	 * @param dataSourceCoordinates
	 *            the value to use for {@link #getDataSourceCoordinates()}
	 * @param adminAccountConfig
	 *            the value to use for {@link #getAdminAccountConfig()}
	 */
	public ServiceConfig(IDataSourceCoordinates dataSourceCoordinates, AdminAccountConfig adminAccountConfig) {
		this.dataSourceCoordinates = dataSourceCoordinates;
		this.adminAccountConfig = adminAccountConfig;
	}

	/**
	 * @return the {@link IDataSourceCoordinates} that identify the database
	 *         that the application will persist all of its relational data to
	 */
	public IDataSourceCoordinates getDataSourceCoordinates() {
		return dataSourceCoordinates;
	}

	/**
	 * @return the {@link AdminAccountConfig} that specifies the application's
	 *         default {@link SecurityRole#ADMINS} {@link Account}, which will
	 *         be managed by {@link AdminAccountInitializer} at application
	 *         startup
	 */
	public AdminAccountConfig getAdminAccountConfig() {
		return adminAccountConfig;
	}
}
