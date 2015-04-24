package com.justdavis.karl.rpstourney.service.client.auth;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.AuthToken;
import com.justdavis.karl.rpstourney.service.api.auth.IAccountsResource;

/**
 * <p>
 * Implementations of this interface act as factories for one-off/custom
 * {@link IAccountsResource} instances. In general, this interface should only
 * be used directly when there's a need for an {@link IAccountsResource}
 * instance with custom credentials. The rest of the time, there will generally
 * be a DI-provided {@link IAccountsResource} client available for use that is a
 * better choice.
 * </p>
 * <p>
 * This abstraction is needed, as some services will need the ability to create
 * new {@link IAccountsResource} clients, using the credentials from
 * {@link Account}s that are not currently logged in.
 * </p>
 */
public interface IAccountsClientFactory {
	/**
	 * @param authTokenForAccount
	 *            a valid {@link AuthToken#getToken()} (stringified) value for
	 *            the {@link Account} that the new {@link IAccountsResource}
	 *            client should be logged in as
	 * @return a new {@link IAccountsResource} client instance, which will
	 *         authenticate as the specified {@link Account}
	 */
	IAccountsResource createAccountsClient(String authTokenValueForAccount);
}
