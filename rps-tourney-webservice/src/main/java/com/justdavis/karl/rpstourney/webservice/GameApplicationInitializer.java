package com.justdavis.karl.rpstourney.webservice;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
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
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.IDataSourceConnector;
import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext.AccountSecurityContextProvider;
import com.justdavis.karl.rpstourney.webservice.auth.AuthenticationFilter;
import com.justdavis.karl.rpstourney.webservice.auth.AuthorizationFilter.AuthorizationFilterFeature;
import com.justdavis.karl.rpstourney.webservice.auth.game.InternetAddressReader;
import com.justdavis.karl.rpstourney.webservice.config.GameConfig;
import com.justdavis.karl.rpstourney.webservice.config.IConfigLoader;
import com.justdavis.karl.rpstourney.webservice.config.XmlConfigLoader;
import com.justdavis.karl.rpstourney.webservice.demo.HelloWorldServiceImpl;

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
public final class GameApplicationInitializer implements
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
		 * com.justdavis.karl.rpstourney.webservice.SpringITConfigWithJetty for
		 * more details.)
		 */
		ApplicationContext springParentContext = findSpringParentContext(container);
		if (springParentContext != null) {
			rootContext.setParent(springParentContext);
		} else {
			rootContext.register(AppSpringConfig.class);
		}

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
	@ComponentScan(basePackageClasses = { IDataSourceConnector.class,
			GameApplication.class }, excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "com.justdavis.karl.rpstourney.webservice.SpringITConfigWithJetty") })
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
		GameApplication jaxRsApiApplication() {
			return new GameApplication();
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
					"accountService"));
			resourceProviders.add(new RequestScopeResourceFactory(
					"guestAuthService"));
			resourceProviders.add(new RequestScopeResourceFactory(
					"gameAuthService"));

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
		 *         {@link GameConfig} instance
		 */
		@Bean
		GameConfig gameConfig(IConfigLoader configLoader) {
			return configLoader.getConfig();
		}

		/**
		 * @param dsConnectorsManager
		 *            the injected {@link DataSourceConnectorsManager} for the
		 *            application
		 * @param gameConfig
		 *            the injected {@link GameConfig} for the application
		 * @return the {@link DataSource} for the application's database
		 */
		@Bean
		public DataSource dataSource(
				DataSourceConnectorsManager dsConnectorsManager,
				GameConfig gameConfig) {
			return dsConnectorsManager.createDataSource(gameConfig
					.getDataSourceCoordinates());
		}

		/**
		 * @return the Spring {@link JpaVendorAdapter} for the application's
		 *         database
		 */
		@Bean
		public JpaVendorAdapter jpaVendorAdapter() {
			HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
			hibernateJpaVendorAdapter.setShowSql(false);
			hibernateJpaVendorAdapter.setGenerateDdl(true);
			return hibernateJpaVendorAdapter;
		}

		/**
		 * @param dataSource
		 *            the injected {@link DataSource} that the JPA
		 *            {@link EntityManagerFactory} should be connected to
		 * @param jpaVendorAdapter
		 *            the injected {@link JpaVendorAdapter} for the application
		 * @return the {@link LocalContainerEntityManagerFactoryBean} instance
		 *         that Spring will use to inject the application's
		 *         {@link EntityManagerFactory} and {@link EntityManager}s, when
		 *         requested
		 */
		@Bean
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(
				DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
			LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
			lef.setDataSource(dataSource);
			lef.setJpaVendorAdapter(jpaVendorAdapter);
			// lef.setPackagesToScan("hello");
			return lef;
		}

		/**
		 * @param entityManagerFactoryBean
		 *            the injected
		 *            {@link LocalContainerEntityManagerFactoryBean} that the
		 *            {@link PlatformTransactionManager} will be associated with
		 * @return the {@link PlatformTransactionManager} that Spring will use
		 *         for its managed transactions
		 */
		@Bean
		public PlatformTransactionManager transactionManager(
				LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
			JpaTransactionManager transactionManager = new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(entityManagerFactoryBean
					.getObject());
			return transactionManager;
		}

		/**
		 * @return a Spring {@link BeanPostProcessor} that enables the use of
		 *         the JPA {@link PersistenceUnit} and
		 *         {@link PersistenceContext} annotations for injection of
		 *         {@link EntityManagerFactory} and {@link EntityManager}
		 *         instances, respectively, into beans
		 */
		@Bean
		public PersistenceAnnotationBeanPostProcessor persistenceAnnotationProcessor() {
			return new PersistenceAnnotationBeanPostProcessor();
		}
	}
}