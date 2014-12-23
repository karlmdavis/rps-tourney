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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException.ConflictType;
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
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
	/**
	 * The {@link RedirectAttributes#getFlashAttributes()} key used to store
	 * {@link ConflictType}s when {@link GameConflictException}s are
	 * encountered.
	 */
	private static final String FLASH_ATTRIB_WARNING_TYPE = "warningType";

	private final MessageSource messageSource;
	private final IGameResource gameClient;
	private final IAccountsResource accountsClient;
	private final IGuestLoginManager guestLoginManager;

	/**
	 * Constructs a new {@link GameController} instance.
	 * 
	 * @param messageSource
	 *            the {@link MessageSource} to use
	 * @param gameClient
	 *            the {@link IGameResource} client to use
	 * @param accountsClient
	 *            the {@link IAccountsResource} client to use
	 * @param guestLoginManager
	 *            the {@link IGuestLoginManager} to use
	 */
	@Inject
	public GameController(MessageSource messageSource,
			IGameResource gameClient, IAccountsResource accountsClient,
			IGuestLoginManager guestLoginManager) {
		this.messageSource = messageSource;
		this.gameClient = gameClient;
		this.accountsClient = accountsClient;
		this.guestLoginManager = guestLoginManager;
	}

	/**
	 * <p>
	 * Creates a new persistent {@link Game} and redirects to
	 * {@link #getGame(String, Principal)} to display it.
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
	 * @return a <code>redirect:</code> view name for the {@link Game} that's
	 *         been created
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String createNewGame(Principal authenticatedUser,
			HttpServletRequest request, HttpServletResponse response) {
		// If the user isn't already logged in, log them in as a guest.
		if (authenticatedUser == null) {
			this.guestLoginManager.loginClientAsGuest(request, response);
		}

		// Create the new game.
		GameView game = gameClient.createGame();

		// Redirect the user to that new game.
		return "redirect:/game/" + game.getId();
	}

	/**
	 * @param gameId
	 *            the {@link Game#getId()} of the game being requested
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @param locale
	 *            the {@link Locale} to target for display
	 * @return a {@link ModelAndView} that can be used to render an existing
	 *         gameplay session
	 */
	@RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
	public ModelAndView getGame(@PathVariable String gameId,
			Principal authenticatedUser, Locale locale) {
		/*
		 * FIXME Per the suggestion in
		 * https://jira.spring.io/browse/SPR-12481?focusedCommentId
		 * =110879&page=com
		 * .atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel
		 * #comment-110879, this method's @RequestMapping is not marked as
		 * "produces = MediaType.TEXT_HTML_VALUE". It does return HTML, though.
		 * This workaround should be removed if that JIRA issue is resolved.
		 */

		GameView game = loadGame(gameId);

		ModelAndView modelAndView = buildGameModelAndView(locale,
				authenticatedUser, game);

		return modelAndView;
	}

	/**
	 * @param gameId
	 *            the {@link Game#getId()} of the game being requested
	 * @return a {@link GameView} instance with the current game state for the
	 *         requesting user
	 */
	@RequestMapping(value = "/{gameId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public GameView getGameView(@PathVariable String gameId) {
		GameView game = loadGame(gameId);
		return game;
	}

	/**
	 * The controller facade for
	 * {@link IGameResource#submitThrow(String, int, Throw)}.
	 * 
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param throwToPlay
	 *            the {@link Throw} that the current user/player is submitting
	 *            for the current {@link GameRound} in the specified
	 *            {@link Game}
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/updateName", method = { RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String updateName(@PathVariable String gameId,
			String currentPlayerName, Principal authenticatedUser) {
		// Load the specified game.
		GameView gameBeforeThrow = loadGame(gameId);

		// There's nothing to change if they haven't logged in yet.
		if (authenticatedUser == null)
			// TODO better exception
			throw new IllegalArgumentException();

		// Update the user's name for themselves.
		Account account = accountsClient.getAccount();
		account.setName(currentPlayerName);
		accountsClient.updateAccount(account);

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameBeforeThrow.getId();
	}

	/**
	 * The controller facade for
	 * {@link IGameResource#submitThrow(String, int, Throw)}.
	 * 
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param throwToPlay
	 *            the {@link Throw} that the current user/player is submitting
	 *            for the current {@link GameRound} in the specified
	 *            {@link Game}
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to
	 *            pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/playThrow", method = {
			RequestMethod.GET, RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String submitThrow(@PathVariable String gameId,
			@RequestParam Throw throwToPlay,
			RedirectAttributes redirectAttributes) {
		// Load the specified game.
		GameView gameBeforeThrow = loadGame(gameId);

		// Submit the throw.
		int currentRoundIndex = gameBeforeThrow.getRounds().size() - 1;
		try {
			gameClient.submitThrow(gameBeforeThrow.getId(), currentRoundIndex,
					throwToPlay);
		} catch (GameConflictException e) {
			// Catch these errors and display them in a friendlier fashion.
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, e
					.getType().name());
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameId;
	}

	/**
	 * The controller facade for {@link IGameResource#joinGame(String)}.
	 * 
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param request
	 *            the {@link HttpServletRequest} being processed
	 * @param response
	 *            the {@link HttpServletResponse} being generated
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to
	 *            pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/join", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String joinGame(@PathVariable String gameId,
			HttpServletRequest request, HttpServletResponse response,
			Principal authenticatedUser, RedirectAttributes redirectAttributes) {
		// If the user isn't already logged in, log them in as a guest.
		if (authenticatedUser == null) {
			this.guestLoginManager.loginClientAsGuest(request, response);
			authenticatedUser = request.getUserPrincipal();
		}

		// Load the specified game.
		GameView gameBeforeJoin = loadGame(gameId);

		// Try to join the game.
		try {
			gameClient.joinGame(gameBeforeJoin.getId());
		} catch (GameConflictException e) {
			// Catch these errors and display them in a friendlier fashion.
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, e
					.getType().name());
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameId;
	}

	/**
	 * The controller facade for
	 * {@link IGameResource#setMaxRounds(String, int, int)}.
	 * 
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param oldMaxRoundsValue
	 *            the current/old value of {@link Game#getMaxRounds()} (used to
	 *            help prevent synchronization issues)
	 * @param newMaxRoundsValue
	 *            the new value for {@link Game#getMaxRounds()}
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to
	 *            pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/setMaxRounds", method = {
			RequestMethod.GET, RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String setMaxRounds(@PathVariable String gameId,
			@RequestParam int oldMaxRoundsValue,
			@RequestParam int newMaxRoundsValue, Principal authenticatedUser,
			RedirectAttributes redirectAttributes) {
		// Load the specified game.
		GameView game = loadGame(gameId);

		// In the webapp, we'll only allow actual players to adjust this.
		if (!isUserThisPlayer(authenticatedUser, game.getPlayer1())
				&& !isUserThisPlayer(authenticatedUser, game.getPlayer2())) {
			throw new AccessDeniedException(
					"Only players in a game may adjust the number of rounds.");
		}

		// Try to set the number of rounds.
		try {
			gameClient.setMaxRounds(gameId, oldMaxRoundsValue,
					newMaxRoundsValue);
		} catch (GameConflictException e) {
			// Catch these errors and display them in a friendlier fashion.
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, e
					.getType().name());
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameId;
	}

	/**
	 * @param gameId
	 *            the {@link Game#getId()} to match against
	 * @return the specified {@link GameView}, as returned by
	 *         {@link #gameClient}
	 */
	private GameView loadGame(String gameId) {
		GameView game = null;
		try {
			game = gameClient.getGame(gameId);
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
	 *            the {@link GameView} to render
	 * @return the {@link ModelAndView} for <code>game.jsp</code> to render
	 */
	private ModelAndView buildGameModelAndView(Locale locale,
			Principal authenticatedUser, GameView game) {
		ModelAndView modelAndView = new ModelAndView("game");

		// Add the Game to the model.
		modelAndView.addObject("game", game);

		// Add some display-related data to the model.
		modelAndView.addObject("hasPlayer1", game.getPlayer1() != null);
		modelAndView.addObject("player1Label",
				getPlayer1Label(game, messageSource, locale));
		modelAndView.addObject("hasPlayer2", game.getPlayer2() != null);
		modelAndView.addObject("player2Label",
				getPlayer2Label(game, messageSource, locale));
		modelAndView.addObject(
				"currentAdjustedRoundIndex",
				game.getRounds().isEmpty() ? 0 : game.getRounds()
						.get(game.getRounds().size() - 1)
						.getAdjustedRoundIndex());

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

		// This is used for the name-editing widget.
		if (isPlayer1)
			modelAndView.addObject("currentPlayerName",
					getPlayerName(game.getPlayer1()));
		else if (isPlayer2)
			modelAndView.addObject("currentPlayerName",
					getPlayerName(game.getPlayer2()));

		// Setup some winner/loser properties.
		boolean hasWinner = game.getWinner() != null;
		modelAndView.addObject("isUserTheWinner", hasWinner && isPlayer
				&& isUserThisPlayer(authenticatedUser, game.getWinner()));
		modelAndView.addObject("isUserTheLoser", hasWinner && isPlayer
				&& !isUserThisPlayer(authenticatedUser, game.getWinner()));
		modelAndView.addObject("isPlayer1TheWinner", hasWinner
				&& game.getWinner().equals(game.getPlayer1()));
		modelAndView.addObject("isPlayer1TheLoser", hasWinner
				&& !game.getWinner().equals(game.getPlayer1()));
		modelAndView.addObject("isPlayer2TheWinner", hasWinner
				&& game.getWinner().equals(game.getPlayer2()));
		modelAndView.addObject("isPlayer2TheLoser", hasWinner
				&& !game.getWinner().equals(game.getPlayer2()));

		return modelAndView;
	}

	/**
	 * @param player
	 *            the {@link Player} to get the {@link Player#getName()} value
	 *            of
	 * @return the specified {@link Player}'s {@link Player#getName()} value, or
	 *         <code>null</code> if either the {@link Player} or name is
	 *         <code>null</code>
	 */
	private static String getPlayerName(Player player) {
		if (player == null)
			return null;
		return player.getName();
	}

	/**
	 * @param game
	 *            the {@link GameView} to get the label for
	 * @param messageSource
	 *            the {@link MessageSource} to look up text from
	 * @param locale
	 *            the {@link Locale} to display text for
	 * @return the display text/label to use to represent
	 *         {@link Game#getPlayer1()}
	 */
	private static String getPlayer1Label(GameView game,
			MessageSource messageSource, Locale locale) {
		// If the Player has an actual name, use that.
		if (game.getPlayer1() != null && game.getPlayer1().getName() != null)
			return game.getPlayer1().getName();

		// Has the Player joined the game yet?
		if (game.getPlayer1() != null)
			return messageSource.getMessage("game.player1.label", null, locale);
		else
			return messageSource.getMessage("game.player1.label.waiting", null,
					locale);
	}

	/**
	 * @param game
	 *            the {@link GameView} to get the label for
	 * @param messageSource
	 *            the {@link MessageSource} to look up text from
	 * @param locale
	 *            the {@link Locale} to display text for
	 * @return the display text/label to use to represent
	 *         {@link Game#getPlayer2()}
	 */
	private static String getPlayer2Label(GameView game,
			MessageSource messageSource, Locale locale) {
		// If the Player has an actual name, use that.
		if (game.getPlayer2() != null && game.getPlayer2().getName() != null)
			return game.getPlayer2().getName();

		// Has the Player joined the game yet?
		if (game.getPlayer2() != null)
			return messageSource.getMessage("game.player2.label", null, locale);
		else
			return messageSource.getMessage("game.player2.label.waiting", null,
					locale);
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
