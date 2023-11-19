package com.justdavis.karl.rpstourney.service.api.jaxb;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This JAXB {@link XmlAdapter} marshalls/unmarshalls {@link Instant}s as {@link String}s.
 */
public final class InstantJaxbAdapter extends XmlAdapter<String, Instant> {
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Instant unmarshal(String v) throws Exception {
		if (v == null)
			return null;

		return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(v));
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Instant v) throws Exception {
		if (v == null)
			return null;

		return DateTimeFormatter.ISO_INSTANT.format(v);
	}
}
