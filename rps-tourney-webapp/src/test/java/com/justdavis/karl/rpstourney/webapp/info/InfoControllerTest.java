package com.justdavis.karl.rpstourney.webapp.info;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for {@link InfoController}.
 */
public final class InfoControllerTest {
	/**
	 * Tests the {@link InfoController#ping()} response.
	 *
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void ping() throws Exception {
		// Build the controller and prepare it for mock testing.
		InfoController infoController = new InfoController();
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(infoController).build();

		// Run the mock tests against the controller.
		mockMvc.perform(MockMvcRequestBuilders.get("/info/ping")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.content().string("OK"));
	}

	/**
	 * Tests the {@link InfoController#getAppInfo()} response.
	 *
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void getAppInfo() throws Exception {
		// Build the controller and prepare it for mock testing.
		InfoController infoController = new InfoController();
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(infoController).build();

		// Run the mock tests against the controller.
		mockMvc.perform(MockMvcRequestBuilders.get("/info")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.forwardedUrl("app-info"));
	}
}
