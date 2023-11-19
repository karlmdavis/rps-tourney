package com.justdavis.karl.rpstourney.service.app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.spring.SpringResourceFactory;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.apache.cxf.message.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.rpstourney.service.api.game.GameConflictException.GameConflictExceptionMapper;
import com.justdavis.karl.rpstourney.service.app.auth.AuthenticationFilter;
import com.justdavis.karl.rpstourney.service.app.auth.AuthorizationFilter.AuthorizationFilterFeature;
import com.justdavis.karl.rpstourney.service.app.auth.game.InternetAddressReader;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;
import com.justdavis.karl.rpstourney.service.app.config.ServiceConfig;
import com.justdavis.karl.rpstourney.service.app.demo.HelloWorldServiceImpl;
import com.justdavis.karl.rpstourney.service.app.jpa.SpringBindingsForJpa;

/**
 * Provides the primary Spring {@link Configuration} for the JAX-RS application.
 */
@Configuration
@ComponentScan(basePackageClasses = { ServiceApplication.class })
@Import({ SpringBindingsForDaos.class, SpringBindingsForJpa.class })
public class SpringBindingsForWebServices {
	/**
	 * @return Returns the {@link SpringBus} that the CXF application uses. Such a {@link SpringBus} instance
	 *         <strong>must</strong> be provided in the application's {@link Configuration}.
	 */
	@Bean(destroyMethod = "shutdown")
	SpringBus cxf() {
		SpringBus springBus = new SpringBus();

		/*
		 * Enable request/response logging. These messages will use the
		 * 'org.apache.cxf.interceptor.LoggingInInterceptor' and 'org.apache.cxf.interceptor.LoggingOutInterceptor'
		 * logging categories, at the 'INFO' level.
		 */
		LoggerForInbound inInterceptor = new LoggerForInbound();
		LoggerForOutbound outInterceptor = new LoggerForOutbound();
		springBus.getInInterceptors().add(inInterceptor);
		springBus.getOutInterceptors().add(outInterceptor);
		springBus.getInFaultInterceptors().add(inInterceptor);
		springBus.getOutFaultInterceptors().add(outInterceptor);

		return springBus;
	}

	/**
	 * @return Returns the {@link Application} instance that the JAX-RS application uses. Such an {@link Application}
	 *         instance <strong>must</strong> be provided in the application's {@link Configuration}.
	 */
	@Bean
	ServiceApplication jaxRsApiApplication() {
		return new ServiceApplication();
	}

	/**
	 * @param springApplicationContext
	 *            the Spring {@link ApplicationContext} that the {@link Server} will be running in
	 * @param authenticationFilter
	 *            the injected {@link AuthenticationFilter} bean for the application
	 * @return Returns the {@link Server} instance that the CXF application uses. Such an {@link Server} instance
	 *         <strong>must</strong> be provided in the application's {@link Configuration}. This largely replaces the
	 *         resource/provider declarations that would be included in an {@link Application} instance for non-Spring
	 *         JAX-RS applications.
	 */
	@Bean
	@DependsOn({ "cxf" })
	Server jaxRsServer(ApplicationContext springApplicationContext, AuthenticationFilter authenticationFilter) {
		JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance().createEndpoint(jaxRsApiApplication(),
				JAXRSServerFactoryBean.class);

		List<Object> providers = getProviders();
		providers.add(authenticationFilter);
		factory.setProviders(providers);

		/*
		 * The following Spring Beans will be request-scoped.
		 */
		List<ResourceProvider> resourceProviders = new LinkedList<>();
		resourceProviders.add(new RequestScopeResourceFactory("serviceStatusResourceImpl"));
		resourceProviders.add(new RequestScopeResourceFactory("helloWorldResource"));
		resourceProviders.add(new RequestScopeResourceFactory("accountsResourceImpl"));
		resourceProviders.add(new RequestScopeResourceFactory("guestAuthResourceImpl"));
		resourceProviders.add(new RequestScopeResourceFactory("gameAuthResourceImpl"));
		resourceProviders.add(new RequestScopeResourceFactory("playersResourceImpl"));
		resourceProviders.add(new RequestScopeResourceFactory("gameResourceImpl"));

		/*
		 * Initialize all of the SpringResourceFactory instances. This non-obvious step is required, and I came across
		 * the idea here: https://issues.apache.org/jira/browse/CXF-3725
		 */
		for (ResourceProvider resourceProvider : resourceProviders)
			if (resourceProvider instanceof SpringResourceFactory)
				((SpringResourceFactory) resourceProvider).setApplicationContext(springApplicationContext);

		/*
		 * This is equivalent to <jaxrs:serviceFactories/> elements in a Spring XML configuration, as can be seen in
		 * org.apache.cxf.jaxrs.spring .JAXRSServerFactoryBeanDefinitionParser.mapElement(...).
		 */
		factory.setResourceProviders(resourceProviders);

		/*
		 * Set the interceptors to be used.
		 */
		List<Interceptor<? extends Message>> inInterceptors = new ArrayList<>();
		inInterceptors.add(new CxfBeanValidationInInterceptor());
		factory.setInInterceptors(inInterceptors);

		return factory.create();
	}

	/**
	 * @return The {@link List} of JAX-RS/CXF providers that Spring/CXF should register via
	 *         {@link JAXRSServerFactoryBean#setProviders(List)}. Basically, this would be anything else that can be a
	 *         singleton that might have been specified in {@link Application#getClasses()} or
	 *         {@link Application#getSingletons()}.
	 */
	private static List<Object> getProviders() {
		List<Object> providers = new LinkedList<>();

		/*
		 * Right now, these providers are all being created manually. However, if any of them require injection, they'd
		 * need to be injected into here via Spring.
		 */

		// Register the entity translators.
		providers.add(new InternetAddressReader());

		// Register any custom context providers.
		providers.add(new AuthorizationFilterFeature());

		// Register the exception mappers.
		providers.add(new GameConflictExceptionMapper());

		// Register the bean validator's exception mapper.
		providers.add(new ValidationExceptionMapper());

		return providers;
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	HelloWorldServiceImpl helloWorldResource() {
		return new HelloWorldServiceImpl();
	}

	/**
	 * @param configLoader
	 *            the injected {@link IConfigLoader} for the application
	 * @return the application's settings, as represented by a {@link ServiceConfig} instance
	 */
	@Bean
	ServiceConfig serviceConfig(IConfigLoader configLoader) {
		return configLoader.getConfig();
	}

	/**
	 * @param dsConnectorsManager
	 *            the injected {@link DataSourceConnectorsManager} for the application
	 * @param serviceConfig
	 *            the injected {@link ServiceConfig} for the application
	 * @return the {@link DataSource} for the application's database
	 */
	@Bean
	public DataSource dataSource(DataSourceConnectorsManager dsConnectorsManager, ServiceConfig serviceConfig) {
		return dsConnectorsManager.createDataSource(serviceConfig.getDataSourceCoordinates());
	}
}
