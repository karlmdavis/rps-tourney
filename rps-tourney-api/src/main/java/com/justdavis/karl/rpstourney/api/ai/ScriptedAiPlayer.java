package com.justdavis.karl.rpstourney.api.ai;

import java.util.List;

import com.justdavis.karl.rpstourney.api.GameRound;
import com.justdavis.karl.rpstourney.api.Throw;

/**
 * This {@link IAiPlayer} just selects all of its moves from a pre-determined
 * script. If all moves in the script are exhausted, it starts back at the
 * beginning.
 */
public final class ScriptedAiPlayer implements IAiPlayer {
	private final Throw[] scriptedThrows;

	/**
	 * Constructs a new {@link ScriptedAiPlayer} instance.
	 */
	public ScriptedAiPlayer(Throw... scriptedThrows) {
		// Sanity check: null script.
		if (scriptedThrows == null)
			throw new IllegalArgumentException();
		// Sanity check: empty script.
		if (scriptedThrows.length < 1)
			throw new IllegalArgumentException();

		this.scriptedThrows = scriptedThrows;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.api.ai.IAiPlayer#selectThrow(int,
	 *      java.util.List)
	 */
	@Override
	public Throw selectThrow(int maxRounds, List<GameRound> completedRounds) {
		// Sanity check: valid max rounds.
		if (maxRounds < 1)
			throw new IllegalArgumentException();
		// Sanity check: valid max rounds.
		if (maxRounds % 2 == 0)
			throw new IllegalArgumentException();
		// Sanity check: null history.
		if (completedRounds == null)
			throw new IllegalArgumentException();

		// Determine where we ought to be in the script.
		int currentRound = completedRounds.size();
		int scriptIndex = currentRound % scriptedThrows.length;

		// Select the next Throw from the script.
		return scriptedThrows[scriptIndex];
	}
}
