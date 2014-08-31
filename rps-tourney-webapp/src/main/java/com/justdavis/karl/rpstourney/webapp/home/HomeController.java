package com.justdavis.karl.rpstourney.webapp.home;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource;

/**
 * The {@link Controller} for the site's home page.
 */
@Controller
@RequestMapping("/")
public class HomeController {
	private final IGameSessionResource gameClient;

	/**
	 * Constructs a new {@link HomeController} instance.
	 * 
	 * @param gameClient
	 *            the {@link IGameSessionResource} client to use
	 */
	@Inject
	public HomeController(IGameSessionResource gameClient) {
		this.gameClient = gameClient;
	}

	/**
	 * @return a {@link ModelAndView} that can be used to render some basic
	 *         information about the application
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getHomePage() {
		ModelAndView modelAndView = new ModelAndView("home");

		// Get the current player's games (if any).
		modelAndView.addObject("games", gameClient.getGamesForPlayer());

		return modelAndView;
	}
}
