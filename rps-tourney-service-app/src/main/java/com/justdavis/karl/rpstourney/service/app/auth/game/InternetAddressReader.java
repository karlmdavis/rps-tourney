package com.justdavis.karl.rpstourney.service.app.auth.game;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.utils.HttpUtils;

/**
 * Allows web service methods to accept email addresses, in the form of
 * {@link InternetAddress} instances.
 */
@Provider
public final class InternetAddressReader implements
		MessageBodyReader<InternetAddress> {
	/**
	 * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class,
	 *      java.lang.reflect.Type, java.lang.annotation.Annotation[],
	 *      javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return InternetAddress.class.isAssignableFrom(type);
	}

	/**
	 * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class,
	 *      java.lang.reflect.Type, java.lang.annotation.Annotation[],
	 *      javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
	 *      java.io.InputStream)
	 */
	@Override
	public InternetAddress readFrom(Class<InternetAddress> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		// Pull the email address out of the stream as a String.
		String emailAddressString = IOUtils.toString(entityStream,
				HttpUtils.getEncoding(mediaType, "UTF-8"));

		try {
			// Try to convert the String to an InternetAddress instance.
			return new InternetAddress(emailAddressString);
		} catch (AddressException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
	}
}
