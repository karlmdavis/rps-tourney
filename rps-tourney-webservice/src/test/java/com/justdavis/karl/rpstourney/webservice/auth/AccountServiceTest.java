package com.justdavis.karl.rpstourney.webservice.auth;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AccountService}.
 */
public final class AccountServiceTest {
	/**
	 * FIXME Remove or rework once actual persistence is in place.
	 */
	@After
	public void removeAccounts() {
		AccountService.existingAccounts.clear();
	}

	/**
	 * Ensures that {@link AccountService#validateAuth()} works as expected.
	 */
	@Test
	public void validateAuth() {
		// Create and persist an Account to test against.
		Account account = new Account(UUID.randomUUID());
		AccountService.existingAccounts.add(account);

		// Call the service.
		Response response = new AccountService(new AccountSecurityContext(
				account)).validateAuth();

		// Verify the results
		Assert.assertNotNull(response);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Account resultingAccount = (Account) response.getEntity();
		Assert.assertNotNull(resultingAccount);
	}
}
