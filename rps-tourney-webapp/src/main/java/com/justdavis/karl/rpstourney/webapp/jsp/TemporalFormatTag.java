package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAccessor;

/**
 * A JSP tag handler that provides the
 * <code>&lt;rps:instant value="${someInstant}" /&gt;</code> tag, for printing
 * out formatted {@link Instant}s and other {@link TemporalAccessor}s.
 */
public final class TemporalFormatTag extends SimpleTagSupport {
	private TemporalAccessor value;
	private Format format;

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		// If no Instant was provided, just print out nothing.
		if (value == null)
			return;

		Format format = this.format != null ? this.format : Format.ISO_INSTANT;
		getJspContext().getOut().write(format.formatter.format(value));
	}

	/**
	 * @param value
	 *            the {@link TemporalAccessor} to be rendered
	 */
	public void setValue(TemporalAccessor value) {
		this.value = value;
	}

	/**
	 * @param format
	 *            the {@link Format#name()} of the {@link Format} to use
	 */
	public void setFormat(String format) {
		for (Format formatConstant : Format.values())
			if (formatConstant.name().equalsIgnoreCase(format))
				this.format = formatConstant;
	}

	/**
	 * Enumerates the supported {@link DateTimeFormatter}s for
	 * {@link TemporalFormatTag}.
	 */
	private static enum Format {
		/**
		 * Represents {@link DateTimeFormatter#ISO_INSTANT}.
		 */
		ISO_INSTANT(DateTimeFormatter.ISO_INSTANT);

		private final DateTimeFormatter formatter;

		/**
		 * Constructs a new {@link Format} constant.
		 * 
		 * @param formatter
		 *            the {@link DateTimeFormatter} that this {@link Format}
		 *            constant will represent
		 */
		private Format(DateTimeFormatter formatter) {
			this.formatter = formatter;
		}
	}
}
