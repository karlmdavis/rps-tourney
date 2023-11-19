package com.justdavis.karl.rpstourney.service.client.auth.game;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * A client-side implementation/binding for the {@link IGameAuthResource} web service.
 */
public final class GameAuthClient implements IGameAuthResource {
	private final ClientConfig config;
	private final CookieStore cookieStore;

	/**
	 * Constructs a new {@link GameAuthClient} instance.
	 *
	 * @param config
	 *            the {@link ClientConfig} to use
	 * @param cookieStore
	 *            the {@link CookieStore} to use
	 */
	@Inject
	public GameAuthClient(ClientConfig config, CookieStore cookieStore) {
		this.config = config;
		this.cookieStore = cookieStore;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource#loginWithGameAccount(javax.mail.internet.InternetAddress,
	 *      java.lang.String)
	 */
	@Override
	public Account loginWithGameAccount(InternetAddress emailAddress, String password) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IGameAuthResource.SERVICE_PATH)
				.path(IGameAuthResource.SERVICE_PATH_LOGIN).request();
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();
		params.param("emailAddress", emailAddress.toString());
		params.param("password", password);

		Response response = requestBuilder.post(Entity.form(params));
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		Account account = response.readEntity(Account.class);
		cookieStore.remember(response.getCookies());

		return account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource#createGameLogin(javax.mail.internet.InternetAddress,
	 *      java.lang.String)
	 */
	@Override
	public Account createGameLogin(InternetAddress emailAddress, String password) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IGameAuthResource.SERVICE_PATH)
				.path(IGameAuthResource.SERVICE_PATH_CREATE_LOGIN).request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();
		params.param("emailAddress", emailAddress.toString());
		params.param("password", password);

		Response response = requestBuilder.post(Entity.form(params));
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		Account account = response.readEntity(Account.class);
		cookieStore.remember(response.getCookies());

		return account;
	}
}
