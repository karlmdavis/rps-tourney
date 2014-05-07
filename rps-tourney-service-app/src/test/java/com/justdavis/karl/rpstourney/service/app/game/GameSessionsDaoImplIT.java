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
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.app.SpringConfig;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.jpa.DaoTestHelper;

/**
 * Integration tests for {@link GameSessionsDaoImpl}.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { SpringConfig.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS)
public final class GameSessionsDaoImplIT {
	/**
	 * @return the test run parameters to pass to
	 *         {@link #GameSessionsDaoImplIT(IProvisioningRequest)}, where each
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
	 * Constructs a new {@link GameSessionsDaoImplIT} instance. The test runner
	 * will generate the parameters to pass to this from the
	 * {@link #createTestParameters()} method.
	 * 
	 * @param provisioningRequest
	 * @throws Exception
	 *             An {@link Exception} might be thrown by the Spring context
	 *             initialization.
	 */
	public GameSessionsDaoImplIT(IProvisioningRequest provisioningRequest)
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
	 * Tests {@link GameSessionsDaoImpl#save(GameSession)} with a new/empty
	 * {@link GameSession}.
	 */
	@Test
	public void saveNewGame() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GameSessionsDaoImpl gamesDao = new GameSessionsDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Player player1 = new Player(new Account());
			GameSession game = new GameSession(player1);

			// Try to save the entity.
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				gamesDao.save(game);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Verify the result.
			Assert.assertEquals(1, gamesDao.getGameSessions().size());
			GameSession gameFromDb = gamesDao.getGameSessions().get(0);
			Assert.assertNotNull(gameFromDb.getPlayer1());
			Assert.assertEquals(game.getState(), gameFromDb.getState());
			Assert.assertEquals(game.getMaxRounds(), gameFromDb.getMaxRounds());
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GameSessionsDaoImpl#save(GameSession)} with an updated
	 * {@link GameSession}.
	 */
	@Test
	public void saveUpdatedGame() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GameSessionsDaoImpl gamesDao = new GameSessionsDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Player player1 = new Player(new Account());
			GameSession game = new GameSession(player1);

			// Try to save the entity.
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				gamesDao.save(game);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Try to save the updated entity.
			tx = entityManager.getTransaction();
			try {
				tx.begin();

				game = gamesDao.findById(game.getId());
				game.setMaxRounds(41);
				gamesDao.save(game);

				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Verify the result.
			Assert.assertEquals(1, gamesDao.getGameSessions().size());
			GameSession gameFromDb = gamesDao.getGameSessions().get(0);
			Assert.assertNotNull(gameFromDb.getPlayer1());
			Assert.assertEquals(game.getState(), gameFromDb.getState());
			Assert.assertEquals(game.getMaxRounds(), gameFromDb.getMaxRounds());
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GameSessionsDaoImpl#save(GameSession)} with a
	 * {@link GameSession} that has some {@link GameRound}s.
	 */
	@Test
	public void saveWithRounds() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GameSessionsDaoImpl gamesDao = new GameSessionsDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Player player1 = new Player(new Account());
			GameSession game = new GameSession(player1);
			Player player2 = new Player(new Account());
			game.setPlayer2(player2);
			game.submitThrow(0, player1, Throw.ROCK);
			game.submitThrow(0, player2, Throw.ROCK);
			game.submitThrow(1, player1, Throw.PAPER);
			game.submitThrow(1, player2, Throw.PAPER);

			// Try to save the entity.
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				gamesDao.save(game);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Verify the result.
			Assert.assertEquals(1, gamesDao.getGameSessions().size());
			GameSession gameFromDb = gamesDao.getGameSessions().get(0);
			Assert.assertNotNull(gameFromDb.getPlayer1());
			Assert.assertNotNull(gameFromDb.getPlayer2());
			Assert.assertEquals(game.getState(), gameFromDb.getState());
			Assert.assertEquals(game.getMaxRounds(), gameFromDb.getMaxRounds());
			Assert.assertEquals(game.getRounds().size(), gameFromDb.getRounds()
					.size());
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GameSessionsDaoImpl#findById(String)}.
	 */
	@Test
	public void findById() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GameSessionsDaoImpl gamesDao = new GameSessionsDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create and save the entity to test against.
			Player player1 = new Player(new Account());
			GameSession game = new GameSession(player1);
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				gamesDao.save(game);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Try to query for the entity.
			GameSession gameThatShouldExist = gamesDao.findById(game.getId());
			Assert.assertNotNull(gameThatShouldExist);
			Assert.assertEquals(game.getId(), gameThatShouldExist.getId());

			// Try to query for a non-existent entity.
			GameSession gameThatShouldntExist = gamesDao.findById("123");
			Assert.assertNull(gameThatShouldntExist);
		} finally {
			entityManager.close();
		}
	}
}
