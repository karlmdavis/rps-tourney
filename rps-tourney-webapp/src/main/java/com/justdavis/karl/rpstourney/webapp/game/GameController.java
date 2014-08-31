package com.justdavis.karl.rpstourney.webapp.game;

import java.security.Principal;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;
import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.IGameSessionResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.webapp.security.IGuestLoginManager;

/**
 * The {@link Controller} for the page that's actually used to play the game.
 */
@Controller
@RequestMapping("/game")
public class GameController {
	private final MessageSource messageSource;
	private final IGameSessionResource gameClient;
	private final IGuestLoginManager guestLoginManager;

	/**
	 * Constructs a new {@link GameController} instance.
	 * 
	 * @param messageSource
	 *            the {@link MessageSource} to use
	 * @param gameClient
	 *            the {@link IGameSessionResource} client to use
	 * @param guestLoginManager
	 *            the {@link IGuestLoginManager} to use
	 */
	@Inject
	public GameController(MessageSource messageSource,
			IGameSessionResource gameClient,
			IGuestLoginManager guestLoginManager) {
		this.messageSource = messageSource;
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
		return "redirect:/game/" + game.getId();
	}

	/**
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} of the game being requested
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @param locale
	 *            the {@link Locale} to target for display
	 * @return a {@link ModelAndView} that can be used to render an existing
	 *         gameplay session
	 */
	@RequestMapping(value = "/{gameSessionId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView getGameSession(@PathVariable String gameSessionId,
			Principal authenticatedUser, Locale locale) {
		GameSession game = loadGame(gameSessionId);

		ModelAndView modelAndView = buildGameModelAndView(locale,
				authenticatedUser, game);

		return modelAndView;
	}

	/**
	 * The controller facade for
	 * {@link IGameSessionResource#submitThrow(String, int, Throw)}.
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} of the game being updated
	 * @param throwToPlay
	 *            the {@link Throw} that the current user/player is submitting
	 *            for the current {@link GameRound} in the specified
	 *            {@link GameSession}
	 * @return a <code>redirect:</code> view name for the
	 *         {@link #getGameSession()} that's been created
	 */
	@RequestMapping(value = "/{gameSessionId}/playThrow", method = {
			RequestMethod.GET, RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String submitThrow(@PathVariable String gameSessionId,
			@RequestParam Throw throwToPlay) {
		// Load the specified game.
		GameSession gameBeforeThrow = loadGame(gameSessionId);

		// Submit the throw.
		GameRound currentRound = gameBeforeThrow.getRounds().get(
				gameBeforeThrow.getRounds().size() - 1);
		gameClient.submitThrow(gameBeforeThrow.getId(),
				currentRound.getRoundIndex(), throwToPlay);

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameSessionId;
	}

	/**
	 * The controller facade for {@link IGameSessionResource#joinGame(String)}.
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} of the game being updated
	 * @param request
	 *            the {@link HttpServletRequest} being processed
	 * @param response
	 *            the {@link HttpServletResponse} being generated
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @return a <code>redirect:</code> view name for the
	 *         {@link #getGameSession()} that's been created
	 */
	@RequestMapping(value = "/{gameSessionId}/join", method = {
			RequestMethod.GET, RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String joinGame(@PathVariable String gameSessionId,
			HttpServletRequest request, HttpServletResponse response,
			Principal authenticatedUser) {
		// If the user isn't already logged in, log them in as a guest.
		if (authenticatedUser == null) {
			this.guestLoginManager.loginClientAsGuest(request, response);
			authenticatedUser = request.getUserPrincipal();
		}

		// Load the specified game.
		GameSession gameBeforeJoin = loadGame(gameSessionId);

		// Try to join the game.
		gameClient.joinGame(gameBeforeJoin.getId());

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameSessionId;
	}

	/**
	 * The controller facade for
	 * {@link IGameSessionResource#setMaxRounds(String, int, int)}.
	 * 
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} of the game being updated
	 * @param oldMaxRoundsValue
	 *            the current/old value of {@link GameSession#getMaxRounds()}
	 *            (used to help prevent synchronization issues)
	 * @param newMaxRoundsValue
	 *            the new value for {@link GameSession#getMaxRounds()}
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @return a <code>redirect:</code> view name for the
	 *         {@link #getGameSession()} that's been created
	 */
	@RequestMapping(value = "/{gameSessionId}/setMaxRounds", method = {
			RequestMethod.GET, RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String setMaxRounds(@PathVariable String gameSessionId,
			@RequestParam int oldMaxRoundsValue,
			@RequestParam int newMaxRoundsValue, Principal authenticatedUser) {
		// Load the specified game.
		GameSession game = loadGame(gameSessionId);

		// In the webapp, we'll only allow actual players to adjust this.
		if (!isUserThisPlayer(authenticatedUser, game.getPlayer1())
				&& !isUserThisPlayer(authenticatedUser, game.getPlayer2())) {
			throw new AccessDeniedException(
					"Only players in a game may adjust the number of rounds.");
		}

		// Try to set the number of rounds.
		try {
			gameClient.setMaxRounds(gameSessionId, oldMaxRoundsValue,
					newMaxRoundsValue);
		} catch (GameConflictException e) {
			/*
			 * This will occur if the current user/client tried to set a new
			 * value, but had an incorrect/stale current value. We'll just
			 * ignore those errors, let the user see in the reloaded GUI that
			 * nothing changed, and try again if they'd like.
			 */
			/*
			 * TODO Consider adding a warning message somewhere explaining what
			 * happened.
			 */
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameSessionId;
	}

	/**
	 * @param gameSessionId
	 *            the {@link GameSession#getId()} to match against
	 * @return the specified {@link GameSession}, as returned by
	 *         {@link #gameClient}
	 */
	private GameSession loadGame(String gameSessionId) {
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

		return game;
	}

	/**
	 * @param locale
	 *            the {@link Locale} to target for display
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @param game
	 *            the {@link GameSession} to render
	 * @return the {@link ModelAndView} for <code>game.jsp</code> to render
	 */
	private ModelAndView buildGameModelAndView(Locale locale,
			Principal authenticatedUser, GameSession game) {
		ModelAndView modelAndView = new ModelAndView("game");

		// Add the GameSession to the model.
		modelAndView.addObject("game", game);

		// Add some display-related data to the model.
		boolean hasPlayer1 = game.getPlayer1() != null;
		modelAndView.addObject("hasPlayer1", hasPlayer1);
		modelAndView.addObject(
				"player1Name",
				hasPlayer1 ? game.getPlayer1().getName() : messageSource
						.getMessage("game.player1.waiting", null, locale));
		boolean hasPlayer2 = game.getPlayer2() != null;
		modelAndView.addObject("hasPlayer2", hasPlayer2);
		modelAndView.addObject(
				"player2Name",
				hasPlayer2 ? game.getPlayer2().getName() : messageSource
						.getMessage("game.player2.waiting", null, locale));

		// Calculate the current scores.
		int player1Score = 0;
		int player2Score = 0;
		for (GameRound round : game.getRounds()) {
			if (round.getResult() == Result.PLAYER_1_WON)
				player1Score++;
			else if (round.getResult() == Result.PLAYER_2_WON)
				player2Score++;
		}
		modelAndView.addObject("player1Score", player1Score);
		modelAndView.addObject("player2Score", player2Score);

		/*
		 * The following model entries are ONLY used for display purposes. They
		 * are not and should not be used for any sort of real/actual access
		 * control.
		 */
		boolean isPlayer1 = isUserThisPlayer(authenticatedUser,
				game.getPlayer1());
		boolean isPlayer2 = isUserThisPlayer(authenticatedUser,
				game.getPlayer2());
		boolean isPlayer = isPlayer1 || isPlayer2;
		modelAndView.addObject("isPlayer", isPlayer);
		modelAndView.addObject("isPlayer1", isPlayer1);
		modelAndView.addObject("isPlayer2", isPlayer2);

		return modelAndView;
	}

	/**
	 * @param authenticatedUser
	 *            the user/client {@link Principal} that made the request, or
	 *            <code>null</code> if the request was unauthenticated
	 * @param player
	 *            the {@link Player} to check against, or <code>null</code> if
	 *            the player hasn't joined the game yet
	 * @return <code>true</code> if the specified {@link Principal} is the
	 *         specified {@link Player}, <code>false</code> if not
	 */
	private static boolean isUserThisPlayer(Principal authenticatedUser,
			Player player) {
		/*
		 * This is a bit tricky. Because we're using Spring Security, we can
		 * expect that the Principal instances passed to any controller methods
		 * will be Authentication instances. From that Authentication, we can
		 * extract the actual/underlying principal. Given this application's
		 * SecurityConfig, that should always be an Account.
		 */
		Object actualPrincipal = null;
		if (authenticatedUser == null)
			return false;
		else if (authenticatedUser instanceof Authentication)
			actualPrincipal = ((Authentication) authenticatedUser)
					.getPrincipal();
		else
			throw new BadCodeMonkeyException("Unhandled security object: "
					+ authenticatedUser);

		if (player == null)
			return false;
		if (player.getHumanAccount() == null)
			/*
			 * AI players will not & cannot use the web app; they must use the
			 * web service or Java API.
			 */
			return false;

		return actualPrincipal.equals(player.getHumanAccount());
	}
}
