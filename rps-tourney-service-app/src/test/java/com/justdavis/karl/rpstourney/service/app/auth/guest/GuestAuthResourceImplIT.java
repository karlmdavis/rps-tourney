package com.justdavis.karl.rpstourney.service.app.auth.guest;

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

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;
import com.justdavis.karl.rpstourney.service.app.SpringITConfigWithJetty;
import com.justdavis.karl.rpstourney.service.app.auth.guest.GuestAuthResourceImpl;
import com.justdavis.karl.rpstourney.service.app.auth.guest.IGuestLoginIndentitiesDao;

/**
 * Integration tests for {@link GuestAuthResourceImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringITConfigWithJetty.class })
@WebAppConfiguration
public final class GuestAuthResourceImplIT {
	@Inject
	private EmbeddedServer server;

	@Inject
	private IGuestLoginIndentitiesDao loginsDao;

	/**
	 * Ensures that {@link GuestAuthResourceImpl} creates new
	 * {@link GuestLoginIdentity}s as expected.
	 */
	@Test
	public void createLogin() {
		WebClient client = WebClient.create(server.getServerBaseAddress());

		client.accept(MediaType.TEXT_XML);
		client.path(IGuestAuthResource.SERVICE_PATH);
		client.post(null);
		Response response = client.getResponse();

		// Verify the results
		Assert.assertNotNull(response);
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Account account = (Account) response.readEntity(Account.class);
		Assert.assertNotNull(account);
		Assert.assertEquals(1, loginsDao.getLogins().size());
	}
}
