package com.justdavis.karl.rpstourney.webservice.auth;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * Unit tests for {@link AccountService}.
 */
public final class AccountServiceTest {
	/**
	 * Ensures that {@link AccountService#validateAuth()} works as expected.
	 */
	@Test
	public void validateAuth() {
		// Create and persist an Account to test against.
		Account account = new Account();

		// Build the service
		AccountService accountService = new AccountService();
		accountService.setAccountSecurityContext(new AccountSecurityContext(
				account));

		// Call the service.
		Account responseAccount = accountService.validateAuth();

		// Verify the results
		Assert.assertNotNull(responseAccount);
	}
}
