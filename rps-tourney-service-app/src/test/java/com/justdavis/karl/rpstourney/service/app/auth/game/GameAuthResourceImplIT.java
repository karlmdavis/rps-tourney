package com.justdavis.karl.rpstourney.service.app.auth.game;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.app.SpringITConfigWithJetty;
import com.justdavis.karl.rpstourney.service.app.WebClientHelper;
import com.justdavis.karl.rpstourney.service.app.auth.game.GameAuthResourceImpl;
import com.justdavis.karl.rpstourney.service.app.auth.game.IGameLoginIndentitiesDao;

/**
 * Integration tests for {@link GameAuthResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringITConfigWithJetty.class })
@WebAppConfiguration
public final class GameAuthResourceImplIT {
	@Inject
	private EmbeddedServer server;

	@Inject
	private IGameLoginIndentitiesDao loginsDao;

	/**
	 * Ensures that {@link GameAuthResourceImpl} creates new
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
				.path(IGameAuthResource.SERVICE_PATH)
				.path(IGameAuthResource.SERVICE_PATH_CREATE_LOGIN)
				.form(new Form().param("emailAddress", "foo@example.com")
						.param("password", "secret"));

		// Verify the create results.
		Assert.assertNotNull(createResponse);
		Assert.assertEquals(Status.OK.getStatusCode(),
				createResponse.getStatus());
		Account account = (Account) createResponse.readEntity(Account.class);
		Assert.assertNotNull(account);
		Assert.assertEquals(1, loginsDao.getLogins().size());
		Assert.assertEquals(new InternetAddress("foo@example.com"), loginsDao
				.getLogins().get(0).getEmailAddress());

		// Validate the login.
		Response validateResponse = client.replacePath(null)
				.path(IAccountsResource.SERVICE_PATH)
				.path(IAccountsResource.SERVICE_PATH_VALIDATE).get();

		// Verify the validate results.
		Assert.assertEquals(Status.OK.getStatusCode(),
				validateResponse.getStatus());
	}
}
