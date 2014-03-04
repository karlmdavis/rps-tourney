package com.justdavis.karl.rpstourney.service.app.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Provides the Spring {@link Configuration} for JPA.
 */
@Configuration
public class SpringJpaConfig {
	/**
	 * @return the Spring {@link JpaVendorAdapter} for the application's
	 *         database
	 */
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(false);
		hibernateJpaVendorAdapter.setGenerateDdl(false);
		return hibernateJpaVendorAdapter;
	}

	/**
	 * <p>
	 * Creates the {@link LocalContainerEntityManagerFactoryBean} which
	 * manages/creates the application's JPA {@link EntityManagerFactory}.
	 * </p>
	 * <p>
	 * Note that the application's <code>persistence.xml</code> has set the
	 * Hibernate "schema creation" flag to "validate (not create) the schema".
	 * for this validation to succeed, the database schema must be
	 * created/updated before this Spring bean is created, which is why this
	 * method has a {@link DependsOn} annotation.
	 * </p>
	 * 
	 * @param dataSource
	 *            the injected {@link DataSource} that the JPA
	 *            {@link EntityManagerFactory} should be connected to
	 * @param jpaVendorAdapter
	 *            the injected {@link JpaVendorAdapter} for the application
	 * @return the {@link LocalContainerEntityManagerFactoryBean} instance that
	 *         Spring will use to inject the application's
	 *         {@link EntityManagerFactory} and {@link EntityManager}s, when
	 *         requested
	 */
	@Bean
	@DependsOn({ "databaseSchemaInitializer" })
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
		lef.setDataSource(dataSource);
		lef.setJpaVendorAdapter(jpaVendorAdapter);
		return lef;
	}

	/**
	 * @param entityManagerFactoryBean
	 *            the injected {@link LocalContainerEntityManagerFactoryBean}
	 *            that the {@link PlatformTransactionManager} will be associated
	 *            with
	 * @return the {@link PlatformTransactionManager} that Spring will use for
	 *         its managed transactions
	 */
	@Bean
	public PlatformTransactionManager transactionManager(
			LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean
				.getObject());
		return transactionManager;
	}

	/**
	 * @return a Spring {@link BeanPostProcessor} that enables the use of the
	 *         JPA {@link PersistenceUnit} and {@link PersistenceContext}
	 *         annotations for injection of {@link EntityManagerFactory} and
	 *         {@link EntityManager} instances, respectively, into beans
	 */
	@Bean
	public PersistenceAnnotationBeanPostProcessor persistenceAnnotationProcessor() {
		return new PersistenceAnnotationBeanPostProcessor();
	}
}
