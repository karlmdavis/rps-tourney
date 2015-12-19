package com.justdavis.karl.rpstourney.service.api.game;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

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
	public void equalsAndHashCode()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
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

	/**
	 * Verifies that instances serialize correctly with Jackson.
	 * 
	 * @throws JsonProcessingException
	 *             (indicates a test failure)
	 * @throws SecurityException
	 *             (won't occur)
	 * @throws NoSuchFieldException
	 *             (won't occur)
	 * @throws IllegalAccessException
	 *             (won't occur)
	 * @throws IllegalArgumentException
	 *             (won't occur)
	 */
	@Test
	public void jsonSerialization() throws JsonProcessingException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		ObjectMapper jacksonMapper = new ObjectMapper();
		Field playerIdField = Player.class.getDeclaredField("id");
		playerIdField.setAccessible(true);
		Field accountIdField = Account.class.getDeclaredField("id");
		accountIdField.setAccessible(true);

		// Create the mock instances to test against.
		Player playerA = new Player(new Account());
		playerIdField.setInt(playerA, 1);
		accountIdField.setInt(playerA.getHumanAccount(), 1);
		Player playerB = new Player(BuiltInAi.THREE_SIDED_DIE_V1);
		playerIdField.setInt(playerB, 2);

		JsonNode playerAJson = jacksonMapper.convertValue(playerA, JsonNode.class);
		Assert.assertNotNull(playerAJson);
		Assert.assertEquals(playerA.getId(), playerAJson.get("id").asInt());
		Assert.assertNotNull(playerAJson.get("humanAccount"));
		Assert.assertEquals(playerA.getHumanAccount().getId(), playerAJson.get("humanAccount").get("id").asInt());

		JsonNode playerBJson = jacksonMapper.convertValue(playerB, JsonNode.class);
		Assert.assertNotNull(playerBJson);
		Assert.assertNotNull(playerBJson.get("builtInAi"));
		Assert.assertNotNull(playerBJson.get("builtInAi").get("displayNameKey"));
		Assert.assertEquals(playerB.getBuiltInAi().getDisplayNameKey(),
				playerBJson.get("builtInAi").get("displayNameKey").asText());
	}
}
