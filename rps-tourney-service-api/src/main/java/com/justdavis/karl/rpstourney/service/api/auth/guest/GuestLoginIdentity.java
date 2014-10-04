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
@Table(name = "`GuestLoginIdentities`")
public class GuestLoginIdentity implements ILoginIdentity {
	/*
	 * FIXME Would rather use GenerationType.IDENTITY, but can't, due to
	 * https://hibernate.atlassian.net/browse/HHH-9430.
	 */
	/*
	 * FIXME Would rather sequence name was mixed-case, but it can't be, due to
	 * https://hibernate.atlassian.net/browse/HHH-9431.
	 */
	@Id
	@Column(name = "`id`", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GuestLoginIdentities_id_seq")
	@SequenceGenerator(name = "GuestLoginIdentities_id_seq", sequenceName = "guestloginidentities_id_seq")
	private long id;

	@OneToOne(optional = false, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
	@JoinColumn(name = "`accountId`")
	private Account account;

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
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	GuestLoginIdentity() {
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.auth.ILoginIdentity#getLoginProvider()
	 */
	@Override
	public LoginProvider getLoginProvider() {
		return LoginProvider.GUEST;
	}

	/**
	 * @return <code>true</code> if this {@link GuestLoginIdentity} has been
	 *         assigned an ID (which it should if it's been persisted),
	 *         <code>false</code> if it has not
	 */
	public boolean hasId() {
		return id > 0;
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
		if (!hasId())
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
