package com.justdavis.karl.rpstourney.service.api.auth.guest;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;

/**
 * Unit tests for {@link GuestLoginIdentity}.
 */
public final class GuestLoginIdentityTest {
	/**
	 * Tests normal usage of {@link GuestLoginIdentity}.
	 */
	@Test
	public void simpleUsage() {
		/*
		 * This is a silly & pointless test. It's really just a placeholder for
		 * now. Once persistence has been added, it'll be worth testing that.
		 */

		Account account = new Account();
		GuestLoginIdentity login = new GuestLoginIdentity(account);

		Assert.assertSame(account, login.getAccount());
	}
}
