package com.justdavis.karl.rpstourney.webapp.game;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

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
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.IGameResource;
import com.justdavis.karl.rpstourney.service.api.game.IPlayersResource;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;
import com.justdavis.karl.rpstourney.webapp.security.IGuestLoginManager;

/**
 * The {@link Controller} for the page that's actually used to play the game.
 */
@Controller
@RequestMapping("/game")
public class GameController {
	/**
	 * <p>
	 * The {@link RedirectAttributes#getFlashAttributes()} key used to store error codes when problems are encountered.
	 * The following error codes are supported:
	 * </p>
	 * <ul>
	 * <li>All of the {@link GameConflictException} constant names.</li>
	 * <li>{@link #WARNING_CODE_INVALID_NAME}</li>
	 * </ul>
	 */
	static final String FLASH_ATTRIB_WARNING_TYPE = "warningType";

	/**
	 * A possible value for {@link #FLASH_ATTRIB_WARNING_TYPE}.
	 */
	static final String WARNING_CODE_INVALID_NAME = "INVALID_ACCOUNT_NAME";

	private final IGameResource gameClient;
	private final IAccountsResource accountsClient;
	private final IPlayersResource playersClient;
	private final IGuestLoginManager guestLoginManager;

	/**
	 * Constructs a new {@link GameController} instance.
	 *
	 * @param gameClient
	 *            the {@link IGameResource} client to use
	 * @param accountsClient
	 *            the {@link IAccountsResource} client to use
	 * @param playersClient
	 *            the {@link IPlayersResource} client to use
	 * @param guestLoginManager
	 *            the {@link IGuestLoginManager} to use
	 */
	@Inject
	public GameController(IGameResource gameClient, IAccountsResource accountsClient, IPlayersResource playersClient,
			IGuestLoginManager guestLoginManager) {
		this.gameClient = gameClient;
		this.accountsClient = accountsClient;
		this.playersClient = playersClient;
		this.guestLoginManager = guestLoginManager;
	}

	/**
	 * <p>
	 * Creates a new persistent {@link Game} and redirects to {@link #getGame(String, Principal)} to display it.
	 * </p>
	 * <p>
	 * Note: If the requesting client is not logged in/authenticated when making this request, they will be
	 * automatically logged in via {@link IGuestLoginManager} as a guest.
	 * </p>
	 *
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}, or <code>null</code> if no user is logged in
	 * @param request
	 *            the {@link HttpServletRequest} being processed
	 * @param response
	 *            the {@link HttpServletResponse} being generated
	 * @return a <code>redirect:</code> view name for the {@link Game} that's been created
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String createNewGame(Principal authenticatedUser, HttpServletRequest request, HttpServletResponse response) {
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
	 * @return a {@link ModelAndView} that can be used to render an existing gameplay session
	 */
	@RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
	public ModelAndView getGameAsHtml(@PathVariable String gameId, Principal authenticatedUser, Locale locale) {
		/*
		 * FIXME Per the suggestion in https://jira.spring.io/browse/SPR-12481?focusedCommentId =110879&page=com
		 * .atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel #comment-110879, this method's @RequestMapping
		 * is not marked as "produces = MediaType.TEXT_HTML_VALUE". It does return HTML, though. This workaround should
		 * be removed if that JIRA issue is resolved.
		 */

		GameView game = loadGame(gameId);

		ModelAndView modelAndView = buildGameModelAndView(locale, authenticatedUser, game);

		return modelAndView;
	}

	/**
	 * @param gameId
	 *            the {@link Game#getId()} of the game being requested
	 * @return a {@link GameView} instance with the current game state for the requesting user
	 */
	@RequestMapping(value = "/{gameId}/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public GameView getGameAsJson(@PathVariable String gameId) {
		GameView game = loadGame(gameId);
		return game;
	}

	/**
	 * The controller for accepting {@link Account#getName()} updates.
	 *
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param inputPlayerName
	 *            the new value for {@link Account#getName()}
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal} (whose {@link Account#getName()} is to be updated)
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/updateName", method = {
			RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String updateName(@PathVariable String gameId, String inputPlayerName, Principal authenticatedUser,
			RedirectAttributes redirectAttributes) {
		// Load the specified game.
		GameView gameBeforeThrow = loadGame(gameId);

		// There's nothing to change if they haven't logged in yet.
		if (authenticatedUser == null)
			// TODO better exception
			throw new IllegalArgumentException();

		// Update the user's name for themselves.
		Account account = accountsClient.getAccount();
		account.setName(inputPlayerName);

		/*
		 * Try to submit the name update. If the user has specified an invalid name (per the web service's bean
		 * validation), this will go boom with an HTTP 400.
		 */
		try {
			accountsClient.updateAccount(account);
		} catch (BadRequestException e) {
			// Catch these errors and display them in a friendlier fashion.
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, WARNING_CODE_INVALID_NAME);
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameBeforeThrow.getId();
	}

	/**
	 * The controller facade for {@link IGameResource#submitThrow(String, int, Throw)}.
	 *
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param throwToPlay
	 *            the {@link Throw} that the current user/player is submitting for the current {@link GameRound} in the
	 *            specified {@link Game}
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/playThrow", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String submitThrow(@PathVariable String gameId, @RequestParam Throw throwToPlay,
			RedirectAttributes redirectAttributes) {
		// Load the specified game.
		GameView gameBeforeThrow = loadGame(gameId);

		// Prepare the round, if needed.
		try {
			if (!gameBeforeThrow.isRoundPrepared())
				gameClient.prepareRound(gameId);
		} catch (HttpClientException e) {
			if (e.getStatus().getStatusCode() == Status.CONFLICT.getStatusCode()) {
				/*
				 * This is perfectly normal and not a problem: it can happen due to one of the clients being slightly
				 * out of date before making the call.
				 */
			} else {
				// For anything other than a CONFLICT, wrap & rethrow.
				throw new HttpClientException(e);
			}
		}

		// Submit the throw.
		int currentRoundIndex = gameBeforeThrow.getRounds().size() - 1;
		try {
			gameClient.submitThrow(gameBeforeThrow.getId(), currentRoundIndex, throwToPlay);
		} catch (GameConflictException e) {
			// Catch these errors and display them in a friendlier fashion.
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, e.getType().name());
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
	 *            the Spring MVC {@link RedirectAttributes} that will be used to pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/join", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String joinGame(@PathVariable String gameId, HttpServletRequest request, HttpServletResponse response,
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
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, e.getType().name());
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameId;
	}

	/**
	 * The controller facade for {@link IGameResource#inviteOpponent(String, long)}.
	 *
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param opponentType
	 *            either <code>friend</code> or <code>ai</code>, indicating which type of opponent the user has
	 *            requested
	 * @param playerId
	 *            the {@link Player#getId()} of the player being invited as the user's opponent
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/inviteOpponent", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String inviteOpponent(@PathVariable String gameId, @RequestParam String opponentType,
			@RequestParam int playerId, Principal authenticatedUser, RedirectAttributes redirectAttributes) {
		// Load the specified game.
		GameView game = loadGame(gameId);

		/*
		 * If they haven't requested an AI opponent, do nothing. (The submit button generally wouldn't be visible for
		 * them, but if they have disabled JS, they might still use it.)
		 */
		if (!"ai".equals(opponentType))
			return "redirect:/game/" + gameId;

		// In the webapp, we'll only allow player 1 to call this.
		if (!isUserThisPlayer(authenticatedUser, game.getPlayer1())) {
			throw new AccessDeniedException("Only the first player in a game may invite an opponent.");
		}

		// Try to invite the specified player as an opponent.
		try {
			gameClient.inviteOpponent(gameId, playerId);
		} catch (GameConflictException e) {
			// Catch these errors and display them in a friendlier fashion.
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, e.getType().name());
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameId;
	}

	/**
	 * The controller facade for {@link IGameResource#setMaxRounds(String, int, int)}.
	 *
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param oldMaxRoundsValue
	 *            the current/old value of {@link Game#getMaxRounds()} (used to help prevent synchronization issues)
	 * @param newMaxRoundsValue
	 *            the new value for {@link Game#getMaxRounds()}
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to pass flash attributes around
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/{gameId}/setMaxRounds", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public String setMaxRounds(@PathVariable String gameId, @RequestParam int oldMaxRoundsValue,
			@RequestParam int newMaxRoundsValue, Principal authenticatedUser, RedirectAttributes redirectAttributes) {
		// Load the specified game.
		GameView game = loadGame(gameId);

		// In the webapp, we'll only allow actual players to adjust this.
		if (!isUserThisPlayer(authenticatedUser, game.getPlayer1())
				&& !isUserThisPlayer(authenticatedUser, game.getPlayer2())) {
			throw new AccessDeniedException("Only players in a game may adjust the number of rounds.");
		}

		// Try to set the number of rounds.
		try {
			gameClient.setMaxRounds(gameId, oldMaxRoundsValue, newMaxRoundsValue);
		} catch (GameConflictException e) {
			// Catch these errors and display them in a friendlier fashion.
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_WARNING_TYPE, e.getType().name());
		}

		// Redirect the user to the updated game.
		return "redirect:/game/" + gameId;
	}

	/**
	 * @param gameId
	 *            the {@link Game#getId()} to match against
	 * @return the specified {@link GameView}, as returned by {@link #gameClient}
	 */
	private GameView loadGame(String gameId) {
		GameView game = null;
		try {
			game = gameClient.getGame(gameId);
		} catch (NotFoundException e) {
			throw new GameNotFoundException(e);
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
	private ModelAndView buildGameModelAndView(Locale locale, Principal authenticatedUser, GameView game) {
		ModelAndView modelAndView = new ModelAndView("game");

		// Add the Game to the model.
		modelAndView.addObject("game", game);

		/*
		 * Determine Player display order. Always display current user first, if they're a Player.
		 */
		Player firstPlayer = isUserThisPlayer(authenticatedUser, game.getPlayer2()) ? game.getPlayer2()
				: game.getPlayer1();
		modelAndView.addObject("firstPlayer", firstPlayer);
		modelAndView.addObject("firstPlayerScore", game.getScoreForPlayer(firstPlayer));
		Player secondPlayer = game.getPlayer1().equals(firstPlayer) ? game.getPlayer2() : game.getPlayer1();
		modelAndView.addObject("secondPlayer", secondPlayer);
		modelAndView.addObject("secondPlayerScore", game.getScoreForPlayer(secondPlayer));

		// Add some display-related data to the model.
		modelAndView.addObject("currentAdjustedRoundIndex", game.getRounds().isEmpty() ? 0
				: game.getRounds().get(game.getRounds().size() - 1).getAdjustedRoundIndex());

		/*
		 * The following model entries are ONLY used for display purposes. They are not and should not be used for any
		 * sort of real/actual access control.
		 */
		boolean isPlayer1 = isUserThisPlayer(authenticatedUser, game.getPlayer1());
		boolean isPlayer2 = isUserThisPlayer(authenticatedUser, game.getPlayer2());
		boolean isPlayer = isPlayer1 || isPlayer2;
		modelAndView.addObject("isPlayer", isPlayer);

		// Setup some winner/loser properties.
		boolean hasWinner = game.getWinner() != null;
		modelAndView.addObject("isUserTheWinner",
				hasWinner && isPlayer && isUserThisPlayer(authenticatedUser, game.getWinner()));
		modelAndView.addObject("isUserTheLoser",
				hasWinner && isPlayer && !isUserThisPlayer(authenticatedUser, game.getWinner()));

		// Collect and add the AI players.
		Set<Player> aiPlayersSet = playersClient.getPlayersForBuiltInAis(BuiltInAi.active());
		List<Player> aiPlayersList = new ArrayList<>(aiPlayersSet);
		Collections.sort(aiPlayersList, new Comparator<Player>() {
			/**
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Player o1, Player o2) {
				/*
				 * This is hacky, but it gives the AIs a stable order that happens to be from least-->most difficult.
				 */
				Integer ai1Ordinal = o1.getBuiltInAi().ordinal();
				Integer ai2Ordinal = o2.getBuiltInAi().ordinal();
				return ai1Ordinal.compareTo(ai2Ordinal);
			}
		});
		modelAndView.addObject("aiPlayers", aiPlayersList);

		return modelAndView;
	}

	/**
	 * @param authenticatedUser
	 *            the user/client {@link Principal} that made the request, or <code>null</code> if the request was
	 *            unauthenticated
	 * @param player
	 *            the {@link Player} to check against, or <code>null</code> if the player hasn't joined the game yet
	 * @return <code>true</code> if the specified {@link Principal} is the specified {@link Player}, <code>false</code>
	 *         if not
	 */
	private static boolean isUserThisPlayer(Principal authenticatedUser, Player player) {
		/*
		 * This is a bit tricky. Because we're using Spring Security, we can expect that the Principal instances passed
		 * to any controller methods will be Authentication instances. From that Authentication, we can extract the
		 * actual/underlying principal. Given this application's SecurityConfig, that should always be an Account.
		 */
		Object actualPrincipal = null;
		if (authenticatedUser == null)
			return false;
		else if (authenticatedUser instanceof Authentication)
			actualPrincipal = ((Authentication) authenticatedUser).getPrincipal();
		else
			throw new BadCodeMonkeyException("Unhandled security object: " + authenticatedUser);

		if (player == null)
			return false;
		if (player.getHumanAccount() == null)
			/*
			 * AI players will not & cannot use the web app; they must use the web service or Java API.
			 */
			return false;

		return actualPrincipal.equals(player.getHumanAccount());
	}
}
