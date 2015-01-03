package com.justdavis.karl.rpstourney.webapp.game;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.MockGameClient;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.webapp.security.IGuestLoginManager;
import com.justdavis.karl.rpstourney.webapp.security.WebServiceAccountAuthentication;

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
		final Game game = new Game(new Player(new Account()));
		IGameResource gameClient = new MockGameClient(game) {
			/**
			 * @see com.justdavis.karl.rpstourney.service.api.game.MockGameClient#createGame()
			 */
			@Override
			public GameView createGame() {
				return new GameView(game, null);
			}
		};
		IAccountsResource accountsClient = new MockAccountsClient();
		IGuestLoginManager guestLoginManager = new MockGuestLoginManager();

		// Build the controller and prepare it for mock testing.
		GameController GameController = new GameController(messageSource,
				gameClient, accountsClient, guestLoginManager);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(GameController)
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
	 * {@link GameController#getGameAsHtml(String, java.security.Principal, java.util.Locale)}
	 * .
	 * 
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void getGame() throws Exception {
		// Build the mocks that will be needed by the controller.
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource
				.setBasename("file:./src/main/webapp/WEB-INF/i18n/messages");
		messageSource.setFallbackToSystemLocale(false);
		Game game = new Game(new Player(new Account()));
		IGameResource gameClient = new MockGameClient(game);
		IAccountsResource accountsClient = new MockAccountsClient();
		IGuestLoginManager guestLoginManager = new MockGuestLoginManager();

		// Build the controller and prepare it for mock testing.
		GameController GameController = new GameController(messageSource,
				gameClient, accountsClient, guestLoginManager);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(GameController)
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
	 * Tests
	 * {@link GameController#updateName(String, String, java.security.Principal)}
	 * .
	 * 
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void updateName() throws Exception {
		// Build the mocks that will be needed by the controller.
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource
				.setBasename("file:./src/main/webapp/WEB-INF/i18n/messages");
		messageSource.setFallbackToSystemLocale(false);
		Account player1 = new Account();
		Game game = new Game(new Player(player1));
		IGameResource gameClient = new MockGameClient(game);
		IAccountsResource accountsClient = new MockUpdatableAccountsClient(
				player1);
		IGuestLoginManager guestLoginManager = new MockGuestLoginManager();

		// Build the controller and prepare it for mock testing.
		GameController gameController = new GameController(messageSource,
				gameClient, accountsClient, guestLoginManager);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(gameController)
				.build();

		/*
		 * Run the mock tests against the controller, and verify that nothing
		 * goes boom.
		 */
		String gamePath = "/game/" + game.getId();
		String updateNamePath = gamePath + "/updateName";
		Principal principal = new WebServiceAccountAuthentication(player1);
		mockMvc.perform(
				MockMvcRequestBuilders.post(updateNamePath)
						.principal(principal).param("inputPlayerName", "foo"))
				.andExpect(MockMvcResultMatchers.redirectedUrl(gamePath));
		Assert.assertEquals("foo", accountsClient.getAccount().getName());

		// Now make sure the model contains the updated name.
		mockMvc.perform(MockMvcRequestBuilders.get(gamePath))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(
						MockMvcResultMatchers.model().attribute("player1Label",
								"foo"));
	}

	/**
	 * A mock {@link IAccountsResource} client implementation for use in
	 * {@link GameControllerTest#updateName()}, and other tests.
	 */
	private static final class MockUpdatableAccountsClient extends
			MockAccountsClient {
		private Account account;

		/**
		 * Constructs a new {@link MockUpdatableAccountsClient} instance.
		 * 
		 * @param account
		 *            the value to use for {@link #getAccount()}
		 */
		private MockUpdatableAccountsClient(Account account) {
			this.account = account;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient#getAccount()
		 */
		@Override
		public Account getAccount() {
			return account;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.MockAccountsClient#updateAccount(com.justdavis.karl.rpstourney.service.api.auth.Account)
		 */
		@Override
		public Account updateAccount(Account accountToUpdate) {
			this.account = accountToUpdate;
			return accountToUpdate;
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
