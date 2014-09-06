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
import org.threeten.bp.Instant;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.jaxb.InstantJaxbAdapter;

/**
 * <p>
 * Represents a round of a {@link GameSession}, tracking the moves made by the
 * players.
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
@Table(name = "\"GameRounds\"")
@DynamicUpdate(true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class GameRound {
	@Id
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH })
	@JoinColumn(name = "\"gameSessionId\"")
	@XmlTransient
	private final GameSession gameSession;

	@Id
	@Column(name = "\"roundIndex\"", nullable = false, updatable = false)
	@XmlElement
	private final int roundIndex;

	@Column(name = "\"throwForPlayer1\"")
	@Enumerated(EnumType.STRING)
	@XmlElement
	private Throw throwForPlayer1;

	@Column(name = "\"throwForPlayer1Timestamp\"", nullable = true, updatable = true)
	@org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp")
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	private Instant throwForPlayer1Timestamp;

	@Column(name = "\"throwForPlayer2\"")
	@Enumerated(EnumType.STRING)
	@XmlElement
	private Throw throwForPlayer2;

	@Column(name = "\"throwForPlayer2Timestamp\"", nullable = true, updatable = true)
	@org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp")
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	private Instant throwForPlayer2Timestamp;

	/**
	 * Constructs a new {@link GameRound} instance.
	 * 
	 * @param gameSession
	 *            the value to use for {@link #getGameSession()}
	 * @param roundIndex
	 *            the value to use for {@link #getRoundIndex()}
	 */
	public GameRound(GameSession gameSession, int roundIndex) {
		if (gameSession == null)
			throw new IllegalArgumentException();
		if (roundIndex < 0)
			throw new IllegalArgumentException();

		this.gameSession = gameSession;
		this.roundIndex = roundIndex;
		this.throwForPlayer1 = null;
		this.throwForPlayer1Timestamp = null;
		this.throwForPlayer2 = null;
		this.throwForPlayer2Timestamp = null;
	}

	/**
	 * JAXB and JPA require a default/no-args constructor. JPA also requires it
	 * to be non-private.
	 */
	protected GameRound() {
		this.gameSession = null;
		this.roundIndex = -1;
		this.throwForPlayer1 = null;
		this.throwForPlayer1Timestamp = null;
		this.throwForPlayer2 = null;
		this.throwForPlayer2Timestamp = null;
	}

	/**
	 * @return the {@link GameSession} that this {@link GameRound} is a part of
	 */
	public GameSession getGameSession() {
		return gameSession;
	}

	/**
	 * @return the index of this {@link GameRound} in the {@link GameSession} 
	 *         it's part of
	 */
	public int getRoundIndex() {
		return roundIndex;
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
	 * {@link GameSession}.
	 * 
	 * @param throwForPlayer1
	 *            the value to use for {@link #getThrowForPlayer1()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if the
	 *             {@link Player} has already submitted a {@link Throw} for this
	 *             {@link GameRound}.
	 * @see GameSession#submitThrow(int, Player, Throw)
	 */
	void setThrowForPlayer1(Throw throwForPlayer1) {
		if (throwForPlayer1 == null)
			throw new IllegalArgumentException();
		if (this.throwForPlayer1 != null)
			throw new GameConflictException(String.format(
					"Throw already set to '%s'; can't set to '%s'.",
					this.throwForPlayer1, throwForPlayer1));

		this.throwForPlayer1 = throwForPlayer1;
		this.throwForPlayer1Timestamp = Instant.now();
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
	 * {@link GameSession}.
	 * 
	 * @param throwForPlayer2
	 *            the value to use for {@link #getThrowForPlayer2()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if the
	 *             {@link Player} has already submitted a {@link Throw} for this
	 *             {@link GameRound}.
	 * @see GameSession#submitThrow(int, Player, Throw)
	 */
	void setThrowForPlayer2(Throw throwForPlayer2) {
		if (throwForPlayer2 == null)
			throw new IllegalArgumentException();
		if (this.throwForPlayer2 != null)
			throw new GameConflictException(String.format(
					"Throw already set to '%s'; can't set to '%s'.",
					this.throwForPlayer2, throwForPlayer2));

		this.throwForPlayer2 = throwForPlayer2;
		this.throwForPlayer2Timestamp = Instant.now();
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
		PLAYER_1_WON,

		PLAYER_2_WON,

		TIED;
	}

	/**
	 * The {@link IdClass} for the {@link GameRound} JPA {@link Entity} class.
	 */
	public static final class GameRoundPk implements Serializable {
		private static final long serialVersionUID = 7308207335090557784L;

		private GameSession gameSession;
		private int roundIndex;

		/**
		 * This public, no-arg/default constructor is required by JPA.
		 */
		public GameRoundPk() {
			this.gameSession = null;
			this.roundIndex = -1;
		}

		/**
		 * @return the {@link IdClass} field for
		 *         {@link GameRound#getGameSession()}
		 */
		public GameSession getGameSession() {
			return gameSession;
		}

		/**
		 * @param gameSession
		 *            the value to use for {@link #getGameSession()}
		 */
		public void setGameSession(GameSession gameSession) {
			this.gameSession = gameSession;
		}

		/**
		 * @return the {@link IdClass} field for
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
	}
}
