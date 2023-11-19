package com.justdavis.karl.rpstourney.service.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.justdavis.karl.misc.SpringConfigForJEMisc;
import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.misc.datasources.schema.LiquibaseSchemaManager;
import com.justdavis.karl.rpstourney.service.app.jpa.SpringBindingsForJpa;

/**
 * <p>
 * The Spring {@link Configuration} required by this project's DAOs (and their ITs).
 * </p>
 * <p>
 * Design Note: This specifically does <strong>not</strong> import {@link SpringBindingsForJpa}, as the DAO ITs manage
 * all of that themselves. Instead, that is included in {@link SpringBindingsForWebServices}.
 * </p>
 */
@Configuration
@Import({ SpringConfigForJEMisc.class })
public class SpringBindingsForDaos {
	/**
	 * @return the {@link IDataSourceSchemaManager} for the application to use
	 */
	@Bean
	public IDataSourceSchemaManager schemaManager(DataSourceConnectorsManager connectorsManager) {
		/*
		 * The rps-tourney-webservice/src/main/resources/liquibase-change-log .xml file contains the Liquibase schema
		 * changelog, which will be applied at application startup via the DatabaseSchemaInitializer.
		 */
		return new LiquibaseSchemaManager(connectorsManager, "liquibase-change-log.xml");
	}
}
