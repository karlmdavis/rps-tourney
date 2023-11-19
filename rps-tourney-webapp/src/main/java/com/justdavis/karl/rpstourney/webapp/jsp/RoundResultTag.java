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
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;

/**
 * <p>
 * A JSP tag handler that provides the <code>&lt;rps:roundResult /&gt;</code> tag, for printing out the winner/result
 * for the specified {@link GameRound}.
 * </p>
 * <p>
 * Please note that this class and its properties must be correctly listed in this project's
 * <code>src/main/webapp/WEB-INF/rps.tld</code> file.
 * </p>
 */
public final class RoundResultTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -2870916586936080691L;

	private boolean initialized;

	private SecurityContext mockSecurityContext;
	private MessageSource messageSource;
	private GameView game;
	private GameRound round;

	/**
	 * Constructs a new {@link RoundResultTag} instance.
	 */
	public RoundResultTag() {
		this.initialized = false;

		this.mockSecurityContext = null;
		this.messageSource = null;
	}

	/**
	 * @return the {@link SecurityContext} that should be used, to determine the logged in user for the request being
	 *         processed
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
		 * Note: the lack of @Inject here is intentional, as Spring doesn't bind or inject SecurityContext instances.
		 */
		this.mockSecurityContext = securityContext;
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
	 * @param game
	 *            the {@link GameView} that the specified {@link GameRound} is part of
	 */
	public void setGame(GameView game) {
		this.game = game;
	}

	/**
	 * @param round
	 *            the {@link GameRound} to display the {@link GameRound#getResult()} of
	 */
	public void setRound(GameRound round) {
		this.round = round;
	}

	/**
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {
		if (!initialized) {
			/*
			 * If we haven't already initialized ourselves, inject dependencies into this instance now.
			 */
			ApplicationContext applicationContext = WebApplicationContextUtils
					.getWebApplicationContext(pageContext.getServletContext());
			applicationContext.getAutowireCapableBeanFactory().autowireBean(this);

			this.initialized = true;
		}

		return EVAL_BODY_INCLUDE;
	}

	/**
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		String content = generateContent();

		// Write out the tag's output.
		try {
			pageContext.getOut().write(content);
		} catch (IOException e) {
			throw new JspTagException(e);
		}

		return EVAL_PAGE;
	}

	/**
	 * @return the text, tags, etc. to be rendered
	 */
	private String generateContent() {
		// If no GameRound was provided, just print out nothing.
		if (round == null)
			return null;

		// Grab some of the simple variables we'll be using.
		Locale locale = pageContext.getELContext().getLocale();
		Account authenticatedAccount = getAuthenticatedAccount();

		// Determine the Players that won and lost the round (if any).
		Player roundWinner = null;
		Player roundLoser = null;
		if (round.getResult() == Result.PLAYER_1_WON) {
			roundWinner = game.getPlayer1();
			roundLoser = game.getPlayer2();
		} else if (round.getResult() == Result.PLAYER_2_WON) {
			roundWinner = game.getPlayer2();
			roundLoser = game.getPlayer1();
		}

		// Determine whether or not the current user won or lost.
		String wonOrLostClass = "";
		if (game.isPlayer(authenticatedAccount) && game.getPlayer(authenticatedAccount).equals(roundWinner))
			wonOrLostClass = "won";
		else if (game.isPlayer(authenticatedAccount) && game.getPlayer(authenticatedAccount).equals(roundLoser))
			wonOrLostClass = "lost";

		// Calculate the tag to return.
		if (round.getResult() == Result.PLAYER_1_WON || round.getResult() == Result.PLAYER_2_WON) {
			String winnerNameTag = PlayerNameTag.generateContent(messageSource, locale, authenticatedAccount, game,
					roundWinner, false);
			return String.format("<span class=\"%s\">%s</span>", wonOrLostClass, winnerNameTag);
		} else if (round.getResult() == Result.TIED) {
			return messageSource.getMessage("roundResult.tied", null, locale);
		} else {
			return messageSource.getMessage("roundResult.none", null, locale);
		}
	}

	/**
	 * @return the {@link Account} of the currently-authenticated user, or <code>null</code> if the user isn't logged in
	 */
	private Account getAuthenticatedAccount() {
		// Get the Authentication token for the current user (if any).
		Authentication auth = getSecurityContext().getAuthentication();
		if (auth == null)
			return null;

		/*
		 * Grab the Principal from the token. All of the tokens used in this app should have a principal.
		 */
		Object principal = auth.getPrincipal();
		if (principal == null)
			throw new BadCodeMonkeyException();

		/*
		 * All of the tokens used in this app should use an Account as principal.
		 */
		if (principal != null && !(principal instanceof Account))
			throw new BadCodeMonkeyException();
		Account authenticatedAccount = (Account) auth.getPrincipal();

		return authenticatedAccount;
	}
}
