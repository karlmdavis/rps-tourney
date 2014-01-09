package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Component;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * The default {@link IGameLoginIndentitiesDao} implementation.
 */
@Component
public final class GameLoginIdentitiesDaoImpl implements
		IGameLoginIndentitiesDao {
	private EntityManager entityManager;

	/**
	 * Constructs a new {@link GameLoginIdentitiesDaoImpl} instance.
	 */
	public GameLoginIdentitiesDaoImpl() {
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
	 * @see com.justdavis.karl.rpstourney.webservice.auth.game.IGameLoginIndentitiesDao#save(com.justdavis.karl.rpstourney.webservice.auth.game.GameLoginIdentity)
	 */
	@Override
	public void save(GameLoginIdentity login) {
		entityManager.persist(login);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.game.IGameLoginIndentitiesDao#find(javax.mail.internet.InternetAddress)
	 */
	@Override
	public GameLoginIdentity find(InternetAddress emailAddress) {
		// Build a query for the matching AuthToken.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<GameLoginIdentity> criteria = criteriaBuilder
				.createQuery(GameLoginIdentity.class);
		criteria.where(criteriaBuilder.equal(
				criteria.from(GameLoginIdentity.class).get(
						GameLoginIdentity_.emailAddress), emailAddress));

		// Run the query.
		TypedQuery<GameLoginIdentity> query = entityManager
				.createQuery(criteria);
		List<GameLoginIdentity> results = query.getResultList();

		/*
		 * The GameLoginIdentity.emailAddress field should have a UNIQUE
		 * constraint.
		 */
		if (results.isEmpty())
			return null;
		else if (results.size() != 1)
			throw new BadCodeMonkeyException();

		// Return the result.
		GameLoginIdentity login = results.get(0);
		return login;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.guest.IGuestLoginIndentitiesDao#getLogins()
	 */
	@Override
	public List<GameLoginIdentity> getLogins() {
		// Build a query for the logins.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<GameLoginIdentity> criteria = criteriaBuilder
				.createQuery(GameLoginIdentity.class);
		criteria.from(GameLoginIdentity.class);

		// Run the query.
		TypedQuery<GameLoginIdentity> query = entityManager
				.createQuery(criteria);
		List<GameLoginIdentity> results = query.getResultList();

		return results;
	}
}
