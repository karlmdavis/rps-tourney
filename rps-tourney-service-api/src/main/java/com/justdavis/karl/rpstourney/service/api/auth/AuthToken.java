package com.justdavis.karl.rpstourney.service.api.auth;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.threeten.bp.Instant;

import com.justdavis.karl.rpstourney.service.api.jaxb.InstantJaxbAdapter;

/**
 * <p>
 * Each {@link AuthToken} instance represents an authentication token that has
 * been issued for a specific {@link Account}.
 * </p>
 * <p>
 * For all intents and purposes, these values are a "free pass" to a user's
 * account. Any time they're sent over the network, care must be taken to ensure
 * they are transmitted and managed securely.
 * </p>
 * <p>
 * This class supports JPA. The JPA SQL-specific data (e.g. column names) is
 * specified in the <code>META-INF/orm.xml</code> file.
 * </p>
 */
@XmlRootElement
@Entity
@Table(name = "\"AuthTokens\"")
public final class AuthToken {
	@XmlElement
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH, CascadeType.DETACH })
	@JoinColumn(name = "\"accountId\"")
	private final Account account;

	@XmlElement
	@Id
	@Column(name = "\"token\"", nullable = false, updatable = false)
	private final UUID token;

	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	@org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp")
	@Column(name = "\"creationTimestamp\"", nullable = false, updatable = false)
	private final Instant creationTimestamp;

	/**
	 * This no-arg/default constructor is required by JPA.
	 */
	@SuppressWarnings("unused")
	private AuthToken() {
		this.account = null;
		this.token = null;
		this.creationTimestamp = null;
	}

	/**
	 * Constructs a new {@link AuthToken} instance.
	 * 
	 * @param account
	 *            the value to use for {@link #getAccount()}
	 * @param token
	 *            the value to use for {@link #getToken()}
	 * @param creationTimestamp
	 *            the value to use for {@link #getCreationTimestamp()}
	 */
	public AuthToken(Account account, UUID token, Instant creationTimestamp) {
		// Sanity check: null Account?
		if (account == null)
			throw new IllegalArgumentException();
		// Sanity check: null UUID?
		if (token == null)
			throw new IllegalArgumentException();
		// Sanity check: null Instant?
		if (creationTimestamp == null)
			throw new IllegalArgumentException();

		this.account = account;
		this.token = token;
		this.creationTimestamp = creationTimestamp;
	}

	/**
	 * @return the {@link Account} that this {@link AuthToken} was issued for
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @return the unique, random token that represents and identifies this
	 *         {@link AuthToken}
	 */
	public UUID getToken() {
		return token;
	}

	/**
	 * Note: Because this will be persisted as a SQL <code>TIMESTAMP</code>
	 * column, sub-second values will likely be discarded when it's persisted to
	 * a database via JPA.
	 * 
	 * @return the timestamp for (approximately) when this {@link AuthToken} was
	 *         created
	 */
	public Instant getCreationTimestamp() {
		return creationTimestamp;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		/*
		 * Created by Eclipse's "Source > Generate hashCode() and equals()..."
		 * function.
		 */

		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		/*
		 * Created by Eclipse's "Source > Generate hashCode() and equals()..."
		 * function.
		 */

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthToken other = (AuthToken) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}
}
