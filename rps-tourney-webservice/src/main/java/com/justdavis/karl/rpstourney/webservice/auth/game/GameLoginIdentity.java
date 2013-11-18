package com.justdavis.karl.rpstourney.webservice.auth.game;

import javax.mail.internet.InternetAddress;

import com.justdavis.karl.rpstourney.webservice.auth.Account;
import com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.webservice.auth.LoginProvider;

/**
 * <p>
 * The {@link ILoginIdentity} implementation for {@link LoginProvider#GAME}
 * logins.
 * </p>
 * <p>
 * Each {@link GameLoginIdentity} instance is basically just a username (always
 * an email address) and password hash, with an associated {@link Account}. When
 * creating one of these accounts, the game UI should also prompt users to flesh
 * out some of the {@link Account} details, e.g. their name, though that's not
 * required.
 * </p>
 */
public final class GameLoginIdentity implements ILoginIdentity {
	private final Account account;
	private final InternetAddress emailAddress;
	private final String passwordHash;

	/**
	 * Constructs a new {@link GameLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 * @param emailAddress
	 *            the value to use for {@link #getEmailAddress()}
	 * @param passwordHash
	 *            the value to use for {@link #getPasswordHash()}
	 */
	public GameLoginIdentity(Account account, InternetAddress emailAddress,
			String passwordHash) {
		this.account = account;
		this.emailAddress = emailAddress;
		this.passwordHash = passwordHash;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getLoginProvider()
	 */
	@Override
	public LoginProvider getLoginProvider() {
		return LoginProvider.GAME;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getAccount()
	 */
	@Override
	public Account getAccount() {
		return account;
	}

	/**
	 * @return the email address that the user's account is tied to
	 */
	public InternetAddress getEmailAddress() {
		return emailAddress;
	}

	/**
	 * <p>
	 * Returns the scrypt hash of the user's password that is stored for
	 * authentication purposes.
	 * </p>
	 * <p>
	 * In general, this should never be sent off of the server.
	 * </p>
	 * 
	 * @return the scrypt hash of the user's password
	 * @see GameAuthService#hashPassword(String)
	 * @see GameAuthService#checkPassword(String, GameLoginIdentity)
	 */
	public String getPasswordHash() {
		return passwordHash;
	}
}
