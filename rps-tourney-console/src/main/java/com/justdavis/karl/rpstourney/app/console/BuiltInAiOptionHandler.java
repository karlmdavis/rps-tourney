package com.justdavis.karl.rpstourney.app.console;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * This Args4J {@link OptionHandler} handles parsing of {@link BuiltInAi}
 * instances from {@link String}s. This will map "Easy", "Medium", or "Hard"
 * {@link String}s to hardcoded {@link BuiltInAi} constants.
 */
public final class BuiltInAiOptionHandler extends OptionHandler<BuiltInAi> {
	/**
	 * Constructs a new {@link BuiltInAiOptionHandler} instance.
	 * 
	 * @param parser
	 *            the {@link CmdLineParser} that this {@link OptionHandler} will
	 *            be used with
	 * @param option
	 *            the {@link OptionDef} being handled
	 * @param setter
	 *            a {@link Setter} for the options object being parsed into
	 */
	public BuiltInAiOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<? super BuiltInAi> setter) {
		super(parser, option, setter);
	}

	/**
	 * @see org.kohsuke.args4j.spi.OptionHandler#parseArguments(org.kohsuke.args4j.spi.Parameters)
	 */
	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		String param = params.getParameter(0).trim();

		if ("easy".equalsIgnoreCase(param)) {
			setter.addValue(BuiltInAi.THREE_SIDED_DIE_V1);
			return 1;
		} else if ("medium".equalsIgnoreCase(param)) {
			setter.addValue(BuiltInAi.WIN_STAY_LOSE_SHIFT_V1);
			return 1;
		} else if ("hard".equalsIgnoreCase(param)) {
			setter.addValue(BuiltInAi.META_WIN_STAY_LOSE_SHIFT_V1);
			return 1;
		}

		throw new CmdLineException(owner, Messages.ILLEGAL_OPERAND.format(
				params.getParameter(-1), param));
	}

	/**
	 * @see org.kohsuke.args4j.spi.OptionHandler#getDefaultMetaVariable()
	 */
	@Override
	public String getDefaultMetaVariable() {
		return "AI";
	}
}