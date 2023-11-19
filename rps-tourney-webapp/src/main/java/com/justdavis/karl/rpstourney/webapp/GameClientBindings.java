package com.justdavis.karl.rpstourney.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.rpstourney.service.api.IServiceStatusResource;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.IPlayersResource;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.ServiceStatusClient;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient.DefaultAccountsClientFactory;
import com.justdavis.karl.rpstourney.service.client.auth.IAccountsClientFactory;
import com.justdavis.karl.rpstourney.service.client.auth.game.GameAuthClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;
import com.justdavis.karl.rpstourney.service.client.game.PlayersClient;
import com.justdavis.karl.rpstourney.webapp.config.AppConfig;

/**
 * The Spring {@link Configuration} for the game web service client bindings.
 */
@Configuration
public class GameClientBindings {
	/**
	 * @param appConfig
	 *            the injected {@link AppConfig} for the application
	 * @return the {@link ClientConfig} for the application to use
	 */
	@Bean
	public ClientConfig serviceClientConfig(AppConfig appConfig) {
		ClientConfig serviceClientConfig = new ClientConfig(appConfig.getClientServiceRoot());
		return serviceClientConfig;
	}

	/**
	 * @return the {@link CookieStore} for the application to use, which will be session-scoped via a proxy to ensure
	 *         that each client gets their own instance
	 */
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public CookieStore cookieStore() {
		/*
		 * The CookieStore will be used to store the authentication tokens/cookies used by the web service clients (and
		 * possibly other stuff, later). Keeping the CookieStore, and those auth cookies, around in the sessions
		 * prevents us from having to recreate it on each request. (As an alternative, if this turns out to be a bad
		 * idea, the auth cookies could be rebuilt from the Authentication instances or Account principals in them that
		 * Spring Security is already saving in the session.)
		 */

		CookieStore cookieStore = new CookieStore();
		return cookieStore;
	}

	/**
	 * @param config
	 *            the {@link ClientConfig} being used
	 * @return the {@link IServiceStatusResource} client for the application to use
	 */
	@Bean
	public IServiceStatusResource statusClient(ClientConfig config) {
		IServiceStatusResource gameClient = new ServiceStatusClient(config);
		return gameClient;
	}

	/**
	 * @param config
	 *            the {@link ClientConfig} being used
	 * @param cookieStore
	 *            the {@link CookieStore} being used (likely session scoped and proxied)
	 * @return the {@link IAccountsResource} client for the application to use
	 */
	@Bean
	public IAccountsResource accountsClient(ClientConfig config, CookieStore cookieStore) {
		IAccountsResource accountsClient = new AccountsClient(config, cookieStore);
		return accountsClient;
	}

	/**
	 * @param config
	 *            the {@link ClientConfig} being used
	 * @return the {@link IAccountsClientFactory} client for the application to use
	 */
	@Bean
	public IAccountsClientFactory accountsClientFactory(ClientConfig config) {
		IAccountsClientFactory accountsClientFactory = new DefaultAccountsClientFactory(config);
		return accountsClientFactory;
	}

	/**
	 * @param config
	 *            the {@link ClientConfig} being used
	 * @param cookieStore
	 *            the {@link CookieStore} being used (likely session scoped and proxied)
	 * @return the {@link IGuestAuthResource} client for the application to use
	 */
	@Bean
	public IGuestAuthResource guestAuthClient(ClientConfig config, CookieStore cookieStore) {
		IGuestAuthResource gameAuthClient = new GuestAuthClient(config, cookieStore);
		return gameAuthClient;
	}

	/**
	 * @param config
	 *            the {@link ClientConfig} being used
	 * @param cookieStore
	 *            the {@link CookieStore} being used (likely session scoped and proxied)
	 * @return the {@link IGameAuthResource} client for the application to use
	 */
	@Bean
	public IGameAuthResource gameAuthClient(ClientConfig config, CookieStore cookieStore) {
		IGameAuthResource gameAuthClient = new GameAuthClient(config, cookieStore);
		return gameAuthClient;
	}

	/**
	 * @param config
	 *            the {@link ClientConfig} being used
	 * @param cookieStore
	 *            the {@link CookieStore} being used (likely session scoped and proxied)
	 * @return the {@link IGameResource} client for the application to use
	 */
	@Bean
	public IGameResource gameClient(ClientConfig config, CookieStore cookieStore) {
		IGameResource gameClient = new GameClient(config, cookieStore);
		return gameClient;
	}

	/**
	 * @param config
	 *            the {@link ClientConfig} being used
	 * @param cookieStore
	 *            the {@link CookieStore} being used (likely session scoped and proxied)
	 * @return the {@link IPlayersResource} client for the application to use
	 */
	@Bean
	public IPlayersResource playersClient(ClientConfig config, CookieStore cookieStore) {
		IPlayersResource playersClient = new PlayersClient(config, cookieStore);
		return playersClient;
	}
}
