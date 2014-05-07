package com.justdavis.karl.rpstourney.service.app.auth;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
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
import com.justdavis.karl.rpstourney.service.app.GameServiceApplicationInitializer.AppSpringConfig;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.jpa.DaoTestHelper;

/**
 * Integration tests for {@link AccountsDaoImpl}.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { AppSpringConfig.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS)
public final class AccountsDaoImplIT {
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
	public AccountsDaoImplIT(IProvisioningRequest provisioningRequest)
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
	 * Tests {@link AccountsDaoImpl#save(Account)}.
	 */
	@Test
	public void save() {
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Create the DAO.
			AccountsDaoImpl accountsDao = new AccountsDaoImpl();
			accountsDao.setEntityManager(entityManager);

			// Create the entity to try saving.
			Account account = new Account();
			AuthToken authToken = new AuthToken(account, UUID.randomUUID(),
					Clock.systemUTC().instant());
			account.getAuthTokens().add(authToken);

			// Try to save the entity.
			EntityTransaction tx = null;
			try {
				tx = entityManager.getTransaction();
				tx.begin();
				accountsDao.save(account);
				tx.commit();
			} finally {
				if (tx != null && tx.isActive())
					tx.rollback();
			}

			// Verify the result.
			Assert.assertEquals(1, accountsDao.getAccounts().size());
			Account accountFromDb = accountsDao.getAccounts().get(0);
			Assert.assertEquals(1, accountFromDb.getId());
			Assert.assertEquals(account.getRoles().size(), accountFromDb
					.getRoles().size());
			Assert.assertNotNull(accountFromDb.getAuthToken(authToken
					.getToken()));
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link AccountsDaoImpl#getAccount(UUID)}.
	 */
	@Test
	public void getAccountByUuuid() {
		// Create the DAO.
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			AccountsDaoImpl accountsDao = new AccountsDaoImpl();
			accountsDao.setEntityManager(entityManager);

			// Create and save the entity to test against.
			Account account = new Account();
			AuthToken authToken = new AuthToken(account, UUID.randomUUID(),
					Clock.systemUTC().instant());
			account.getAuthTokens().add(authToken);
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				accountsDao.save(account);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

			// Try to query for the entity.
			Account accountFromDb = accountsDao
					.getAccount(authToken.getToken());
			Assert.assertNotNull(accountFromDb);
			Assert.assertEquals(authToken.getToken(), accountFromDb
					.getAuthTokens().iterator().next().getToken());

			// Try to query for a non-existent entity.
			Account accountThatShouldntExist = accountsDao.getAccount(UUID
					.randomUUID());
			Assert.assertNull(accountThatShouldntExist);
		} finally {
			entityManager.close();
		}
	}

	/**
	 * Tests {@link AccountsDaoImpl#selectOrCreateAuthToken(Account)}.
	 */
	@Test
	public void selectOrCreateAuthToken() {
		// Create the DAO.
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			AccountsDaoImpl accountsDao = new AccountsDaoImpl();
			accountsDao.setEntityManager(entityManager);

			// Create and save the entities to test against.
			Account account1 = new Account();
			AuthToken authToken1 = new AuthToken(account1, UUID.randomUUID(),
					Clock.systemUTC().instant());
			account1.getAuthTokens().add(authToken1);
			Account account2 = new Account();
			EntityTransaction tx = entityManager.getTransaction();
			try {
				tx.begin();
				accountsDao.save(account1);
				accountsDao.save(account2);
				tx.commit();
			} finally {
				if (tx.isActive())
					tx.rollback();
			}

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
		} finally {
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
		EntityManager entityManager = daoTestHelper.getEntityManagerFactory()
				.createEntityManager();

		try {
			// Verify the SQL type for AuthToken.getCreationTimestamp().
			Session session = entityManager.unwrap(Session.class);
			SessionImplementor sessionImplementor = (SessionImplementor) session;
			Connection connection = sessionImplementor
					.getJdbcConnectionAccess().obtainConnection();
			try {
				DatabaseMetaData metaData = connection.getMetaData();
				ResultSet columns = metaData.getColumns(null, null,
						"AuthTokens", "creationTimestamp");
				boolean hasAtLeastOneColumn = columns.next();
				Assert.assertTrue(hasAtLeastOneColumn);
				int firstColumnSqlType = columns.getInt("DATA_TYPE");
				// Just FYI: If this is actually -3, that's Types.VARBINARY.
				Assert.assertEquals(Types.TIMESTAMP, firstColumnSqlType);
				boolean hasMoreThanOneColumn = columns.next();
				Assert.assertFalse(hasMoreThanOneColumn);
			} finally {
				sessionImplementor.getJdbcConnectionAccess().releaseConnection(
						connection);
			}
		} finally {
			entityManager.close();
		}
	}
}
