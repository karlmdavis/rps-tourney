package com.justdavis.karl.rpstourney.webapp.game;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.webapp.security.IGuestLoginManager;

/**
 * The {@link Controller} for the page that's actually used to play the game.
 */
@Controller
@RequestMapping("/game")
public class GameController {
	private final IGameSessionResource gameClient;
	private final IGuestLoginManager guestLoginManager;

	/**
	 * Constructs a new {@link GameController} instance.
	 * 
	 * @param gameClient
	 *            the {@link IGameSessionResource} client to use
	 * @param guestLoginManager
	 *            the {@link IGuestLoginManager} to use
	 */
	@Inject
	public GameController(IGameSessionResource gameClient,
			IGuestLoginManager guestLoginManager) {
		this.gameClient = gameClient;
		this.guestLoginManager = guestLoginManager;
	}

	/**
	 * <p>
	 * Creates a new persistent {@link GameSession} and redirects to
	 * {@link #getGameSession(String, Principal)} to display it.
	 * </p>
	 * <p>
	 * Note: If the requesting client is not logged in/authenticated when making
	 * this request, they will be automatically logged in via
	 * {@link IGuestLoginManager} as a guest.
	 * </p>
	 * 
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}, or
	 *            <code>null</code> if no user is logged in
	 * @param request
	 *            the {@link HttpServletRequest} being processed
	 * @param response
	 *            the {@link HttpServletResponse} being generated
	 * @return a <code>redirect:</code> view name for the
	 *         {@link #getGameSession()} that's been created
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String createNewGame(Principal authenticatedUser,
			HttpServletRequest request, HttpServletResponse response) {
		// If the user isn't already logged in, log them in as a guest.
		if (authenticatedUser == null) {
			this.guestLoginManager.loginClientAsGuest(request, response);
		}

		// Create the new game.
		GameSession game = gameClient.createGame();

		// Redirect the user to that new game.
		return "redirect:" + game.getId();
	}

	/**
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} of the game being requested
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @return a {@link ModelAndView} that can be used to render an existing
	 *         gameplay session
	 */
	@RequestMapping(value = "/{gameSessionId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView getGameSession(@PathVariable String gameSessionId,
			Principal authenticatedUser) {
		// Load the specified game.
		GameSession game = null;
		try {
			game = gameClient.getGame(gameSessionId);
		} catch (HttpClientException e) {
			if (e.getStatus().getStatusCode() == Status.NOT_FOUND
					.getStatusCode())
				throw new GameNotFoundException(e);

			// TODO choose a better exception wrapper for other stuff
			throw new RuntimeException(e);
		}

		ModelAndView modelAndView = new ModelAndView("game");
		modelAndView.addObject("game", game);

		return modelAndView;
	}
}
