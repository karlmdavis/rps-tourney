package com.justdavis.karl.rpstourney.service.api.game;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.DynamicUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.AbstractGame.GamePk;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException.ConflictType;
import com.justdavis.karl.rpstourney.service.api.jaxb.InstantJaxbAdapter;

/**
 * <p>
 * Represents a round of a {@link Game}, tracking the moves made by the players.
 * </p>
 * <p>
 * Please note that instances of this class are <strong>not</strong> immutable:
 * the {@link #setThrowForPlayer1(Throw)} and {@link #setThrowForPlayer2(Throw)}
 * methods will modify data. However, as both of those methods may only be
 * called once, instances are effectively immutable after they've both been
 * supplied with a value.
 * </p>
 */
@Entity
@IdClass(GameRound.GameRoundPk.class)
@Table(name = "`GameRounds`")
@DynamicUpdate(true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GameRound {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GameRound.class);

	@Id
	/*
	 * FIXME This column can't be quoted unless/until
	 * https://hibernate.atlassian.net/browse/HHH-9427 is resolved.
	 */
	@JoinColumn(name = "gameId")
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH })
	@XmlTransient
	private Game game;

	@Id
	@Column(name = "`roundIndex`", nullable = false, updatable = false)
	@XmlElement
	private int roundIndex;

	@Column(name = "`adjustedRoundIndex`", nullable = false, updatable = false)
	@XmlElement
	private int adjustedRoundIndex;

	@Column(name = "`throwForPlayer1`")
	@Enumerated(EnumType.STRING)
	@XmlElement
	private Throw throwForPlayer1;

	@Column(name = "`throwForPlayer1Timestamp`", nullable = true, updatable = true)
	@org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp")
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	private Instant throwForPlayer1Timestamp;

	@Column(name = "`throwForPlayer2`")
	@Enumerated(EnumType.STRING)
	@XmlElement
	private Throw throwForPlayer2;

	@Column(name = "`throwForPlayer2Timestamp`", nullable = true, updatable = true)
	@org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp")
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	private Instant throwForPlayer2Timestamp;

	/**
	 * Constructs a new {@link GameRound} instance.
	 * 
	 * @param game
	 *            the value to use for {@link #getGame()}
	 * @param roundIndex
	 *            the value to use for {@link #getRoundIndex()}
	 * @param adjustedRoundIndex
	 *            the value to use for {@link #getAdjustedRoundIndex()}
	 */
	public GameRound(Game game, int roundIndex, int adjustedRoundIndex) {
		if (game == null)
			throw new IllegalArgumentException();
		if (roundIndex < 0)
			throw new IllegalArgumentException();
		if (adjustedRoundIndex < 0)
			throw new IllegalArgumentException();
		if (adjustedRoundIndex > roundIndex)
			throw new IllegalArgumentException();

		this.game = game;
		this.roundIndex = roundIndex;
		this.adjustedRoundIndex = adjustedRoundIndex;
		this.throwForPlayer1 = null;
		this.throwForPlayer1Timestamp = null;
		this.throwForPlayer2 = null;
		this.throwForPlayer2Timestamp = null;
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	GameRound() {
	}

	/**
	 * @return the {@link Game} that this {@link GameRound} is a part of, or
	 *         <code>null</code> if this {@link GameRound} was accessed via an
	 *         unmarshalled {@link GameView}
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @return the index of this {@link GameRound} in the {@link Game} it's part
	 *         of
	 */
	public int getRoundIndex() {
		return roundIndex;
	}

	/**
	 * @return the index of this {@link GameRound} as if the {@link Game} it's
	 *         part of had not had any ties
	 */
	public int getAdjustedRoundIndex() {
		return adjustedRoundIndex;
	}

	/**
	 * @return the {@link Throw} that was selected by the first player in this
	 *         {@link GameRound}, or <code>null</code> if that player has not
	 *         yet selected their move
	 */
	public Throw getThrowForPlayer1() {
		return throwForPlayer1;
	}

	/**
	 * @return the date-time that {@link #setThrowForPlayer1(Throw)} was called
	 *         (with a valid value) for this {@link GameRound}, or
	 *         <code>null</code> if it hasn't yet
	 */
	public Instant getThrowForPlayer1Timestamp() {
		return throwForPlayer1Timestamp;
	}

	/**
	 * Note: this method may only be called once, and should only be called by
	 * {@link Game}.
	 * 
	 * @param throwForPlayer1
	 *            the value to use for {@link #getThrowForPlayer1()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if the
	 *             {@link Player} has already submitted a {@link Throw} for this
	 *             {@link GameRound}.
	 * @see Game#submitThrow(int, Player, Throw)
	 */
	void setThrowForPlayer1(Throw throwForPlayer1) {
		setThrowForPlayer1(throwForPlayer1, Instant.now());
	}

	/**
	 * Note: this method may only be called once, and should only be called by
	 * {@link GameView}.
	 * 
	 * @param throwForPlayer1
	 *            the value to use for {@link #getThrowForPlayer1()}
	 * @param throwForPlayer1Timestamp
	 *            the value to use for {@link #getThrowForPlayer1Timestamp()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if the
	 *             {@link Player} has already submitted a {@link Throw} for this
	 *             {@link GameRound}.
	 * @see Game#submitThrow(int, Player, Throw)
	 */
	void setThrowForPlayer1(Throw throwForPlayer1,
			Instant throwForPlayer1Timestamp) {
		if (throwForPlayer1 == null)
			throw new IllegalArgumentException();
		if (this.throwForPlayer1 != null) {
			LOGGER.warn("Throw already set to '{}'; can't set to '{}'.",
					this.throwForPlayer1, throwForPlayer1);
			throw new GameConflictException(ConflictType.THROW_ALREADY_SET);
		}

		this.throwForPlayer1 = throwForPlayer1;
		this.throwForPlayer1Timestamp = throwForPlayer1Timestamp;
	}

	/**
	 * @return the {@link Throw} that was selected by the second player in this
	 *         {@link GameRound}, or <code>null</code> if that player has not
	 *         yet selected their move
	 */
	public Throw getThrowForPlayer2() {
		return throwForPlayer2;
	}

	/**
	 * @return the date-time that {@link #setThrowForPlayer2(Throw)} was called
	 *         (with a valid value) for this {@link GameRound}, or
	 *         <code>null</code> if it hasn't yet
	 */
	public Instant getThrowForPlayer2Timestamp() {
		return throwForPlayer2Timestamp;
	}

	/**
	 * Note: this method may only be called once, and should only be called by
	 * {@link Game}.
	 * 
	 * @param throwForPlayer2
	 *            the value to use for {@link #getThrowForPlayer2()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if the
	 *             {@link Player} has already submitted a {@link Throw} for this
	 *             {@link GameRound}.
	 * @see Game#submitThrow(int, Player, Throw)
	 */
	void setThrowForPlayer2(Throw throwForPlayer2) {
		setThrowForPlayer2(throwForPlayer2, Instant.now());
	}

	/**
	 * Note: this method may only be called once, and should only be called by
	 * {@link GameView}.
	 * 
	 * @param throwForPlayer2
	 *            the value to use for {@link #getThrowForPlayer2()}
	 * @param throwForPlayer2Timestamp
	 *            the value to use for {@link #getThrowForPlayer2Timestamp()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if the
	 *             {@link Player} has already submitted a {@link Throw} for this
	 *             {@link GameRound}.
	 * @see Game#submitThrow(int, Player, Throw)
	 */
	void setThrowForPlayer2(Throw throwForPlayer2,
			Instant throwForPlayer2Timestamp) {
		if (throwForPlayer2 == null)
			throw new IllegalArgumentException();
		if (this.throwForPlayer2 != null) {
			LOGGER.warn("Throw already set to '{}'; can't set to '{}'.",
					this.throwForPlayer2, throwForPlayer2);
			throw new GameConflictException(ConflictType.THROW_ALREADY_SET);
		}

		this.throwForPlayer2 = throwForPlayer2;
		this.throwForPlayer2Timestamp = throwForPlayer2Timestamp;
	}

	/**
	 * @param role
	 *            the {@link PlayerRole} to get the {@link Throw} (if any) for
	 * @return the result of either {@link #getThrowForPlayer1()} or
	 *         {@link #getThrowForPlayer2()}, as specified
	 */
	public Throw getThrowForPlayer(PlayerRole role) {
		if (role == null)
			throw new IllegalArgumentException();

		if (role == PlayerRole.PLAYER_1)
			return getThrowForPlayer1();
		else if (role == PlayerRole.PLAYER_2)
			return getThrowForPlayer2();

		// Must have missed a case.
		throw new BadCodeMonkeyException();
	}

	/**
	 * @return the {@link Result} of this {@link GameRound}, or
	 *         <code>null</code> if the {@link GameRound} has not yet completed.
	 */
	public Result getResult() {
		if (throwForPlayer1 == null || throwForPlayer2 == null)
			return null;

		/*
		 * Eventually, I may want to abstract out this logic to allow for custom
		 * Throw types. For right now, though, this works.
		 */
		if (throwForPlayer1.equals(throwForPlayer2))
			return Result.TIED;
		if (throwForPlayer1 == Throw.ROCK && throwForPlayer2 == Throw.PAPER)
			return Result.PLAYER_2_WON;
		if (throwForPlayer1 == Throw.ROCK && throwForPlayer2 == Throw.SCISSORS)
			return Result.PLAYER_1_WON;
		if (throwForPlayer1 == Throw.PAPER && throwForPlayer2 == Throw.ROCK)
			return Result.PLAYER_1_WON;
		if (throwForPlayer1 == Throw.PAPER && throwForPlayer2 == Throw.SCISSORS)
			return Result.PLAYER_2_WON;
		if (throwForPlayer1 == Throw.SCISSORS && throwForPlayer2 == Throw.ROCK)
			return Result.PLAYER_2_WON;
		if (throwForPlayer1 == Throw.SCISSORS && throwForPlayer2 == Throw.PAPER)
			return Result.PLAYER_1_WON;

		// Must have a missing case in the logic above.
		throw new BadCodeMonkeyException();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameRound [roundIndex=");
		builder.append(roundIndex);
		builder.append(", throwForPlayer1=");
		builder.append(throwForPlayer1);
		builder.append(", throwForPlayer2=");
		builder.append(throwForPlayer2);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Enumerates the possible results for a completed {@link GameRound}.
	 */
	public static enum Result {
		PLAYER_1_WON(PlayerRole.PLAYER_1),

		PLAYER_2_WON(PlayerRole.PLAYER_2),

		TIED(null);

		private final PlayerRole playerRole;

		/**
		 * Enum constant constructor.
		 * 
		 * @param playerRole
		 *            the value to use for {@link #getWinningPlayerRole()}
		 */
		private Result(PlayerRole playerRole) {
			this.playerRole = playerRole;
		}

		/**
		 * @return the winning {@link PlayerRole} represented by this
		 *         {@link Result}, or <code>null</code> for {@link Result#TIED}
		 */
		public PlayerRole getWinningPlayerRole() {
			return playerRole;
		}
	}

	/**
	 * The JPA {@link IdClass} for {@link GameRound}.
	 */
	public static final class GameRoundPk implements Serializable {
		private static final long serialVersionUID = 9072061865707404807L;

		private GamePk game;
		private int roundIndex;

		/**
		 * This public, no-arg/default constructor is required by JPA.
		 */
		public GameRoundPk() {
		}

		/**
		 * @return this {@link IdClass} field corresponds to
		 *         {@link GameRound#getGame()}, which is mapped as a foreign key
		 *         to {@link Game#getId()}
		 */
		public GamePk getGame() {
			return game;
		}

		/**
		 * @param game
		 *            the value to use for {@link #getGame()}
		 */
		public void setGame(GamePk game) {
			this.game = game;
		}

		/**
		 * @return this {@link IdClass} field corresponds to
		 *         {@link GameRound#getRoundIndex()}
		 */
		public int getRoundIndex() {
			return roundIndex;
		}

		/**
		 * @param roundIndex
		 *            the value to use for {@link #getRoundIndex()}
		 */
		public void setRoundIndex(int roundIndex) {
			this.roundIndex = roundIndex;
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
			result = prime * result + ((game == null) ? 0 : game.hashCode());
			result = prime * result + roundIndex;
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
			GameRoundPk other = (GameRoundPk) obj;
			if (game == null) {
				if (other.game != null)
					return false;
			} else if (!game.equals(other.game))
				return false;
			if (roundIndex != other.roundIndex)
				return false;
			return true;
		}
	}
}
