package com.justdavis.karl.rpstourney.webservice.auth;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.MockUriInfo;

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
	 * Ensures that {@link AccountService#validateAuth(UriInfo, UUID)} works as
	 * expected.
	 */
	@Test
	public void validateAuth_invalidToken() {
		// Create the service.
		AccountService accountService = new AccountService();

		// Create the mock params to pass to the service.
		UriInfo uriInfo = new MockUriInfo() {
			/**
			 * @see com.justdavis.karl.rpstourney.webservice.MockUriInfo#getBaseUri()
			 */
			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost");
			}
		};

		// Call the service.
		Response response = accountService.validateAuth(uriInfo, null);

		// Verify the results
		Assert.assertNotNull(response);
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				response.getStatus());
	}

	/**
	 * Ensures that {@link AccountService#validateAuth(UriInfo, UUID)} works as
	 * expected.
	 */
	@Test
	public void validateAuth_validToken() {
		// Create the service.
		AccountService accountService = new AccountService();

		// Create and persist an Account to test against.
		Account account = new Account(UUID.randomUUID());
		AccountService.existingAccounts.add(account);

		// Create the mock params to pass to the service.
		UriInfo uriInfo = new MockUriInfo() {
			/**
			 * @see com.justdavis.karl.rpstourney.webservice.MockUriInfo#getBaseUri()
			 */
			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost");
			}
		};

		// Call the service.
		Response response = accountService.validateAuth(uriInfo,
				account.getAuthToken());

		// Verify the results
		Assert.assertNotNull(response);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Account resultingAccount = (Account) response.getEntity();
		Assert.assertNotNull(resultingAccount);
	}
}
