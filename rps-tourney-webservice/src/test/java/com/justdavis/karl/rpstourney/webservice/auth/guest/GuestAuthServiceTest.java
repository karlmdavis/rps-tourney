package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.MockUriInfo;

/**
 * Unit tests for {@link GuestAuthService}.
 */
public final class GuestAuthServiceTest {
	/**
	 * Ensures that {@link GuestAuthService} creates new
	 * {@link GuestLoginIdentity}s as expected.
	 */
	@Test
	public void createLogin() {
		// Create the service.
		GuestAuthService authService = new GuestAuthService();

		// Create the mock params to pass to the service .
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
		Response loginResponse = authService.loginAsGuest(uriInfo, null);

		// Verify the results
		Assert.assertNotNull(loginResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				loginResponse.getStatus());
		GuestLoginIdentity login = (GuestLoginIdentity) loginResponse
				.getEntity();
		Assert.assertNotNull(login);
		Assert.assertNotNull(login.getAuthToken());
		Assert.assertNotNull(login.getAccount());
		// TODO verify Account (once that's been fleshed out)
		// TODO ensure the login was saved to the DB (once we have a DB)
	}

	/**
	 * Ensures that {@link GuestAuthService} handles existing logins
	 * {@link GuestLoginIdentity}s as expected.
	 */
	@Test
	public void existingLogin() {
		// Create the service.
		GuestAuthService authService = new GuestAuthService();

		// Create the mock params to pass to the service .
		UriInfo uriInfo = new MockUriInfo() {
			/**
			 * @see com.justdavis.karl.rpstourney.webservice.MockUriInfo#getBaseUri()
			 */
			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost");
			}
		};

		/*
		 * Call the service twice (passing the first response back to the second
		 * call).
		 */
		GuestLoginIdentity login = (GuestLoginIdentity) authService
				.loginAsGuest(uriInfo, null).getEntity();
		Response secondLoginResponse = authService.loginAsGuest(uriInfo,
				login.getAuthToken());

		// Verify the results
		GuestLoginIdentity secondLoginEntity = (GuestLoginIdentity) secondLoginResponse
				.getEntity();
		Assert.assertEquals(login.getAuthToken(),
				secondLoginEntity.getAuthToken());
		// TODO Once we have an Account, verify it's the same in both responses.
	}
}
