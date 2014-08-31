package com.justdavis.karl.rpstourney.service.client.game;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * A client-side implementation/binding for the {@link IGameSessionResource} web
 * service.
 */
public final class GameSessionClient implements IGameSessionResource {
	private final ClientConfig config;
	private final CookieStore cookieStore;

	/**
	 * Constructs a new {@link GameSessionClient} instance.
	 * 
	 * @param config
	 *            the {@link ClientConfig} to use
	 * @param cookieStore
	 *            the {@link CookieStore} to use
	 */
	@Inject
	public GameSessionClient(ClientConfig config, CookieStore cookieStore) {
		this.config = config;
		this.cookieStore = cookieStore;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#createGame()
	 */
	@Override
	public GameSession createGame() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameSessionResource.SERVICE_PATH)
				.path(IGameSessionResource.SERVICE_PATH_NEW)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();

		Response response = requestBuilder.post(Entity.form(params));
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameSession game = response.readEntity(GameSession.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#getGame(java.lang.String)
	 */
	@Override
	public GameSession getGame(String gameSessionId) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameSessionResource.SERVICE_PATH).path(gameSessionId)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameSessionId,
					response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameSession game = response.readEntity(GameSession.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public GameSession setMaxRounds(String gameSessionId,
			int oldMaxRoundsValue, int newMaxRoundsValue) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameSessionResource.SERVICE_PATH).path(gameSessionId)
				.path(IGameSessionResource.SERVICE_PATH_MAX_ROUNDS)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();
		params.param("oldMaxRoundsValue", "" + oldMaxRoundsValue);
		params.param("newMaxRoundsValue", "" + newMaxRoundsValue);

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameSessionId,
					response);
		else if (response.getStatus() == Status.CONFLICT.getStatusCode())
			throw new GameConflictException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameSession game = response.readEntity(GameSession.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#joinGame(java.lang.String)
	 */
	@Override
	public GameSession joinGame(String gameSessionId) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameSessionResource.SERVICE_PATH).path(gameSessionId)
				.path(IGameSessionResource.SERVICE_PATH_JOIN)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameSessionId,
					response);
		else if (response.getStatus() == Status.CONFLICT.getStatusCode())
			throw new GameConflictException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameSession game = response.readEntity(GameSession.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#prepareRound(String)
	 */
	@Override
	public GameSession prepareRound(String gameSessionId) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameSessionResource.SERVICE_PATH).path(gameSessionId)
				.path(IGameSessionResource.SERVICE_PATH_PREPARE)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameSessionId,
					response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameSession game = response.readEntity(GameSession.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@Override
	public GameSession submitThrow(String gameSessionId, int roundIndex,
			Throw throwToPlay) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameSessionResource.SERVICE_PATH).path(gameSessionId)
				.path(IGameSessionResource.SERVICE_PATH_THROW)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();
		params.param("roundIndex", "" + roundIndex);
		params.param("throwToPlay", throwToPlay.name());

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameSessionId,
					response);
		else if (response.getStatus() == Status.CONFLICT.getStatusCode())
			throw new GameConflictException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameSession game = response.readEntity(GameSession.class);
		cookieStore.remember(response.getCookies());

		return game;
	}
}
