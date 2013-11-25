package com.justdavis.karl.rpstourney.webservice.auth.guest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.EmbeddedServerResource;
import com.justdavis.karl.rpstourney.webservice.auth.Account;

/**
 * Integration tests for {@link GuestAuthService}.
 */
public final class GuestAuthServiceIT {
	@ClassRule
	public static EmbeddedServerResource server = new EmbeddedServerResource();

	/**
	 * Ensures that {@link GuestAuthService} creates new
	 * {@link GuestLoginIdentity}s as expected.
	 */
	@Test
	public void createLogin() {
		WebClient client = WebClient.create(server.getServerBaseAddress());

		client.accept(MediaType.TEXT_XML);
		client.path(GuestAuthService.SERVICE_PATH);
		client.post(null);
		Response response = client.getResponse();

		// Verify the results
		Assert.assertNotNull(response);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Account account = (Account) response.readEntity(Account.class);
		Assert.assertNotNull(account);
		// TODO ensure the login was saved to the DB (once we have a DB)
	}
}
