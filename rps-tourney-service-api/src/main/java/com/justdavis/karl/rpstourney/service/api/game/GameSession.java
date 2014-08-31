package com.justdavis.karl.rpstourney.service.api.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.DynamicUpdate;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;

/**
 * <p>
 * Models a given play session of the game: tracking the moves that have been
 * made, determining the winner (if any), etc.
 * </p>
 * <p>
 * Here are the rules:
 * </p>
 * <ul>
 * <li>Each game will last a pre-specified (odd) number of rounds.</li>
 * <li>Each round of the game, players select a {@link Throw}.</li>
 * <li>If the two throws are different, one of the players will have won the
 * round. If the two throws are the same, the players have tied. Tied rounds do
 * not count towards the agreed-upon total number of rounds.</li>
 * <li>{@link Throw#ROCK} beats {@link Throw#SCISSORS}, {@link Throw#SCISSORS}
 * beats {@link Throw#PAPER}, and {@link Throw#PAPER} beats {@link Throw#ROCK}.
 * (This logic is captured in {@link GameRound#getResult()}.)</li>
 * <li>The player that wins the most rounds wins the game. (The game stops at
 * whatever point it becomes impossible for one player to win.)</li>
 * </ul>
 * <p>
 * <strong>Warning:</strong> This class is not at all immutable or thread-safe.
 * It's the responsibility of the application using the class to ensure that
 * conflicting modifications aren't made. In the common case, where the
 * {@link GameSession} is being used with a database, it's recommended that each
 * single operation on the {@link GameSession} be a pessimistic transaction.
 * This allows the database to act as the mediator/enforcer of thread safety.
 * </p>
 */
@Entity
@Table(name = "\"GameSessions\"")
@DynamicUpdate(true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class GameSession {
	/**
	 * The maximum allowed value for {@link #getMaxRounds()}.
	 */
	public static final int MAX_MAX_ROUNDS = 1000000;

	/**
	 * The starting/default value for {@link #getMaxRounds()}.
	 */
	private static final int MAX_ROUNDS_DEFAULT = 3;

	/**
	 * The regular expression that all {@link #getId()} values must match.
	 */
	private static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z]{1,10}");

	private static final SecureRandom RANDOM = new SecureRandom();

	@Id
	@Column(name = "\"id\"", nullable = false, updatable = false)
	@XmlElement
	private final String id;

	@Column(name = "\"state\"")
	@Enumerated(EnumType.STRING)
	@XmlElement
	private State state;

	@Column(name = "\"maxRounds\"")
	@XmlElement
	private int maxRounds;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "gameSession", orphanRemoval = true)
	@OrderBy("roundIndex ASC")
	@XmlElementWrapper(name = "rounds")
	@XmlElement(name = "round")
	private List<GameRound> rounds;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "\"player1Id\"")
	@XmlElement
	private final Player player1;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "\"player2Id\"")
	@XmlElement
	private Player player2;

	/**
	 * Constructs a new {@link GameSession} instance. The
	 * 
	 * @param player1
	 *            the value to use for {@link #getPlayer1()}
	 */
	public GameSession(Player player1) {
		this.id = generateRandomId();
		this.state = State.WAITING_FOR_PLAYER;

		if (player1 == null)
			throw new IllegalArgumentException();
		this.player1 = player1;

		this.maxRounds = MAX_ROUNDS_DEFAULT;
		this.rounds = new ArrayList<>();
		this.player2 = null;
	}

	/**
	 * JAXB and JPA require a default/no-args constructor. JPA also requires it
	 * to be non-private.
	 */
	protected GameSession() {
		this.id = null;
		this.state = null;
		this.maxRounds = -1;
		this.player1 = null;
		this.player2 = null;
		this.rounds = null;
	}

	/**
	 * @return a random value for the {@link #getId()} field, of exactly 10
	 *         alphabetic characters
	 */
	private static String generateRandomId() {
		StringBuilder id = new StringBuilder(10);
		// Generate 10 random chars matching [a-zA-Z]
		for (int i = 0; i < 10; i++) {
			// Generate a single random char matching [a-zA-Z].
			char randomChar = (char) RANDOM.nextInt(52);
			if (randomChar < 26)
				randomChar = (char) (randomChar + 'A');
			else
				randomChar = (char) (randomChar - 26 + 'a');

			id.append(randomChar);
		}

		// Sanity check the result.
		if (!ID_PATTERN.matcher(id).matches())
			throw new BadCodeMonkeyException();

		return id.toString();
	}

	/**
	 * @return the unique ID for this {@link GameSession}, which will match the
	 *         following regular expression: <code>[a-zA-Z]{1,10}</code>
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the {@link State} that this {@link GameSession} is currently in
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
	 * <p>
	 * Sets a new value for {@link #getMaxRounds()}.
	 * </p>
	 * <p>
	 * Concurrency/JPA safety: This method is not safe for use when the
	 * {@link GameSession} is being stored in a JPA database and being modified
	 * by more than one thread/client/whatever.
	 * </p>
	 * 
	 * @param maxRounds
	 *            the value to use for {@link #getMaxRounds()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if
	 *             {@link #getState()} is not {@link State#WAITING_FOR_PLAYER}
	 *             or {@link State#WAITING_FOR_FIRST_THROW}.
	 */
	public void setMaxRounds(int maxRounds) {
		validateMaxRoundsValue(maxRounds);
		if (!(state == State.WAITING_FOR_PLAYER || state == State.WAITING_FOR_FIRST_THROW))
			throw new GameConflictException("Game has already started.");

		this.maxRounds = maxRounds;
	}

	/**
	 * Validates the proposed {@link #setMaxRounds(int)} value.
	 * 
	 * @param maxRounds
	 *            a value that might be passed to {@link #setMaxRounds(int)}
	 * @throws IllegalArgumentException
	 *             An {@link IllegalArgumentException} will be thrown if the
	 *             specified number of rounds is not allowed.
	 */
	public static void validateMaxRoundsValue(int maxRounds) {
		if (maxRounds < 1)
			throw new IllegalArgumentException();
		if (maxRounds % 2 == 0)
			throw new IllegalArgumentException();
		if (maxRounds > MAX_MAX_ROUNDS)
			throw new IllegalArgumentException();

		// It passes muster. Just return.
	}

	/**
	 * Returns an immutable copy of the {@link List} of {@link GameRound}s that
	 * are part of this {@link GameSession}, where the last {@link GameRound} in
	 * the {@link List} will be the current or final round of play. Will return
	 * an empty {@link List} if {@link #getState()} is
	 * {@value State#WAITING_FOR_PLAYER}.
	 * 
	 * @return an immutable copy of the {@link List} of {@link GameRound}s that
	 *         are part of this {@link GameSession}, or an empty {@link List} if
	 *         {@link #getState()} is {@value State#WAITING_FOR_PLAYER}
	 */
	public List<GameRound> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	/**
	 * 
	 * @return <code>true</code> if {@link #prepareRound()} needs to be called
	 *         before the next {@link #submitThrow(int, Player, Throw)},
	 *         <code>false</code> if it does not
	 * @see #prepareRound()
	 */
	public boolean isRoundPrepared() {
		if (state == State.WAITING_FOR_PLAYER)
			return false;
		if (state == State.FINISHED)
			return false;

		int currentRoundIndex = rounds.size() - 1;
		GameRound currentRound = rounds.get(currentRoundIndex);

		/*
		 * Is the current round complete? (Note that the game isn't marked
		 * FINISHED.)
		 */
		if (currentRound.getResult() != null) {
			return false;
		}

		return true;
	}

	/**
	 * <p>
	 * Before calling {@link #submitThrow(int, Player, Throw)} each
	 * {@link GameRound}, players/clients must first do the following:
	 * </p>
	 * 
	 * <pre>
	 * GameSession game = ...;
	 * 
	 * if (!game.isRoundPrepared()) {
	 *   game.prepareRound();
	 * }
	 * </pre>
	 * <p>
	 * The call to {@link #prepareRound()} will first check to see if the game
	 * is still in progress or if it has been won. If it's been won, it will set
	 * {@link #getState()} to {@link State#FINISHED}. If it's still in-progress,
	 * it will create the next {@link GameRound} and append it to
	 * {@link #getRounds()}. Subsequent calls {@link #prepareRound()} within the
	 * same actual round will not do anything.
	 * </p>
	 * <p>
	 * <strong>Caution:</strong> If the {@link GameSession} being used is
	 * persistent (to a DB via JPA), then any call to this method must be be
	 * made by itself in a single transaction. Please note that multiple
	 * concurrent calls to {@link #prepareRound()} by clients with different
	 * copies of the {@link GameSession} (from the DB) may lead to DB constraint
	 * violations for some of the calls. Such errors can be safely ignored, as
	 * they simply indicate that another client's call was successful.
	 * </p>
	 * <p>
	 * Design note: Originally, this method's logic was part of
	 * {@link #submitThrow(int, Player, Throw)}. However, in the face of
	 * concurrent clients/players, it's quite likely that both players will make
	 * their move at the same time, and thus each copy of the
	 * {@link GameSession} will not realize that the round is complete. By
	 * splitting this logic into a separate method, we also ensure that
	 * {@link #submitThrow(int, Player, Throw)} won't generate DB constraint
	 * errors; isolating the logic that might cause such errors in a separate
	 * method seems prudent.
	 * </p>
	 */
	public void prepareRound() {
		if (state == State.WAITING_FOR_PLAYER)
			return;
		if (state == State.FINISHED)
			return;

		int roundIndex = rounds.size() - 1;
		GameRound currentRound = rounds.get(roundIndex);

		// Is the current round complete?
		if (currentRound.getResult() != null) {
			Player winner = checkForWinner();

			// Is the game still going?
			if (winner == null) {
				// Add a new round.
				GameRound nextRound = new GameRound(this, roundIndex + 1);
				rounds.add(nextRound);
			} else {
				// Mark the game as finished.
				state = State.FINISHED;
			}
		}
	}

	/**
	 * Submits the specified {@link Throw} for the specified {@link Player}, to
	 * the current {@link GameRound}.
	 * 
	 * @param roundIndex
	 *            the {@link GameRound#getRoundIndex()} of the current round
	 *            (used to verify that gameplay is correctly synchronized)
	 * @param player
	 *            the {@link Player} to submit the {@link Throw} for
	 * @param throwForPlayer
	 *            the {@link Throw} to submit for the {@link Player}
	 * @throws GameConflictException
	 *             <p>
	 *             An {@link GameConflictException} will be thrown in the
	 *             following cases:
	 *             </p>
	 *             <ul>
	 *             <li>If {@link #getState()} is not {@link State#STARTED}.</li>
	 *             <li>If the specified <code>roundIndex</code> is not the same
	 *             as {@link GameRound#getRoundIndex()} in the last/current
	 *             round.</li>
	 *             <li>If the {@link Player} has already submitted a
	 *             {@link Throw} for the {@link GameRound}.</li>
	 *             </ul>
	 */
	public void submitThrow(int roundIndex, Player player, Throw throwForPlayer) {
		if (state == State.WAITING_FOR_PLAYER)
			throw new GameConflictException("Game has not started.");
		if (state == State.FINISHED)
			throw new GameConflictException("Game has ended.");
		if (player == null)
			throw new IllegalArgumentException();
		if (throwForPlayer == null)
			throw new IllegalArgumentException();

		/*
		 * We need to ensure that the move clients think they're making is for
		 * the actual current round. This is to prevent (and expose) gameplay
		 * synchronization issues.
		 */
		GameRound currentRound = rounds.get(rounds.size() - 1);
		if (roundIndex != currentRound.getRoundIndex()) {
			/*
			 * Either the client is out-of-synch or it neglected to call
			 * prepareRound() first.
			 */
			throw new GameConflictException(
					String.format(
							"Specified round '%d' is not current round '%d': %s",
							roundIndex, currentRound.getRoundIndex(),
							rounds.toString()));
		}

		// Add the Throw to the round.
		if (player.equals(player1))
			currentRound.setThrowForPlayer1(throwForPlayer);
		else if (player.equals(player2))
			currentRound.setThrowForPlayer2(throwForPlayer);
		else
			throw new IllegalArgumentException();

		// Has the first throw been made?
		if (state == State.WAITING_FOR_FIRST_THROW) {
			state = State.STARTED;
		}

		/*
		 * To hopefully save a write transaction, we'll call isRoundPrepared()
		 * and prepareRound() here. This isn't mentioned in the JavaDocs,
		 * though, as clients always need to duplicate this themselves. This is
		 * because, in the case of concurrent clients, neither players' round
		 * may "look" complete until after both of their throws' transaction
		 * have committed.
		 */
		if (!isRoundPrepared())
			prepareRound();
	}

	/**
	 * Determines the {@link Player} that won this {@link GameSession}, or
	 * <code>null</code> if {@link GameSession#getState()} is not yet
	 * {@link State#FINISHED}.
	 * 
	 * @return the {@link Player} that won this {@link GameSession}.
	 */
	@XmlElement
	public Player getWinner() {
		Player winningPlayer = checkForWinner();
		return winningPlayer;
	}

	/**
	 * @return the {@link Player} that won this {@link GameSession}, or
	 *         <code>null</code> if the game is still in-progress
	 */
	private Player checkForWinner() {
		if (state == State.WAITING_FOR_PLAYER)
			return null;

		// Count the number of rounds won by each player.
		int player1Wins = 0;
		int player2Wins = 0;
		for (GameRound round : rounds) {
			// If this round isn't complete, the game is still in-progress.
			if (round.getResult() == null)
				return null;

			if (round.getResult() == Result.PLAYER_1_WON)
				player1Wins++;
			if (round.getResult() == Result.PLAYER_2_WON)
				player2Wins++;
		}

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
	 * <p>
	 * This method may only be called if {@link #getPlayer2()} is
	 * <code>null</code>; {@link Player}s may not be removed from a game, once
	 * joined to it.
	 * </p>
	 * <p>
	 * Calling this method will also transition {@link #getState()} to
	 * {@value State#STARTED}.
	 * </p>
	 * 
	 * @param player2
	 *            the value to use for {@link #getPlayer2()}
	 * @throws GameConflictException
	 *             A {@link GameConflictException} will be thrown if
	 *             {@link #getState()} is not {@link State#WAITING_FOR_PLAYER}.
	 */
	public void setPlayer2(Player player2) {
		// Once set, the player can't be changed.
		if (this.player2 != null)
			throw new GameConflictException("Game already has both players.");

		// Can't set a null player.
		if (player2 == null)
			throw new IllegalArgumentException();

		// The logic in this class doesn't work if the players are the same.
		if (player2.equals(player1))
			throw new IllegalArgumentException();

		this.player2 = player2;
		player2Joined();
	}

	/**
	 * Will initialize {@link #getRounds()} and then transition
	 * {@link #getState()} to {@link State#STARTED}.
	 */
	private void player2Joined() {
		GameRound firstRound = new GameRound(this, 0);
		this.rounds.add(firstRound);

		this.state = State.WAITING_FOR_FIRST_THROW;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameSession [id=");
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
	 * These values for the {@link GameSession#getState()} field represent a
	 * very simple state machine for the state of each game.
	 */
	public static enum State {
		/**
		 * {@link GameSession}s in this state are waiting for
		 * {@link GameSession#getPlayer2()} to be identified. In this
		 * {@link State}, the {@link GameSession#setMaxRounds(int)} and
		 * {@link GameSession#setPlayer2(Player)} methods may be used, and
		 * {@link GameSession#getRounds()} will return an empty {@link List}.
		 */
		WAITING_FOR_PLAYER,

		/**
		 * {@link GameSession}s in this state are ready to be played, but have
		 * not yet had a call to
		 * {@link GameSession#submitThrow(int, Player, Throw)} complete yet. In
		 * this {@link State}, the {@link GameSession#setMaxRounds(int)} method
		 * may be used, and {@link GameSession#getRounds()} will return a
		 * {@link List} with just the first {@link GameRound} in it.
		 */
		WAITING_FOR_FIRST_THROW,

		/**
		 * {@link GameSession}s in this state represent games that are
		 * in-progress. None of the {@link GameSession} setters may be used, and
		 * {@link GameSession#getRounds()} will return a non-<code>null</code>,
		 * non-empty {@link List}.
		 */
		STARTED,

		/**
		 * {@link GameSession}s in this state represent games that were played
		 * and have completed.
		 */
		FINISHED;
	}
}
