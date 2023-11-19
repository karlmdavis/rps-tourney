package com.justdavis.karl.rpstourney.webapp.account;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.client.HttpClientException;

/**
 * The {@link Controller} for the site's account registration/signup page.
 */
@Controller
@RequestMapping("/register")
public class RegisterController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

	/**
	 * The {@link RedirectAttributes#getFlashAttributes()} key used to store errors/warnings that should be displayed to
	 * the user.
	 */
	private static final String FLASH_ATTRIB_MESSAGE_TYPE = "messageType";

	private final IAccountsResource accountsClient;
	private final IGameAuthResource gameAuthClient;

	/**
	 * Constructs a new {@link RegisterController} instance.
	 *
	 * @param accountsClient
	 *            the {@link IAccountsResource} client to use
	 * @param gameAuthClient
	 *            the {@link IAccountsResource} client to use
	 */
	@Inject
	public RegisterController(IAccountsResource accountsClient, IGameAuthResource gameAuthClient) {
		this.accountsClient = accountsClient;
		this.gameAuthClient = gameAuthClient;
	}

	/**
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @return a {@link ModelAndView} that can be used to render some basic information about the current user
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getPage(Principal authenticatedUser) {
		// Grab the Account (if any) via the web service.
		Account authenticatedAccount = null;
		if (authenticatedUser != null) {
			authenticatedAccount = accountsClient.getAccount();

			/*
			 * Does the user's account already have an associated (non-anonymous) login? If so, perhaps the best thing
			 * to do is redirect the user to their account details.
			 */
			if (!authenticatedAccount.isAnonymous())
				return new ModelAndView("redirect:/account");
		}

		/*
		 * Just a note: If authenticatedAccount is not null and we've reached this point, it means that the user has a
		 * pre-existing anonymous login that will need to be merged with any new login that they create.
		 */

		// Build the model for the account.jsp view.
		ModelAndView modelAndView = new ModelAndView("register");
		modelAndView.addObject("account", authenticatedAccount);

		return modelAndView;
	}

	/**
	 * Accepts form submissions from <code>register.jsp</code>, allowing users to create a new {@link GameLoginIdentity}
	 * (and, if needed, an {@link Account}).
	 *
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal} (whose {@link Account#getName()} is to be updated)
	 * @param redirectAttributes
	 *            the Spring MVC {@link RedirectAttributes} that will be used to pass flash attributes around
	 * @param inputEmail
	 *            the email address that the user has entered
	 * @param inputPassword1
	 *            the password that the user has entered
	 * @param inputPassword2
	 *            the password confirmation that the user has entered (hopefully matches the other one)
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(method = { RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView registerLogin(Principal authenticatedUser, RedirectAttributes redirectAttributes,
			String inputEmail, String inputPassword1, String inputPassword2) {
		// Grab the Account (if any) via the web service.
		Account authenticatedAccount = null;
		if (authenticatedUser != null)
			authenticatedAccount = accountsClient.getAccount();

		/*
		 * Does the user's account already have an associated (non-anonymous) login? If so, they shouldn't have been
		 * able to view the form, much less submit it.
		 */
		if (authenticatedAccount != null && !authenticatedAccount.isAnonymous())
			throw new BadCodeMonkeyException("Unable to create second game login for user.");

		/*
		 * TODO This should be using data binding and JSR-303 validation, which can collect multiple validation failures
		 * at once. See http://www.journaldev .com/2668/spring-mvc-form-validation-example-using
		 * -annotation-and-custom-validator-implementation for an example.
		 */

		// Validate and convert the specified input.
		InternetAddress emailAddress;
		try {
			emailAddress = new InternetAddress(inputEmail, false);
		} catch (AddressException e) {
			/*
			 * The InternetAddress constructor will throw this exception if the address fails to parse correctly.
			 */
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_MESSAGE_TYPE, "emailParseFailure");
			return new ModelAndView("redirect:/register");
		}
		if (inputPassword1 != null && !inputPassword1.equals(inputPassword2)) {
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_MESSAGE_TYPE, "passwordMismatch");
			return new ModelAndView("redirect:/register");
		}

		/*
		 * Create the new login (and an Account if they didn't already have one).
		 */
		Account possiblyNewAccount;
		try {
			possiblyNewAccount = gameAuthClient.createGameLogin(emailAddress, inputPassword1);
		} catch (HttpClientException e) {
			LOGGER.warn("Client error creating login.", e);
			redirectAttributes.addFlashAttribute(FLASH_ATTRIB_MESSAGE_TYPE, "loginCreationFailure");
			return new ModelAndView("redirect:/register");
		}

		/*
		 * Create a Spring Security 'Authentication' token for the login and use it to programmatically login the
		 * new/updated account. This token will end up being saved in the session. The principal saved in the token will
		 * be passed to anything else that asks for the request's security/authorization principal.
		 */
		List<SimpleGrantedAuthority> grantedAuthorities = new LinkedList<>();
		for (SecurityRole role : possiblyNewAccount.getRoles())
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getId()));
		Authentication auth = new UsernamePasswordAuthenticationToken(possiblyNewAccount, null, grantedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(auth);

		// Redirect the user to the home page.
		return new ModelAndView("redirect:/");
	}
}
