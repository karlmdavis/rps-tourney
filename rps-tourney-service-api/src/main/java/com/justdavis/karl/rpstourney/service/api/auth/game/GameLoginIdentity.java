package com.justdavis.karl.rpstourney.service.api.auth.game;

import javax.mail.internet.InternetAddress;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
 * This class supports JPA. The JPA SQL-specific data (e.g. column names) is
 * specified in the <code>META-INF/orm.xml</code> file.
 * </p>
 */
@Entity
@Table(name = "\"GameLoginIdentities\"")
public final class GameLoginIdentity implements ILoginIdentity {
	@Id
	@Column(name = "\"id\"", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GameLoginIdentities_id_seq")
	@SequenceGenerator(name = "GameLoginIdentities_id_seq", sequenceName = "gameloginidentities_id_seq")
	private long id;

	@OneToOne(optional = false, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
	@JoinColumn(name = "\"accountId\"")
	private final Account account;

	@org.hibernate.annotations.Type(type = InternetAddressUserType.TYPE_NAME)
	@Column(name = "\"emailAddress\"", unique = true, nullable = false)
	private final InternetAddress emailAddress;

	@Column(name = "\"passwordHash\"", nullable = false)
	private final String passwordHash;

	/**
	 * This no-arg/default constructor is required by JPA.
	 */
	@SuppressWarnings("unused")
	private GameLoginIdentity() {
		this.id = -1;
		this.account = null;
		this.emailAddress = null;
		this.passwordHash = null;
	}

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
		this.id = -1;
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
	 * <p>
	 * Returns the unique integer that identifies and represents this
	 * {@link GameLoginIdentity} instance.
	 * </p>
	 * <p>
	 * This value will be assigned by JPA when the {@link Entity} is persisted.
	 * Until then, this value should not be accessed.
	 * </p>
	 * 
	 * @return the unique integer that identifies and represents this
	 *         {@link GameLoginIdentity} instance
	 */
	public long getId() {
		if (id == -1)
			throw new IllegalStateException("Field value not yet available.");

		return id;
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
