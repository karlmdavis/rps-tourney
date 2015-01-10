package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;

/**
 * <p>
 * A JSP tag handler that provides the
 * <code>&lt;rps:gameOpponent game="${someGame}" /&gt;</code> tag, for printing
 * out a user's opponent in a {@link GameView}.
 * </p>
 * <p>
 * Please note that this class and its properties must be correctly listed in
 * this project's <code>src/main/webapp/WEB-INF/rps.tld</code> file.
 * </p>
 */
public final class GameOpponentTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -2870916586936080691L;

	private GameView game;
	private SecurityContext mockSecurityContext;

	/**
	 * @param game
	 *            the {@link GameView} to be rendered
	 */
	public void setGame(GameView value) {
		this.game = value;
	}

	/**
	 * @return the {@link SecurityContext} that should be used, to determine the
	 *         logged in user for the request being processed
	 */
	private SecurityContext getSecurityContext() {
		if (mockSecurityContext != null)
			return mockSecurityContext;
		else
			return SecurityContextHolder.getContext();
	}

	/**
	 * @param mockSecurityContext
	 *            the mock {@link SecurityContext} to use
	 */
	void setMockSecurityContext(SecurityContext securityContext) {
		/*
		 * Note: the lack of @Inject here is intentional, as Spring doesn't bind
		 * or inject SecurityContext instances.
		 */
		this.mockSecurityContext = securityContext;
	}

	/**
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			printOutOpponent();
		} catch (IOException e) {
			throw new JspTagException(e);
		}

		return EVAL_PAGE;
	}

	/**
	 * Does the heavy lifting of writing out the current user's opponent in the
	 * configured {@link #game}.
	 * 
	 * @throws IOException
	 *             Any {@link IOException}s encountered while writing out to the
	 *             page will be bubbled up.
	 */
	private void printOutOpponent() throws IOException {
		// If no Game was provided, just print out nothing.
		if (game == null)
			return;

		// If the user isn't logged in, this tag can't do anything.
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			return;

		// Determine the players, and deduce the opponent.
		Account opponent = determineOpponent(game, authenticatedAccount);

		// Determine what to write out.
		String result;
		if (opponent == null)
			result = getRequestContext().getMessage("game.opponent.none");
		else if (opponent.getName() != null)
			result = opponent.getName();
		else
			result = getRequestContext().getMessage("game.opponent.anonymous");

		// Write out things to the page.
		pageContext.getOut().write(result);
	}

	/**
	 * @return the {@link Account} of the currently-authenticated user, or
	 *         <code>null</code> if the user isn't logged in
	 */
	private Account getAuthenticatedAccount() {
		// Get the Authentication token for the current user (if any).
		Authentication auth = getSecurityContext().getAuthentication();
		if (auth == null)
			return null;

		/*
		 * Grab the Principal from the token. All of the tokens used in this app
		 * should have a principal.
		 */
		Object principal = auth.getPrincipal();
		if (principal == null)
			throw new BadCodeMonkeyException();

		/*
		 * All of the tokens used in this app should use an Account as
		 * principal.
		 */
		if (principal != null && !(principal instanceof Account))
			throw new BadCodeMonkeyException();
		Account authenticatedAccount = (Account) auth.getPrincipal();

		return authenticatedAccount;
	}

	/**
	 * @param game
	 *            the {@link GameView} to find the user's opponent in
	 * @param authenticatedAccount
	 *            the {@link Account} of the user/{@link Player} to find the
	 *            opponent for
	 * @return the {@link Account} of the specified user's opponent in the
	 *         specified {@link Game}, or <code>null</code> if no opponent could
	 *         be determined
	 */
	private static Account determineOpponent(GameView game,
			Account authenticatedAccount) {
		// If the user isn't logged in, we can't determine an opponent.
		if (authenticatedAccount == null)
			return null;

		Account player1Account = getPlayerAccount(game.getPlayer1());
		Account player2Account = getPlayerAccount(game.getPlayer2());
		Account opponent;
		if (authenticatedAccount.equals(player1Account)
				&& player2Account != null)
			opponent = player2Account;
		else if (authenticatedAccount.equals(player2Account)
				&& player1Account != null)
			opponent = player1Account;
		else
			opponent = null;
		return opponent;
	}

	/**
	 * Just a <code>null</code>-safe wrapper for
	 * {@link Player#getHumanAccount()}.
	 * 
	 * @param player
	 *            the {@link Player} to get the {@link Player#getHumanAccount()}
	 *            value from
	 * @return the value of {@link Player#getHumanAccount()}, or
	 *         <code>null</code> if the specified {@link Player} was
	 *         <code>null</code>
	 */
	private static Account getPlayerAccount(Player player) {
		if (player == null)
			return null;

		return player.getHumanAccount();
	}
}
