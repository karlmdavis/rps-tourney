package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.PlayerRole;

/**
 * <p>
 * A JSP tag handler that provides the
 * <code>&lt;rps:playerName game=${someGame} player="${player}" /&gt;</code>
 * tag, for printing out the {@link Player#getName()} value for the specified
 * {@link Player}, or a suitable set of "filler" text if that value is
 * <code>null</code>. Note that this tag will also render with CSS classes equal
 * to {@link PlayerRole#PLAYER_1} and/or {@link PlayerRole#PLAYER_2}, as
 * appropriate.
 * </p>
 * <p>
 * Please note that this class and its properties must be correctly listed in
 * this project's <code>src/main/webapp/WEB-INF/rps.tld</code> file.
 * </p>
 */
public final class PlayerNameTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 329694465757744778L;

	private GameView game;
	private Player player;
	private SecurityContext mockSecurityContext;
	private MessageSource messageSource;
	private boolean initialized;

	/**
	 * This is passed in as a required tag attribute.
	 * 
	 * @param game
	 *            the {@link GameView} to be rendered
	 */
	public void setGame(GameView game) {
		this.game = game;
	}

	/**
	 * This is passed in as a required tag attribute.
	 * 
	 * @param player
	 *            the {@link Player} to be rendered, which must be one of the
	 *            {@link Player}s in the value passed to
	 *            {@link #setGame(GameView)}, or must instead be
	 *            <code>null</code>, to indicate that the game's
	 *            {@link PlayerRole#PLAYER_2} has not yet joined
	 */
	public void setPlayer(Player player) {
		this.player = player;
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
	 * <strong>Warning:</strong> This method is only intended for use by unit
	 * tests.
	 * 
	 * @param mockSecurityContext
	 *            the mock {@link SecurityContext} to use
	 */
	void setMockSecurityContext(SecurityContext mockSecurityContext) {
		/*
		 * Note: the lack of @Inject here is intentional, as Spring doesn't bind
		 * or inject SecurityContext instances.
		 */
		this.mockSecurityContext = mockSecurityContext;
	}

	/**
	 * @param messageSource
	 *            the {@link MessageSource} to use
	 */
	@Inject
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {
		if (!initialized) {
			/*
			 * If we haven't already initialized ourselves, inject dependencies
			 * into this instance now.
			 */
			ApplicationContext applicationContext = WebApplicationContextUtils
					.getWebApplicationContext(pageContext.getServletContext());
			applicationContext.getAutowireCapableBeanFactory().autowireBean(
					this);

			this.initialized = true;
		}

		return EVAL_BODY_INCLUDE;
	}

	/**
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		String gameTitle = generateContent(messageSource, pageContext
				.getELContext().getLocale(), getAuthenticatedAccount(), game,
				player, false);

		// Write out the tag's output.
		try {
			pageContext.getOut().write(gameTitle);
		} catch (IOException e) {
			throw new JspTagException(e);
		}

		return EVAL_PAGE;
	}

	/**
	 * @param messageSource
	 *            the {@link MessageSource} to use
	 * @param locale
	 *            the {@link Locale} being rendered to
	 * @param authenticatedAccount
	 *            the currently-authenticated user {@link Account}, or
	 *            <code>null</code> if no user is authenticated
	 * @param game
	 *            the {@link GameView} to render the {@link Player} for
	 * @param player
	 *            the {@link Player} whose name is being rendered
	 * @param textOnly
	 *            if <code>true</code>, only text will be returned, if
	 *            <code>false</code>, the result will include HTML markup
	 * @return a rendered <code>&lt;span /&gt;</code> tag that wraps the
	 *         specified {@link Player}'s name, and includes CSS classes
	 *         indicating which {@link PlayerRole}s they represent
	 */
	static String generateContent(MessageSource messageSource, Locale locale,
			Account authenticatedAccount, GameView game, Player player,
			boolean textOnly) {
		// If no Game was provided, just print out nothing.
		if (game == null)
			return null;

		if (player == null && game.getPlayer2() == null) {
			// We're printing out a not-yet-joined Player 2.
			String displayName = messageSource.getMessage(
					"game.player.notJoined", null, locale);

			if (textOnly)
				return displayName;
			else
				return generateTag(displayName, PlayerRole.PLAYER_2);
		} else {
			// We should be printing out an already-joined Player...

			// If no Player was provided, just print out nothing.
			if (player == null)
				return null;

			// If the Player is not part of the Game, just print out nothing.
			if (!game.isPlayer(player.getHumanAccount()))
				return null;

			// Select the base display name.
			String displayName = player.getName() != null ? player.getName()
					: messageSource.getMessage("game.player.anonymous", null,
							locale);

			// Append a " (You)" indicator, if appropriate.
			if (player.getHumanAccount() != null
					&& player.getHumanAccount().equals(authenticatedAccount))
				displayName = displayName
						+ messageSource.getMessage(
								"game.player.current.suffix", null, locale);

			if (textOnly)
				return displayName;
			else
				return generateTag(displayName, game.getPlayerRoles(player));
		}
	}

	/**
	 * @param displayName
	 *            the display text for the {@link Player} being rendered by this
	 *            {@link PlayerNameTag}
	 * @param roles
	 *            the {@link PlayerRole}s represented by the {@link Player}
	 *            being rendered
	 * @return the generated tag output
	 */
	private static String generateTag(String displayName, PlayerRole... roles) {
		// Generate the 'class' attribute's value.
		StringBuilder classValue = new StringBuilder();
		for (int i = 0; i < roles.length; i++) {
			classValue.append(roles[i].toString());
			if (i < (roles.length - 1))
				classValue.append(' ');
		}

		return String.format("<span class=\"%s\">%s</span>",
				classValue.toString(), displayName);
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
}
