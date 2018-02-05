package com.justdavis.karl.rpstourney.service.app.jpa;

import java.io.StringWriter;
import java.util.Map;

import javax.persistence.Persistence;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.hsql.HsqlCoordinates;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForWebServiceITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.config.ServiceConfig;

/**
 * This utility class just prints out the Hibernate/JPA schema to the console.
 * Will use the syntax for whatever database is configured for the application's
 * integration tests via {@link SpringBindingsForWebServiceITs}.
 */
final class DatabaseSchemaPrinter {
	public static void main(String[] args) throws ClassNotFoundException {
		AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
		springContext.getEnvironment().setActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY);
		springContext.register(SpringBindingsForWebServiceITs.class);
		springContext.refresh();
		try {
			ServiceConfig appConfig = springContext.getBean(ServiceConfig.class);
			DataSourceConnectorsManager connectorsManager = springContext.getBean(DataSourceConnectorsManager.class);
			Map<String, Object> jpaCoords = connectorsManager
					.convertToJpaProperties(new HsqlCoordinates("jdbc:hsqldb:mem:foo"));

			StringWriter ddlWriter = new StringWriter();
			jpaCoords.put(AvailableSettings.HBM2DDL_SCRIPTS_ACTION, "create");
			jpaCoords.put(AvailableSettings.HBM2DDL_SCRIPTS_CREATE_TARGET, ddlWriter);
			jpaCoords.put(AvailableSettings.HBM2DDL_DELIMITER, ";");
			Persistence.generateSchema("com.justdavis.karl.rpstourney", jpaCoords);

			String systemNewLine = System.getProperty("line.separator");
			systemNewLine = !systemNewLine.isEmpty() ? systemNewLine : "\n";
			String[] ddlLines = ddlWriter.toString().split(systemNewLine);

			System.out.println("\nGenerated SQL DDL:\n----");
			for (String ddlLine : ddlLines)
				System.out.println(ddlLine);
			System.out.println("----\n");
		} finally {
			// FIXME Application never actually exits.
			springContext.stop();
			springContext.close();
		}
	}
}
