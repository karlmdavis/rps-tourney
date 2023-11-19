package com.justdavis.karl.rpstourney.webapp.security;

import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

/**
 * <p>
 * The subclass of {@link AbstractSecurityWebApplicationInitializer} will be automagically discovered by Spring and will
 * cause Spring's Security filters and such to be registered with the Servlet 3.0 web application.
 * </p>
 */
@Order(1)
public final class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer
		implements WebApplicationInitializer {
	/*
	 * A bunch of Spring documentation mentions that it's important that these filters be configured before anything
	 * else, thus the {@link Order} annotation here.
	 */

	/*
	 * Note: For some reason, it seems that this class must explicitly implement WebApplicationInitializer in order to
	 * be picked up by Spring, even though the superclass already implements that interface.
	 */

	/*
	 * Don't need to do anything else. This class will be picked up off of the classpath and the superclass will handle
	 * registering the Spring Security filters appropriately.
	 */
}
