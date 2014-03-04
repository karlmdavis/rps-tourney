package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.util.List;

import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;

/**
 * A DAO for {@link GuestLoginIdentity} JPA entities.
 */
public interface IGuestLoginIndentitiesDao {
	/**
	 * @param login
	 *            the {@link GuestLoginIdentity} instance to be inserted/updated
	 *            in the database
	 */
	void save(GuestLoginIdentity login);

	/**
	 * Note: This method is really only appropriate for use in testing; it's a
	 * bad idea to use it in production code.
	 * 
	 * @return all of the {@link GuestLoginIdentity} instances in the database
	 */
	List<GuestLoginIdentity> getLogins();
}
