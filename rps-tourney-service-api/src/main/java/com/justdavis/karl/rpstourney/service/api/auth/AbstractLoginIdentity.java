package com.justdavis.karl.rpstourney.service.api.auth;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.threeten.bp.Instant;

import com.justdavis.karl.rpstourney.service.api.jaxb.InstantJaxbAdapter;

/**
 * <p>
 * A common base class for all {@link ILoginIdentity} implementations.
 * </p>
 * <p>
 * This is required by JPA, to enable queries of the
 * "select all logins for user 'foo'" variety. Specifically, JPA only supports
 * those queries for for supertypes if the types all share a common base
 * class/table.
 * </p>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "`LoginIdentities`")
public abstract class AbstractLoginIdentity implements ILoginIdentity {
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "LoginIdentities_id_seq")
	@SequenceGenerator(name = "LoginIdentities_id_seq", sequenceName = "loginidentities_id_seq")
	@XmlElement
	protected long id;

	@OneToOne(optional = false, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
	@JoinColumn(name = "`accountId`")
	@XmlElement
	protected Account account;

	@Column(name = "`createdTimestamp`", nullable = false, updatable = false)
	@org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp")
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	protected Instant createdTimestamp;

	/**
	 * Constructs a new {@link AbstractLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 * @param createdTimestamp
	 *            the value to use for {@link #getCreatedTimestamp()}
	 */
	protected AbstractLoginIdentity(Account account, Instant createdTimestamp) {
		if (account == null)
			throw new IllegalArgumentException();
		if (createdTimestamp == null)
			throw new IllegalArgumentException();

		this.account = account;
		this.createdTimestamp = createdTimestamp;
	}

	/**
	 * Constructs a new {@link AbstractLoginIdentity} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 */
	protected AbstractLoginIdentity(Account account) {
		this(account, Instant.now());
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB and JPA specs.
	 */
	@Deprecated
	protected AbstractLoginIdentity() {
	}

	/**
	 * @return <code>true</code> if this {@link AbstractLoginIdentity} has been
	 *         assigned an ID (which it should if it's been persisted),
	 *         <code>false</code> if it has not
	 */
	public boolean hasId() {
		return id > 0;
	}

	/**
	 * <p>
	 * Returns the unique integer that identifies and represents this
	 * {@link AbstractLoginIdentity} instance.
	 * </p>
	 * <p>
	 * This value will be assigned by JPA when the {@link Entity} is persisted.
	 * Until then, this value should not be accessed.
	 * </p>
	 * 
	 * @return the unique integer that identifies and represents this
	 *         {@link AbstractLoginIdentity} instance
	 */
	public long getId() {
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
	 * @see com.justdavis.karl.rpstourney.service.api.auth.ILoginIdentity#getCreatedTimestamp()
	 */
	@Override
	public Instant getCreatedTimestamp() {
		return createdTimestamp;
	}
}
