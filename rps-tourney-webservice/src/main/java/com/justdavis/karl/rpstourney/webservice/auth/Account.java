package com.justdavis.karl.rpstourney.webservice.auth;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Each {@link Account} instance represents a given user. It is what a user's
 * details, preferences, and history are associated with.
 * </p>
 * <p>
 * This class supports JPA. The JPA SQL-specific data (e.g. column names) is
 * specified in the <code>META-INF/orm.xml</code> file.
 * </p>
 * <p>
 * This class support JAX-B.
 * </p>
 */
@XmlRootElement
@Entity
@Table(name = "\"Accounts\"")
public final class Account implements Principal {
	@Id
	@Column(name = "\"id\"", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Accounts_id_seq")
	@SequenceGenerator(name = "Accounts_id_seq", sequenceName = "accounts_id_seq")
	private long id;

	@XmlElementWrapper(name = "roles")
	@XmlElement(name = "role")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "\"AccountRoles\"", joinColumns = @JoinColumn(name = "\"accountId\""))
	@Column(name = "\"role\"")
	@Enumerated(EnumType.STRING)
	private final Set<SecurityRole> roles;

	/**
	 * This field is marked {@link XmlTransient} to help ensure it's never sent
	 * off of the server by mistake. Any web services wishing to use it in a
	 * response will have to do so explicitly.
	 */
	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "account", orphanRemoval = true)
	private final Set<AuthToken> authTokens;

	/**
	 * This no-arg/default constructor is required by JAX-B and JPA.
	 */
	@SuppressWarnings("unused")
	private Account() {
		this.id = -1;
		this.roles = new HashSet<>();
		this.authTokens = new HashSet<>();
	}

	/**
	 * Constructs a new {@link Account} instance.
	 * 
	 * @param roles
	 *            the value to use for {@link #getRoles()} (
	 *            {@link SecurityRole#USERS} will always be added to this)
	 */
	public Account(SecurityRole... roles) {
		this.id = -1;
		this.roles = new HashSet<>();
		this.roles.add(SecurityRole.USERS);
		for (SecurityRole role : roles)
			this.roles.add(role);
		this.authTokens = new HashSet<>();
	}

	/**
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName() {
		// TODO Use a more suitable field (once one is available)
		return "" + id;
	}

	/**
	 * <p>
	 * Returns the unique integer that identifies and represents this
	 * {@link Account} instance.
	 * </p>
	 * <p>
	 * This value will be assigned by JPA when the {@link Entity} is persisted.
	 * Until then, this value should not be accessed.
	 * </p>
	 * 
	 * @return the unique integer that identifies and represents this
	 *         {@link Account} instance
	 */
	public long getId() {
		if (id == -1)
			throw new IllegalStateException("Field value not yet available.");

		return id;
	}

	/**
	 * Returns the modifiable {@link Set} of {@link SecurityRole}s that this
	 * user {@link Account} is a member of. This {@link Set} should never be
	 * modified to remove {@link SecurityRole#USERS}; all {@link Account}s
	 * should be a member of that {@link SecurityRole}.
	 * 
	 * @return the modifiable {@link Set} of {@link SecurityRole}s that this
	 *         user {@link Account} is a member of
	 */
	public Set<SecurityRole> getRoles() {
		return roles;
	}

	/**
	 * @return the modifiable {@link Set} of {@link AuthToken}s for this
	 *         {@link Account}
	 */
	public Set<AuthToken> getAuthTokens() {
		return authTokens;
	}

	/**
	 * @param authTokenValue
	 *            the {@link AuthToken#getToken()} value to match against
	 * @return the {@link #getAuthTokens()} entry that matches the specified
	 *         value
	 */
	public AuthToken getAuthToken(UUID authTokenValue) {
		for (AuthToken authToken : authTokens)
			if (authToken.getToken().equals(authTokenValue))
				return authToken;

		return null;
	}

	/**
	 * @param authTokenValue
	 *            the {@link AuthToken#getToken()} value to match against
	 * @return <code>true</code> if a matching {@link #getAuthTokens()} entry
	 *         could be found, <code>false</code> if not
	 */
	public boolean isValidAuthToken(UUID authTokenValue) {
		return getAuthToken(authTokenValue) != null;
	}
}
