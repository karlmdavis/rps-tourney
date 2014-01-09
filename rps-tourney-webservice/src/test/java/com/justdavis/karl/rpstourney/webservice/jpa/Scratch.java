package com.justdavis.karl.rpstourney.webservice.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.SchemaGenAction;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbc.JDBCDriver;

/**
 * Just a scratch/sample file for learning the Hibernate API.
 */
public final class Scratch {
	public void theWholeShebang() {
		// Create the HSQL DataSource to use
		JDBCDataSource hsqlDataSource = new JDBCDataSource();
		hsqlDataSource.setUrl("jdbc:hsqldb:mem:mymemdb");
		hsqlDataSource.setUser("SA");

		// Store that DataSource in JNDI
		// TODO

		// Create a JPA EM that uses that DataSource
		Map<String, String> jpaProps = new HashMap<>();
		jpaProps.put(AvailableSettings.JDBC_DRIVER, JDBCDriver.class.getName());
		jpaProps.put(AvailableSettings.JDBC_URL, "jdbc:hsqldb:mem:mymemdb");
		jpaProps.put(AvailableSettings.JDBC_USER, "SA");
		jpaProps.put(AvailableSettings.SCHEMA_GEN_DATABASE_ACTION, "create");
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("com.justdavis.karl.rpstourney",
						jpaProps);
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		
		entityManager.getTransaction().begin();
		entityManager.persist(new FooEntity("Our very first event!"));
		entityManager.persist(new FooEntity("A follow up event"));
		entityManager.getTransaction().commit();
		entityManager.close();

		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<FooEntity> result = entityManager.createQuery("from FooEntity",
				FooEntity.class).getResultList();
		for (FooEntity foo : result) {
			System.out.println("Foo: " + foo.getTitle());
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		
		entityManagerFactory.close();
	}

	public static void main(String[] args) {
		Scratch scratch = new Scratch();
		scratch.theWholeShebang();
	}
}
