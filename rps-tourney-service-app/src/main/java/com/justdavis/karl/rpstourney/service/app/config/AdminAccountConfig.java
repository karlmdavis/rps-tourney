package com.justdavis.karl.rpstourney.service.app.config;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.exceptions.UncheckedAddressException;

/**
 * Models the configuration of the application's default
 * {@link SecurityRole#ADMINS} {@link Account}.
 * 
 * @see ServiceConfig
 */
public final class AdminAccountConfig {
	@XmlElement(required = true, nillable = false)
	@XmlJavaTypeAdapter(InternetAddressStringAdapter.class)
	private final InternetAddress address;

	@XmlElement(required = true, nillable = false)
	private final String password;

	/**
	 * Constructs a new {@link AdminAccountConfig} instance.
	 * 
	 * @param address
	 *            the value to use for {@link #getAddress()}
	 * @param password
	 *            the value to use for {@link #getPassword()}
	 */
	public AdminAccountConfig(InternetAddress address, String password) {
		this.address = address;
		this.password = password;
	}

	/**
	 * Constructs a new {@link AdminAccountConfig} instance.
	 * 
	 * @param address
	 *            the value to use for {@link #getAddress()}
	 * @param password
	 *            the value to use for {@link #getPassword()}
	 */
	public AdminAccountConfig(String address, String password) {
		this(createInternetAddress(address), password);
	}

	/**
	 * @param address
	 *            the value to pass in to
	 *            {@link InternetAddress#InternetAddress(String)}
	 * @return a new {@link InternetAddress} instance
	 */
	private static InternetAddress createInternetAddress(String address) {
		try {
			return new InternetAddress(address);
		} catch (AddressException e) {
			throw new UncheckedAddressException(e);
		}
	}

	/**
	 * This private no-arg constructor is required by JAX-B.
	 */
	@SuppressWarnings("unused")
	private AdminAccountConfig() {
		this.address = null;
		this.password = null;
	}

	/**
	 * @return the {@link GameLoginIdentity#getEmailAddress()} value for the
	 *         application's default {@link SecurityRole#ADMINS} {@link Account}
	 */
	public InternetAddress getAddress() {
		return address;
	}

	/**
	 * @return the (not yet hashed) {@link GameLoginIdentity#getPasswordHash()}
	 *         value for the application's default {@link SecurityRole#ADMINS}
	 *         {@link Account}
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * This JAX-B {@link XmlAdapter} marshalls {@link InternetAddress} instances
	 * as {@link String}s.
	 */
	private static final class InternetAddressStringAdapter extends XmlAdapter<String, InternetAddress> {
		/**
		 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
		 */
		@Override
		public InternetAddress unmarshal(String v) throws Exception {
			if (v == null)
				return null;
			return new InternetAddress(v);
		}

		/**
		 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
		 */
		@Override
		public String marshal(InternetAddress v) throws Exception {
			if (v == null)
				return null;
			return v.toString();
		}
	}
}
