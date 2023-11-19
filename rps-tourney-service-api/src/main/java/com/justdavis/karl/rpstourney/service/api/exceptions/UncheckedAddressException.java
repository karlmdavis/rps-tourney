package com.justdavis.karl.rpstourney.service.api.exceptions;

import javax.mail.internet.AddressException;

/**
 * Wraps {@link AddressException}s in an unchecked exception.
 */
public final class UncheckedAddressException extends RuntimeException {
	private static final long serialVersionUID = -5537227225783561126L;

	/**
	 * Constructor.
	 *
	 * @param e
	 *            the checked exception to wrap
	 */
	public UncheckedAddressException(AddressException e) {
		super(e);
	}
}
