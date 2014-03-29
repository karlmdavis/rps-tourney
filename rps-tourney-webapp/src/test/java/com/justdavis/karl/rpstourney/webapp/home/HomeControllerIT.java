package com.justdavis.karl.rpstourney.webapp.home;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.rpstourney.webapp.SpringMvcConfig;

/**
 * Integration tests for {@link HomeController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringMvcConfig.class })
public final class HomeControllerIT {
	@Inject
	private WebApplicationContext webAppContext;

	private MockMvc mockMvc;

	/**
	 * Initializes {@link #mockMvc}.
	 */
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webAppContext)
				.build();
	}

	/**
	 * Tests the {@link HomeController#getHomePage()} response.
	 * 
	 * @throws Exception
	 *             (all of the MVC test methods declare this exception)
	 */
	@Test
	public void getHomePage() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(
						MockMvcResultMatchers
								.forwardedUrl("/WEB-INF/views/home.jsp"));
	}
}
