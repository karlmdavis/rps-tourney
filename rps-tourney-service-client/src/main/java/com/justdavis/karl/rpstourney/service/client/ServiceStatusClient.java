package com.justdavis.karl.rpstourney.service.client;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.justdavis.karl.rpstourney.service.api.IServiceStatusResource;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * A client-side implementation/binding for the {@link IServiceStatusResource} web service.
 */
public class ServiceStatusClient implements IServiceStatusResource {
	private final ClientConfig config;

	/**
	 * Constructs a new {@link ServiceStatusClient} instance.
	 *
	 * @param config
	 *            the {@link ClientConfig} to use
	 */
	@Inject
	public ServiceStatusClient(ClientConfig config) {
		this.config = config;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.IServiceStatusResource#ping()
	 */
	@Override
	public String ping() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IServiceStatusResource.SERVICE_PATH)
				.path(IServiceStatusResource.SERVICE_PATH_PING).request(MediaType.TEXT_PLAIN);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		String pong = response.readEntity(String.class);
		return pong;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.IServiceStatusResource#echo(java.lang.String)
	 */
	@Override
	public String echo(String text) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IServiceStatusResource.SERVICE_PATH)
				.path(IServiceStatusResource.SERVICE_PATH_ECHO).request();

		Form params = new Form();
		params.param("text", text);

		Response response = requestBuilder.post(Entity.form(params));
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		String echo = response.readEntity(String.class);
		return echo;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.IServiceStatusResource#getVersion()
	 */
	@Override
	public String getVersion() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot()).path(IServiceStatusResource.SERVICE_PATH)
				.path(IServiceStatusResource.SERVICE_PATH_VERSION).request(MediaType.TEXT_PLAIN);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		String version = response.readEntity(String.class);
		return version;
	}
}
