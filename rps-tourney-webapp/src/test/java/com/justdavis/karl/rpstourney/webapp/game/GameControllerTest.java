package com.justdavis.karl.rpstourney.webapp.game;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.webapp.security.IGuestLoginManager;

/**
 * Unit tests for {@link GameController}.
 */
public final class GameControllerTest {
	/**
	 * Tests
	 * {@link GameController#createNewGame(java.security.Principal, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * .
	 * 
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void createNewGame() throws Exception {
		// Build the mocks that will be needed by the controller.
		MessageSource messageSource = new ResourceBundleMessageSource();
		GameSession game = new GameSession(new Player(new Account()));
		IGameSessionResource gameClient = new MockGameClient(game);
		IGuestLoginManager guestLoginManager = new MockGuestLoginManager();

		// Build the controller and prepare it for mock testing.
		GameController gameController = new GameController(messageSource,
				gameClient, guestLoginManager);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(gameController)
				.build();

		// Run the mock tests against the controller.
		mockMvc.perform(MockMvcRequestBuilders.get("/game/"))
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(
						MockMvcResultMatchers.redirectedUrl("/game/"
								+ game.getId()));
	}

	/**
	 * Tests
	 * {@link GameController#getGameSession(String, java.security.Principal, java.util.Locale)}
	 * .
	 * 
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void getGameSession() throws Exception {
		// Build the mocks that will be needed by the controller.
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource
				.setBasename("file:./src/main/webapp/WEB-INF/i18n/messages");
		messageSource.setFallbackToSystemLocale(false);
		GameSession game = new GameSession(new Player(new Account()));
		IGameSessionResource gameClient = new MockGameClient(game);
		IGuestLoginManager guestLoginManager = new MockGuestLoginManager();

		// Build the controller and prepare it for mock testing.
		GameController gameController = new GameController(messageSource,
				gameClient, guestLoginManager);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(gameController)
				.build();

		/*
		 * Run the mock tests against the controller. We'll check the status and
		 * then just spot-check a few model attributes.
		 */
		mockMvc.perform(MockMvcRequestBuilders.get("/game/" + game.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(
						MockMvcResultMatchers.model().attributeExists("game"))
				.andExpect(
						MockMvcResultMatchers.model().attribute("hasPlayer2",
								false));
	}

	/**
	 * A mock {@link IGameSessionResource} implementation for use in tests.
	 */
	private static final class MockGameClient implements IGameSessionResource {
		private final GameSession game;

		/**
		 * Constructs a new {@link MockGameClient} instance.
		 * 
		 * @param game
		 *            the shared, mutable {@link GameSession} instance that will
		 *            be returned by every single method here
		 */
		public MockGameClient(GameSession game) {
			this.game = game;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#createGame()
		 */
		@Override
		public GameSession createGame() {
			return game;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#getGame(java.lang.String)
		 */
		@Override
		public GameSession getGame(String gameSessionId) {
			return game;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#setMaxRounds(java.lang.String,
		 *      int, int)
		 */
		@Override
		public GameSession setMaxRounds(String gameSessionId,
				int oldMaxRoundsValue, int newMaxRoundsValue) {
			return game;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#joinGame(java.lang.String)
		 */
		@Override
		public GameSession joinGame(String gameSessionId) {
			return game;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#prepareRound(java.lang.String)
		 */
		@Override
		public GameSession prepareRound(String gameSessionId) {
			return game;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource#submitThrow(java.lang.String,
		 *      int, com.justdavis.karl.rpstourney.service.api.game.Throw)
		 */
		@Override
		public GameSession submitThrow(String gameSessionId, int roundIndex,
				Throw throwToPlay) {
			return game;
		}
	}

	/**
	 * A mock {@link IGuestLoginManager} implementation for use in tests.
	 */
	private static final class MockGuestLoginManager implements
			IGuestLoginManager {
		/**
		 * @see com.justdavis.karl.rpstourney.webapp.security.IGuestLoginManager#loginClientAsGuest(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		@Override
		public void loginClientAsGuest(HttpServletRequest request,
				HttpServletResponse response) {
			// Do nothing.
		}
	}
}
