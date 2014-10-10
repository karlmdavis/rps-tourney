package com.justdavis.karl.rpstourney.service.app.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.threeten.bp.Clock;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;

/**
 * A mock {@link IAccountsDao} implementation for use in tests. Stores
 * {@link Account} instances in-memory.
 */
public final class MockAccountsDao implements IAccountsDao {
	public final List<Account> accounts = new ArrayList<>();

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.IAccountsDao#save(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public void save(Account account) {
		if (!accounts.contains(account))
			accounts.add(account);
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.IAccountsDao#merge(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public Account merge(Account account) {
		if (!accounts.contains(account))
			throw new IllegalArgumentException();

		accounts.remove(account);
		accounts.add(account);

		return account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.IAccountsDao#getAccounts()
	 */
	@Override
	public List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.IAccountsDao#getAccountById(long)
	 */
	@Override
	public Account getAccountById(long id) {
		for (Account account : accounts)
			if (account.getId() == id)
				return account;

		return null;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.IAccountsDao#getAccountByAuthToken(java.util.UUID)
	 */
	@Override
	public Account getAccountByAuthToken(UUID authTokenValue) {
		for (Account account : accounts)
			for (AuthToken authToken : account.getAuthTokens())
				if (authToken.getToken().equals(authTokenValue))
					return account;

		return null;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.auth.IAccountsDao#selectOrCreateAuthToken(com.justdavis.karl.rpstourney.service.api.auth.Account)
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
