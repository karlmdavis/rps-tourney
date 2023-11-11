package com.justdavis.karl.rpstourney.webapp;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.justdavis.karl.rpstourney.webapp.config.AppConfig;
import com.justdavis.karl.rpstourney.webapp.config.BaseUrlInterceptor;
import com.justdavis.karl.rpstourney.webapp.config.IConfigLoader;
import com.justdavis.karl.rpstourney.webapp.error.UnhandledExceptionResolver;
import com.justdavis.karl.rpstourney.webapp.security.SecurityConfig;

/**
 * The Spring configuration used by {@link GameWebApplicationInitializer}, and
 * thus the entire Spring Web MVC application.
 */
@Configuration
@Import({ SecurityConfig.class, GameClientBindings.class, ConfigLoaderBindingForProduction.class })
@EnableWebMvc
@ComponentScan(basePackageClasses = { SpringMvcConfig.class })
public class SpringMvcConfig implements WebMvcConfigurer {
	@Inject
	private BaseUrlInterceptor baseUrlInterceptor;

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureDefaultServletHandling(org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer)
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable("default");
	}

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureContentNegotiation(org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer)
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		/*
		 * Older browsers, like IE8, send less-than-great "Accept" headers.
		 * We'll assume that any client that cares will request a specific
		 * content type, and anything requesting "*" is a dumb browser. (See
		 * https://jira.spring.io/browse/SPR-12481 for details on how this
		 * doesn't work quite as expected.)
		 */
		configurer.defaultContentType(MediaType.TEXT_HTML);
	}

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry)
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/*
		 * Each entry here will also need a corresponding entry in
		 * SecurityConfig.configure(WebSecurity).
		 */

		registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/resources/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/resources/js/");
		registry.addResourceHandler("/i18n/**").addResourceLocations("/WEB-INF/i18n/");

		/*
		 * Though all of the sources from Bootstrap and FontAwesome are
		 * available, only the fonts from them are needed. (All of their LESS
		 * and JS have been copied by wro4j-maven-plugin into the css and js
		 * folders.)
		 */
		registry.addResourceHandler("/bootstrap-3.2.0/fonts/**")
				.addResourceLocations("/WEB-INF/resources/bootstrap-3.2.0/fonts/");
		registry.addResourceHandler("/font-awesome-4.7.0/fonts/**")
				.addResourceLocations("/WEB-INF/resources/font-awesome-4.7.0/fonts/");
	}

	/**
	 * @return the {@link ViewResolver} for the application to use
	 */
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		/*
		 * The login view's controller is provided by the Spring Security
		 * filters.
		 */
		registry.addViewController("/login").setViewName("login");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	/**
	 * @return the {@link MessageSource} for the application to use
	 */
	@Bean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("/WEB-INF/i18n/messages");
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RequestResponseLoggingInterceptor());
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(baseUrlInterceptor);
	}

	/**
	 * @return the {@link LocaleChangeInterceptor} for the application to use
	 */
	@Bean
	public HandlerInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	/**
	 * @return the {@link CookieLocaleResolver} that will be used as the
	 *         application's {@link LocaleResolver} (Spring looks this up by
	 *         bean name)
	 */
	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#extendHandlerExceptionResolvers(java.util.List)
	 */
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
		/*
		 * Add a Spring MVC {@link HandlerExceptionResolver} that configures how
		 * the application will render unhandled/uncaught {@link Exception}s.
		 */
		resolvers.add(new UnhandledExceptionResolver());
	}

	/**
	 * @param configLoader
	 *            the injected {@link IConfigLoader} for the application
	 * @return the application's settings, as represented by a {@link AppConfig}
	 *         instance
	 */
	@Bean
	AppConfig appConfig(IConfigLoader configLoader) {
		return configLoader.getConfig();
	}
}
