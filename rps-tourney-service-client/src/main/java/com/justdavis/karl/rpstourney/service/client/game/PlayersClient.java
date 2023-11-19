package com.justdavis.karl.rpstourney.service.client.game;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.justdavis.karl.rpstourney.service.api.game.IPlayersResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * A client-side implementation/binding for the {@link IPlayersResource} web service.
 */
public final class PlayersClient implements IPlayersResource {
	private final ClientConfig config;
	private final CookieStore cookieStore;

	/**
	 * Constructs a new {@link PlayersClient} instance.
	 *
	 * @param config
	 *            the {@link ClientConfig} to use
	 * @param cookieStore
	 *            the {@link CookieStore} to use
	 */
	@Inject
	public PlayersClient(ClientConfig config, CookieStore cookieStore) {
		this.config = config;
		this.cookieStore = cookieStore;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#findOrCreatePlayer()
	 */
	@Override
	public Player findOrCreatePlayer() {
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(config.getServiceRoot()).path(IPlayersResource.SERVICE_PATH)
				.path(IPlayersResource.SERVICE_PATH_PLAYER);
		Builder requestBuilder = webTarget.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		Player player = response.readEntity(Player.class);
		cookieStore.remember(response.getCookies());

		return player;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IPlayersResource#getPlayersForBuiltInAis(java.util.List)
	 */
	@Override
	public Set<Player> getPlayersForBuiltInAis(List<BuiltInAi> ais) {
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(config.getServiceRoot()).path(IPlayersResource.SERVICE_PATH)
				.path(IPlayersResource.SERVICE_PATH_BUILT_IN_AIS);
		for (BuiltInAi ai : ais)
			webTarget = webTarget.queryParam("ais", ai);
		Builder requestBuilder = webTarget.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GenericType<Set<Player>> playersSetType = new GenericType<Set<Player>>() {
		};
		Set<Player> players = response.readEntity(playersSetType);

		cookieStore.remember(response.getCookies());

		return players;
	}
}
