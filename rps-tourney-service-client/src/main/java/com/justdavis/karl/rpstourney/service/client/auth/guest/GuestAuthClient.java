package com.justdavis.karl.rpstourney.service.client.auth.guest;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * A client-side implementation/binding for the {@link IGuestAuthResource} web
 * service.
 */
public final class GuestAuthClient implements IGuestAuthResource {
	private final ClientConfig config;
	private final CookieStore cookieStore;

	/**
	 * Constructs a new {@link GuestAuthClient} instance.
	 * 
	 * @param config
	 *            the {@link ClientConfig} to use
	 * @param cookieStore
	 *            the {@link CookieStore} to use
	 */
	@Inject
	public GuestAuthClient(ClientConfig config, CookieStore cookieStore) {
		this.config = config;
		this.cookieStore = cookieStore;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource#loginAsGuest()
	 */
	@Override
	public Account loginAsGuest() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IGuestAuthResource.SERVICE_PATH).request();
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();

		Response response = requestBuilder.post(Entity.form(params));
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		Account account = response.readEntity(Account.class);
		cookieStore.remember(response.getCookies());

		return account;
	}
}
