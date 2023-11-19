package com.justdavis.karl.rpstourney.service.api.auth;

import java.util.UUID;

/**
 * A mock {@link IAccountsResource} implementation for use in tests.
 */
public class MockAccountsClient implements IAccountsResource {
	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#validateAuth()
	 */
	@Override
	public Account validateAuth() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#getAccount()
	 */
	@Override
	public Account getAccount() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#updateAccount(com.justdavis.karl.rpstourney.service.api.auth.Account)
	 */
	@Override
	public Account updateAccount(Account accountToUpdate) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#selectOrCreateAuthToken()
	 */
	@Override
	public AuthToken selectOrCreateAuthToken() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource#mergeAccount(long, java.util.UUID)
	 */
	@Override
	public void mergeAccount(long targetAccountId, UUID sourceAccountAuthTokenValue) {
		throw new UnsupportedOperationException();
	}
}
