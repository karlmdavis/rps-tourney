package com.justdavis.karl.rpstourney.webservice.auth;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jpa.AvailableSettings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.threeten.bp.Clock;

import com.justdavis.karl.misc.datasources.hsql.HsqlConnector;
import com.justdavis.karl.misc.datasources.hsql.HsqlCoordinates;

/**
 * Unit tests for {@link AccountsDaoImpl}.
 */
public final class AccountsDaoImplTest {
	/**
	 * Tests {@link AccountsDaoImpl#save(Account)}.
	 */
	@Test
	public void save() {
		// Create the DAO.
		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
		EntityManager entityManager;
		entityManager = entityManagerFactory.createEntityManager();
		AccountsDaoImpl accountsDao = new AccountsDaoImpl();
		accountsDao.setEntityManager(entityManager);

		// Create the entity to try saving.
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);

		// Try to save the entity.
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			accountsDao.save(account);
			tx.commit();
		} finally {
			if (tx.isActive())
				tx.rollback();
			entityManager.close();
		}

		// Reset the EntityManager.
		entityManager = entityManagerFactory.createEntityManager();
		accountsDao.setEntityManager(entityManager);

		// Verify the result.
		Assert.assertEquals(1, accountsDao.getAccounts().size());
		Account accountFromDb = accountsDao.getAccounts().get(0);
		Assert.assertEquals(1, accountFromDb.getId());
		Assert.assertEquals(account.getRoles().size(), accountFromDb.getRoles()
				.size());
		Assert.assertNotNull(accountFromDb.getAuthToken(authToken.getToken()));
		entityManager.close();
	}

	/**
	 * Tests {@link AccountsDaoImpl#getAccount(UUID)}.
	 */
	@Test
	public void getAccountByUuuid() {
		// Create the DAO.
		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
		EntityManager entityManager;
		entityManager = entityManagerFactory.createEntityManager();
		AccountsDaoImpl accountsDao = new AccountsDaoImpl();
		accountsDao.setEntityManager(entityManager);

		// Create and save the entity to test against.
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			accountsDao.save(account);
			tx.commit();
		} finally {
			if (tx.isActive())
				tx.rollback();
			entityManager.close();
		}

		// Reset the EntityManager.
		entityManager = entityManagerFactory.createEntityManager();
		accountsDao.setEntityManager(entityManager);

		// Try to query for the entity.
		Account accountFromDb = accountsDao.getAccount(authToken.getToken());
		Assert.assertNotNull(accountFromDb);
		Assert.assertEquals(authToken.getToken(), accountFromDb.getAuthTokens()
				.iterator().next().getToken());

		// Try to query for a non-existent entity.
		Account accountThatShouldntExist = accountsDao.getAccount(UUID
				.randomUUID());
		Assert.assertNull(accountThatShouldntExist);

		entityManager.close();
	}

	/**
	 * Tests {@link AccountsDaoImpl#selectOrCreateAuthToken(Account)}.
	 */
	@Test
	public void selectOrCreateAuthToken() {
		// Create the DAO.
		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
		EntityManager entityManager;
		entityManager = entityManagerFactory.createEntityManager();
		AccountsDaoImpl accountsDao = new AccountsDaoImpl();
		accountsDao.setEntityManager(entityManager);

		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();

			// Create and save the entities to test against.
			Account account1 = new Account();
			AuthToken authToken1 = new AuthToken(account1, UUID.randomUUID(),
					Clock.systemUTC().instant());
			account1.getAuthTokens().add(authToken1);
			Account account2 = new Account();
			accountsDao.save(account1);
			accountsDao.save(account2);

			// Try to query for the entities.
			AuthToken authTokenThatShouldAlreadyExist = accountsDao
					.selectOrCreateAuthToken(account1);
			Assert.assertNotNull(authTokenThatShouldAlreadyExist);
			Assert.assertEquals(authToken1.getToken(),
					authTokenThatShouldAlreadyExist.getToken());
			AuthToken authTokenThatShouldBeNew = accountsDao
					.selectOrCreateAuthToken(account2);
			Assert.assertNotNull(authTokenThatShouldBeNew);
			Assert.assertEquals(account2.getId(), authTokenThatShouldBeNew
					.getAccount().getId());

			tx.commit();
		} finally {
			if (tx.isActive())
				tx.rollback();
			entityManager.close();
		}
	}

	/**
	 * Tests {@link AccountsDaoImpl} to ensure that
	 * {@link AuthToken#getCreationTimestamp()} is persisted as a legitimate SQL
	 * type (and not just a binary or character field).
	 * 
	 * @throws SQLException
	 *             (shouldn't happen)
	 */
	@Test
	public void authTokenCreationTimestampSqlType() throws SQLException {
		// Create the EntityManager.
		EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
		EntityManager entityManager;
		entityManager = entityManagerFactory.createEntityManager();

		// Verify the SQL type for AuthToken.getCreationTimestamp().
		Session session = entityManager.unwrap(Session.class);
		SessionImplementor sessionImplementor = (SessionImplementor) session;
		Connection connection = sessionImplementor.getJdbcConnectionAccess()
				.obtainConnection();
		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet columns = metaData.getColumns(null, null, "AUTHTOKENS",
				"CREATIONTIMESTAMP");
		boolean hasAtLeastOneColumn = columns.next();
		Assert.assertTrue(hasAtLeastOneColumn);
		int firstColumnSqlType = columns.getInt("DATA_TYPE");
		// Just FYI: If this is actually -3, that's Types.VARBINARY.
		Assert.assertEquals(Types.TIMESTAMP, firstColumnSqlType);
		boolean hasMoreThanOneColumn = columns.next();
		Assert.assertFalse(hasMoreThanOneColumn);

		connection.close();
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
