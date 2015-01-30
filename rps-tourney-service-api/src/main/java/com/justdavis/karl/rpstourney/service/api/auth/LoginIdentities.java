package com.justdavis.karl.rpstourney.service.api.auth;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;

/**
 * <p>
 * A JAXB wrapper for a {@link List} of {@link ILoginIdentity} instances.
 * </p>
 * <p>
 * Needed to allow JAX-RS support for {@link IAccountsResource#getLogins()} and
 * other such methods.
 * </p>
 */
@XmlRootElement(name = "logins")
public class LoginIdentities {
	@XmlElements({
			@XmlElement(name = "guestLogin", type = GuestLoginIdentity.class),
			@XmlElement(name = "gameLogin", type = GameLoginIdentity.class) })
	private List<ILoginIdentity> logins;

	/**
	 * Constructs a new {@link LoginIdentities} instance.
	 * 
	 * @param logins
	 *            the value to use for {@link #getLogins()}
	 */
	public LoginIdentities(List<ILoginIdentity> logins) {
		this.logins = logins;
	}

	/**
	 * <strong>Not intended for use:</strong> This constructor is only provided
	 * to comply with the JAXB spec.
	 */
	@Deprecated
	LoginIdentities() {
	}

	/**
	 * @return the {@link List} of {@link ILoginIdentity}s contained in this
	 *         object
	 */
	public List<ILoginIdentity> getLogins() {
		return logins;
	}
}
