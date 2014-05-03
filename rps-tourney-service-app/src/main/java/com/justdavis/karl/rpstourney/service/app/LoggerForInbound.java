package com.justdavis.karl.rpstourney.service.app;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;

import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;

/**
 * A custom {@link LoggingInInterceptor} subclass that doesn't add linebreaks to
 * the log output and attempts to prevent passwords from ending up in the log.
 */
public final class LoggerForInbound extends LoggingInInterceptor {
	/**
	 * The number of bytes of each message to log out, before truncating the
	 * rest.
	 */
	static final int MESSAGE_BYTES_TRUNCATION_LIMIT = 1000;

	/**
	 * Should match password strings in {@link LoggingMessage#getPayload()} from
	 * {@link IGameAuthResource#createGameLogin(javax.mail.internet.InternetAddress, String)}
	 * requests/responses.
	 */
	static final Pattern GAME_ACCOUNT_PASSWORD_REGEX = Pattern
			.compile("password=[^&]*");

	private static Field loggingMessageIdField;

	/**
	 * Constructs a new {@link LoggerForInbound} instance.
	 */
	public LoggerForInbound() {
		super(MESSAGE_BYTES_TRUNCATION_LIMIT);

		if (loggingMessageIdField == null) {
			try {
				Field loggingMessageIdField = LoggingMessage.class
						.getDeclaredField("id");
				loggingMessageIdField.setAccessible(true);

				/*
				 * Thread safety: this might get set multiple times, but that's
				 * not actually a problem.
				 */
				LoggerForInbound.loggingMessageIdField = loggingMessageIdField;
			} catch (NoSuchFieldException e) {
				throw new IllegalStateException(e);
			} catch (SecurityException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * @see org.apache.cxf.interceptor.LoggingInInterceptor#formatLoggingMessage(org.apache.cxf.interceptor.LoggingMessage)
	 */
	@Override
	protected String formatLoggingMessage(LoggingMessage loggingMessage) {
		StringBuilder buffer = new StringBuilder();

		buffer.append("Inbound Message: [");

		buffer.append("id=").append(getId(loggingMessage));
		if (loggingMessage.getAddress().length() > 0)
			buffer.append(",address='").append(loggingMessage.getAddress())
					.append("'");
		if (loggingMessage.getResponseCode().length() > 0)
			buffer.append(",responseCode=").append(
					loggingMessage.getResponseCode());
		if (loggingMessage.getEncoding().length() > 0)
			buffer.append(",encoding='").append(loggingMessage.getEncoding())
					.append("'");
		if (loggingMessage.getHttpMethod().length() > 0)
			buffer.append(",httpMethod=")
					.append(loggingMessage.getHttpMethod());
		buffer.append(",contentType='").append(loggingMessage.getContentType())
				.append("'");
		buffer.append(",headers=").append(loggingMessage.getHeader());
		buffer.append(",message='").append(loggingMessage.getMessage())
				.append("'");
		buffer.append(",payload='").append(getPayload(loggingMessage))
				.append("'");

		buffer.append("]");

		return buffer.toString();
	}

	/**
	 * @param loggingMessage
	 *            the {@link LoggingMessage} to get the <code>id</code> field's
	 *            value for
	 * @return the value of the specified {@link LoggingMessage}'s private
	 *         <code>id</code> field
	 */
	private String getId(LoggingMessage loggingMessage) {
		if (loggingMessage == null)
			return "null";

		try {
			return (String) loggingMessageIdField.get(loggingMessage);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @param loggingMessage
	 *            the {@link LoggingMessage} to get the
	 *            {@link LoggingMessage#getPayload()} value for
	 * @return the {@link LoggingMessage#getPayload()} value from the specified
	 *         {@link LoggingMessage}, with (hopefully) any sensitive contents
	 *         stripped out
	 */
	private String getPayload(LoggingMessage loggingMessage) {
		String address = loggingMessage.getAddress().toString();
		String payload = loggingMessage.getPayload().toString();

		// This method is known to involve passwords.
		if (address.contains("auth/game/create"))
			return GAME_ACCOUNT_PASSWORD_REGEX.matcher(payload).replaceAll(
					"password=***");

		// This method is known to involve passwords.
		if (address.contains("auth/game/login"))
			return GAME_ACCOUNT_PASSWORD_REGEX.matcher(payload).replaceAll(
					"password=***");

		return payload;
	}
}
