package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.Map;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.jpa.AvailableSettings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.threeten.bp.Clock;

import com.justdavis.karl.misc.datasources.hsql.HsqlConnector;
import com.justdavis.karl.misc.datasources.hsql.HsqlCoordinates;
import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AuthToken;

/**
 * Unit tests for {@link GameLoginIdentitiesDaoImpl}.
 */
public final class GameLoginIdentitiesDaoImplTest {
	/**
	 * Tests {@link GameLoginIdentitiesDaoImpl#save(GameLoginIdentity)}.
	 * 
	 * @throws AddressException
	 *             (shouldn't happen)
	 */
	@Test
	public void save() throws AddressException {
		// Create the DAO.
		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
		EntityManager entityManager;
		entityManager = entityManagerFactory.createEntityManager();
		GameLoginIdentitiesDaoImpl loginsDao = new GameLoginIdentitiesDaoImpl();
		loginsDao.setEntityManager(entityManager);

		// Create the entity to try saving.
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
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
			entityManager.close();
		}

		// Reset the EntityManager.
		entityManager = entityManagerFactory.createEntityManager();
		loginsDao.setEntityManager(entityManager);

		// Verify the result.
		Assert.assertEquals(1, loginsDao.getLogins().size());
		GameLoginIdentity loginFromDb = loginsDao.getLogins().get(0);
		Assert.assertNotNull(loginFromDb.getAccount());
		Assert.assertEquals(1, loginFromDb.getId());
		Assert.assertEquals(login.getEmailAddress(),
				loginFromDb.getEmailAddress());
		Assert.assertEquals(login.getPasswordHash(),
				loginFromDb.getPasswordHash());
		entityManager.close();
	}

	/**
	 * Tests {@link GameLoginIdentitiesDaoImpl#find(InternetAddress)}.
	 * 
	 * @throws AddressException
	 *             (shouldn't happen)
	 */
	@Test
	public void findByEmailAddress() throws AddressException {
		// Create the DAO.
		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
		EntityManager entityManager;
		entityManager = entityManagerFactory.createEntityManager();
		GameLoginIdentitiesDaoImpl loginsDao = new GameLoginIdentitiesDaoImpl();
		loginsDao.setEntityManager(entityManager);

		// Create and save the entity to test against.
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
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
			entityManager.close();
		}

		// Reset the EntityManager.
		entityManager = entityManagerFactory.createEntityManager();
		loginsDao.setEntityManager(entityManager);

		// Try to query for the entity.
		GameLoginIdentity loginThatShouldExist = loginsDao.find(login
				.getEmailAddress());
		Assert.assertNotNull(loginThatShouldExist);
		Assert.assertEquals(login.getId(), loginThatShouldExist.getId());

		// Try to query for a non-existent entity.
		GameLoginIdentity loginThatShouldntExist = loginsDao
				.find(new InternetAddress("bar@example.com"));
		Assert.assertNull(loginThatShouldntExist);

		entityManager.close();
	}

	/**
	 * Runs after each test case, shutting down the HSQL database, which also
	 * drops all the objects and data from it.
	 */
	@After
	public void shutdownDb() {
		HsqlCoordinates coords = createDbCoords();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(
				new HsqlConnector().createDataSource(coords));
		jdbcTemplate.execute("SHUTDOWN");
	}

	/**
	 * @return the {@link EntityManagerFactory} instance to use
	 */
	private EntityManagerFactory createEntityManagerFactory() {
		HsqlCoordinates coords = createDbCoords();
		Map<String, Object> jpaCoords = new HsqlConnector()
				.convertToJpaProperties(coords);
		jpaCoords.put(AvailableSettings.SCHEMA_GEN_DATABASE_ACTION, "create");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				"com.justdavis.karl.rpstourney", jpaCoords);

		return emf;
	}

	/**
	 * @return the {@link HsqlCoordinates} of the in-memory database to use for
	 *         tests
	 */
	private HsqlCoordinates createDbCoords() {
		HsqlCoordinates coords = new HsqlCoordinates("jdbc:hsqldb:mem:foo");
		return coords;
	}
}
