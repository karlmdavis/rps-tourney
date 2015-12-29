package org.rps.tourney.service.benchmarks;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;

/**
 * Enumerates the standard {@link GameLoginIdentity}s that the benchmarks should
 * use. Using these identities makes it easier to go clean up any mistakes if
 * the benchmarks fail to clean up after themselves for some reason.
 */
public enum BenchmarkUser {
	USER_A("benchmarker.a@example.com", "player.a.pw1"), USER_B("benchmarker.b@example.com", "player.b.pw1");

	private final InternetAddress address;
	private final String password;

	/**
	 * Enum constant constructor.
	 * 
	 * @param address
	 *            the value to use for {@link #getAddress()}
	 * @param password
	 *            the value to use for {@link #getPassword()}
	 */
	private BenchmarkUser(String address, String password) {
		try {
			this.address = new InternetAddress(address);
		} catch (AddressException e) {
			throw new BadCodeMonkeyException(e);
		}
		this.password = password;
	}

	/**
	 * @return the {@link GameLoginIdentity#getEmailAddress()} value for this
	 *         {@link BenchmarkUser}
	 */
	public InternetAddress getAddress() {
		return address;
	}

	/**
	 * @return the (unhashed) {@link GameLoginIdentity#getPasswordHash()} value
	 *         for this {@link BenchmarkUser}
	 */
	public String getPassword() {
		return password;
	}
}
