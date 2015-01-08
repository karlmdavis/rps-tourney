package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Player;

/**
 * A DAO for {@link Player} JPA entities.
 */
public interface IPlayersDao {
	/**
	 * @param account
	 *            the {@link Account} record to find a matching {@link Player}
	 *            for
	 * @return the existing {@link Player} record that matches the specified
	 *         criteria, or <code>null</code> if no existing record was found
	 */
	Player findPlayerForAccount(Account account);

	/**
	 * @param account
	 *            the {@link Account} record to find/create a matching
	 *            {@link Player} for
	 * @return the existing {@link Player} record that matches the specified
	 *         criteria, or a new matching {@link Player} record (if no existing
	 *         record was found)
	 */
	Player findOrCreatePlayerForAccount(Account account);

	/**
	 * Note: This method is really only appropriate for use in testing; it's a
	 * bad idea to use it in production code.
	 * 
	 * @return all of the {@link Player} instances in the database
	 */
	List<Player> getPlayers();
}
