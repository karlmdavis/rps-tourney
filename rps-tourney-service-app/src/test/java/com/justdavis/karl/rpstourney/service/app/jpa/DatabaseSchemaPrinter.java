package com.justdavis.karl.rpstourney.service.app.jpa;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.Target;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
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
					.convertToJpaProperties(appConfig.getDataSourceCoordinates());
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.justdavis.karl.rpstourney",
					jpaCoords);

			Configuration hibernateConfig = new Configuration();
			hibernateConfig.setProperty(Environment.DIALECT,
					HSQLDialect.class.getName());
			for (EntityType<?> entityType : emf.getMetamodel().getEntities()) {
				hibernateConfig.addAnnotatedClass(entityType.getJavaType());
			}
			SchemaExport schemaExporter = new SchemaExport(hibernateConfig);
			schemaExporter.setFormat(true);
			schemaExporter.setDelimiter(";");
			schemaExporter.create(Target.SCRIPT);
		} finally {
			// FIXME Application never actually exits.
			springContext.stop();
			springContext.close();
		}
	}
}
