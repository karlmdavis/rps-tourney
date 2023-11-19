package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.ocpsoft.prettytime.PrettyTime;

/**
 * A JSP tag handler that provides the <code>&lt;rps:instant value="${someInstant}" /&gt;</code> tag, for printing out
 * formatted {@link Instant}s and other {@link TemporalAccessor}s.
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
	 * Enumerates the supported {@link DateTimeFormatter}s for {@link TemporalFormatTag}.
	 */
	private static enum Format {
		/**
		 * Represents {@link DateTimeFormatter#ISO_INSTANT}.
		 */
		ISO_INSTANT(new ThreeTenFormatter(DateTimeFormatter.ISO_INSTANT)),

		/**
		 * Represents {@link DateTimeFormatter#ISO_DATE}.
		 */
		ISO_DATE(new ThreeTenFormatter(DateTimeFormatter.ISO_DATE)),

		/**
		 * Represents {@link PrettyTime#format(Date)}.
		 */
		PRETTY_TIME(new PrettyTimeFormatter());

		private final TemporalFormatter formatter;

		/**
		 * Constructs a new {@link Format} constant.
		 *
		 * @param formatter
		 *            the {@link DateTimeFormatter} that this {@link Format} constant will represent
		 */
		private Format(TemporalFormatter formatter) {
			this.formatter = formatter;
		}
	}

	/**
	 * Abstracts the functionality of the various formatters used in {@link Format}.
	 */
	private static interface TemporalFormatter {
		/**
		 * @param temporalValue
		 *            the {@link TemporalAccessor} to be formatted
		 * @return the formatted-for-humans representation of the specified {@link TemporalAccessor}
		 */
		String format(TemporalAccessor temporalValue);
	}

	/**
	 * A {@link TemporalFormatter} implementation for the <code>threetenbp</code> library's {@link DateTimeFormatter}.
	 */
	private static final class ThreeTenFormatter implements TemporalFormatter {
		private final DateTimeFormatter formatter;

		/**
		 * Constructs a new {@link ThreeTenFormatter} instance.
		 *
		 * @param formatter
		 *            the {@link DateTimeFormatter} to use
		 */
		public ThreeTenFormatter(DateTimeFormatter formatter) {
			this.formatter = formatter;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.webapp.jsp.TemporalFormatTag.TemporalFormatter#format(org.threeten.bp.temporal.TemporalAccessor)
		 */
		@Override
		public String format(TemporalAccessor temporalValue) {
			/*
			 * If the value to be formatted is an Instant, it can only be converted to a date-time in the context of a
			 * timezone.
			 */
			DateTimeFormatter formatterToUse = formatter;
			if (temporalValue instanceof Instant)
				formatterToUse = formatter.withZone(ZoneId.systemDefault());

			return formatterToUse.format(temporalValue);
		}
	}

	/**
	 * A {@link TemporalFormatter} implementation that uses the {@link PrettyTime} library.
	 */
	private static final class PrettyTimeFormatter implements TemporalFormatter {
		/**
		 * @see com.justdavis.karl.rpstourney.webapp.jsp.TemporalFormatTag.TemporalFormatter#format(org.threeten.bp.temporal.TemporalAccessor)
		 */
		@Override
		public String format(TemporalAccessor temporalValue) {
			Date date = Date.from(Instant.from(temporalValue));
			PrettyTime prettyTime = new PrettyTime();
			return prettyTime.format(date);
		}
	}
}
