package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;

import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.Player;

/**
 * A DAO for {@link Game} JPA entities (and indirectly, its child
 * {@link GameRound} entities).
 */
public interface IGamesDao {
	/**
	 * @param game
	 *            the {@link Game} instance to be inserted/updated in the
	 *            database
	 */
	void save(Game game);

	/**
	 * @param gameId
	 *            the {@link Game#getId()} value to match against
	 * @return the {@link Game} that matches the specified parameters, or
	 *         <code>null</code> if no match was found
	 */
	Game findById(String gameId);

	/**
	 * Note: This method is really only appropriate for use in testing; it's a
	 * bad idea to use it in production code.
	 * 
	 * @return all of the {@link Game} instances in the database
	 */
	List<Game> getGames();

	/**
	 * @param player
	 *            the {@link Game#getPlayer1()} / {@link Game#getPlayer2()}
	 *            value to match against
	 * @return the {@link Game}s that match the specified parameters, or an
	 *         empty {@link List} if none are found
	 */
	List<Game> getGamesForPlayer(Player player);

	/**
	 * <p>
	 * Alters the value of {@link Game#getMaxRounds()}.
	 * </p>
	 * <p>
	 * This method must be used instead of {@link Game#setMaxRounds(int)} when
	 * the {@link Game} is being accessed concurrently from a JPA database.
	 * </p>
	 * 
	 * @param gameId
	 *            the {@link Game#getId()} of the {@link Game} to be modified
	 * @param oldMaxRoundsValue
	 *            the current value of {@link Game#getMaxRounds()} for the
	 *            {@link Game} to be modified
	 * @param newMaxRoundsValue
	 *            the new/desired value for {@link Game#getMaxRounds()}
	 * @return Returns the updated {@link Game}. Please note that the
	 *         {@link Game#getMaxRounds()} value returned may not match the one
	 *         passed in, if another concurrent modification was made between
	 *         this method's update and load operations.
	 * @throws GameConflictException
	 *             A {@link GameConflictException} indicates that an attempt was
	 *             made to call this method with a stale copy of the
	 *             {@link Game}. Either <code>oldMaxRoundsValue</code> is stale,
	 *             or the {@link Game#getState()} no longer allows for the
	 *             rounds to be modified.
	 */
	Game setMaxRounds(String gameId, int oldMaxRoundsValue, int newMaxRoundsValue);

	/**
	 * Removes/deletes the specified {@link Game} from the database.
	 * 
	 * @param gameId
	 *            the {@link Game#getId()} of the {@link Game} to be deleted
	 */
	void delete(String gameId);
}
