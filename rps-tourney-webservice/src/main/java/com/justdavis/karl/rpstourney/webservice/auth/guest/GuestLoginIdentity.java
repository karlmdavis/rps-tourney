package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.webservice.auth.LoginProvider;

/**
 * <p>
 * The {@link ILoginIdentity} implementation for {@link LoginProvider#GUEST}
 * logins.
 * </p>
 * <p>
 * Each {@link GuestLoginIdentity} instance is basically just a {@link UUID}
 * with an associated {@link Account}. The {@link Account}s for
 * {@link GuestLoginIdentity}s are often created automatically and are thus
 * pretty much blank, unless/until the user fleshes them out, e.g. by providing
 * their name.
 * </p>
 */
@XmlRootElement(name = "guestLogin")
public final class GuestLoginIdentity implements ILoginIdentity {
	@XmlElement
	private final Account account;
	@XmlAttribute
	private final UUID authToken;

	/**
	 * This no-arg/default constructor is required by JAX-B.
	 */
	@SuppressWarnings("unused")
	private GuestLoginIdentity() {
		this.account = null;
		this.authToken = null;
	}

	/**
	 * Constructs a new {@link GuestLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 * @param authToken
	 *            the value to use for {@link #getAuthToken()}
	 */
	public GuestLoginIdentity(Account account, UUID authToken) {
		this.account = account;
		this.authToken = authToken;
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

	/**
	 * @return the {@link UUID} that uniquely identifies/references this
	 *         {@link GuestLoginIdentity} instance
	 */
	public UUID getAuthToken() {
		return this.authToken;
	}
}
