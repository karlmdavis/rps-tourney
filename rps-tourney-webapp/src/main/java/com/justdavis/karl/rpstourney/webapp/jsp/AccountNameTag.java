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
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;

/**
 * <p>
 * A JSP tag handler that provides the <code>&lt;rps:accountName /&gt;</code>
 * tag, for printing out the {@link Account#getName()} value for the currently
 * authenticated user (if any), or "<code>Anonymous</code>" if not (or whatever
 * the localized version of that is).
 * </p>
 * <p>
 * Please note that this class and its properties must be correctly listed in
 * this project's <code>src/main/webapp/WEB-INF/rps.tld</code> file.
 * </p>
 */
public final class AccountNameTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -2870916586936080691L;

	private boolean initialized;

	private SecurityContext mockSecurityContext;
	private MessageSource messageSource;
	private IAccountsResource accountsClient;

	/**
	 * Constructs a new {@link AccountNameTag} instance.
	 */
	public AccountNameTag() {
		this.initialized = false;

		this.mockSecurityContext = null;
		this.messageSource = null;
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
	 * @param accountsClient
	 *            the {@link IAccountsResource} client to use
	 */
	@Inject
	public void setAccountsClient(IAccountsResource accountsClient) {
		this.accountsClient = accountsClient;
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
		try {
			pageContext.getOut().write(getAccountDisplayName());
		} catch (IOException e) {
			throw new JspTagException(e);
		}

		return EVAL_PAGE;
	}

	/**
	 * @return the current user's name, or whatever alternative should be
	 *         displayed instead
	 */
	private String getAccountDisplayName() throws IOException {
		// Determine the current authenticated account (if any).
		Account authenticatedAccount = getAuthenticatedAccount();

		// If the user is not authenticated, return the anon text.
		if (authenticatedAccount == null)
			return getAnonDisplayText();

		/*
		 * At this point, we know the user is authenticated. Even though we have
		 * an Account instance, that instance is old; it was populated when the
		 * user first authenticated the current session. We need to grab the
		 * current Account data to determine what the current Account.getName()
		 * value is.
		 */

		// Grab the latest Account.
		Account refreshedAccount = accountsClient.getAccount();

		// Return the name to display.
		if (refreshedAccount.getName() != null)
			return refreshedAccount.getName();
		else
			return getAnonDisplayText();
	}

	/**
	 * @return the text to display if the user is not authenticated, or if their
	 *         {@link Account#getName()} is <code>null</code>
	 */
	private String getAnonDisplayText() {
		Locale locale = pageContext.getELContext().getLocale();
		return messageSource.getMessage("accountName.anon", null, locale);
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
		System.out.println(principal);
		System.out.println(authenticatedAccount);

		return authenticatedAccount;
	}
}
