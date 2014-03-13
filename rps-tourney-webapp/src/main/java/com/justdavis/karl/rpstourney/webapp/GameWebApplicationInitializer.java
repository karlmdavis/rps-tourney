package com.justdavis.karl.rpstourney.webapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * The programmatic replacement for <code>web.xml</code> in this project.
 */
public final class GameWebApplicationInitializer implements
		WebApplicationInitializer {
	/**
	 * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		// Create the Spring context for the application.
		AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
		springContext.register(SpringMvcConfig.class);

		// Manage the lifecycle of the root application context
		servletContext.addListener(new ContextLoaderListener(springContext));

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
}
