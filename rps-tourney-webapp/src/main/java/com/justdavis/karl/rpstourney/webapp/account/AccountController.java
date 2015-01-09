package com.justdavis.karl.rpstourney.webapp.account;

import java.security.Principal;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;
import com.justdavis.karl.rpstourney.service.api.game.Game;

/**
 * The {@link Controller} for the site's "My Account" page.
 */
@Controller
@RequestMapping("/account")
public class AccountController {
	private final IAccountsResource accountsClient;

	/**
	 * Constructs a new {@link AccountController} instance.
	 * 
	 * @param accountsClient
	 *            the {@link IAccountsResource} client to use
	 */
	@Inject
	public AccountController(IAccountsResource accountsClient) {
		this.accountsClient = accountsClient;
	}

	/**
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal}
	 * @return a {@link ModelAndView} that can be used to render some basic
	 *         information about the current user
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getPage(Principal authenticatedUser) {
		/*
		 * If a client requests this page without having authenticated, we want
		 * to redirect them to the login page.
		 */
		if (authenticatedUser == null)
			return new ModelAndView("redirect:/login");

		// Grab the Account via the web service.
		Account authenticatedAccount = accountsClient.getAccount();

		// Build the model for the account.jsp view.
		ModelAndView modelAndView = new ModelAndView("account");
		modelAndView.addObject("account", authenticatedAccount);

		return modelAndView;
	}

	/**
	 * Accepts form submissions from <code>account.jsp</code>, allowing users to
	 * make changes to their {@link Account}.
	 * 
	 * @param gameId
	 *            the {@link Game#getId()} of the game being updated
	 * @param inputName
	 *            the new value for {@link Account#getName()}
	 * @param authenticatedUser
	 *            the currently logged in user {@link Principal} (whose
	 *            {@link Account#getName()} is to be updated)
	 * @return a <code>redirect:</code> view name for the updated {@link Game}
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView updateAccount(Principal authenticatedUser,
			String inputName) {
		/*
		 * If a client requests this page without having authenticated, we want
		 * to redirect them to the login page.
		 */
		if (authenticatedUser == null)
			return new ModelAndView("redirect:/login");

		// Update the user's Account.
		Account account = accountsClient.getAccount();
		account.setName(inputName);
		accountsClient.updateAccount(account);

		// Redirect the user to the updated game.
		return new ModelAndView("redirect:/account");
	}
}
