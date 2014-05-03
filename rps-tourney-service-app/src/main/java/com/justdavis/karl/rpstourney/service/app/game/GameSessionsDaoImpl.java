package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Component;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.GameSession_;

/**
 * The default {@link IGameSessionsDao} implementation.
 */
@Component
public final class GameSessionsDaoImpl implements IGameSessionsDao {
	private EntityManager entityManager;

	/**
	 * Constructs a new {@link GameSessionsDaoImpl} instance.
	 */
	public GameSessionsDaoImpl() {
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
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGameSessionsDao#save(com.justdavis.karl.rpstourney.service.api.game.GameSession)
	 */
	@Override
	public void save(GameSession game) {
		entityManager.persist(game);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGameSessionsDao#findById(java.lang.String)
	 */
	@Override
	public GameSession findById(String gameSessionId) {
		// Build a query for the matching AuthToken.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<GameSession> criteria = criteriaBuilder
				.createQuery(GameSession.class);
		criteria.where(criteriaBuilder.equal(criteria.from(GameSession.class)
				.get(GameSession_.id), gameSessionId));

		// Run the query.
		TypedQuery<GameSession> query = entityManager.createQuery(criteria);
		List<GameSession> results = query.getResultList();

		/*
		 * The GameLoginIdentity.emailAddress field should have a UNIQUE
		 * constraint.
		 */
		if (results.isEmpty())
			return null;
		else if (results.size() != 1)
			throw new BadCodeMonkeyException();

		// Return the result.
		GameSession game = results.get(0);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGameSessionsDao#getGameSessions()
	 */
	@Override
	public List<GameSession> getGameSessions() {
		// Build a query.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<GameSession> criteria = criteriaBuilder
				.createQuery(GameSession.class);
		criteria.from(GameSession.class);

		// Run the query.
		TypedQuery<GameSession> query = entityManager.createQuery(criteria);
		List<GameSession> results = query.getResultList();

		return results;
	}
}
