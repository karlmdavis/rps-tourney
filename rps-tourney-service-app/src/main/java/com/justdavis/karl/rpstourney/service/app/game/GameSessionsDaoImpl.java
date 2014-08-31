package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.GameSession.State;
import com.justdavis.karl.rpstourney.service.api.game.GameSession_;

/**
 * The default {@link IGameSessionsDao} implementation.
 */
@Repository
public class GameSessionsDaoImpl implements IGameSessionsDao {
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
		// Build a query for the matching game.
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
		 * The GameSession.id field should have a UNIQUE constraint.
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

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGameSessionsDao#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public GameSession setMaxRounds(String gameSessionId,
			int oldMaxRoundsValue, int newMaxRoundsValue) {
		// Make sure the desired new value is legit.
		GameSession.validateMaxRoundsValue(newMaxRoundsValue);

		/*
		 * Build an update query for the matching game. Note that in addition to
		 * matching against the game's ID, we also match against the current/old
		 * maxRounds value. This helps to prevent "mid-air collision" issues
		 * when multiple users/clients try to update this at the same time.
		 */
		CriteriaBuilder cb = entityManager.getEntityManagerFactory()
				.getCriteriaBuilder();
		CriteriaUpdate<GameSession> cu = cb
				.createCriteriaUpdate(GameSession.class);
		Root<GameSession> gameSession = cu.from(GameSession.class);
		cu.set(GameSession_.maxRounds, newMaxRoundsValue)
				.where(cb.and(
						cb.equal(gameSession.get(GameSession_.id),
								gameSessionId),
						cb.equal(gameSession.get(GameSession_.maxRounds),
								oldMaxRoundsValue),
						gameSession.get(GameSession_.state).in(
								State.WAITING_FOR_PLAYER,
								State.WAITING_FOR_FIRST_THROW)));

		// Run the update query.
		int numEntitiesUpdated = entityManager.createQuery(cu).executeUpdate();
		if (numEntitiesUpdated == 0)
			throw new GameConflictException(String.format(
					"Stale maxRounds '%d' for game '%s'.", oldMaxRoundsValue,
					gameSessionId));
		else if (numEntitiesUpdated > 1)
			throw new BadCodeMonkeyException();

		/*
		 * The executeUpdate(...) above sidesteps the Hibernate entity cache, so
		 * the GameSession retrieved here will be stale. Calling refresh(...)
		 * fixes that.
		 */
		GameSession game = findById(gameSessionId);
		entityManager.refresh(game);
		return game;
	}
}
