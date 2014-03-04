package com.justdavis.karl.rpstourney.webservice.auth;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;
import com.justdavis.karl.rpstourney.webservice.EmbeddedServer;
import com.justdavis.karl.rpstourney.webservice.SpringITConfigWithJetty;
import com.justdavis.karl.rpstourney.webservice.WebClientHelper;
import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestAuthResourceImpl;

/**
 * Integration tests for {@link AccountsResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringITConfigWithJetty.class })
@WebAppConfiguration
public final class AccountsResourceImplIT {
	@Inject
	private EmbeddedServer server;

	/**
	 * Ensures that {@link AccountsResourceImpl#validateAuth()} returns
	 * {@link Status#UNAUTHORIZED} as expected when called without
	 * authentication.
	 */
	@Test
	public void validateGuestLoginDenied() {
		/*
		 * Just a note: the AccountsResourceImpl will never even be run if everything
		 * is working correctly. Instead, the AuthorizationFilter will handle
		 * this.
		 */

		WebClient client = WebClient.create(server.getServerBaseAddress());

		// Validate the login.
		Response validateResponse = client
				.replacePath(null)
				.accept(MediaType.TEXT_XML)
				.path(IAccountsResource.SERVICE_PATH
						+ IAccountsResource.SERVICE_PATH_VALIDATE).get();

		// Verify the results
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				validateResponse.getStatus());
	}

	/**
	 * Ensures that {@link AccountsResourceImpl#validateAuth()} works as expected when
	 * used with an {@link Account} created via
	 * {@link GuestAuthResourceImpl#loginAsGuest()}.
	 */
	@Test
	public void createAndValidateGuestLogin() {
		WebClient client = WebClient.create(server.getServerBaseAddress());
		WebClientHelper.enableSessionMaintenance(client, true);

		// Login as guest.
		Response loginResponse = client.accept(MediaType.TEXT_XML)
				.path(IGuestAuthResource.SERVICE_PATH).post(null);
		Assert.assertEquals(Status.OK.getStatusCode(),
				loginResponse.getStatus());

		// Validate the login.
		Response validateResponse = client
				.replacePath(null)
				.accept(MediaType.TEXT_XML)
				.path(IAccountsResource.SERVICE_PATH
						+ IAccountsResource.SERVICE_PATH_VALIDATE).get();
		Assert.assertEquals(Status.OK.getStatusCode(),
				validateResponse.getStatus());

		// Verify the results
		Account account = (Account) validateResponse.readEntity(Account.class);
		Assert.assertNotNull(account);
	}
}
