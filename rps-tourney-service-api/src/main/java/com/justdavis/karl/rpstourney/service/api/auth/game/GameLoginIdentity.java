package com.justdavis.karl.rpstourney.service.api.auth.game;

import javax.mail.internet.InternetAddress;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.justdavis.karl.rpstourney.service.api.auth.AbstractLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.LoginProvider;
import com.justdavis.karl.rpstourney.service.api.hibernate.InternetAddressUserType;

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
 * <p>
 * This class supports JPA.
 * </p>
 */
@Entity
@Table(name = "`GameLoginIdentities`")
@PrimaryKeyJoinColumn(name="`id`", referencedColumnName="`id`")
public class GameLoginIdentity extends AbstractLoginIdentity implements
		ILoginIdentity {
	@org.hibernate.annotations.Type(type = InternetAddressUserType.TYPE_NAME)
	@Column(name = "`emailAddress`", unique = true, nullable = false)
	private InternetAddress emailAddress;

	@Column(name = "`passwordHash`", nullable = false)
	private String passwordHash;

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
		super(account);

		this.emailAddress = emailAddress;
		this.passwordHash = passwordHash;
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	GameLoginIdentity() {
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getLoginProvider()
	 */
	@Override
	public LoginProvider getLoginProvider() {
		return LoginProvider.GAME;
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
