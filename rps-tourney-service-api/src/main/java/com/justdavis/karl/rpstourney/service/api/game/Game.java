package com.justdavis.karl.rpstourney.service.api.game;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException.ConflictType;
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
 * {@link Game} is being used with a database, it's recommended that each single
 * operation on the {@link Game} be a pessimistic transaction. This allows the
 * database to act as the mediator/enforcer of thread safety.
 * </p>
 */
@Entity
@Table(name = "`Games`")
@DynamicUpdate(true)
public class Game extends AbstractGame {
	private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

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
	public static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z]{1,10}");

	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Constructs a new {@link Game} instance.
	 * 
	 * @param player1
	 *            the value to use for {@link #getPlayer1()}
	 */
	public Game(Player player1) {
		super(generateRandomId(), Instant.now(), State.WAITING_FOR_PLAYER, MAX_ROUNDS_DEFAULT,
				new ArrayList<GameRound>(), player1, null);
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	Game() {
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
	 * <p>
	 * Sets a new value for {@link #getMaxRounds()}.
	 * </p>
	 * <p>
	 * Concurrency/JPA safety: This method is not safe for use when the
	 * {@link Game} is being stored in a JPA database and being modified by more
	 * than one thread/client/whatever.
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
		if (!(state == State.WAITING_FOR_PLAYER || state == State.WAITING_FOR_FIRST_THROW)) {
			throw new GameConflictException(ConflictType.ROUNDS_FINALIZED);
		}

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
			throw new GameConflictException(ConflictType.ROUNDS_INVALID);
		if (maxRounds % 2 == 0)
			throw new GameConflictException(ConflictType.ROUNDS_INVALID);
		if (maxRounds > MAX_MAX_ROUNDS)
			throw new GameConflictException(ConflictType.ROUNDS_INVALID);

		// It passes muster. Just return.
	}

	/**
	 * <p>
	 * Before calling {@link #submitThrow(int, Player, Throw)} each
	 * {@link GameRound}, players/clients must first do the following:
	 * </p>
	 * 
	 * <pre>
	 * Game game = ...;
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
	 * <strong>Caution:</strong> If the {@link Game} being used is persistent
	 * (to a DB via JPA), then any call to this method must be be made by itself
	 * in a single transaction. Please note that multiple concurrent calls to
	 * {@link #prepareRound()} by clients with different copies of the
	 * {@link Game} (from the DB) may lead to DB constraint violations for some
	 * of the calls. Such errors can be safely ignored, as they simply indicate
	 * that another client's call was successful.
	 * </p>
	 * <p>
	 * Design note: Originally, this method's logic was part of
	 * {@link #submitThrow(int, Player, Throw)}. However, in the face of
	 * concurrent clients/players, it's quite likely that both players will make
	 * their move at the same time, and thus each copy of the {@link Game} will
	 * not realize that the round is complete. By splitting this logic into a
	 * separate method, we also ensure that
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
				int newRoundIndex = roundIndex + 1;
				int newAdjustedRoundIndex;
				if (currentRound.getResult() != Result.TIED)
					newAdjustedRoundIndex = currentRound.getAdjustedRoundIndex() + 1;
				else
					newAdjustedRoundIndex = currentRound.getAdjustedRoundIndex();
				GameRound nextRound = new GameRound(this, newRoundIndex, newAdjustedRoundIndex);
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
			throw new GameConflictException(ConflictType.THROW_BEFORE_START);
		if (state == State.FINISHED)
			throw new GameConflictException(ConflictType.THROW_AFTER_FINISH);
		if (player == null)
			throw new IllegalArgumentException();
		if (throwForPlayer == null)
			throw new GameConflictException(ConflictType.THROW_INVALID);

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
			LOGGER.warn("Specified round '{}' is not current round '{}': {}",
					new Object[] { roundIndex, currentRound.getRoundIndex(), rounds.toString() });
			throw new GameConflictException(ConflictType.THROW_WRONG_ROUND);
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
			throw new GameConflictException(ConflictType.PLAYER_2_FINALIZED);

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
	 * <p>
	 * This method is intended for use in {@link Account} management operations,
	 * such as when {@link Account}s are merged.
	 * </p>
	 * 
	 * @param player1
	 *            the new value to use for {@link #getPlayer1()}
	 */
	public void replacePlayer1(Player player1) {
		if (this.player1 == null)
			throw new IllegalStateException();

		// Can't set a null player.
		if (player1 == null)
			throw new IllegalArgumentException();

		this.player1 = player1;
	}

	/**
	 * <p>
	 * This method may only be called if {@link #getPlayer2()} is not
	 * <code>null</code>. It is intended for use in {@link Account} management
	 * operations, such as when {@link Account}s are merged.
	 * </p>
	 * 
	 * @param player2
	 *            the new value to use for {@link #getPlayer2()}
	 * @throws IllegalStateException
	 *             An {@link IllegalStateException} will be thrown if
	 *             {@link #getPlayer2()} was not already non-<code>null</code>.
	 */
	public void replacePlayer2(Player player2) {
		if (this.player2 == null)
			throw new IllegalStateException();

		// Can't set a null player.
		if (player2 == null)
			throw new IllegalArgumentException();

		this.player2 = player2;
	}

	/**
	 * Will initialize {@link #getRounds()} and then transition
	 * {@link #getState()} to {@link State#STARTED}.
	 */
	private void player2Joined() {
		GameRound firstRound = new GameRound(this, 0, 0);
		this.rounds.add(firstRound);

		this.state = State.WAITING_FOR_FIRST_THROW;
	}
}
