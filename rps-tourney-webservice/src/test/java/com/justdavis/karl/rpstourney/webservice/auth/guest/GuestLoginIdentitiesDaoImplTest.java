package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.util.Map;
import java.util.UUID;

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
 * Unit tests for {@link GuestLoginIdentitiesDaoImpl}.
 */
public final class GuestLoginIdentitiesDaoImplTest {
	/**
	 * Tests {@link GuestLoginIdentitiesDaoImpl#save(GuestLoginIdentity)}.
	 */
	@Test
	public void save() {
		// Create the DAO.
		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
		EntityManager entityManager;
		entityManager = entityManagerFactory.createEntityManager();
		GuestLoginIdentitiesDaoImpl loginsDao = new GuestLoginIdentitiesDaoImpl();
		loginsDao.setEntityManager(entityManager);

		// Create the entity to try saving.
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
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
			entityManager.close();
		}

		// Reset the EntityManager.
		entityManager = entityManagerFactory.createEntityManager();
		loginsDao.setEntityManager(entityManager);

		// Verify the result.
		Assert.assertEquals(1, loginsDao.getLogins().size());
		GuestLoginIdentity loginFromDb = loginsDao.getLogins().get(0);
		Assert.assertNotNull(loginFromDb.getAccount());
		Assert.assertEquals(1, loginFromDb.getId());
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
