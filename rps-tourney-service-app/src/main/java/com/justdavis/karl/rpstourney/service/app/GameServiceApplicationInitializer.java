package com.justdavis.karl.rpstourney.service.app;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * <p>
 * Initializes the JAX-RS game web service application via Spring.
 * </p>
 * <p>
 * If deployed in a Servlet 3.0 container, this application will be found and
 * loaded automagically, as this project includes Spring's
 * {@link SpringServletContainerInitializer} in its dependencies. That class is
 * a registered {@link ServletContainerInitializer} SPI and will in turn search
 * for and enable any {@link WebApplicationInitializer} implementations (such as
 * this class).
 * </p>
 * <p>
 * By default, this will create a new standalone Spring
 * {@link ApplicationContext}. If, however, this will be running inside of a
 * parent Spring {@link ApplicationContext} (as is the case with some of this
 * project's integration tests), that parent context should be provided via the
 * {@link #SPRING_PARENT_CONTEXT} mechanism.
 * </p>
 * 
 * @see SpringConfig
 */
public final class GameServiceApplicationInitializer implements
		WebApplicationInitializer {
	/**
	 * The web application context-wide initialization parameter that specifies
	 * the Spring parent {@link ApplicationContext} instance that should used
	 * (if any). This is mostly intended for use by integration tests.
	 * 
	 * @see ServletContext#setAttribute(String, Object)
	 */
	public static final String SPRING_PARENT_CONTEXT = "spring.context.parent";

	/**
	 * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(ServletContext container) {
		// Create the Spring application context.
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

		/*
		 * If a parent Spring ApplicationContext is available, we'll use its
		 * configuration. If one is not available, we'll configure things
		 * ourselves. It's expected that any integration tests that use Jetty as
		 * the web application container will provide a parent
		 * ApplicationContext. (See
		 * com.justdavis.karl.rpstourney.service.app.JettyBindingsForITs for
		 * more details.)
		 */
		ApplicationContext springParentContext = findSpringParentContext(container);
		if (springParentContext != null) {
			rootContext.setParent(springParentContext);
		} else {
			rootContext.register(SpringConfig.class);
		}

		// Set the Spring PRODUCTION profile as the default.
		ConfigurableEnvironment springEnv = rootContext.getEnvironment();
		springEnv.setDefaultProfiles(SpringProfile.PRODUCTION);

		// Refresh/process the Spring context.
		// TODO this might be needed when there's a parent context?
		// TODO definitely not needed when there isn't
		// rootContext.refresh();

		// Manage the lifecycle of the root application context.
		container.addListener(new ContextLoaderListener(rootContext));

		// Ensure that request-scoped resource beans work correctly.
		container.addListener(RequestContextListener.class);

		// Register the Servlet that will handle the Apache CXF JAX-RS services.
		CXFServlet cxfServlet = new CXFServlet();
		ServletRegistration.Dynamic cxfServletReg = container.addServlet(
				"CXFServlet", cxfServlet);
		cxfServletReg.setLoadOnStartup(1);
		cxfServletReg.addMapping("/*");
	}

	/**
	 * Allow the web application context's attributes to provide a parent
	 * {@link ApplicationContext} for Spring. This is intended for use in
	 * integration tests, where the container (i.e. Jetty) will be created in
	 * the parent context and the JAX-RS application will be created in its own
	 * child context.
	 * 
	 * @param container
	 *            the {@link ServletContext} being configured
	 * @return the parent {@link ApplicationContext} for Spring, or
	 *         <code>null</code> if none was found
	 */
	private static ApplicationContext findSpringParentContext(
			ServletContext container) {
		ApplicationContext springParentContext = (ApplicationContext) container
				.getAttribute(SPRING_PARENT_CONTEXT);
		if (springParentContext == null)
			return null;

		return springParentContext;
	}
}