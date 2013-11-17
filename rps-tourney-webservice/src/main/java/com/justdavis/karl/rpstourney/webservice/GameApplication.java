package com.justdavis.karl.rpstourney.webservice;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import com.justdavis.karl.rpstourney.webservice.auth.AccountService;
import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestAuthService;

/**
 * The JAX-RS {@link Application} for the game web service. Specifies all of the
 * services and resources that will be hosted by the application.
 */
public final class GameApplication extends Application {
	/**
	 * Constructs a new {@link GameApplication} instance.
	 * 
	 * @param sc
	 *            the {@link ServletContext} that the application is running
	 *            within
	 */
	public GameApplication(@Context ServletContext sc) {
		if (sc == null) {
			throw new IllegalArgumentException("ServletContext is null");
		}
	}

	/**
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		/*
		 * Any services specified here will be instantiated once per request.
		 */

		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(HelloWorldService.class);
		classes.add(AccountService.class);
		classes.add(GuestAuthService.class);
		return classes;
	}

	/**
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		/*
		 * Any services specified here will use a single instance for all
		 * requests.
		 */

		Set<Object> singletons = new HashSet<Object>();
		// singletons.add(new HelloWorldService());
		return singletons;
	}
}
