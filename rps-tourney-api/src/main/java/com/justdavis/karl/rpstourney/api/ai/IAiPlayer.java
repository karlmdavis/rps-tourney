package com.justdavis.karl.rpstourney.api.ai;

import java.util.List;

import com.justdavis.karl.rpstourney.api.GameRound;
import com.justdavis.karl.rpstourney.api.GameSession;
import com.justdavis.karl.rpstourney.api.Throw;
import com.justdavis.karl.rpstourney.api.ai.tests.AbstractAiPlayerTester;

/**
 * <p>
 * The API for AI/computer players.
 * </p>
 * <h4>Implementation Requirements</h4>
 * <p>
 * Each {@link IAiPlayer} implementation must comply with the following
 * requirements:
 * </p>
 * <ul>
 * <li>Each {@link IAiPlayer} instance may be used to "play" multiple games at
 * once; instances must not assume that they are only playing a single game in
 * order.</li>
 * <li>Each instance must be thread-safe.</li>
 * <li>Each instance must be able to handle at least 1000 calls to
 * {@link #selectThrow(int, List)} in under 5 seconds.</li>
 * </ul>
 * <p>
 * It is strongly recommended that each implementation of this class be tested
 * using the JUnit test cases in {@link AbstractAiPlayerTester}. These test
 * cases try to guarantee as many of the above requirements as possible.
 * </p>
 */
public interface IAiPlayer {
	/*
	 * Please note that we very specifically and intentionally do not pass AI
	 * players a GameSession object. That would make it too easy for them to
	 * cheat via reflection (e.g. watch for their opponent's move submissions).
	 * This is definitely not a sufficient guard against that, just a prudent
	 * first step.
	 */

	/**
	 * This method will be called to request the next move in the game from the
	 * {@link IAiPlayer}.
	 * 
	 * @param maxRounds
	 *            the {@link GameSession#getMaxRounds()} value for the game
	 *            being played
	 * @param completedRounds
	 *            the ordered {@link List} of {@link GameRound}s that have been
	 *            completed
	 * @return the {@link Throw} that the {@link IAiPlayer} wishes to make in
	 *         the current game round
	 */
	Throw selectThrow(int maxRounds, List<GameRound> completedRounds);
}
