package com.justdavis.karl.rpstourney.app.console;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * This Args4J {@link OptionHandler} handles parsing of {@link InternetAddress} instances from {@link String}s.
 */
public final class InternetAddressOptionHandler extends OptionHandler<InternetAddress> {
	/**
	 * Constructs a new {@link InternetAddressOptionHandler} instance.
	 *
	 * @param parser
	 *            the {@link CmdLineParser} that this {@link OptionHandler} will be used with
	 * @param option
	 *            the {@link OptionDef} being handled
	 * @param setter
	 *            a {@link Setter} for the options object being parsed into
	 */
	public InternetAddressOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<? super InternetAddress> setter) {
		super(parser, option, setter);
	}

	/**
	 * @see org.kohsuke.args4j.spi.OptionHandler#parseArguments(org.kohsuke.args4j.spi.Parameters)
	 */
	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		String param = params.getParameter(0);
		try {
			setter.addValue(new InternetAddress(param));
			return 1;
		} catch (AddressException e) {
			throw new CmdLineException(owner, Messages.ILLEGAL_OPERAND.format(params.getParameter(-1), param), e);
		}
	}

	/**
	 * @see org.kohsuke.args4j.spi.OptionHandler#getDefaultMetaVariable()
	 */
	@Override
	public String getDefaultMetaVariable() {
		return "EMAIL";
	}
}
