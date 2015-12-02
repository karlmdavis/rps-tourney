package com.justdavis.karl.rpstourney.service.app.auth.game;

import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.lambdaworks.crypto.SCryptUtil;

/**
 * Contains utilities for the hashing and verification of passwords.
 */
public final class PasswordUtils {
	/**
	 * The CPU cost factor (<code>"N"</code>) that will be passed to
	 * {@link SCryptUtil#scrypt(String, int, int, int)} when new passwords are
	 * first converted to hashes.
	 * 
	 * @see http://stackoverflow.com/a/12581268/1851299
	 */
	private static final int SCRYPT_CPU_COST = (int) Math.pow(2, 14);

	/**
	 * The memory cost factor (<code>"r"</code>) that will be passed to
	 * {@link SCryptUtil#scrypt(String, int, int, int)} when new passwords are
	 * first converted to hashes.
	 * 
	 * @see http://stackoverflow.com/a/12581268/1851299
	 */
	private static final int SCRYPT_MEMORY_COST = 8;

	/**
	 * The parallelization factor (<code>"p"</code>) that will be passed to
	 * {@link SCryptUtil#scrypt(String, int, int, int)} when new passwords are
	 * first converted to hashes.
	 * 
	 * @see http://stackoverflow.com/a/12581268/1851299
	 */
	private static final int SCRYPT_PARALLELIZATION = 1;

	/**
	 * @param password
	 *            the password to be hashed
	 * @return an scrypt password hash of the specified password
	 */
	public static String hashPassword(String password) {
		String passwordHash = SCryptUtil.scrypt(password, SCRYPT_CPU_COST, SCRYPT_MEMORY_COST, SCRYPT_PARALLELIZATION);
		return passwordHash;
	}

	/**
	 * @param password
	 *            the password to compare against the hash in
	 *            {@link GameLoginIdentity#getPasswordHash()}
	 * @param login
	 *            the {@link GameLoginIdentity} to pull the password hash from
	 * @return <code>true</code> if the the specified password matches the
	 *         specified password hash (as created by
	 *         {@link #hashPassword(String)}), <code>false</code> if it does not
	 */
	public static boolean checkPassword(String password, GameLoginIdentity login) {
		return SCryptUtil.check(password, login.getPasswordHash());
	}
}
