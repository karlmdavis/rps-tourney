package com.justdavis.karl.rpstourney.service.app.auth;

import java.util.UUID;

import javax.mail.internet.AddressException;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;

/**
 * Unit tests for {@link AccountsResourceImpl}.
 */
public final class AccountsResourceImplTest {
	/**
	 * Ensures that {@link AccountsResourceImpl#validateAuth()} works as
	 * expected.
	 */
	@Test
	public void validateAuth() {
		// Create an Account to test against.
		Account account = new Account();

		// Build the service
		AccountsResourceImpl accountService = new AccountsResourceImpl();
		accountService.setAccountSecurityContext(new AccountSecurityContext(
				account));
		MockAccountsDao accountsDao = new MockAccountsDao();
		accountService.setAccountsDao(accountsDao);

		// Call the service.
		Account responseAccount = accountService.validateAuth();

		// Verify the results
		Assert.assertNotNull(responseAccount);
	}

	/**
	 * Ensures that {@link AccountsResourceImpl#selectOrCreateAuthToken()} works
	 * as expected.
	 */
	@Test
	public void selectOrCreateAuthToken() throws AddressException {
		// Create and "persist" the entities to test against.
		MockAccountsDao accountsDao = new MockAccountsDao();
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID());
		account.getAuthTokens().add(authToken);
		accountsDao.save(account);

		// Build the service
		AccountsResourceImpl accountService = new AccountsResourceImpl();
		accountService.setAccountSecurityContext(new AccountSecurityContext(
				account));
		accountService.setAccountsDao(accountsDao);

		// Call the service.
		AuthToken responseToken = accountService.selectOrCreateAuthToken();

		// Verify the results
		Assert.assertNotNull(responseToken);
		Assert.assertSame(authToken, responseToken);
	}
}
