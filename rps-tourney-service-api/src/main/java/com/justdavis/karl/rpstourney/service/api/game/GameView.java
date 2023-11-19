package com.justdavis.karl.rpstourney.service.api.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * <p>
 * An immutable, point-in-time view of a {@link Game}'s state, filtered to hide moves that shouldn't yet be revealed to
 * other players.
 * </p>
 * <p>
 * Instances of this class, rather than {@link Game}, should be returned by the web service in order to:
 * </p>
 * <ul>
 * <li>Prevent web service users from trying to directly modify the game state, rather than going through the web
 * service.</li>
 * <li>Hide moves made by players in rounds that aren't yet complete.</li>
 * </ul>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public final class GameView extends AbstractGame {
	@XmlElement
	private final Player viewPlayer;

	/**
	 * Constructs a new {@link GameView} instance.
	 *
	 * @param gameToWrap
	 *            the {@link Game} instance that the new {@link GameView} will be a view of
	 * @param viewPlayer
	 *            the value to use for {@link #getViewPlayer()}
	 */
	public GameView(Game gameToWrap, Player viewPlayer) {
		super(gameToWrap.getId(), gameToWrap.getCreatedTimestamp(), gameToWrap.getState(), gameToWrap.getMaxRounds(),
				filterRoundsForPlayer(gameToWrap, viewPlayer), gameToWrap.getPlayer1(), gameToWrap.getPlayer2());

		this.viewPlayer = viewPlayer;
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided to comply with the JAXB spec.
	 */
	@Deprecated
	GameView() {
		this.viewPlayer = null;
	}

	/**
	 * @param gameToWrap
	 *            the {@link Game} instance to filter the {@link Game#getRounds()} of
	 * @param player
	 *            the {@link Player} who requested or will be shown the resulting {@link GameView}, or <code>null</code>
	 *            if it's for someone other than one of the game's players
	 * @return the filtered {@link GameRound}s that should be visible to the specified {@link Player}
	 */
	private static List<GameRound> filterRoundsForPlayer(Game gameToWrap, Player player) {
		boolean isPlayer1 = player != null && player.equals(gameToWrap.getPlayer1());
		boolean isPlayer2 = player != null && gameToWrap.getPlayer2() != null && player.equals(gameToWrap.getPlayer2());

		List<GameRound> rounds = gameToWrap.getRounds();
		List<GameRound> filteredRounds = new ArrayList<GameRound>(rounds.size());
		for (GameRound round : rounds) {
			if (round.getResult() != null) {
				// Any completed round should be visible to everyone.
				filteredRounds.add(round);
			} else {
				// Otherwise, build a copy of the round and hide others' moves.
				GameRound filteredRound = new GameRound(round.getGame(), round.getRoundIndex(),
						round.getAdjustedRoundIndex());

				Throw player1Throw = round.getThrowForPlayer1();
				if (player1Throw != null && isPlayer1)
					filteredRound.setThrowForPlayer1(player1Throw, round.getThrowForPlayer1Timestamp());

				Throw player2Throw = round.getThrowForPlayer2();
				if (player2Throw != null && isPlayer2)
					filteredRound.setThrowForPlayer2(player2Throw, round.getThrowForPlayer2Timestamp());

				filteredRounds.add(filteredRound);
			}
		}

		return Collections.unmodifiableList(filteredRounds);
	}

	/**
	 * @return the {@link Player} in the {@link Game} who requested or will be shown the resulting {@link GameView},
	 *         which will be used to determine how to filter {@link GameView#getRounds()}, or <code>null</code> if it
	 *         will be displayed to someone who is not one of the {@link Game}'s {@link Player}s
	 */
	public Player getViewPlayer() {
		return viewPlayer;
	}
}
