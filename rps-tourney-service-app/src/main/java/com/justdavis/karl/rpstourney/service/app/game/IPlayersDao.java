package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;
import java.util.Set;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * A DAO for {@link Player} JPA entities.
 */
public interface IPlayersDao {
	/**
	 * @param player
	 *            the {@link Player} to be saved/updated in the database
	 */
	void save(Player player);

	/**
	 * @param playerId
	 *            the {@link Player#getId()} value to find a matching
	 *            {@link Player} for
	 * @return the existing {@link Player} record that matches the specified
	 *         criteria, or <code>null</code> if no existing record was found
	 */
	Player getPlayer(long playerId);

	/**
	 * @param account
	 *            the {@link Account} record to find a matching {@link Player}
	 *            for
	 * @return the existing {@link Player} record that matches the specified
	 *         criteria, or <code>null</code> if no existing record was found
	 */
	Player findPlayerForAccount(Account account);

	/**
	 * @param ais
	 *            the {@link Player#getBuiltInAi()} values to match against
	 * @return the {@link Player} records that match the specified criteria
	 */
	Set<Player> findPlayerForBuiltInAi(BuiltInAi... ais);

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
	 * @param player
	 *            the {@link Player} to be deleted from the database
	 */
	void delete(Player player);

	/**
	 * Note: This method is really only appropriate for use in testing; it's a
	 * bad idea to use it in production code.
	 * 
	 * @return all of the {@link Player} instances in the database
	 */
	List<Player> getPlayers();
}
