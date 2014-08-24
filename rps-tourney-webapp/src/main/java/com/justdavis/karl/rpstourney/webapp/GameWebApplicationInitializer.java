package com.justdavis.karl.rpstourney.webapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * The programmatic replacement for <code>web.xml</code> in this project.
 */
@Order(2)
public final class GameWebApplicationInitializer implements
		WebApplicationInitializer {
	/*
	 * A bunch of Spring documentation mentions that it's important that the
	 * filters from {@link SecurityWebApplicationInitializer} be configured
	 * before anything else, thus the {@link Order} annotation here.
	 */

	/**
	 * The web application context-wide initialization parameter that specifies
	 * the Spring parent {@link ApplicationContext} instance that should be used
	 * (if any). This is mostly intended for use by integration tests.
	 * 
	 * @see ServletContext#setAttribute(String, Object)
	 */
	public static final String SPRING_PARENT_CONTEXT = "spring.context.parent";

	/**
	 * The {@link ServletContext#getAttribute(String)} key that the Spring
	 * {@link ApplicationContext} will be saved under. This is intended for use
	 * by certain integration tests that want to allow the container to
	 * initialize the Spring context on its own.
	 * 
	 * @see ServletContext#getAttribute(String)
	 */
	public static final String SPRING_CONTEXT_ATTRIBUTE = "spring.context";

	/**
	 * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		// Create the Spring context for the application.
		AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();

		/*
		 * If a parent Spring ApplicationContext is available, we'll use its
		 * configuration. If one is not available, we'll configure things
		 * ourselves. It's expected that some integration tests that use Jetty
		 * as the web application container will provide a parent
		 * ApplicationContext. (See
		 * ccom.justdavis.karl.rpstourney.webapp.SpringITConfigWithJetty for
		 * more details.)
		 */
		ApplicationContext springParentContext = findSpringParentContext(servletContext);
		if (springParentContext != null) {
			springContext.setParent(springParentContext);
		} else {
			springContext.register(SpringMvcConfig.class);
		}

		// Set the Spring PRODUCTION profile as the default.
		ConfigurableEnvironment springEnv = springContext.getEnvironment();
		springEnv.setDefaultProfiles(SpringProfile.PRODUCTION);

		// Refresh/process the Spring context.
		// TODO this might be needed when there's a parent context?
		// TODO definitely not needed when there isn't
		// rootContext.refresh();

		// Manage the lifecycle of the root application context
		servletContext.addListener(new ContextLoaderListener(springContext));

		// Ensure that request-scoped resource beans work correctly.
		servletContext.addListener(RequestContextListener.class);

		/*
		 * Save the Spring context as a ServletContext attribute. It's expected
		 * that some integration tests will allow Jetty to start "clean" and
		 * will need to grab the Spring context out from it once it's running.
		 * See EmbeddedJettySpringContextRule for details.
		 */
		servletContext.setAttribute(SPRING_CONTEXT_ATTRIBUTE, springContext);

		/*
		 * Note: Getting the servlet mappings here correct took forever. The
		 * following discussion proved to be very helpful:
		 * http://www.jroller.com
		 * /kenwdelong/entry/spring_default_servlets_and_serving. Please also
		 * see the EmbeddedServer class, which provides the JSP and default
		 * servlets required by this application. Other containers that might be
		 * used, e.g. Tomcat, would also have to provide these somewhere.
		 */

		// Register the Spring MVC DispatcherServlet to handle requests.
		ServletRegistration.Dynamic mvcGameAppServlet = servletContext
				.addServlet("gameapp", new DispatcherServlet(springContext));
		mvcGameAppServlet.setLoadOnStartup(1);
		mvcGameAppServlet.addMapping("/");
	}

	/**
	 * Allow the web application context's attributes to provide a parent
	 * {@link ApplicationContext} for Spring. This is intended for use in
	 * integration tests, where the container (i.e. Jetty) will be created in
	 * the parent context and the JAX-RS application will be created in its own
	 * child context.
	 * 
	 * @param servletContext
	 *            the {@link ServletContext} being configured
	 * @return the parent {@link ApplicationContext} for Spring, or
	 *         <code>null</code> if none was found
	 */
	private static ApplicationContext findSpringParentContext(
			ServletContext servletContext) {
		ApplicationContext springParentContext = (ApplicationContext) servletContext
				.getAttribute(SPRING_PARENT_CONTEXT);
		if (springParentContext == null)
			return null;

		return springParentContext;
	}
}
