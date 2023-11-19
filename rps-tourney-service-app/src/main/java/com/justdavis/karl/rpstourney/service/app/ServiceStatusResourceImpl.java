package com.justdavis.karl.rpstourney.service.app;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.rpstourney.service.api.IServiceStatusResource;

/**
 * The JAX-RS server-side implementation of {@link IServiceStatusResource}.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ServiceStatusResourceImpl implements IServiceStatusResource {
	private final String version;

	/**
	 * This public, default, no-arg constructor is required by Spring (for request-scoped beans).
	 */
	public ServiceStatusResourceImpl() {
		try {
			// Load the .properties resource that has the version number in it.
			Properties versionProps = new Properties();
			versionProps.load(
					Thread.currentThread().getContextClassLoader().getResourceAsStream("project-version.properties"));

			// Read the version from that resource.
			this.version = versionProps.getProperty("project.version");
		} catch (IOException e) {
			// Shouldn't happen, as this is a classpath resource.
			throw new UncheckedIoException(e);
		}
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.IServiceStatusResource#ping()
	 */
	@Override
	public String ping() {
		return IServiceStatusResource.PONG;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.IServiceStatusResource#echo(java.lang.String)
	 */
	@Override
	public String echo(String text) {
		return text;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.api.IServiceStatusResource#getVersion()
	 */
	@Override
	public String getVersion() {
		return version;
	}
}
