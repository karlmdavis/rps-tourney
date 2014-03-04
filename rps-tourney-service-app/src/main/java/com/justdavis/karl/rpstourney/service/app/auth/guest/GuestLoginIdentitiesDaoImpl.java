package com.justdavis.karl.rpstourney.service.app.auth.guest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Component;

import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;

/**
 * The default {@link IGuestLoginIndentitiesDao} implementation.
 */
@Component
public class GuestLoginIdentitiesDaoImpl implements IGuestLoginIndentitiesDao {
	private EntityManager entityManager;

	/**
	 * Constructs a new {@link GuestLoginIdentitiesDaoImpl} instance.
	 */
	public GuestLoginIdentitiesDaoImpl() {
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
	 * @see com.justdavis.karl.rpstourney.service.app.auth.guest.IGuestLoginIndentitiesDao#save(com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity)
	 */
	@Override
	public void save(GuestLoginIdentity login) {
		entityManager.persist(login);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.guest.IGuestLoginIndentitiesDao#getLogins()
	 */
	@Override
	public List<GuestLoginIdentity> getLogins() {
		// Build a query for the logins.
		CriteriaBuilder criteriaBuilder = entityManager
				.getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQuery<GuestLoginIdentity> criteria = criteriaBuilder
				.createQuery(GuestLoginIdentity.class);
		criteria.from(GuestLoginIdentity.class);

		// Run the query.
		TypedQuery<GuestLoginIdentity> query = entityManager
				.createQuery(criteria);
		List<GuestLoginIdentity> results = query.getResultList();

		return results;
	}
}
