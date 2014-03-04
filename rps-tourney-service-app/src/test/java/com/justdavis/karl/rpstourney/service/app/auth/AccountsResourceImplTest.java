package com.justdavis.karl.rpstourney.service.app.auth;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext;
import com.justdavis.karl.rpstourney.service.app.auth.AccountsResourceImpl;

/**
 * Unit tests for {@link AccountsResourceImpl}.
 */
public final class AccountsResourceImplTest {
	/**
	 * Ensures that {@link AccountsResourceImpl#validateAuth()} works as expected.
	 */
	@Test
	public void validateAuth() {
		// Create and persist an Account to test against.
		Account account = new Account();

		// Build the service
		AccountsResourceImpl accountService = new AccountsResourceImpl();
		accountService.setAccountSecurityContext(new AccountSecurityContext(
				account));

		// Call the service.
		Account responseAccount = accountService.validateAuth();

		// Verify the results
		Assert.assertNotNull(responseAccount);
	}
}
