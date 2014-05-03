package com.justdavis.karl.rpstourney.service.app.game;

import java.util.List;

import com.justdavis.karl.rpstourney.api.GameRound;
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
}
