package com.justdavis.karl.rpstourney.service.app.jpa;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextListener;

import org.springframework.stereotype.Component;

import com.justdavis.karl.misc.datasources.IDataSourceCoordinates;
import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForDaos;
import com.justdavis.karl.rpstourney.service.app.config.ServiceConfig;

/**
 * <p>
 * This Spring {@link Component} runs the application's
 * {@link IDataSourceSchemaManager} during its bean initialization.
 * </p>
 * <p>
 * This is admittedly a bit of a hack. It would be more appropriate to run this
 * after Spring initialization, but before the webapp context has completed
 * startup (e.g. as a {@link ServletContextListener}). However, this proved to
 * not be possible as Spring cannot lazy-load the application's
 * {@link EntityManagerFactory}, which validates the schema as part of its
 * initialization: the schema has to be populated before that happens.
 * </p>
 * 
 * @see SpringBindingsForDaos#schemaManager(com.justdavis.karl.misc.datasources.DataSourceConnectorsManager)
 */
@Component
public class DatabaseSchemaInitializer {
	private final IDataSourceSchemaManager schemaManager;
	private final ServiceConfig config;

	/**
	 * Constructs a new {@link DatabaseSchemaInitializer} instance.
	 * 
	 * @param schemaManager
	 *            the injected {@link IDataSourceSchemaManager} to use
	 * @param config
	 *            the injected {@link ServiceConfig} to use
	 */
	@Inject
	public DatabaseSchemaInitializer(IDataSourceSchemaManager schemaManager,
			ServiceConfig config) {
		this.schemaManager = schemaManager;
		this.config = config;
	}

	/**
	 * <p>
	 * Runs
	 * {@link IDataSourceSchemaManager#createOrUpgradeSchema(IDataSourceCoordinates)}
	 * .
	 * </p>
	 * <p>
	 * This method will be called by Spring after the bean is constructed.
	 * </p>
	 */
	@PostConstruct
	public void initializeSchema() {
		IDataSourceCoordinates coords = config.getDataSourceCoordinates();
		schemaManager.createOrUpgradeSchema(coords);
	}
}
