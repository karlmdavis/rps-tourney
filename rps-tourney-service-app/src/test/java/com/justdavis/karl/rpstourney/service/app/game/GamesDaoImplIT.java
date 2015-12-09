package com.justdavis.karl.rpstourney.service.app.game;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

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
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForDaoITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.jpa.DaoTestHelper;

/**
 * Integration tests for {@link GamesDaoImpl}.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { SpringBindingsForDaoITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS)
public final class GamesDaoImplIT {
	/**
	 * @return the test run parameters to pass to
	 *         {@link #GamesDaoImplIT(IProvisioningRequest)}, where each
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
	 * Constructs a new {@link GamesDaoImplIT} instance. The test runner
	 * will generate the parameters to pass to this from the
	 * {@link #createTestParameters()} method.
	 * 
	 * @param provisioningRequest
	 * @throws Exception
	 *             An {@link Exception} might be thrown by the Spring context
	 *             initialization.
	 */
	public GamesDaoImplIT(IProvisioningRequest provisioningRequest)
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
	 * Tests {@link GamesDaoImpl#save(Game)} with a new/empty
	 * {@link Game}.
	 */
	@Test
	public void saveNewGame() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GamesDaoImpl gamesDao = new GamesDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Player player1 = new Player(new Account());
			Game game = new Game(player1);

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
			Assert.assertEquals(1, gamesDao.getGames().size());
			Game gameFromDb = gamesDao.getGames().get(0);
			Assert.assertNotNull(gameFromDb.getPlayer1());
			Assert.assertEquals(game.getState(), gameFromDb.getState());
			Assert.assertEquals(game.getMaxRounds(), gameFromDb.getMaxRounds());
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GamesDaoImpl#save(Game)} with an updated
	 * {@link Game}.
	 */
	@Test
	public void saveUpdatedGame() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GamesDaoImpl gamesDao = new GamesDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Player player1 = new Player(new Account());
			Game game = new Game(player1);

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
			Assert.assertEquals(1, gamesDao.getGames().size());
			Game gameFromDb = gamesDao.getGames().get(0);
			Assert.assertNotNull(gameFromDb.getPlayer1());
			Assert.assertEquals(game.getState(), gameFromDb.getState());
			Assert.assertEquals(game.getMaxRounds(), gameFromDb.getMaxRounds());
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GamesDaoImpl#save(Game)} with a
	 * {@link Game} that has some {@link GameRound}s.
	 */
	@Test
	public void saveWithRounds() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GamesDaoImpl gamesDao = new GamesDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Player player1 = new Player(new Account());
			Game game = new Game(player1);
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
			Assert.assertEquals(1, gamesDao.getGames().size());
			Game gameFromDb = gamesDao.getGames().get(0);
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
	 * Tests {@link GamesDaoImpl#findById(String)}.
	 */
	@Test
	public void findById() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GamesDaoImpl gamesDao = new GamesDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create and save the entity to test against.
			Player player1 = new Player(new Account());
			Game game = new Game(player1);
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
			Game gameThatShouldExist = gamesDao.findById(game.getId());
			Assert.assertNotNull(gameThatShouldExist);
			Assert.assertEquals(game.getId(), gameThatShouldExist.getId());

			// Try to query for a non-existent entity.
			Game gameThatShouldntExist = gamesDao.findById("123");
			Assert.assertNull(gameThatShouldntExist);
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GamesDaoImpl#getGamesForPlayer(Player)}.
	 */
	@Test
	public void getGamesForPlayer() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			GamesDaoImpl gamesDao = new GamesDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create and save the entities to test against.
			Player playerA = new Player(new Account());
			Player playerB = new Player(new Account());
			Game game1 = new Game(playerA);
			game1.setPlayer2(playerB);
			Game game2 = new Game(playerA);
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				gamesDao.save(game1);
				gamesDao.save(game2);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Try to query for the entities.
			List<Game> gamesForPlayerA = gamesDao
					.getGamesForPlayer(playerA);
			Assert.assertNotNull(gamesForPlayerA);
			Assert.assertEquals(2, gamesForPlayerA.size());
			List<Game> gamesForPlayerB = gamesDao
					.getGamesForPlayer(playerB);
			Assert.assertNotNull(gamesForPlayerB);
			Assert.assertEquals(1, gamesForPlayerB.size());
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link GamesDaoImpl#delete(String)}.
	 */
	@Test
	public void deleteGameAndRounds() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory().createEntityManager();

		try {
			// Create the DAO.
			GamesDaoImpl gamesDao = new GamesDaoImpl();
			gamesDao.setEntityManager(entityManager);

			// Create the Game, GameRounds, and other required entities.
			Player player1 = new Player(new Account());
			Player player2 = new Player(new Account());
			Game game = new Game(player1);
			game.setPlayer2(player2);
			game.submitThrow(0, player1, Throw.ROCK);

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

			// Try to delete the game
			tx = entityManager.getTransaction();
			try {
				tx.begin();

				gamesDao.delete(game.getId());

				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Verify that the Game was deleted.
			Assert.assertEquals(0, gamesDao.getGames().size());

			/*
			 * Verify that the GameRounds were also deleted. Have to fall back
			 * to JPA here, as they're not directly exposed by a DAO.
			 */
			CriteriaBuilder criteriaBuilder = entityManager.getEntityManagerFactory().getCriteriaBuilder();
			CriteriaQuery<GameRound> criteria = criteriaBuilder.createQuery(GameRound.class);
			criteria.from(GameRound.class);
			TypedQuery<GameRound> query = entityManager.createQuery(criteria);
			Assert.assertEquals(0, query.getResultList().size());
		} finally {
			entityManager.close();
		}
	}
}
