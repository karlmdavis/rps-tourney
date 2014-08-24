package com.justdavis.karl.rpstourney.webapp.home;

import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
		HomeController homeController = new HomeController();
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(homeController)
				.build();

		// Run the mock tests against the controller.
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.forwardedUrl("home"));
	}
}
