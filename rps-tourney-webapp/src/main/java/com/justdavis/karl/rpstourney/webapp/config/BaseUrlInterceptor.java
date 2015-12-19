package com.justdavis.karl.rpstourney.webapp.config;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * This Spring interceptor runs on every request/response and sets the value of
 * {@link AppConfig#getBaseUrl()} as a
 * {@link HttpServletRequest#getAttribute(String)} entry (using the key
 * {@link BaseUrlInterceptor#REQUEST_ATTRIB_BASE_URL}). This allows the JSP
 * views to use it to build correct URLs. (This is necessary in the case of a
 * proxy deployment when the proxied location has a different context path.)
 */
@Component
public class BaseUrlInterceptor extends HandlerInterceptorAdapter {
	/**
	 * The {@link HttpServletRequest#setAttribute(String, Object)} key used to
	 * store the application's {@link AppConfig#getBaseUrl()} value.
	 */
	public static final String REQUEST_ATTRIB_BASE_URL = "rpstourney.config.baseurl";

	private final AppConfig appConfig;

	/**
	 * Constructs a new {@link BaseUrlInterceptor} instance.
	 * 
	 * @param appConfig
	 *            the application's {@link AppConfig}
	 */
	@Inject
	public BaseUrlInterceptor(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	/**
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setAttribute(REQUEST_ATTRIB_BASE_URL, appConfig.getBaseUrl());

		return super.preHandle(request, response, handler);
	}
}
