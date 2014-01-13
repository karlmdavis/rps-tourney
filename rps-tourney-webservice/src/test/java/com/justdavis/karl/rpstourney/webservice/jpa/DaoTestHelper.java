package com.justdavis.karl.rpstourney.webservice.jpa;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.jpa.AvailableSettings;
import org.junit.rules.ExternalResource;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.provisioners.DataSourceProvisionersManager;
import com.justdavis.karl.misc.datasources.provisioners.DataSourceProvisionersManager.ProvisioningResult;
import com.justdavis.karl.misc.datasources.provisioners.IProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.IProvisioningTargetsProvider;

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

		// Create the EMF to use for the test.
		DataSourceConnectorsManager connectorsManager = springAppContext
				.getBean(DataSourceConnectorsManager.class);
		Map<String, Object> jpaCoords = connectorsManager
				.convertToJpaProperties(this.provisioningResult.getCoords());
		jpaCoords.put(AvailableSettings.SCHEMA_GEN_DATABASE_ACTION, "create");
		this.entityManagerFactory = Persistence.createEntityManagerFactory(
				"com.justdavis.karl.rpstourney", jpaCoords);
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
		if (this.provisionersManager != null && this.provisioningResult != null)
			this.provisionersManager.delete(provisioningResult);
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
	 * @return the {@link EntityManagerFactory} to use for the current test case
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
}
