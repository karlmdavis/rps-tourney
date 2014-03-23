package com.justdavis.karl.rpstourney.service.app;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.spring.SpringResourceFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.justdavis.karl.misc.SpringConfigForJEMisc;
import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.misc.datasources.schema.LiquibaseSchemaManager;
import com.justdavis.karl.rpstourney.service.app.auth.AuthenticationFilter;
import com.justdavis.karl.rpstourney.service.app.auth.AccountSecurityContext.AccountSecurityContextProvider;
import com.justdavis.karl.rpstourney.service.app.auth.AuthorizationFilter.AuthorizationFilterFeature;
import com.justdavis.karl.rpstourney.service.app.auth.game.InternetAddressReader;
import com.justdavis.karl.rpstourney.service.app.config.ServiceConfig;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.app.config.XmlConfigLoader;
import com.justdavis.karl.rpstourney.service.app.demo.HelloWorldServiceImpl;
import com.justdavis.karl.rpstourney.service.app.jpa.SpringJpaConfig;

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
 * @see AppSpringConfig
 */
public final class GameServiceApplicationInitializer implements
		WebApplicationInitializer {
	/**
	 * The web application context-wide initialization parameter that specifies
	 * the Spring parent {@link ApplicationContext} instance that should used
	 * (if any). This is mostly intended for use by unit tests.
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
		 * com.justdavis.karl.rpstourney.service.app.SpringITConfigWithJetty for
		 * more details.)
		 */
		ApplicationContext springParentContext = findSpringParentContext(container);
		if (springParentContext != null) {
			rootContext.setParent(springParentContext);
		} else {
			rootContext.register(AppSpringConfig.class);
		}
		rootContext.refresh();

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

	/**
	 * Provides the primary Spring {@link Configuration} for the JAX-RS
	 * application.
	 */
	@Configuration
	@EnableJpaRepositories
	@EnableTransactionManagement
	@ComponentScan(basePackageClasses = { ServiceApplication.class }, excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "com.justdavis.karl.rpstourney.service.app.SpringITConfigWithJetty") })
	@Import({ SpringConfigForJEMisc.class, SpringJpaConfig.class })
	public static class AppSpringConfig {
		/**
		 * @return Returns the {@link SpringBus} that the CXF application uses.
		 *         Such a {@link SpringBus} instance <strong>must</strong> be
		 *         provided in the application's {@link Configuration}.
		 */
		@Bean(destroyMethod = "shutdown")
		SpringBus cxf() {
			return new SpringBus();
		}

		/**
		 * @return Returns the {@link Application} instance that the JAX-RS
		 *         application uses. Such an {@link Application} instance
		 *         <strong>must</strong> be provided in the application's
		 *         {@link Configuration}.
		 */
		@Bean
		ServiceApplication jaxRsApiApplication() {
			return new ServiceApplication();
		}

		/**
		 * @param springApplicationContext
		 *            the Spring {@link ApplicationContext} that the
		 *            {@link Server} will be running in
		 * @param authenticationFilter
		 *            the injected {@link AuthenticationFilter} bean for the
		 *            application
		 * @return Returns the {@link Server} instance that the CXF application
		 *         uses. Such an {@link Server} instance <strong>must</strong>
		 *         be provided in the application's {@link Configuration}. This
		 *         largely replaces the resource/provider declarations that
		 *         would be included in an {@link Application} instance for
		 *         non-Spring JAX-RS applications.
		 */
		@Bean
		@DependsOn({ "cxf" })
		Server jaxRsServer(ApplicationContext springApplicationContext,
				AuthenticationFilter authenticationFilter) {
			JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance()
					.createEndpoint(jaxRsApiApplication(),
							JAXRSServerFactoryBean.class);

			List<Object> providers = getProviders();
			providers.add(authenticationFilter);
			factory.setProviders(providers);

			/*
			 * The following Spring Beans will be request-scoped.
			 */
			List<ResourceProvider> resourceProviders = new LinkedList<>();
			resourceProviders.add(new RequestScopeResourceFactory(
					"helloWorldResource"));
			resourceProviders.add(new RequestScopeResourceFactory(
					"accountsResourceImpl"));
			resourceProviders.add(new RequestScopeResourceFactory(
					"guestAuthResourceImpl"));
			resourceProviders.add(new RequestScopeResourceFactory(
					"gameAuthResourceImpl"));

			/*
			 * Initialize all of the SpringResourceFactory instances. This
			 * non-obvious step is required, and I came across the idea here:
			 * https://issues.apache.org/jira/browse/CXF-3725
			 */
			for (ResourceProvider resourceProvider : resourceProviders)
				if (resourceProvider instanceof SpringResourceFactory)
					((SpringResourceFactory) resourceProvider)
							.setApplicationContext(springApplicationContext);

			/*
			 * This is equivalent to <jaxrs:serviceFactories/> elements in a
			 * Spring XML configuration, as can be seen in
			 * org.apache.cxf.jaxrs.spring
			 * .JAXRSServerFactoryBeanDefinitionParser.mapElement(...).
			 */
			factory.setResourceProviders(resourceProviders);

			return factory.create();
		}

		/**
		 * @return The {@link List} of JAX-RS/CXF providers that Spring/CXF
		 *         should register via
		 *         {@link JAXRSServerFactoryBean#setProviders(List)}. Basically,
		 *         this would be anything else that can be a singleton that
		 *         might have been specified in {@link Application#getClasses()}
		 *         or {@link Application#getSingletons()}.
		 */
		private static List<Object> getProviders() {
			List<Object> providers = new LinkedList<>();

			/*
			 * Right now, these providers are all being created manually.
			 * However, if any of them require injection, they'd need to be
			 * injected into here via Spring.
			 */

			// Register the entity translators.
			providers.add(new InternetAddressReader());

			// Register any custom context providers.
			providers.add(new AccountSecurityContextProvider());

			// Register any custom context providers.
			providers.add(new AuthorizationFilterFeature());

			return providers;
		}

		@Bean
		@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
		HelloWorldServiceImpl helloWorldResource() {
			return new HelloWorldServiceImpl();
		}

		/**
		 * @param dsConnectorsManager
		 *            the injected {@link DataSourceConnectorsManager} for the
		 *            application
		 * @return the {@link IConfigLoader} implementation for the application
		 */
		@Bean
		IConfigLoader configLoader(
				DataSourceConnectorsManager dsConnectorsManager) {
			return new XmlConfigLoader(dsConnectorsManager);
		}

		/**
		 * @param configLoader
		 *            the injected {@link IConfigLoader} for the application
		 * @return the application's settings, as represented by a
		 *         {@link ServiceConfig} instance
		 */
		@Bean
		ServiceConfig serviceConfig(IConfigLoader configLoader) {
			return configLoader.getConfig();
		}

		/**
		 * @param dsConnectorsManager
		 *            the injected {@link DataSourceConnectorsManager} for the
		 *            application
		 * @param serviceConfig
		 *            the injected {@link ServiceConfig} for the application
		 * @return the {@link DataSource} for the application's database
		 */
		@Bean
		public DataSource dataSource(
				DataSourceConnectorsManager dsConnectorsManager,
				ServiceConfig serviceConfig) {
			return dsConnectorsManager.createDataSource(serviceConfig
					.getDataSourceCoordinates());
		}

		/**
		 * @return the {@link IDataSourceSchemaManager} for the application to
		 *         use
		 */
		@Bean
		public IDataSourceSchemaManager schemaManager(
				DataSourceConnectorsManager connectorsManager) {
			/*
			 * The
			 * rps-tourney-webservice/src/main/resources/liquibase-change-log
			 * .xml file contains the Liquibase schema changelog, which will be
			 * applied at application startup via the DatabaseSchemaInitializer.
			 */
			return new LiquibaseSchemaManager(connectorsManager,
					"liquibase-change-log.xml");
		}
	}
}