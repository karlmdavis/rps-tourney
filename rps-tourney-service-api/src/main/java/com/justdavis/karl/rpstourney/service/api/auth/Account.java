package com.justdavis.karl.rpstourney.service.api.auth;

import java.io.Serializable;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.jaxb.InstantJaxbAdapter;

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
 * This class supports JAX-B.
 * </p>
 * <p>
 * This class is marked as {@link Serializable}, as Spring Security will store
 * instances of it as the authenticated {@link Principal}s in user sessions.
 * </p>
 */
@XmlRootElement
@Entity
@Table(name = "`Accounts`")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class Account implements Principal, Serializable {
	private static final long serialVersionUID = 3016213188245722817L;

	/*
	 * FIXME Would rather sequence name was mixed-case, but it can't be, due to
	 * https://hibernate.atlassian.net/browse/HHH-9431.
	 */
	@XmlElement(required = true)
	@Id
	@Column(name = "`id`", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Accounts_id_seq")
	@SequenceGenerator(name = "Accounts_id_seq", sequenceName = "`accounts_id_seq`", allocationSize = 1)
	private long id;

	@Column(name = "`createdTimestamp`", nullable = false, updatable = false)
	@XmlElement
	@XmlJavaTypeAdapter(InstantJaxbAdapter.class)
	private Instant createdTimestamp;

	@XmlElement(required = true, nillable = true)
	@Column(name = "`name`", nullable = true)
	@Size(min = 3, max = 40)
	@Pattern(regexp = "\\S+")
	@SafeHtml(whitelistType = WhiteListType.NONE)
	private String name;

	@XmlElementWrapper(name = "roles", required = true)
	@XmlElement(name = "role")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "`AccountRoles`", joinColumns = @JoinColumn(name = "`accountId`") )
	@Column(name = "`role`")
	@Enumerated(EnumType.STRING)
	private Set<SecurityRole> roles;

	/**
	 * This field is marked {@link XmlTransient} to help ensure it's never sent
	 * off of the server by mistake. Any web services wishing to use it in a
	 * response will have to do so explicitly.
	 */
	@XmlTransient
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE,
			CascadeType.DETACH }, fetch = FetchType.EAGER, mappedBy = "account", orphanRemoval = true)
	private Set<AuthToken> authTokens;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@Fetch(value = FetchMode.SUBSELECT)
	@OrderBy("createdTimestamp ASC")
	@XmlElementWrapper(name = "logins")
	@XmlElement(name = "login")
	@JsonManagedReference
	private List<AbstractLoginIdentity> logins;

	/**
	 * Constructs a new {@link Account} instance.
	 * 
	 * @param roles
	 *            the value to use for {@link #getRoles()} (
	 *            {@link SecurityRole#USERS} will always be added to this)
	 */
	public Account(SecurityRole... roles) {
		this.id = -1;
		this.createdTimestamp = Instant.now();
		this.name = null;
		this.roles = new HashSet<>();
		this.roles.add(SecurityRole.USERS);
		for (SecurityRole role : roles)
			this.roles.add(role);
		this.authTokens = new HashSet<>();
		this.logins = new ArrayList<>();
	}

	/**
	 * Constructs a new {@link Account} instance.
	 */
	public Account() {
		this(new SecurityRole[] {});
	}

	/**
	 * @return <code>true</code> if this {@link Account} has been assigned an ID
	 *         (which it should if it's been persisted), <code>false</code> if
	 *         it has not
	 */
	public boolean hasId() {
		return id > -1;
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
		if (!hasId())
			throw new IllegalStateException("Field value not yet available.");

		return id;
	}

	/**
	 * @return the date-time that this {@link Account} was created
	 */
	public Instant getCreatedTimestamp() {
		return createdTimestamp;
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
	 * @return the user-defined name for this {@link Account}, or
	 *         <code>null</code> if the user has not (yet) specified a name for
	 *         themselves
	 * 
	 * @see java.security.Principal#getName()
	 */
	@XmlTransient
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the new value to use for {@link #getName()}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param role
	 *            the {@link SecurityRole} to check for
	 * @return <code>true</code> if {@link Account#getRoles()} contains the
	 *         specified {@link SecurityRole}, <code>false</code> if it does not
	 */
	public boolean hasRole(SecurityRole role) {
		if (role == null)
			throw new IllegalArgumentException();

		return getRoles().contains(role);
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
	 *         value, or <code>null</code> if no match is found
	 */
	public AuthToken getAuthToken(UUID authTokenValue) {
		for (AuthToken authToken : authTokens)
			if (authToken.getToken().equals(authTokenValue))
				return authToken;

		return null;
	}

	/**
	 * @return the first valid {@link #getAuthTokens()} entry found, or
	 *         <code>null</code> if no such entry is found
	 */
	public AuthToken getAuthToken() {
		for (AuthToken authToken : authTokens)
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

	/**
	 * @return the {@link List} of {@link AbstractLoginIdentity}s associated
	 *         with this {@link Account}, ordered by
	 *         {@link AbstractLoginIdentity#getCreatedTimestamp()}
	 */
	public List<AbstractLoginIdentity> getLogins() {
		return logins;
	}

	/**
	 * @return <code>false</code> if {@link #getLogins()} contains anything
	 *         other than {@link GuestLoginIdentity}s, <code>true</code>
	 *         otherwise
	 */
	@JsonProperty
	public boolean isAnonymous() {
		for (ILoginIdentity login : logins)
			if (login.getLoginProvider() != LoginProvider.GUEST)
				return false;
		return true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		/*
		 * Uses the id field (which has a UNIQUE constraint in the DB) when
		 * present, otherwise falls back to the superclass' implementation
		 * (mostly for the benefit of unit tests).
		 */

		if (hasId()) {
			/*
			 * Generated by Eclipse's
			 * "Source > Generate hashCode() and equals()..." function.
			 */
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (id ^ (id >>> 32));
			return result;
		} else {
			return super.hashCode();
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		/*
		 * Uses the id field (which has a UNIQUE constraint in the DB) when
		 * present, otherwise falls back to instance equality (mostly for the
		 * benefit of unit tests).
		 */

		if (hasId()) {
			/*
			 * Generated by Eclipse's
			 * "Source > Generate hashCode() and equals()..." function.
			 */
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Account other = (Account) obj;
			if (id != other.id)
				return false;
			return true;
		} else {
			return this == obj;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [id=");
		builder.append(id);
		builder.append(", createdTimestamp=");
		builder.append(createdTimestamp);
		builder.append(", name=");
		builder.append(name);
		builder.append(", roles=");
		builder.append(roles);
		/*
		 * Warning: printing out the actual authTokens here would be a very bad
		 * idea, as it might enable security leaks in logs, error messages, and
		 * such.
		 */
		builder.append(", authTokens.size()=");
		builder.append(authTokens.size());
		builder.append(", logins=");
		builder.append(logins);
		builder.append("]");
		return builder.toString();
	}

}
