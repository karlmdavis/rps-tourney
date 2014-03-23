package com.justdavis.karl.rpstourney.service.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * The JAX-RS {@link Application} for the game web service. In a non-Spring
 * setup, this would specify all of the services and resources that will be
 * hosted by the application. However, since Spring <em>is</em> being used, that
 * is all handled in {@link GameServiceApplicationInitializer}.
 */
@ApplicationPath("/")
public final class ServiceApplication extends Application {
	/**
	 * Constructs a new {@link ServiceApplication} instance.
	 */
	public ServiceApplication() {
	}
}
