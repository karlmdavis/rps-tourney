package com.justdavis.karl.rpstourney.service.api.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.threeten.bp.Instant;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;
import com.justdavis.karl.rpstourney.service.api.jaxb.InstantJaxbAdapter;

/**
 * The base class for {@link Game} and {@link GameView}.
 */
@MappedSuperclass
@IdClass(Game.GamePk.class)
class AbstractGame {
	@Id
	@Column(name = "`id`", nullable = false, updatable = false)
	@XmlElement
	protected String id;

	@Column(name = "`createdTimestamp`", nullable = false, updatable = false)
	@org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp")
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	protected Instant createdTimestamp;

	@Column(name = "`state`")
	@Enumerated(EnumType.STRING)
	@XmlElement
	protected State state;

	@Column(name = "`maxRounds`")
	@XmlElement
	protected int maxRounds;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderBy("roundIndex ASC")
	@XmlElementWrapper(name = "rounds")
	@XmlElement(name = "round")
	protected List<GameRound> rounds;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "`player1Id`")
	@XmlElement
	protected Player player1;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "`player2Id`")
	@XmlElement
	protected Player player2;

	/**
	 * Constructs a new {@link AbstractGame} instance.
	 * 
	 * @param id
	 *            the value to use for {@link #getId()}t
	 * @param createdTimestamp
	 *            the value to use for {@link #getCreatedTimestamp()}
	 * @param state
	 *            the value to use for {@link #getState()}
	 * @param maxRounds
	 *            the value to use for {@link #getMaxRounds()}
	 * @param rounds
	 *            the value to use for {@link #getRounds()}
	 * @param player1
	 *            the value to use for {@link #getPlayer1()}
	 * @param player2
	 *            the value to use for {@link #getPlayer2()}
	 */
	protected AbstractGame(String id, Instant createdTimestamp, State state,
			int maxRounds, List<GameRound> rounds, Player player1,
			Player player2) {
		if (id == null)
			throw new IllegalArgumentException();
		if (createdTimestamp == null)
			throw new IllegalArgumentException();
		if (state == null)
			throw new IllegalArgumentException();
		Game.validateMaxRoundsValue(maxRounds);
		if (rounds == null)
			throw new IllegalArgumentException();
		if (player1 == null)
			throw new IllegalArgumentException();

		this.id = id;
		this.createdTimestamp = createdTimestamp;
		this.state = state;
		this.maxRounds = maxRounds;
		this.rounds = rounds;
		this.player1 = player1;
		this.player2 = player2;
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	protected AbstractGame() {
	}

	/**
	 * @return the unique ID for this {@link Game}, which will match the
	 *         following regular expression: <code>[a-zA-Z]{1,10}</code>
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the date-time that this {@link Game} was created
	 */
	public Instant getCreatedTimestamp() {
		return createdTimestamp;
	}

	/**
	 * @return the {@link State} that this {@link Game} is currently in
	 */
	public State getState() {
		return state;
	}

	/**
	 * @return the maximum number of non-tied rounds that play will continue for
	 *         (though play will end early if a player takes a non-loseable
	 *         lead), which will be at least 1, an odd number, and less than or
	 *         equal to {@link #MAX_MAX_ROUNDS}
	 */
	public int getMaxRounds() {
		return maxRounds;
	}

	/**
	 * Returns an immutable copy of the {@link List} of {@link GameRound}s that
	 * are part of this {@link Game}, where the last {@link GameRound} in the
	 * {@link List} will be the current or final round of play. Will return an
	 * empty {@link List} if {@link #getState()} is
	 * {@value State#WAITING_FOR_PLAYER}.
	 * 
	 * @return an immutable copy of the {@link List} of {@link GameRound}s that
	 *         are part of this {@link Game}, or an empty {@link List} if
	 *         {@link #getState()} is {@value State#WAITING_FOR_PLAYER}
	 */
	public List<GameRound> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	/**
	 * @return the latest {@link GameRound#getThrowForPlayer1Timestamp()} /
	 *         {@link GameRound#getThrowForPlayer2Timestamp()} value for the
	 *         {@link GameRound}s in this {@link Game}, or
	 *         {@link Game#getCreatedTimestamp()} if no throws have yet been
	 *         made
	 */
	public Instant getLastThrowTimestamp() {
		Instant lastThrowTime = createdTimestamp;
		for (GameRound round : rounds) {
			Instant throwForPlayer1Timestamp = round
					.getThrowForPlayer1Timestamp();
			if (throwForPlayer1Timestamp != null
					&& lastThrowTime.compareTo(throwForPlayer1Timestamp) < 1)
				lastThrowTime = throwForPlayer1Timestamp;

			Instant throwForPlayer2Timestamp = round
					.getThrowForPlayer2Timestamp();
			if (throwForPlayer2Timestamp != null
					&& lastThrowTime.compareTo(throwForPlayer2Timestamp) < 1)
				lastThrowTime = throwForPlayer2Timestamp;
		}

		return lastThrowTime;
	}

	/**
	 * @return the number of {@link GameRound}s in {@link #getRounds()} that
	 *         {@link #getPlayer1()} has won
	 */
	@XmlElement
	public int getScoreForPlayer1() {
		Result playerWonResult = Result.PLAYER_1_WON;
		int playerWins = countRoundsWithResult(playerWonResult);
		return playerWins;
	}

	/**
	 * @return the number of {@link GameRound}s in {@link #getRounds()} that
	 *         {@link #getPlayer2()} has won
	 */
	@XmlElement
	public int getScoreForPlayer2() {
		Result playerWonResult = Result.PLAYER_2_WON;
		int playerWins = countRoundsWithResult(playerWonResult);
		return playerWins;
	}

	/**
	 * @param result
	 *            the {@link GameRound#getResult()} value to match
	 * @return the number of {@link GameRound}s in {@link #getRounds()} with the
	 *         specified {@link GameRound#getResult()} value
	 */
	private int countRoundsWithResult(Result result) {
		int playerWins = 0;
		for (GameRound round : rounds) {
			/*
			 * If this round isn't complete, the game is still in-progress and
			 * we can stop counting early.
			 */
			if (round.getResult() == null)
				break;

			if (round.getResult() == result)
				playerWins++;
		}

		return playerWins;
	}

	/**
	 * Determines the {@link Player} that won this {@link Game}, or
	 * <code>null</code> if {@link Game#getState()} is not yet
	 * {@link State#FINISHED}.
	 * 
	 * @return the {@link Player} that won this {@link Game}.
	 */
	@XmlElement
	public Player getWinner() {
		Player winningPlayer = checkForWinner();
		return winningPlayer;
	}

	/**
	 * @return the {@link Player} that won this {@link Game}, or
	 *         <code>null</code> if the game is still in-progress
	 */
	protected Player checkForWinner() {
		if (state == State.WAITING_FOR_PLAYER)
			return null;

		// Count the number of rounds won by each player.
		int player1Wins = getScoreForPlayer1();
		int player2Wins = getScoreForPlayer2();

		// Is the game still in progress?
		int numWinsNeeded = (maxRounds / 2) + 1;
		if (player1Wins < numWinsNeeded && player2Wins < numWinsNeeded)
			return null;

		// Determine who won.
		if (player1Wins > player2Wins)
			return player1;
		else if (player2Wins > player1Wins)
			return player2;
		else
			throw new BadCodeMonkeyException();
	}

	/**
	 * Note: Player order has no effect on gameplay.
	 * 
	 * @return the first {@link Player}
	 */
	public Player getPlayer1() {
		return player1;
	}

	/**
	 * Note: Player order has no effect on gameplay.
	 * 
	 * @return the second {@link Player}, or <code>null</code> if they have not
	 *         yet been identified
	 */
	public Player getPlayer2() {
		return player2;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName());
		builder.append(" [id=");
		builder.append(id);
		builder.append(", state=");
		builder.append(state);
		builder.append(", maxRounds=");
		builder.append(maxRounds);
		builder.append(", player1=");
		builder.append(player1);
		builder.append(", player2=");
		builder.append(player2);
		builder.append(", rounds=");
		builder.append(rounds);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * The JPA {@link IdClass} for {@link AbstractGame}/{@link Game}.
	 */
	public static final class GamePk implements Serializable {
		/*
		 * Design note: This IDClass is required because GameRound has a
		 * compound PK-and-FK relationship to Game, and its IdClass must
		 * reference this one.
		 */

		private static final long serialVersionUID = -2542571612762568669L;

		private String id;

		/**
		 * This public, no-arg/default constructor is required by JPA.
		 */
		public GamePk() {
		}

		/**
		 * @return this {@link IdClass} field corresponds to
		 *         {@link Game#getId()}
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id
		 *            the value to use for {@link #getId()}
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			/*
			 * This method was generated via Eclipse's 'Source > Generate
			 * hashCode() and equals()...' function.
			 */

			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			/*
			 * This method was generated via Eclipse's 'Source > Generate
			 * hashCode() and equals()...' function.
			 */

			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GamePk other = (GamePk) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
}