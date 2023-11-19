package com.justdavis.karl.rpstourney.service.api.auth.game;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * Unit tests for {@link GameLoginIdentity}.
 */
public final class GameLoginIdentityTest {
	/**
	 * Tests normal usage of {@link GameLoginIdentity}.
	 *
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void simpleUsage() throws AddressException {
		/*
		 * This is a silly & pointless test. It's really just a placeholder for now. Once persistence has been added,
		 * it'll be worth testing that.
		 */

		Account account = new Account();
		InternetAddress emailAddress = new InternetAddress("foo@example.com");
		String passwordHash = "foobar";
		GameLoginIdentity login = new GameLoginIdentity(account, emailAddress, passwordHash);

		Assert.assertSame(account, login.getAccount());
	}
}
