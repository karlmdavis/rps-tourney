package com.justdavis.karl.rpstourney.service.api.auth.guest;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.justdavis.karl.rpstourney.service.api.auth.AbstractLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.ILoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.LoginProvider;

/**
 * <p>
 * The {@link ILoginIdentity} implementation for {@link LoginProvider#GUEST} logins.
 * </p>
 * <p>
 * Each {@link GuestLoginIdentity} instance is basically just a reference to its associated {@link Account}. The
 * {@link Account}s for {@link GuestLoginIdentity}s are often created automatically and are thus pretty much blank,
 * unless/until the user fleshes them out, e.g. by providing their name.
 * </p>
 * <p>
 * This class supports JPA.
 * </p>
 */
@Entity
@Table(name = "`GuestLoginIdentities`")
@PrimaryKeyJoinColumn(name = "`id`", referencedColumnName = "`id`")
@XmlRootElement
public class GuestLoginIdentity extends AbstractLoginIdentity implements ILoginIdentity {
	private static final long serialVersionUID = 1153446108796952793L;

	/**
	 * Constructs a new {@link GuestLoginIdentity} instance.
	 *
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 * @param createdTimestamp
	 *            the value to use for {@link #getCreatedTimestamp()}
	 */
	public GuestLoginIdentity(Account account, Instant createdTimestamp) {
		super(account, createdTimestamp);
	}

	/**
	 * Constructs a new {@link GuestLoginIdentity} instance.
	 *
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 */
	public GuestLoginIdentity(Account account) {
		super(account);
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided to comply with the JAXB and JPA specs.
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GuestLoginIdentity [id=");
		builder.append(id);
		/*
		 * Can't just include account.toString(), as that would create a recursive never-ending loop.
		 */
		builder.append(", account.getId()=");
		builder.append(account.hasId() ? account.getId() : "N/A");
		builder.append(", createdTimestamp=");
		builder.append(createdTimestamp);
		builder.append("]");
		return builder.toString();
	}

}
