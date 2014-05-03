package com.justdavis.karl.rpstourney.service.api.game;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * Unit tests for {@link Player}.
 */
public final class PlayerTest {
	/**
	 * Tests {@link Player#equals(Object)} and {@link Player#hashCode()}.
	 * 
	 * @throws SecurityException
	 *             (won't happen)
	 * @throws NoSuchFieldException
	 *             (won't happen)
	 * @throws IllegalAccessException
	 *             (won't happen)
	 * @throws IllegalArgumentException
	 *             (won't happen)
	 */
	@Test
	public void equalsAndHashCode() throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		Account accountA = new Account();
		Player playerA = new Player(accountA);

		Assert.assertEquals(playerA, playerA);
		Assert.assertEquals(playerA.hashCode(), playerA.hashCode());

		Account accountB = new Account();
		Player playerB = new Player(accountB);

		Assert.assertNotEquals(playerA, playerB);

		/*
		 * The logic for Player.equals(...) is different for persisted vs.
		 * non-persisted objects, and we want to cover that logic with this
		 * test. To avoid having to involve the DB here (and actually persist
		 * things), we'll cheat and set the fields' values via reflection.
		 */
		Field accountIdField = Account.class.getDeclaredField("id");
		accountIdField.setAccessible(true);
		Field playerIdField = Player.class.getDeclaredField("id");
		playerIdField.setAccessible(true);

		Account accountC = new Account();
		accountIdField.set(accountC, 3);
		Player playerC = new Player(accountC);
		playerIdField.set(playerC, 3);

		Assert.assertEquals(playerC, playerC);
		Assert.assertNotEquals(playerC, playerA);
		Assert.assertNotEquals(playerA, playerC);

		Account accountD = new Account();
		accountIdField.set(accountD, 4);
		Player playerD = new Player(accountD);
		playerIdField.set(playerD, 4);

		Assert.assertNotEquals(playerD, playerC);
	}
}
