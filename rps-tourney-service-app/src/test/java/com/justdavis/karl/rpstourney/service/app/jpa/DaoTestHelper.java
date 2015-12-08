package com.justdavis.karl.rpstourney.service.app.jpa;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.provisioners.DataSourceProvisionersManager;
import com.justdavis.karl.misc.datasources.provisioners.DataSourceProvisionersManager.ProvisioningResult;
import com.justdavis.karl.misc.datasources.provisioners.IProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.IProvisioningTargetsProvider;
import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;

/**
 * <p>
 * Leverages both the Spring and JUnit test infrastructure to provision data
 * source repositories and JPA {@link EntityManagerFactory}s for DAO test cases
 * to use, and properly cleans things up after each test case.
 * </p>
 * <p>
 * It's hacky, and fairly ugly, but it works.
 * </p>
 */
public final class DaoTestHelper extends ExternalResource implements
		TestExecutionListener {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DaoTestHelper.class);

	private final IProvisioningRequest provisioningRequest;
	private ApplicationContext springAppContext;
	private DataSourceProvisionersManager provisionersManager;
	private ProvisioningResult provisioningResult;
	private EntityManagerFactory entityManagerFactory;

	/**
	 * Constructs a new {@link DaoTestHelper} instance.
	 * 
	 * @param provisioningRequest
	 *            the {@link IProvisioningRequest} that will be processed before
	 *            each test case, and then cleaned up after each test case
	 */
	public DaoTestHelper(IProvisioningRequest provisioningRequest) {
		this.provisioningRequest = provisioningRequest;
	}

	/**
	 * @see org.springframework.test.context.TestExecutionListener#beforeTestClass(org.springframework.test.context.TestContext)
	 */
	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		/*
		 * Due to the way this TestExecutionListener is created and registered,
		 * this will never be called.
		 */
	}

	/**
	 * @see org.springframework.test.context.TestExecutionListener#prepareTestInstance(org.springframework.test.context.TestContext)
	 */
	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		/*
		 * Snag the Spring ApplicationContext here. This is hacky, but due to
		 * the way we're creating and initializing this Spring
		 * TestExecutionListener, this is the only event from that interface
		 * that will be fired.
		 */
		this.springAppContext = testContext.getApplicationContext();
	}

	/**
	 * @see org.springframework.test.context.TestExecutionListener#beforeTestMethod(org.springframework.test.context.TestContext)
	 */
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		/*
		 * Due to the way this TestExecutionListener is created and registered,
		 * this will never be called.
		 */
	}

	/**
	 * @see org.junit.rules.ExternalResource#before()
	 */
	@Override
	protected void before() throws Throwable {
		// Provision the data source repository to use for the test.
		IProvisioningTargetsProvider targetsProvider = springAppContext
				.getBean(IProvisioningTargetsProvider.class);
		this.provisionersManager = springAppContext
				.getBean(DataSourceProvisionersManager.class);
		this.provisioningResult = provisionersManager.provision(
				targetsProvider, provisioningRequest);

		try {
			// Create the data source repository's schema.
			IDataSourceSchemaManager schemaManager = springAppContext
					.getBean(IDataSourceSchemaManager.class);
			schemaManager.createOrUpgradeSchema(this.provisioningResult
					.getCoords());

			// Create the EMF to use for the test.
			DataSourceConnectorsManager connectorsManager = springAppContext
					.getBean(DataSourceConnectorsManager.class);
			Map<String, Object> jpaCoords = connectorsManager
					.convertToJpaProperties(this.provisioningResult.getCoords());
			this.entityManagerFactory = Persistence.createEntityManagerFactory(
					"com.justdavis.karl.rpstourney", jpaCoords);
		} catch (Throwable t) {
			/*
			 * If anything in the try block blows up, the after() method won't
			 * run, and the data source repository won't be deleted, which will
			 * cause all of the next test cases to fail. So, if we catch any
			 * exceptions here, we delete the DB, and then wrap-re-throw the
			 * error.
			 */
			deleteProvisionedDataSourceRepository();

			throw new IllegalStateException(t);
		}
	}

	/**
	 * @see org.junit.rules.ExternalResource#after()
	 */
	@Override
	protected void after() {
		/*
		 * Close the EMF that was used by the test. This is needed, because
		 * apparently the EMF is also keeping a connection to the database open.
		 */
		if (this.entityManagerFactory != null)
			this.entityManagerFactory.close();

		// Delete the data source repository used for the test.
		deleteProvisionedDataSourceRepository();
	}

	/**
	 * Deletes the data source repository that was provisioned and is tracked in
	 * {@link #provisioningResult} (if any).
	 */
	private void deleteProvisionedDataSourceRepository() {
		if (this.provisionersManager == null || this.provisioningResult == null)
			return;

		try {
			this.provisionersManager.delete(provisioningResult);
		} catch (Throwable t) {
			/*
			 * The data source repository delete will often fail if something in
			 * the test has leaked a connection, as most DB servers will refuse
			 * to delete a DB with active connections. We don't want the
			 * "unable to delete" exception to obscure any test failures,
			 * though, so we'll just log the exception and let this method
			 * return normally.
			 */
			LOGGER.warn("Unable to delete data source repository.", t);
		}
	}

	/**
	 * @see org.springframework.test.context.TestExecutionListener#afterTestMethod(org.springframework.test.context.TestContext)
	 */
	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		/*
		 * Due to the way this TestExecutionListener is created and registered,
		 * this will never be called.
		 */
	}

	/**
	 * @see org.springframework.test.context.TestExecutionListener#afterTestClass(org.springframework.test.context.TestContext)
	 */
	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		/*
		 * Due to the way this TestExecutionListener is created and registered,
		 * this will never be called.
		 */
	}

	/**
	 * @return the Spring {@link ApplicationContext} being used for the currect
	 *         test case
	 */
	public ApplicationContext getSpringAppContext() {
		return springAppContext;
	}

	/**
	 * @return the {@link ProvisioningResult} that identifies the DB schema
	 *         being used for the current test run
	 */
	public ProvisioningResult getProvisioningResult() {
		return provisioningResult;
	}

	/**
	 * @return the {@link EntityManagerFactory} to use for the current test case
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
}
