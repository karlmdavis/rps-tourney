package com.justdavis.karl.rpstourney.service.app.auth.guest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.threeten.bp.Clock;

import com.justdavis.karl.misc.datasources.provisioners.IProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.hsql.HsqlProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.postgresql.PostgreSqlProvisioningRequest;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.app.SpringConfig;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.auth.AccountsDaoImplIT;
import com.justdavis.karl.rpstourney.service.app.jpa.DaoTestHelper;

/**
 * Integration tests for {@link GuestLoginIdentitiesDaoImpl}.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { SpringConfig.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS)
public final class GuestLoginIdentitiesDaoImplIT {
	/**
	 * @return the test run parameters to pass to
	 *         {@link #AccountsDaoImplIT(IProvisioningRequest)}, where each
	 *         top-level element in the returned {@link Collection} represents a
	 *         test run
	 */
	@Parameterized.Parameters(name = "{index}: IProvisioningRequest={0}")
	public static Collection<Object[]> createTestParameters() {
		Collection<Object[]> testParameters = new LinkedList<>();

		IProvisioningRequest hsqlRequest = HsqlProvisioningRequest
				.requestForRandomDatabase("integrationtest");
		testParameters.add(new Object[] { hsqlRequest });

		IProvisioningRequest postgreSqlRequest = PostgreSqlProvisioningRequest
				.requestForRandomDatabase("integrationtest");
		testParameters.add(new Object[] { postgreSqlRequest });

		return testParameters;
	}

	@Rule
	public DaoTestHelper daoTestHelper;

	/**
	 * Constructs a new {@link AccountsDaoImplIT} instance. The test runner will
	 * generate the parameters to pass to this from the
	 * {@link #createTestParameters()} method.
	 * 
	 * @param provisioningRequest
	 * @throws Exception
	 *             An {@link Exception} might be thrown by the Spring context
	 *             initialization.
	 */
	public GuestLoginIdentitiesDaoImplIT(
			IProvisioningRequest provisioningRequest) throws Exception {
		this.daoTestHelper = new DaoTestHelper(provisioningRequest);

		/*
		 * Initialize Spring. We're using this mechanism, rather than the {@link
		 * SpringJUnit4ClassRunner}, as this test is already using a different
		 * runner: {@link Parameterized}.
		 */
		TestContextManager testContextManager = new TestContextManager(
				getClass());

		/*
		 * Register the DaoTestHelper with the Spring test context, so it can
		 * snag the ApplicationContext from it. (This is a hack.)
		 */
		testContextManager.registerTestExecutionListeners(daoTestHelper);
		testContextManager.prepareTestInstance(this);
	}

	/**
	 * Tests {@link GuestLoginIdentitiesDaoImpl#save(GuestLoginIdentity)}.
	 */
	@Test
	public void save() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GuestLoginIdentitiesDaoImpl loginsDao = new GuestLoginIdentitiesDaoImpl();
			loginsDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Account account = new Account();
			AuthToken authToken = new AuthToken(account, UUID.randomUUID(),
					Clock.systemUTC().instant());
			account.getAuthTokens().add(authToken);
			GuestLoginIdentity login = new GuestLoginIdentity(account);

			// Try to save the entity.
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				loginsDao.save(login);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Verify the result.
			Assert.assertEquals(1, loginsDao.getLogins().size());
			GuestLoginIdentity loginFromDb = loginsDao.getLogins().get(0);
			Assert.assertNotNull(loginFromDb.getAccount());
			Assert.assertEquals(1, loginFromDb.getId());
		} finally {
			entityManager.close();
		}
	}
}
