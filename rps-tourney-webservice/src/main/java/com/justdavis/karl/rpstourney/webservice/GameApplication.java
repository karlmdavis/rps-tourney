package com.justdavis.karl.rpstourney.webservice;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext.AccountSecurityContextProvider;
import com.justdavis.karl.rpstourney.webservice.auth.AccountService;
import com.justdavis.karl.rpstourney.webservice.auth.AuthenticationFilter;
import com.justdavis.karl.rpstourney.webservice.auth.AuthorizationFilter.AuthorizationFilterFeature;
import com.justdavis.karl.rpstourney.webservice.auth.game.GameAuthService;
import com.justdavis.karl.rpstourney.webservice.auth.game.InternetAddressReader;
import com.justdavis.karl.rpstourney.webservice.auth.guest.GuestAuthService;
import com.justdavis.karl.rpstourney.webservice.demo.HelloWorldServiceImpl;

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

		// Register the entity translators.
		classes.add(InternetAddressReader.class);

		// Register the filters.
		classes.add(AuthenticationFilter.class);
		classes.add(AuthorizationFilterFeature.class);

		// Register the resources.
		classes.add(HelloWorldServiceImpl.class);
		classes.add(AccountService.class);
		classes.add(GuestAuthService.class);
		classes.add(GameAuthService.class);

		// Register any custom context providers.
		classes.add(AccountSecurityContextProvider.class);

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
