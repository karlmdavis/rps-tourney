package com.justdavis.karl.rpstourney.webservice.config;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.justdavis.karl.misc.datasources.IDataSourceCoordinates;

/**
 * Represents the game web service application's configuration data. Please note
 * that instances of this class can be marshalled/unmarshalled via JAX-B.
 * 
 * @see IConfigLoader
 */
@XmlRootElement(name = "gameConfig")
public final class GameConfig {
	@XmlElementRef
	private final IDataSourceCoordinates dataSourceCoordinates;

	/**
	 * This private no-arg constructor is required by JAX-B.
	 */
	@SuppressWarnings("unused")
	private GameConfig() {
		this.dataSourceCoordinates = null;
	}

	/**
	 * Constructs a new {@link GameConfig} instance.
	 * 
	 * @param dataSourceCoordinates
	 *            the value to use for {@link #getDataSourceCoordinates()}
	 */
	public GameConfig(IDataSourceCoordinates dataSourceCoordinates) {
		this.dataSourceCoordinates = dataSourceCoordinates;
	}

	/**
	 * @return the {@link IDataSourceCoordinates} that identify the database
	 *         that the application will persist all of its relational data to
	 */
	public IDataSourceCoordinates getDataSourceCoordinates() {
		return dataSourceCoordinates;
	}
}
