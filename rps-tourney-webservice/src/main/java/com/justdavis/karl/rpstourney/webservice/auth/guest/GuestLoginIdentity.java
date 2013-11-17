package com.justdavis.karl.rpstourney.webservice.auth.guest;

import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.webservice.auth.LoginProvider;

/**
 * <p>
 * The {@link ILoginIdentity} implementation for {@link LoginProvider#GUEST}
 * logins.
 * </p>
 * <p>
 * Each {@link GuestLoginIdentity} instance is basically just a refernce to its
 * associated {@link Account}. The {@link Account}s for
 * {@link GuestLoginIdentity}s are often created automatically and are thus
 * pretty much blank, unless/until the user fleshes them out, e.g. by providing
 * their name.
 * </p>
 */
public final class GuestLoginIdentity implements ILoginIdentity {
	private final Account account;

	/**
	 * Constructs a new {@link GuestLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 */
	public GuestLoginIdentity(Account account) {
		this.account = account;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getLoginProvider()
	 */
	@Override
	public LoginProvider getLoginProvider() {
		return LoginProvider.GUEST;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getAccount()
	 */
	@Override
	public Account getAccount() {
		return this.account;
	}
}
