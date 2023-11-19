package com.justdavis.karl.rpstourney.webapp.home;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.MockGameClient;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;

/**
 * Unit tests for {@link HomeController}.
 */
public final class HomeControllerTest {
	/**
	 * Tests the {@link HomeController#getHomePage()} response.
	 *
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void getHomePage() throws Exception {
		// Build the controller and prepare it for mock testing.
		HomeController homeController = new HomeController(new MockGameClient((GameView) null));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

		// Run the mock tests against the controller.
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.forwardedUrl("home"));
	}

	/**
	 * Tests {@link HomeController#getHomePage()} to ensure its model properly includes the results of
	 * {@link IGameResource#getGamesForPlayer()}.
	 *
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void getGamesForPlayer() throws Exception {
		/*
		 * Build the mocks needed for the test. Add an artificial delay between creating the two games, to ensure they
		 * end up with different timestamps.
		 */
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		Game gameA = new Game(player1);
		final GameView gameViewA = new GameView(gameA, null);
		new CountDownLatch(1).await(100, TimeUnit.MILLISECONDS);
		Game gameB = new Game(player1);
		gameB.setPlayer2(player2);
		gameB.submitThrow(0, player1, Throw.ROCK);
		final GameView gameViewB = new GameView(gameB, null);
		MockGameClient gameClient = new MockGameClient(gameViewA, gameViewB);

		// Build the controller and prepare it for mock testing.
		HomeController homeController = new HomeController(gameClient);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

		// Run the mock tests against the controller.
		Matcher<Object> matcher = new CustomMatcher<Object>("games") {
			@Override
			public boolean matches(Object item) {
				/*
				 * Note the order of the games here: games updated last should now be first in the model's list.
				 */
				return Arrays.asList(gameViewB, gameViewA).equals(item);
			}
		};
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("games"))
				.andExpect(MockMvcResultMatchers.model().attribute("games", matcher));
	}
}
