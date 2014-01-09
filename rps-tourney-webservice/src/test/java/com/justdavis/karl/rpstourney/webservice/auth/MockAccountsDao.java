package com.justdavis.karl.rpstourney.webservice.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.threeten.bp.Clock;

/**
 * A mock {@link IAccountsDao} implementation for use in tests. Stores
 * {@link Account} instances in-memory.
 */
public final class MockAccountsDao implements IAccountsDao {
	public final List<Account> accounts = new ArrayList<>();

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#save(com.justdavis.karl.rpstourney.webservice.auth.Account)
	 */
	@Override
	public void save(Account account) {
		if (!accounts.contains(account))
			accounts.add(account);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#getAccounts()
	 */
	@Override
	public List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#getAccount(java.util.UUID)
	 */
	@Override
	public Account getAccount(UUID authTokenValue) {
		for (Account account : accounts)
			for (AuthToken authToken : account.getAuthTokens())
				if (authToken.getToken().equals(authTokenValue))
					return account;

		return null;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.IAccountsDao#selectOrCreateAuthToken(com.justdavis.karl.rpstourney.webservice.auth.Account)
	 */
	@Override
	public AuthToken selectOrCreateAuthToken(Account account) {
		if (account.getAuthTokens().size() > 1)
			throw new IllegalStateException("not yet supported");

		if (!account.getAuthTokens().isEmpty())
			return account.getAuthTokens().iterator().next();

		AuthToken authToken = new AuthToken(account, UUID.randomUUID(), Clock
				.systemUTC().instant());
		account.getAuthTokens().add(authToken);

		return authToken;
	}
}
