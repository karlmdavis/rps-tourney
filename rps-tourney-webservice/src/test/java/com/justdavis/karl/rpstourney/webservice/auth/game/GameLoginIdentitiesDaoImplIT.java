package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.threeten.bp.Clock;

import com.justdavis.karl.misc.datasources.provisioners.IProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.hsql.HsqlProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.postgresql.PostgreSqlProvisioningRequest;
import com.justdavis.karl.rpstourney.webservice.GameApplicationInitializer.AppSpringConfig;
import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AuthToken;
import com.justdavis.karl.rpstourney.webservice.jpa.DaoTestHelper;

/**
 * Integration tests for {@link GameLoginIdentitiesDaoImpl}.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { AppSpringConfig.class })
public final class GameLoginIdentitiesDaoImplIT {
	/**
	 * @return the test run parameters to pass to
	 *         {@link #AccountsDaoImplIT(IProvisioningRequest)}, where each
	 *         top-level element in the returned {@link Collection} represents a
	 *         test run
	 */
	@Parameterized.Parameters(name = "{index}: IProvisioningRequest={0}")
	public static Collection<Object[]> createTestParameters() {
		Collection<Object[]> testParameters = new LinkedList<>();

		IProvisioningRequest hsqlRequest = new HsqlProvisioningRequest(
				"integrationtest");
		testParameters.add(new Object[] { hsqlRequest });

		IProvisioningRequest postgreSqlRequest = new PostgreSqlProvisioningRequest(
				"integrationtest");
		testParameters.add(new Object[] { postgreSqlRequest });

		return testParameters;
	}

	@Rule
	public DaoTestHelper daoTestHelper;

	/**
	 * Constructs a new {@link GameLoginIdentitiesDaoImplIT} instance. The test
	 * runner will generate the parameters to pass to this from the
	 * {@link #createTestParameters()} method.
	 * 
	 * @param provisioningRequest
	 * @throws Exception
	 *             An {@link Exception} might be thrown by the Spring context
	 *             initialization.
	 */
	public GameLoginIdentitiesDaoImplIT(IProvisioningRequest provisioningRequest)
			throws Exception {
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
	 * Tests {@link GameLoginIdentitiesDaoImpl#save(GameLoginIdentity)}.
	 * 
	 * @throws AddressException
	 *             (shouldn't happen)
	 */
	@Test
	public void save() throws AddressException {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GameLoginIdentitiesDaoImpl loginsDao = new GameLoginIdentitiesDaoImpl();
			loginsDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Account account = new Account();
			AuthToken authToken = new AuthToken(account, UUID.randomUUID(),
					Clock.systemUTC().instant());
			account.getAuthTokens().add(authToken);
			GameLoginIdentity login = new GameLoginIdentity(account,
					new InternetAddress("foo@example.com"),
					GameAuthService.hashPassword("secret"));

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
			GameLoginIdentity loginFromDb = loginsDao.getLogins().get(0);
			Assert.assertNotNull(loginFromDb.getAccount());
			Assert.assertEquals(1, loginFromDb.getId());
			Assert.assertEquals(login.getEmailAddress(),
					loginFromDb.getEmailAddress());
			Assert.assertEquals(login.getPasswordHash(),
					loginFromDb.getPasswordHash());
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GameLoginIdentitiesDaoImpl#find(InternetAddress)}.
	 * 
	 * @throws AddressException
	 *             (shouldn't happen)
	 */
	@Test
	public void findByEmailAddress() throws AddressException {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GameLoginIdentitiesDaoImpl loginsDao = new GameLoginIdentitiesDaoImpl();
			loginsDao.setEntityManager(entityManager);

			// Create and save the entity to test against.
			Account account = new Account();
			AuthToken authToken = new AuthToken(account, UUID.randomUUID(),
					Clock.systemUTC().instant());
			account.getAuthTokens().add(authToken);
			GameLoginIdentity login = new GameLoginIdentity(account,
					new InternetAddress("foo@example.com"),
					GameAuthService.hashPassword("secret"));
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				loginsDao.save(login);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Try to query for the entity.
			GameLoginIdentity loginThatShouldExist = loginsDao.find(login
					.getEmailAddress());
			Assert.assertNotNull(loginThatShouldExist);
			Assert.assertEquals(login.getId(), loginThatShouldExist.getId());

			// Try to query for a non-existent entity.
			GameLoginIdentity loginThatShouldntExist = loginsDao
					.find(new InternetAddress("bar@example.com"));
			Assert.assertNull(loginThatShouldntExist);
		} finally {
			entityManager.close();
		}
	}
}
