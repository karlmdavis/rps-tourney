package com.justdavis.karl.rpstourney.api.ai;

import org.junit.Test;

import com.justdavis.karl.rpstourney.api.ai.tests.AbstractAiPlayerTester;

/**
 * Unit tests for {@link RandomAiPlayer}.
 */
public final class RandomAiPlayerTest extends AbstractAiPlayerTester {
	/**
	 * @see com.justdavis.karl.rpstourney.api.ai.tests.AbstractAiPlayerTester#getAiPlayer()
	 */
	@Override
	protected IAiPlayer getAiPlayer() {
		return new RandomAiPlayer();
	}

	/**
	 * Verifies that {@link RandomAiPlayer} can be constructed without going
	 * boom.
	 */
	@Test
	public void constructor() {
		// Shouldn't throw any exceptions:
		new RandomAiPlayer();
	}
}
