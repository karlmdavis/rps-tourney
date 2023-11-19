package com.justdavis.karl.rpstourney.service.api.auth;

import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;

/**
 * <p>
 * Enumerates the various login providers/mechanisms that are available.
 * </p>
 * <p>
 * Design note: This is an enum, as it really isn't possible to abstract out the functionality required to support the
 * different options available here. For instance, you can't abstract away the fact that guests don't have to login at
 * all, "simple" accounts can be created on the site, or that OAuth logins are an option. It just kind of has to be
 * hard-coded. (Though the various OAuth providers can be abstracted out.)
 * </p>
 */
public enum LoginProvider {
	/**
	 * <p>
	 * Using this {@link LoginProvider}, client browsers receive an automatically created "guest" account. These
	 * accounts are persistent, but can't easily be used in more than one client browser (at least not without the user
	 * manually copy-pasting the cookie to the other browser).
	 * </p>
	 *
	 * @see GuestAuthService
	 * @see GuestLoginIdentity
	 */
	GUEST,

	/**
	 * <p>
	 * Using this {@link LoginProvider}, clients must specify a username and password. It also allows for login sessions
	 * to be cached via a cookie.
	 * </p>
	 *
	 * @see GameAuthService
	 * @see GameLoginIdentity
	 */
	GAME;
}
