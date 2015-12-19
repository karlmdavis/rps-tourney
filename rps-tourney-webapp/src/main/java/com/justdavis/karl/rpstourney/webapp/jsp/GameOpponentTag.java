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
	private MessageSource messageSource;
	private boolean initialized;

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
		// If no Game was provided, just print out nothing.
		if (game == null)
			return null;

		// If the user isn't logged in, this tag can't do anything.
		Account authenticatedAccount = getAuthenticatedAccount();
		if (authenticatedAccount == null)
			return null;

		// If the user isn't one of the Players, this tag can't do anything.
		if (!game.isPlayer(authenticatedAccount))
			return null;

		// Calculate the content.
		String content = PlayerNameTag.generateContent(messageSource, pageContext.getELContext().getLocale(),
				authenticatedAccount, game, game.determineOpponent(authenticatedAccount), false);

		return content;
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
