package com.justdavis.karl.rpstourney.webapp.home;

import java.util.List;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.MockGameClient;
import com.justdavis.karl.rpstourney.service.api.game.Player;

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
		HomeController homeController = new HomeController(new MockGameClient(
				null));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(homeController)
				.build();

		// Run the mock tests against the controller.
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.forwardedUrl("home"));
	}

	/**
	 * Tests {@link HomeController#getHomePage()} to ensure its model properly
	 * includes the results of {@link IGameResource#getGamesForPlayer()}.
	 * 
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void getGamesForPlayer() throws Exception {
		// Build the mocks needed for the test.
		Player player1 = new Player(new Account());
		Game game = new Game(player1);
		MockGameClient gameClient = new MockGameClient(game);

		// Build the controller and prepare it for mock testing.
		HomeController homeController = new HomeController(gameClient);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(homeController)
				.build();

		// Run the mock tests against the controller.
		Matcher<Object> matcher = new CustomMatcher<Object>("games size") {
			@Override
			public boolean matches(Object item) {
				if (!(item instanceof List))
					return false;

				return ((List<?>) item).size() == 1;
			}
		};
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(
						MockMvcResultMatchers.model().attributeExists("games"))
				.andExpect(
						MockMvcResultMatchers.model().attribute("games",
								matcher));
	}
}
