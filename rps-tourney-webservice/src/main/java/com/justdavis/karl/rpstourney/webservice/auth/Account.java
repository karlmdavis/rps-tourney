package com.justdavis.karl.rpstourney.webservice.auth;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestLoginIdentity;

/**
 * Each {@link Account} instance represents a given user. It is what a user's
 * details, preferences, and history are associated with.
 */
@XmlRootElement
public final class Account implements Principal {
	/**
	 * This field is marked {@link XmlTransient} to help ensure it's never sent
	 * off of the server by mistake. Any web services wishing to use it in a
	 * response will have to do so explicitly.
	 */
	@XmlTransient
	private final UUID authToken;

	private final Set<SecurityRole> roles;

	/**
	 * This no-arg/default constructor is required by JAX-B.
	 */
	@SuppressWarnings("unused")
	private Account() {
		this.authToken = null;
		this.roles = new HashSet<>();
	}

	/**
	 * Constructs a new {@link Account} instance.
	 * 
	 * @param authToken
	 *            the value to use for {@link #getAuthToken()}, which should be
	 *            a random {@link UUID} for new {@link Account}s
	 * @param roles
	 *            the value to use for {@link #getRoles()}
	 */
	public Account(UUID authToken, SecurityRole... roles) {
		this.authToken = authToken;

		this.roles = new HashSet<>();
		this.roles.add(SecurityRole.USERS);
		for (SecurityRole role : roles)
			this.roles.add(role);
	}

	/**
	 * <p>
	 * Returns the {@link UUID} that uniquely identifies/references this
	 * {@link GuestLoginIdentity} instance.
	 * </p>
	 * <p>
	 * For all intents and purposes, this value is a "free pass" to a user's
	 * account. Anytime it's sent over the network, care must be taken to ensure
	 * it's transmitted and managed securely.
	 * </p>
	 * 
	 * @return the {@link UUID} that uniquely identifies/references this
	 *         {@link Account} instance
	 */
	public UUID getAuthToken() {
		return this.authToken;
	}

	/**
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName() {
		return authToken.toString();
	}

	/**
	 * @return an unmodifiable copy of the {@link SecurityRole}s that this user
	 *         {@link Account} is a member of, which will always include
	 *         {@link SecurityRole#USERS}
	 */
	public Set<SecurityRole> getRoles() {
		return Collections.unmodifiableSet(roles);
	}
}
