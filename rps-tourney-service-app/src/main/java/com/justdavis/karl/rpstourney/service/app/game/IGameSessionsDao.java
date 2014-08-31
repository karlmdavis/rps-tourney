package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;

import com.justdavis.karl.rpstourney.api.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameSession;

/**
 * A DAO for {@link GameSession} JPA entities (and indirectly, its child
 * {@link GameRound} entities).
 */
public interface IGameSessionsDao {
	/**
	 * @param game
	 *            the {@link GameSession} instance to be inserted/updated in the
	 *            database
	 */
	void save(GameSession game);

	/**
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} value to match against
	 * @return the {@link GameSession} that matches the specified parameters, or
	 *         <code>null</code> if no match was found
	 */
	GameSession findById(String gameSessionId);

	/**
	 * Note: This method is really only appropriate for use in testing; it's a
	 * bad idea to use it in production code.
	 * 
	 * @return all of the {@link GameSession} instances in the database
	 */
	List<GameSession> getGameSessions();

	/**
	 * <p>
	 * Alters the value of {@link GameSession#getMaxRounds()}.
	 * </p>
	 * <p>
	 * This method must be used instead of {@link GameSession#setMaxRounds(int)}
	 * when the {@link GameSession} is being accessed concurrently from a JPA
	 * database.
	 * </p>
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} of the {@link GameSession} to
	 *            be modified
	 * @param oldMaxRoundsValue
	 *            the current value of {@link GameSession#getMaxRounds()} for
	 *            the {@link GameSession} to be modified
	 * @param newMaxRoundsValue
	 *            the new/desired value for {@link GameSession#getMaxRounds()}
	 * @return Returns the updated {@link GameSession}. Please note that the
	 *         {@link GameSession#getMaxRounds()} value returned may not match
	 *         the one passed in, if another concurrent modification was made
	 *         between this method's update and load operations.
	 * @throws GameConflictException
	 *             A {@link GameConflictException} indicates that an attempt was
	 *             made to call this method with a stale copy of the
	 *             {@link GameSession}. Either <code>oldMaxRoundsValue</code> is
	 *             stale, or the {@link GameSession#getState()} no longer allows
	 *             for the rounds to be modified.
	 */
	GameSession setMaxRounds(String gameSessionId, int oldMaxRoundsValue,
			int newMaxRoundsValue);
}
