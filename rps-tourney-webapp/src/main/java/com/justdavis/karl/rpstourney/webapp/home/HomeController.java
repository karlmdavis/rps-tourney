package com.justdavis.karl.rpstourney.webapp.home;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;

/**
 * The {@link Controller} for the site's home page.
 */
@Controller
@RequestMapping("/")
public class HomeController {
	private final IGameResource gameClient;

	/**
	 * Constructs a new {@link HomeController} instance.
	 * 
	 * @param gameClient
	 *            the {@link IGameResource} client to use
	 */
	@Inject
	public HomeController(IGameResource gameClient) {
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
		List<GameView> games = gameClient.getGamesForPlayer();
		Collections.sort(games, new GamesSorter());
		modelAndView.addObject("games", games);

		return modelAndView;
	}

	/**
	 * Sorts {@link GameView} instances based on their
	 * {@link GameView#getLastThrowTimestamp()} value, latest first.
	 */
	private static final class GamesSorter implements Comparator<GameView> {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(GameView o1, GameView o2) {
			if (o1.getLastThrowTimestamp() == null)
				return -1;
			if (o2.getLastThrowTimestamp() == null)
				return -1;

			int timestampComparison = o1.getLastThrowTimestamp().compareTo(
					o2.getLastThrowTimestamp());
			return -1 * timestampComparison;
		}
	}
}
