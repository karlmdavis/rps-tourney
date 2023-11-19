package com.justdavis.karl.rpstourney.service.api.auth;

import javax.annotation.security.RolesAllowed;

/**
 * Enumerates the security roles used by the application.
 *
 * @see RolesAllowed
 */
public enum SecurityRole {
	/**
	 * All authenticated users are members of this role, even guest accounts.
	 */
	USERS(SecurityRole.ID_USERS),

	/**
	 * Grants users administrative access to the application.
	 */
	ADMINS(SecurityRole.ID_ADMINS);

	/**
	 * The {@link #getId()} for {@link #USERS}.
	 */
	public static final String ID_USERS = "Users";

	/**
	 * The {@link #getId()} for {@link #ADMINS}.
	 */
	public static final String ID_ADMINS = "Admins";

	private final String id;

	/**
	 * Enum constant constructor.
	 *
	 * @param id
	 *            the value to use for {@link #getId()}
	 */
	private SecurityRole(String id) {
		this.id = id;
	}

	/**
	 * @return the unique identifier for this {@link SecurityRole}, which for use with the {@link RolesAllowed}
	 *         annotation, will also be available as a <code>SecurityRole.ID_NNN</code> constant
	 */
	public String getId() {
		return id;
	}
}
