package com.justdavis.karl.rpstourney.webapp;

import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * <p>
 * A Spring {@link HandlerInterceptor} that will log request and response
 * details (if its logging category is enabled). Please note:
 * </p>
 * <ul>
 * <li>This must be configured via
 * {@link WebMvcConfigurer#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)}
 * , or some similar mechanism .</li>
 * <li>This will only "catch" requests that are handled by the Spring MVC
 * application.</li>
 * <li>The logging will almost certainly end up containing sensitive
 * information, such as passwords, and thus <strong>must never</strong> be
 * enabled in production. (It's also going to be slow, which is another reason
 * to not use it in production.)</li>
 * </ul>
 */
public final class RequestResponseLoggingInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);

	/**
	 * Constructs a new {@link RequestResponseLoggingInterceptor} instance.
	 */
	public RequestResponseLoggingInterceptor() {
		if (LOGGER.isTraceEnabled())
			LOGGER.warn(
					"Full request and response logging enabled. If you see this message in production, that's bad!");
	}

	/**
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (LOGGER.isTraceEnabled()) {
			StringBuilder requestRepresentation = new StringBuilder();

			requestRepresentation.append(request.getMethod());

			requestRepresentation.append(" url=");
			requestRepresentation.append(request.getRequestURL());
			if (request.getQueryString() != null) {
				requestRepresentation.append('?');
				requestRepresentation.append(request.getQueryString());
			}

			requestRepresentation.append(",headers={");
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();

				requestRepresentation.append(headerName);
				requestRepresentation.append(": ");
				requestRepresentation.append(request.getHeader(headerName));

				if (headerNames.hasMoreElements())
					requestRepresentation.append(", ");
			}
			requestRepresentation.append('}');

			/*
			 * Note: We don't currently log the body/message content. It's
			 * tricky to get, and I don't (yet) have a need for it.
			 */

			LOGGER.trace("Request: {}", requestRepresentation.toString());
		}

		/*
		 * Returning true indicates that the request should continue to be
		 * handled.
		 */
		return true;
	}

	/**
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (LOGGER.isTraceEnabled()) {
			StringBuilder responseRepresentation = new StringBuilder();

			responseRepresentation.append(request.getMethod());

			responseRepresentation.append(" url=");
			responseRepresentation.append(request.getRequestURL());
			if (request.getQueryString() != null) {
				responseRepresentation.append('?');
				responseRepresentation.append(request.getQueryString());
			}

			responseRepresentation.append(",status=");
			responseRepresentation.append(response.getStatus());

			responseRepresentation.append(",headers={");
			Iterator<String> headerNames = response.getHeaderNames().iterator();
			while (headerNames.hasNext()) {
				String headerName = headerNames.next();

				responseRepresentation.append(headerName);
				responseRepresentation.append(": ");
				responseRepresentation.append(response.getHeader(headerName));

				if (headerNames.hasNext())
					responseRepresentation.append(", ");
			}
			responseRepresentation.append('}');

			/*
			 * Note: We don't currently log the body/message content. It's
			 * tricky to get, and I don't (yet) have a need for it.
			 */

			LOGGER.trace("Response: {}", responseRepresentation.toString());
		}
	}
}
