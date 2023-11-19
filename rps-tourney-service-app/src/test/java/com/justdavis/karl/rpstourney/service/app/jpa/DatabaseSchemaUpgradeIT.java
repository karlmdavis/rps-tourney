package com.justdavis.karl.rpstourney.service.app.jpa;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import com.justdavis.karl.misc.datasources.provisioners.IProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.hsql.HsqlProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.postgresql.PostgreSqlProvisioningRequest;
import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForDaoITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.game.GamesDaoImplIT;

/**
 * Integration tests for the application's {@link IDataSourceSchemaManager}.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { SpringBindingsForDaoITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS)
public final class DatabaseSchemaUpgradeIT {
	/**
	 * @return the test run parameters to pass to {@link #GamesDaoImplIT(IProvisioningRequest)}, where each top-level
	 *         element in the returned {@link Collection} represents a test run
	 */
	@Parameterized.Parameters(name = "{index}: IProvisioningRequest={0}")
	public static Collection<Object[]> createTestParameters() {
		Collection<Object[]> testParameters = new LinkedList<>();

		IProvisioningRequest hsqlRequest = HsqlProvisioningRequest.requestForRandomDatabase("integrationtest");
		testParameters.add(new Object[] { hsqlRequest });

		IProvisioningRequest postgreSqlRequest = PostgreSqlProvisioningRequest
				.requestForRandomDatabase("integrationtest");
		testParameters.add(new Object[] { postgreSqlRequest });

		return testParameters;
	}

	@Rule
	public DaoTestHelper daoTestHelper;

	/**
	 * Constructs a new {@link GamesDaoImplIT} instance. The test runner will generate the parameters to pass to this
	 * from the {@link #createTestParameters()} method.
	 *
	 * @param provisioningRequest
	 * @throws Exception
	 *             An {@link Exception} might be thrown by the Spring context initialization.
	 */
	public DatabaseSchemaUpgradeIT(IProvisioningRequest provisioningRequest) throws Exception {
		this.daoTestHelper = new DaoTestHelper(provisioningRequest);

		/*
		 * Initialize Spring. We're using this mechanism, rather than the {@link SpringJUnit4ClassRunner}, as this test
		 * is already using a different runner: {@link Parameterized}.
		 */
		TestContextManager testContextManager = new TestContextManager(getClass());

		/*
		 * Register the DaoTestHelper with the Spring test context, so it can snag the ApplicationContext from it. (This
		 * is a hack.)
		 */
		testContextManager.registerTestExecutionListeners(daoTestHelper);
		testContextManager.prepareTestInstance(this);
	}

	/**
	 * Tests {@link IDataSourceSchemaManager} when run against a schema that's already correct. Basically, just verifies
	 * that the Liquibase checksum handling is correct.
	 */
	@Test
	public void updateSchema() {
		IDataSourceSchemaManager schemaManager = daoTestHelper.getSpringAppContext()
				.getBean(IDataSourceSchemaManager.class);

		/*
		 * The DaoTestHelper has already run this once, so just run it a second time.
		 */
		schemaManager.createOrUpgradeSchema(daoTestHelper.getProvisioningResult().getCoords());
	}
}
