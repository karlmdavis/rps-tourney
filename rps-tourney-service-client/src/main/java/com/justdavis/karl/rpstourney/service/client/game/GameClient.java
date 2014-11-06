package com.justdavis.karl.rpstourney.service.client.game;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedJaxbException;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;

/**
 * A client-side implementation/binding for the {@link IGameResource} web
 * service.
 */
public final class GameClient implements IGameResource {
	private final ClientConfig config;
	private final CookieStore cookieStore;

	/**
	 * Constructs a new {@link GameClient} instance.
	 * 
	 * @param config
	 *            the {@link ClientConfig} to use
	 * @param cookieStore
	 *            the {@link CookieStore} to use
	 */
	@Inject
	public GameClient(ClientConfig config, CookieStore cookieStore) {
		this.config = config;
		this.cookieStore = cookieStore;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#createGame()
	 */
	@Override
	public GameView createGame() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameResource.SERVICE_PATH)
				.path(IGameResource.SERVICE_PATH_NEW)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();

		Response response = requestBuilder.post(Entity.form(params));
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameView game = response.readEntity(GameView.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGamesForPlayer()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<GameView> getGamesForPlayer() {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameResource.SERVICE_PATH)
				.path(IGameResource.SERVICE_PATH_GAMES_FOR_PLAYER)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		/*
		 * FIXME This is a workaround for
		 * https://issues.apache.org/jira/browse/CXF-5980.
		 */
		/*
		 * List<Game> games = response .readEntity(new GenericType<List<Game>>(
		 * Game.class) { });
		 */
		List<GameView> games = null;
		InputStream responseEntityStream = (InputStream) response.getEntity();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GameView.class,
					JaxbListWrapper.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Source responseEntitySource = new StreamSource(responseEntityStream);
			@SuppressWarnings("rawtypes")
			JAXBElement<JaxbListWrapper> responseWrapperEntity = unmarshaller
					.unmarshal(responseEntitySource, JaxbListWrapper.class);
			games = responseWrapperEntity.getValue().getItems();
		} catch (JAXBException e) {
			throw new UncheckedJaxbException(e);
		}

		cookieStore.remember(response.getCookies());

		return games;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#getGame(java.lang.String)
	 */
	@Override
	public GameView getGame(String gameId) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameResource.SERVICE_PATH).path(gameId)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Response response = requestBuilder.get();
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameId, response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameView game = response.readEntity(GameView.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#setMaxRounds(java.lang.String,
	 *      int, int)
	 */
	@Override
	public GameView setMaxRounds(String gameId, int oldMaxRoundsValue,
			int newMaxRoundsValue) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameResource.SERVICE_PATH).path(gameId)
				.path(IGameResource.SERVICE_PATH_MAX_ROUNDS)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();
		params.param("oldMaxRoundsValue", "" + oldMaxRoundsValue);
		params.param("newMaxRoundsValue", "" + newMaxRoundsValue);

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameId, response);
		else if (response.getStatus() == Status.CONFLICT.getStatusCode())
			throw new GameConflictException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameView game = response.readEntity(GameView.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#joinGame(java.lang.String)
	 */
	@Override
	public GameView joinGame(String gameId) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameResource.SERVICE_PATH).path(gameId)
				.path(IGameResource.SERVICE_PATH_JOIN)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameId, response);
		else if (response.getStatus() == Status.CONFLICT.getStatusCode())
			throw new GameConflictException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameView game = response.readEntity(GameView.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#prepareRound(String)
	 */
	@Override
	public GameView prepareRound(String gameId) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameResource.SERVICE_PATH).path(gameId)
				.path(IGameResource.SERVICE_PATH_PREPARE)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameId, response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameView game = response.readEntity(GameView.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.game.IGameResource#submitThrow(java.lang.String,
	 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
	 */
	@Override
	public GameView submitThrow(String gameId, int roundIndex, Throw throwToPlay) {
		Client client = ClientBuilder.newClient();
		Builder requestBuilder = client.target(config.getServiceRoot())
				.path(IGameResource.SERVICE_PATH).path(gameId)
				.path(IGameResource.SERVICE_PATH_THROW)
				.request(MediaType.TEXT_XML_TYPE);
		cookieStore.applyCookies(requestBuilder);

		Form params = new Form();
		params.param("roundIndex", "" + roundIndex);
		params.param("throwToPlay", throwToPlay.name());

		Response response = requestBuilder.post(Entity.form(params));
		if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
			throw new NotFoundException("Game not found: " + gameId, response);
		else if (response.getStatus() == Status.CONFLICT.getStatusCode())
			throw new GameConflictException(response);
		else if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL)
			throw new HttpClientException(response.getStatusInfo());

		GameView game = response.readEntity(GameView.class);
		cookieStore.remember(response.getCookies());

		return game;
	}

	/**
	 * <p>
	 * A JAXB wrapper for generic {@link List} responses (from JAX-RS services).
	 * </p>
	 * <p>
	 * Only needed as a workaround for <a
	 * href="https://issues.apache.org/jira/browse/CXF-5980">CXF-5980</a>. This
	 * solution is courtesy of: <a
	 * href="http://stackoverflow.com/a/13273022/1851299">Generic JAXB
	 * Wrappers</a>.
	 * </p>
	 * 
	 * @param <T>
	 *            the type of the elements that will be stored inside the
	 *            wrapper
	 */
	private static final class JaxbListWrapper<T> {
		private List<T> items = new ArrayList<>();

		/**
		 * @return the items that were placed into this wrapper object, or an
		 *         empty {@link List}
		 */
		@XmlAnyElement(lax = true)
		public List<T> getItems() {
			return items;
		}
	}
}
