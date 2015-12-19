package com.justdavis.karl.rpstourney.service.client.auth;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.AuthTokenCookieHelper;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * A client-side implementation/binding for the {@link IAccountsResource} web
 * service.
 */
public class AccountsClient implements IAccountsResource {
	private final ClientConfig config;
	private final CookieStore cookieStore;

	/**
	 * Constructs a new {@link AccountsClient} instance.
	 * 
	 * @param config
	 *            the {@link ClientConfig} to use
	 * @param cookieStore
	 *            the {@link CookieStore} to use
	 */
	@Inject
	public AccountsClient(ClientConfig config, CookieStore cookieStore) {
		this.config = config;
		this.cookieStore = cookieStore;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#validateAuth()
	 */
	@Override
	public Account validateAuth() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IAccountsResource.SERVICE_PATH)
				.path(IAccountsResource.SERVICE_PATH_VALIDATE).request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		Account account = response.readEntity(Account.class);
		cookieStore.remember(response.getCookies());

		return account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#getAccount()
	 */
	@Override
	public Account getAccount() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IAccountsResource.SERVICE_PATH)
				.path(IAccountsResource.SERVICE_PATH_GET_ACCOUNT).request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		Account account = response.readEntity(Account.class);
		cookieStore.remember(response.getCookies());

		return account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#updateAccount(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public Account updateAccount(Account accountToUpdate) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IAccountsResource.SERVICE_PATH)
				.path(IAccountsResource.SERVICE_PATH_UPDATE_ACCOUNT).request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.post(Entity.xml(accountToUpdate));
		if (response.getStatus() == Status.BAD_REQUEST.getStatusCode())
			throw new BadRequestException(response);
		else if (response.getStatus() == Status.FORBIDDEN.getStatusCode())
			throw new ForbiddenException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		Account account = response.readEntity(Account.class);
		cookieStore.remember(response.getCookies());

		return account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#selectOrCreateAuthToken()
	 */
	@Override
	public AuthToken selectOrCreateAuthToken() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IAccountsResource.SERVICE_PATH)
				.path(IAccountsResource.SERVICE_PATH_AUTH_TOKEN).request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		AuthToken authToken = response.readEntity(AuthToken.class);
		cookieStore.remember(response.getCookies());

		return authToken;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#mergeAccount(long,
	 *      java.util.UUID)
	 */
	@Override
	public void mergeAccount(long targetAccountId, UUID sourceAccountAuthTokenValue) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IAccountsResource.SERVICE_PATH)
				.path(IAccountsResource.SERVICE_PATH_MERGE).request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form formData = new Form().param(IAccountsResource.SERVICE_PARAM_MERGE_TARGET, "" + targetAccountId)
				.param(IAccountsResource.SERVICE_PARAM_MERGE_SOURCE, sourceAccountAuthTokenValue.toString());
		Response response = requestBuilder.post(Entity.form(formData));
		if (response.getStatus() == Status.BAD_REQUEST.getStatusCode())
			throw new BadRequestException(response);
		else if (response.getStatus() == Status.FORBIDDEN.getStatusCode())
			throw new ForbiddenException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		cookieStore.remember(response.getCookies());
	}

	/**
	 * The default {@link IAccountsClientFactory} implementation, which produces
	 * {@link AccountsClient} instances.
	 */
	public static final class DefaultAccountsClientFactory implements IAccountsClientFactory {
		private final ClientConfig config;

		/**
		 * Constructs a new {@link DefaultAccountsClientFactory} instance.
		 * 
		 * @param config
		 *            the {@link ClientConfig} to use
		 */
		public DefaultAccountsClientFactory(ClientConfig config) {
			this.config = config;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.client.auth.IAccountsClientFactory#createAccountsClient(java.lang.String)
		 */
		@Override
		public IAccountsResource createAccountsClient(String authTokenValueForAccount) {
			/*
			 * Create a new (separate) CookieStore for the new client instance
			 * to use. Note that this CookieStore will not be shared with other
			 * clients, and will thus not receive any updates/modifications
			 * applied to them.
			 */
			CookieStore cookieStore = new CookieStore();
			NewCookie authTokenCookie = AuthTokenCookieHelper.createAuthTokenCookie(authTokenValueForAccount,
					config.getServiceRoot());
			cookieStore.remember(authTokenCookie);

			return new AccountsClient(config, cookieStore);
		}
	}
}
