package com.justdavis.karl.rpstourney.service.app.game;

import java.util.Collection;
import java.util.LinkedList;

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

import com.justdavis.karl.misc.datasources.provisioners.IProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.hsql.HsqlProvisioningRequest;
import com.justdavis.karl.misc.datasources.provisioners.postgresql.PostgreSqlProvisioningRequest;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.app.GameServiceApplicationInitializer.AppSpringConfig;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.jpa.DaoTestHelper;

/**
 * Integration tests for {@link PlayersDaoImpl}.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { AppSpringConfig.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS)
public final class PlayersDaoImplIT {
	/**
	 * @return the test run parameters to pass to
	 *         {@link #PlayersDaoImplIT(IProvisioningRequest)}, where each
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
	 * Constructs a new {@link PlayersDaoImplIT} instance. The test runner will
	 * generate the parameters to pass to this from the
	 * {@link #createTestParameters()} method.
	 * 
	 * @param provisioningRequest
	 * @throws Exception
	 *             An {@link Exception} might be thrown by the Spring context
	 *             initialization.
	 */
	public PlayersDaoImplIT(IProvisioningRequest provisioningRequest)
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
	 * Tests {@link PlayersDaoImpl#findOrCreatePlayerForAccount(Account)}.
	 */
	@Test
	public void findOrCreatePlayerForAccount() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAOs.
			PlayersDaoImpl playersDao = new PlayersDaoImpl();
			playersDao.setEntityManager(entityManager);

			/*
			 * Call findOrCreatePlayerForAccount(...) with an Account that
			 * doesn't already have a Player instance for it.
			 */
			Account account = new Account();
			Player newPlayer = null;
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				newPlayer = playersDao.findOrCreatePlayerForAccount(account);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			/*
			 * Pull the persisted Account out of the new Player and call
			 * findOrCreatePlayerForAccount(...) again. This time, it should
			 * return the already-created Player instance.
			 */
			Account savedAccount = newPlayer.getHumanAccount();
			Assert.assertNotNull(savedAccount);
			Player loadedPlayer = null;
			tx = entityManager.getTransaction();
			try {
				tx.begin();
				loadedPlayer = playersDao
						.findOrCreatePlayerForAccount(savedAccount);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Verify the results.
			Assert.assertEquals(1, playersDao.getPlayers().size());
			Assert.assertNotNull(loadedPlayer);
			Assert.assertNotNull(loadedPlayer.getHumanAccount());
			Assert.assertEquals(newPlayer, loadedPlayer);
			Assert.assertEquals(savedAccount, loadedPlayer.getHumanAccount());
		} finally {
			entityManager.close();
		}
	}
}
