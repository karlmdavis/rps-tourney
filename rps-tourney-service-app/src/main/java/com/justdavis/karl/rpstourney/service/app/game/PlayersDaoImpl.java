package com.justdavis.karl.rpstourney.service.app.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Component;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Player_;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * The default {@link IPlayersDao} implementation.
 */
@Component
public final class PlayersDaoImpl implements IPlayersDao {
	private EntityManager entityManager;

	/**
	 * Constructs a new {@link PlayersDaoImpl} instance.
	 */
	public PlayersDaoImpl() {
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
	 * @see com.justdavis.karl.rpstourney.service.app.game.IPlayersDao#save(com.justdavis.karl.rpstourney.service.api.game.Player)
	 */
	@Override
	public void save(Player player) {
		entityManager.persist(player);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IPlayersDao#getPlayer(long)
	 */
	@Override
	public Player getPlayer(long playerId) {
		if (playerId < 0)
			throw new IllegalArgumentException();

		// Build a query for the matching Player.
		CriteriaBuilder criteriaBuilder = entityManager.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<Player> criteria = criteriaBuilder.createQuery(Player.class);
		criteria.where(criteriaBuilder.equal(criteria.from(Player.class).get(Player_.id), playerId));

		// Run the query.
		TypedQuery<Player> query = entityManager.createQuery(criteria);
		List<Player> results = query.getResultList();

		/*
		 * The Player.id field should have a UNIQUE constraint, so this should
		 * only return 0 or 1 results.
		 */
		if (results.isEmpty())
			return null;
		else if (results.size() != 1)
			throw new BadCodeMonkeyException();

		// Return the result.
		Player player = results.get(0);
		return player;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IPlayersDao#findPlayerForAccount(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public Player findPlayerForAccount(Account account) {
		// Look up the pre-existing Player record, if any.
		return find(account);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IPlayersDao#findPlayerForBuiltInAi(com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi[])
	 */
	@Override
	public Set<Player> findPlayerForBuiltInAi(BuiltInAi... ais) {
		if (ais == null)
			throw new IllegalArgumentException();
		if (ais.length <= 0)
			throw new IllegalArgumentException();

		// Build a query for the matching Player(s).
		Collection<BuiltInAi> aisCollection = Arrays.asList(ais);
		CriteriaBuilder criteriaBuilder = entityManager.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<Player> criteria = criteriaBuilder.createQuery(Player.class);
		criteria.where(criteria.from(Player.class).get(Player_.builtInAi).in(aisCollection));

		// Run the query.
		TypedQuery<Player> query = entityManager.createQuery(criteria);
		List<Player> results = query.getResultList();

		// Return the result.
		Set<Player> matchingPlayers = new HashSet<>(results);
		return matchingPlayers;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IPlayersDao#findOrCreatePlayerForAccount(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public Player findOrCreatePlayerForAccount(Account account) {
		// Look up the pre-existing Player record, if any.
		Player existingPlayer = find(account);
		if (existingPlayer != null)
			return existingPlayer;

		/*
		 * If the specified Account has already been persisted, look it up
		 * again, to ensure we have a managed copy of it (rather than a detached
		 * copy).
		 */
		if (account.hasId())
			account = entityManager.find(Account.class, account.getId());

		// No pre-existing Player, so create, save, and return a new one.
		Player newPlayer = new Player(account);
		entityManager.persist(newPlayer);
		return newPlayer;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IPlayersDao#delete(com.justdavis.karl.rpstourney.service.api.game.Player)
	 */
	@Override
	public void delete(Player player) {
		entityManager.remove(player);
	}

	/**
	 * @param account
	 *            the {@link Player#getHumanAccount()} value to find a matching
	 *            {@link Player} for
	 * @return the existing {@link Player} record that matches the specified
	 *         criteria, or <code>null</code> if no such record is found
	 */
	private Player find(Account account) {
		if (account == null)
			throw new IllegalArgumentException();

		// If the Account hasn't been saved, there won't be a Player for it.
		if (!account.hasId())
			return null;

		// Build a query for the matching Account.
		CriteriaBuilder criteriaBuilder = entityManager.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<Player> criteria = criteriaBuilder.createQuery(Player.class);
		criteria.where(criteriaBuilder.equal(criteria.from(Player.class).get(Player_.humanAccount), account));

		// Run the query.
		TypedQuery<Player> query = entityManager.createQuery(criteria);
		List<Player> results = query.getResultList();

		/*
		 * The Player.humanAccount field should have a UNIQUE constraint, so
		 * this should only return 0 or 1 results.
		 */
		if (results.isEmpty())
			return null;
		else if (results.size() != 1)
			throw new BadCodeMonkeyException();

		// Return the result.
		Player player = results.get(0);
		return player;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.game.IPlayersDao#getPlayers()
	 */
	@Override
	public List<Player> getPlayers() {
		// Build a query.
		CriteriaBuilder criteriaBuilder = entityManager.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<Player> criteria = criteriaBuilder.createQuery(Player.class);
		criteria.from(Player.class);

		// Run the query.
		TypedQuery<Player> query = entityManager.createQuery(criteria);
		List<Player> results = query.getResultList();

		return results;
	}
}
