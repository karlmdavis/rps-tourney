package com.justdavis.karl.rpstourney.webservice.auth.game;

import java.util.List;

import javax.mail.internet.InternetAddress;

import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;

/**
 * A DAO for {@link GameLoginIdentity} JPA entities.
 */
public interface IGameLoginIndentitiesDao {
	/**
	 * @param login
	 *            the {@link GameLoginIdentity} instance to be inserted/updated
	 *            in the database
	 */
	void save(GameLoginIdentity login);

	/**
	 * @param emailAddress
	 *            the email address to match against
	 *            {@link GameLoginIdentity#getEmailAddress()}
	 * @return the {@link GameLoginIdentity} that matches the specified
	 *         parameters, or <code>null</code> if no match was found
	 */
	GameLoginIdentity find(InternetAddress emailAddress);

	/**
	 * Note: This method is really only appropriate for use in testing; it's a
	 * bad idea to use it in production code.
	 * 
	 * @return all of the {@link GameLoginIdentity} instances in the database
	 */
	List<GameLoginIdentity> getLogins();
}
