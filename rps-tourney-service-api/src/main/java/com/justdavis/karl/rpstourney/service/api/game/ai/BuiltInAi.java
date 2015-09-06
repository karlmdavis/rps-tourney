package com.justdavis.karl.rpstourney.service.api.game.ai;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * <p>
 * Enumerates the built-in AIs available to the application.
 * </p>
 * <h3>Modifying This Enum and its AIs</h3>
 * <p>
 * Entries must <strong>never</strong> be removed from this enum: doing so would
 * break games where these AIs are a player. In addition, statistics can and
 * will be calculated for each AI's performance, so significant modifications to
 * an existing AI's logic <strong>must</strong> be avoided. Instead, create a
 * new entry for the modified AI, and flag the old entry as retired.
 * </p>
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BuiltInAi {
	/**
	 * Represents a {@link ScriptedLoopBrain} {@link IPositronicBrain}
	 * implementation that always throws {@link Throw#ROCK}. Really only useful
	 * for testing purposes, so it's marked as retired.
	 */
	ONE_SIDED_DIE_ROCK("oneSidedDieRock", true,
			new OneSidedDieBrain(Throw.ROCK)),

	/**
	 * Represents a {@link ScriptedLoopBrain} {@link IPositronicBrain}
	 * implementation that always throws {@link Throw#PAPER}. Really only useful
	 * for testing purposes, so it's marked as retired.
	 */
	ONE_SIDED_DIE_PAPER("oneSidedDiePaper", true, new OneSidedDieBrain(
			Throw.PAPER)),

	/**
	 * Represents the {@link ThreeSidedDieBrain} {@link IPositronicBrain}
	 * implementation.
	 */
	THREE_SIDED_DIE_V1("threeSidedDie", false, new ThreeSidedDieBrain()),

	/**
	 * Represents the {@link WinStayLoseShiftBrain} {@link IPositronicBrain}
	 * implementation.
	 */
	WIN_STAY_LOSE_SHIFT_V1("winStayLoseShift", false,
			new WinStayLoseShiftBrain()),

	/**
	 * Represents the {@link MetaWinStayLoseShiftBrain} {@link IPositronicBrain}
	 * implementation.
	 */
	META_WIN_STAY_LOSE_SHIFT_V1("metaWinStayLoseShift", false,
			new MetaWinStayLoseShiftBrain());

	private final String displayNameKey;
	private final boolean retired;
	private final IPositronicBrain positronicBrain;

	/**
	 * Enum constant constructor.
	 * 
	 * @param displayNameKey
	 *            the value to use for {@link #getDisplayNameKey()}
	 * @param retired
	 *            the value to use for {@link #isRetired()}
	 * @param positronicBrain
	 *            the value to use for {@link #getPositronicBrain()}
	 */
	private BuiltInAi(String displayNameKey, boolean retired,
			IPositronicBrain positronicBrain) {
		this.displayNameKey = displayNameKey;
		this.retired = retired;
		this.positronicBrain = positronicBrain;
	}

	/**
	 * @return a display/resource string lookup key (or key suffix) that
	 *         applications can associate with the display name to use for this
	 *         {@link BuiltInAi}
	 */
	public String getDisplayNameKey() {
		return displayNameKey;
	}

	/**
	 * @return if <code>true</code>, indicates that this particular
	 *         {@link BuiltInAi} should no longer be allowed to join new games
	 */
	public boolean isRetired() {
		return retired;
	}

	/**
	 * @return a singleton instance of the {@link IPositronicBrain}
	 *         implementation that is associated with/represented by this
	 *         particular {@link BuiltInAi}
	 */
	@JsonIgnore
	public IPositronicBrain getPositronicBrain() {
		return positronicBrain;
	}

	/**
	 * @return a {@link List} of all the {@link BuiltInAi}s where
	 *         {@link BuiltInAi#isRetired()} is <code>false</code>
	 */
	public static List<BuiltInAi> active() {
		List<BuiltInAi> activeAis = new ArrayList<>();
		for (BuiltInAi ai : BuiltInAi.values())
			if (!ai.isRetired())
				activeAis.add(ai);
		return activeAis;
	}
}
