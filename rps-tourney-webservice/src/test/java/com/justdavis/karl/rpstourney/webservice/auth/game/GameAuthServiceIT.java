package com.justdavis.karl.rpstourney.webservice.auth.game;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.form.Form;
import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.EmbeddedServerResource;
import com.justdavis.karl.rpstourney.webservice.WebClientHelper;
import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.AccountService;
import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestAuthService;

/**
 * Integration tests for {@link GameAuthService}.
 */
public final class GameAuthServiceIT {
	@ClassRule
	public static EmbeddedServerResource server = new EmbeddedServerResource();

	/**
	 * FIXME Remove or rework once actual persistence is in place.
	 */
	@After
	public void removeAccounts() {
		AccountService.existingAccounts.clear();
		GuestAuthService.existingLogins.clear();
		GameAuthService.existingLogins.clear();
	}

	/**
	 * Ensures that {@link GameAuthService} creates new
	 * {@link GameLoginIdentity}s as expected.
	 * 
	 * @throws AddressException
	 *             (should not occur if test is successful)
	 */
	@Test
	public void createAndValidateLogin() throws AddressException {
		WebClient client = WebClient.create(server.getServerBaseAddress());
		WebClientHelper.enableSessionMaintenance(client, true);

		// Create the login and account.
		Response createResponse = client
				.path(GameAuthService.SERVICE_PATH)
				.path(GameAuthService.SERVICE_PATH_CREATE_LOGIN)
				.form(new Form().set("emailAddress",
						new InternetAddress("foo@example.com")).set("password",
						"secret"));

		// Verify the create results.
		Assert.assertNotNull(createResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				createResponse.getStatus());
		Account account = (Account) createResponse.readEntity(Account.class);
		Assert.assertNotNull(account);
		// TODO ensure the login was saved to the DB (once we have a DB)

		// Validate the login.
		Response validateResponse = client.replacePath(null)
				.path(AccountService.SERVICE_PATH)
				.path(AccountService.SERVICE_PATH_VALIDATE).get();

		// Verify the validate results.
		Assert.assertEquals(Status.OK.getStatusCode(),
				validateResponse.getStatus());
	}
}
