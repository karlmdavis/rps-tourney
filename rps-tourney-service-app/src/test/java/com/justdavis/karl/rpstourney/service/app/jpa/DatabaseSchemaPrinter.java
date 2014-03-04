package com.justdavis.karl.rpstourney.service.app.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.Target;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.justdavis.karl.rpstourney.service.app.SpringITConfig;

/**
 * This utility class just prints out the Hibernate/JPA schema to the console.
 * Will use the syntax for whatever database is configured for the application's
 * integration tests via {@link SpringITConfig}.
 */
final class DatabaseSchemaPrinter {
	public static void main(String[] args) throws ClassNotFoundException {
		AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext(
				SpringITConfig.class);
		try {
			EntityManagerFactory emf = springContext
					.getBean(EntityManagerFactory.class);

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
			springContext.close();
		}
	}
}
