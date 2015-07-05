package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;

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

/**
 * <p>
 * A JSP tag handler that provides the
 * <code>&lt;rps:gameTitle game="${someGame}" /&gt;</code> tag, for printing out
 * the title/display label to use for {@link GameView}s.
 * </p>
 * <p>
 * Please note that this class and its properties must be correctly listed in
 * this project's <code>src/main/webapp/WEB-INF/rps.tld</code> file.
 * </p>
 */
public final class GameTitleTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 6618966382086544238L;

	private GameView game;
	private String variableName;
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
	 * <p>
	 * This is passed in as an optional tag attribute.
	 * </p>
	 * <p>
	 * If this value is set, this tag will not render any page output.
	 * </p>
	 * 
	 * @param var
	 *            the name of the page variable whose value will be set to this
	 *            {@link GameTitleTag}'s output
	 */
	public void setVar(String variableName) {
		this.variableName = variableName;
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
		String content = generateContent(variableName != null);

		if (variableName != null) {
			// Pass the value out as a variable.
			pageContext.setAttribute(variableName, content);
		} else {
			// Write out the tag's output.
			try {
				pageContext.getOut().write(content);
			} catch (IOException e) {
				throw new JspTagException(e);
			}
		}

		return EVAL_PAGE;
	}

	/**
	 * @param textOnly
	 *            if <code>true</code>, only text will be returned, if
	 *            <code>false</code>, the result will include HTML markup
	 * @return the text, tags, etc. to be rendered
	 */
	private String generateContent(boolean textOnly) {
		// If no Game was provided, just print out nothing.
		if (game == null)
			return null;

		// Determine the order to list the Players in.
		Player firstPlayer;
		Player secondPlayer;
		Account authenticatedAccount = getAuthenticatedAccount();
		if (game.isPlayer(authenticatedAccount)) {
			firstPlayer = game.getPlayer(authenticatedAccount);
			secondPlayer = game.getPlayer1().equals(firstPlayer) ? game
					.getPlayer2() : game.getPlayer1();
		} else {
			firstPlayer = game.getPlayer1();
			secondPlayer = game.getPlayer2();
		}

		// Build the display tags for each Player.
		String firstPlayerName = PlayerNameTag.generateContent(messageSource,
				pageContext.getELContext().getLocale(), authenticatedAccount,
				game, firstPlayer, textOnly);
		String secondPlayerName = PlayerNameTag.generateContent(messageSource,
				pageContext.getELContext().getLocale(), authenticatedAccount,
				game, secondPlayer, textOnly);

		// Return the result.
		String versusSeparator = messageSource.getMessage("gameTitle.versus",
				null, pageContext.getELContext().getLocale());
		String result = String.format("%s %s %s", firstPlayerName,
				versusSeparator, secondPlayerName);
		return result;
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
