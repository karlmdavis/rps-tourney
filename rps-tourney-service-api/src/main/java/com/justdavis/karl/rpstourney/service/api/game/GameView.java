package com.justdavis.karl.rpstourney.service.api.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * <p>
 * An immutable, point-in-time view of a {@link Game}'s state.
 * </p>
 * <p>
 * Instances of this class, rather than {@link Game}, should be returned by the
 * web service in order to:
 * </p>
 * <ul>
 * <li>Prevent web service users from trying to directly modify the game state,
 * rather than going through the web service.</li>
 * <li>Hide moves made by players in rounds that aren't yet complete.</li>
 * </ul>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class GameView extends AbstractGame {
	/**
	 * Constructs a new {@link GameView} instance.
	 * 
	 * @param gameToWrap
	 *            the {@link Game} instance that the new {@link GameView} will
	 *            be a view of
	 * @param user
	 *            the user who requested or will be shown the resulting
	 *            {@link GameView}, which will be used to determine how to
	 *            filter {@link GameView#getRounds()}, or <code>null</code> if
	 *            the user is anonymous
	 */
	public GameView(Game gameToWrap, Account user) {
		super(gameToWrap.getId(), gameToWrap.getCreatedTimestamp(), gameToWrap
				.getState(), gameToWrap.getMaxRounds(), filterRoundsForUser(
				gameToWrap, user), gameToWrap.getPlayer1(), gameToWrap
				.getPlayer2());
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB spec.
	 */
	@Deprecated
	GameView() {
	}

	/**
	 * @param gameToWrap
	 *            the {@link Game} instance to filter the
	 *            {@link Game#getRounds()} of
	 * @param user
	 *            the user who requested or will be shown the resulting
	 *            {@link GameView}, or <code>null</code> if the user is
	 *            anonymous
	 * @return the filtered {@link GameRound}s that should be visible to the
	 *         specified user
	 */
	private static List<GameRound> filterRoundsForUser(Game gameToWrap,
			Account user) {
		boolean isPlayer1 = user != null
				&& user.equals(gameToWrap.getPlayer1().getHumanAccount());
		boolean isPlayer2 = user != null && gameToWrap.getPlayer2() != null
				&& user.equals(gameToWrap.getPlayer2().getHumanAccount());

		List<GameRound> rounds = gameToWrap.getRounds();
		List<GameRound> filteredRounds = new ArrayList<GameRound>(rounds.size());
		for (GameRound round : rounds) {
			if (round.getResult() != null) {
				// Any completed round should be visible to everyone.
				filteredRounds.add(round);
			} else {
				// Otherwise, build a copy of the round and hide others' moves.
				GameRound filteredRound = new GameRound(round.getGame(),
						round.getRoundIndex(), round.getAdjustedRoundIndex());

				Throw player1Throw = round.getThrowForPlayer1();
				if (player1Throw != null && isPlayer1)
					filteredRound.setThrowForPlayer1(player1Throw,
							round.getThrowForPlayer1Timestamp());

				Throw player2Throw = round.getThrowForPlayer2();
				if (player2Throw != null && isPlayer2)
					filteredRound.setThrowForPlayer2(player2Throw,
							round.getThrowForPlayer2Timestamp());

				filteredRounds.add(filteredRound);
			}
		}

		return Collections.unmodifiableList(filteredRounds);
	}
}
