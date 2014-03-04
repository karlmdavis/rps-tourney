package com.justdavis.karl.rpstourney.webservice.auth;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.threeten.bp.Clock;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken_;

/**
 * The default {@link IAccountsDao} implementation.
 */
@Component
public final class AccountsDaoImpl implements IAccountsDao {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AccountsDaoImpl.class);

	private EntityManager entityManager;

	/**
	 * Constructs a new {@link AccountsDaoImpl} instance.
	 */
	public AccountsDaoImpl() {
	}

	/**
	 * @param entityManager
	 *            a JPA {@link EntityManager} connected to the application's
	 *            database
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		// Sanity check: null EntityManager?
		if (entityManager == null)
			throw new IllegalArgumentException();

		this.entityManager = entityManager;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#save(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public void save(Account account) {
		entityManager.persist(account);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#getAccounts()
	 */
	@Override
	public List<Account> getAccounts() {
		// Build a query for the logins.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<Account> criteria = criteriaBuilder
				.createQuery(Account.class);
		criteria.from(Account.class);

		// Run the query.
		TypedQuery<Account> query = entityManager.createQuery(criteria);
		List<Account> results = query.getResultList();

		return results;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#getAccount(java.util.UUID)
	 */
	@Override
	public Account getAccount(UUID authTokenValue) {
		// Build a query for the matching AuthToken.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<AuthToken> criteria = criteriaBuilder
				.createQuery(AuthToken.class);
		criteria.where(criteriaBuilder.equal(criteria.from(AuthToken.class)
				.get(AuthToken_.token), authTokenValue));

		// Run the query.
		TypedQuery<AuthToken> query = entityManager.createQuery(criteria);
		List<AuthToken> results = query.getResultList();

		// Verify the results.
		if (authTokenValue != null && results.isEmpty()) {
			/*
			 * If there was an auth token, a match for it wasn't found. Either
			 * someone's trying to hack, the Account has been deleted, or
			 * something's gone fairly badly wrong.
			 */
			LOGGER.warn(
					"Unable to find an existing account for auth token: {}",
					authTokenValue);
		}
		if (results.isEmpty())
			return null;
		else if (results.size() != 1)
			throw new BadCodeMonkeyException();

		// Return the result.
		Account account = results.get(0).getAccount();
		return account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#selectOrCreateAuthToken(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public AuthToken selectOrCreateAuthToken(Account account) {
		if (account.getAuthTokens().size() > 1)
			throw new IllegalStateException("not yet supported");

		if (!account.getAuthTokens().isEmpty())
			return account.getAuthTokens().iterator().next();

		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);
		/*
		 * Note: If this needs to support detached instances, things will have
		 * to be reworked.
		 */
		entityManager.persist(account);

		return authToken;
	}
}
