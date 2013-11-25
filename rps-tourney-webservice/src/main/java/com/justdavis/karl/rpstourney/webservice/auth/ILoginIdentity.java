package com.justdavis.karl.rpstourney.webservice.auth;

/**
 * <p>
 * Each {@link ILoginIdentity} implementation encapsulates the information
 * related to one of a user's various logins to the system, for a given
 * {@link LoginProvider}. There should be a 1:1 relationship between
 * {@link ILoginIdentity} implementations and {@link LoginProvider}s.
 * </p>
 * <p>
 * Each implementation must also comply with the following requirements:
 * </p>
 * <ul>
 * <li>It must provide support for marshalling and unmarshalling via JAX-B.</li>
 * <li>TODO: anything else?</li>
 * </ul>
 */
public interface ILoginIdentity {
	/**
	 * Returns the {@link Account} instance associated with this
	 * {@link ILoginIdentity} instance. There is an N:1 relationship between
	 * {@link ILoginIdentity} instances and {@link Account} instances: every
	 * {@link ILoginIdentity} must have exactly one associated {@link Account}
	 * instance, while any given {@link Account} must have one or more
	 * associated {@link ILoginIdentity} instances.
	 * 
	 * @return the {@link Account} instance associated with this
	 *         {@link ILoginIdentity} instance
	 */
	Account getAccount();

	/**
	 * @return the {@link LoginProvider} associated with this
	 *         {@link ILoginIdentity} implementation
	 */
	LoginProvider getLoginProvider();
}