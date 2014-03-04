package com.justdavis.karl.rpstourney.service.api.auth.guest;

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

/**
 * <p>
 * The {@link ILoginIdentity} implementation for {@link LoginProvider#GUEST}
 * logins.
 * </p>
 * <p>
 * Each {@link GuestLoginIdentity} instance is basically just a reference to its
 * associated {@link Account}. The {@link Account}s for
 * {@link GuestLoginIdentity}s are often created automatically and are thus
 * pretty much blank, unless/until the user fleshes them out, e.g. by providing
 * their name.
 * </p>
 * <p>
 * This class supports JPA. The JPA SQL-specific data (e.g. column names) is
 * specified in the <code>META-INF/orm.xml</code> file.
 * </p>
 */
@Entity
@Table(name = "\"GuestLoginIdentities\"")
public final class GuestLoginIdentity implements ILoginIdentity {
	@Id
	@Column(name = "\"id\"", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GuestLoginIdentities_id_seq")
	@SequenceGenerator(name = "GuestLoginIdentities_id_seq", sequenceName = "guestloginidentities_id_seq")
	private long id;

	@OneToOne(optional = false, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
	@JoinColumn(name = "\"accountId\"")
	private final Account account;

	/**
	 * This no-arg/default constructor is required by JPA.
	 */
	@SuppressWarnings("unused")
	private GuestLoginIdentity() {
		this.id = -1;
		this.account = null;
	}

	/**
	 * Constructs a new {@link GuestLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 */
	public GuestLoginIdentity(Account account) {
		this.id = -1;
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
	 * <p>
	 * Returns the unique integer that identifies and represents this
	 * {@link GuestLoginIdentity} instance.
	 * </p>
	 * <p>
	 * This value will be assigned by JPA when the {@link Entity} is persisted.
	 * Until then, this value should not be accessed.
	 * </p>
	 * 
	 * @return the unique integer that identifies and represents this
	 *         {@link GuestLoginIdentity} instance
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
		return this.account;
	}
}
