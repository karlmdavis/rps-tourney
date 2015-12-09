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
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException.ConflictType;
import com.justdavis.karl.rpstourney.service.api.game.Game_;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.State;

/**
 * The default {@link IGamesDao} implementation.
 */
@Repository
public class GamesDaoImpl implements IGamesDao {
	private EntityManager entityManager;

	/**
	 * Constructs a new {@link GamesDaoImpl} instance.
	 */
	public GamesDaoImpl() {
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
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGamesDao#save(com.justdavis.karl.rpstourney.service.api.game.Game)
	 */
	@Override
	public void save(Game game) {
		entityManager.persist(game);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGamesDao#findById(java.lang.String)
	 */
	@Override
	public Game findById(String gameId) {
		// Build a query for the matching game.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<Game> criteria = criteriaBuilder.createQuery(Game.class);
		criteria.where(criteriaBuilder.equal(
				criteria.from(Game.class).get(Game_.id), gameId));

		// Run the query.
		TypedQuery<Game> query = entityManager.createQuery(criteria);
		List<Game> results = query.getResultList();

		/*
		 * The Game.id field should have a UNIQUE constraint.
		 */
		if (results.isEmpty())
			return null;
		else if (results.size() != 1)
			throw new BadCodeMonkeyException();

		// Return the result.
		Game game = results.get(0);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGamesDao#getGames()
	 */
	@Override
	public List<Game> getGames() {
		// Build a query.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<Game> criteria = criteriaBuilder.createQuery(Game.class);
		criteria.from(Game.class);

		// Run the query.
		TypedQuery<Game> query = entityManager.createQuery(criteria);
		List<Game> results = query.getResultList();

		return results;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGamesDao#getGamesForPlayer(com.justdavis.karl.rpstourney.service.api.game.Player)
	 */
	@Override
	public List<Game> getGamesForPlayer(Player player) {
		// Build a query.
		CriteriaBuilder cb = entityManager.getEntityManagerFactory()
				.getCriteriaBuilder();
		CriteriaQuery<Game> cq = cb.createQuery(Game.class);
		Root<Game> game = cq.from(Game.class);
		cq.where(cb.or(
				cb.and(cb.isNotNull(game.get(Game_.player1)),
						cb.equal(game.get(Game_.player1), player)),
				cb.and(cb.isNotNull(game.get(Game_.player1)),
						cb.equal(game.get(Game_.player2), player))));

		// Run the query.
		TypedQuery<Game> query = entityManager.createQuery(cq);
		List<Game> results = query.getResultList();

		return results;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGamesDao#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public Game setMaxRounds(String gameId, int oldMaxRoundsValue,
			int newMaxRoundsValue) {
		// Make sure the desired new value is legit.
		Game.validateMaxRoundsValue(newMaxRoundsValue);

		/*
		 * Build an update query for the matching game. Note that in addition to
		 * matching against the game's ID, we also match against the current/old
		 * maxRounds value. This helps to prevent "mid-air collision" issues
		 * when multiple users/clients try to update this at the same time.
		 */
		CriteriaBuilder cb = entityManager.getEntityManagerFactory()
				.getCriteriaBuilder();
		CriteriaUpdate<Game> cu = cb.createCriteriaUpdate(Game.class);
		Root<Game> gameRoot = cu.from(Game.class);
		cu.set(Game_.maxRounds, newMaxRoundsValue).where(
				cb.and(cb.equal(gameRoot.get(Game_.id), gameId),
						cb.equal(gameRoot.get(Game_.maxRounds),
								oldMaxRoundsValue),
						gameRoot.get(Game_.state).in(State.WAITING_FOR_PLAYER,
								State.WAITING_FOR_FIRST_THROW)));

		// Run the update query.
		int numEntitiesUpdated = entityManager.createQuery(cu).executeUpdate();
		if (numEntitiesUpdated == 0)
			throw new GameConflictException(ConflictType.ROUNDS_STALE);
		else if (numEntitiesUpdated > 1)
			throw new BadCodeMonkeyException();

		/*
		 * The executeUpdate(...) above sidesteps the Hibernate entity cache, so
		 * the Game retrieved here will be stale. Calling refresh(...) fixes
		 * that.
		 */
		Game game = findById(gameId);
		entityManager.refresh(game);
		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IGamesDao#delete(java.lang.String)
	 */
	@Override
	public void delete(String gameId) {
		/*
		 * Note: I'd prefer to use a CriteriaDelete here, as it saves an extra
		 * query, but Hibernate 4 doesn't seem to correctly handle
		 * CascadeType.REMOVE with that API.
		 * https://hibernate.atlassian.net/browse/HHH-8993
		 */
		Game game = findById(gameId);
		entityManager.remove(game);
	}
}
