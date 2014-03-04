package com.justdavis.karl.rpstourney.webservice.auth;

import java.util.List;
import java.util.UUID;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;

/**
 * A DAO for {@link Account} and {@link AuthToken} JPA entities.
 */
public interface IAccountsDao {
	/**
	 * @param account
	 *            the {@link Account} instance to be inserted/updated in the
	 *            database
	 */
	void save(Account account);

	/**
	 * Note: This method is really only appropriate for use in testing; it's a
	 * bad idea to use it in production code.
	 * 
	 * @return all of the {@link Account} instances in the database
	 */
	List<Account> getAccounts();

	/**
	 * @param authTokenValue
	 *            the {@link AuthToken#getToken()} value to match against
	 *            {@link Account#getAuthTokens()}
	 * @return the {@link Account} instance with the specified
	 *         {@link Account#getAuthTokens()} {@link AuthToken#getToken()}
	 *         value, or <code>null</code> if no match was found
	 */
	Account getAccount(UUID authTokenValue);

	/**
	 * Selects the most recent active {@link AuthToken} for the specified
	 * {@link Account} or, if no such {@link AuthToken} exists, creates a new
	 * one, persists it, and returns that.
	 * 
	 * @param account
	 *            the {@link Account} to get an {@link AuthToken} for
	 * @return the most recent active {@link AuthToken} for the specified
	 *         {@link Account} (or a new one)
	 */
	AuthToken selectOrCreateAuthToken(Account account);
}
