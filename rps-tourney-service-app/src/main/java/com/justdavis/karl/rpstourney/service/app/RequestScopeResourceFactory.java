package com.justdavis.karl.rpstourney.service.app;

import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.spring.SpringResourceFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

/**
 * This CXF {@link ResourceProvider} and Spring {@link ApplicationContextAware}
 * listener should be used to wrap all of the application's request-scope beans.
 * This is needed to ensure that those beans aren't treated as singletons and as
 * part of the configuration necessary to ensure they're only instantiated
 * during requests (rather than at application initialization).
 */
public class RequestScopeResourceFactory extends SpringResourceFactory {
	/**
	 * Constructs a new {@link RequestScopeResourceFactory} instance.
	 * 
	 * @param beanName
	 *            the {@link Bean#name()} value for the Spring bean that will be
	 *            used as a (request-scoped) JAX-RS resource
	 */
	public RequestScopeResourceFactory(String beanName) {
		super(beanName);
	}

	/**
	 * @see org.apache.cxf.jaxrs.spring.SpringResourceFactory#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		/*
		 * This override is basically the entire reason for this subclass.
		 */

		return false;
	}
}