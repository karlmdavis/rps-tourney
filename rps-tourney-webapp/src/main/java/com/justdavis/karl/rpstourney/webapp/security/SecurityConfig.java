package com.justdavis.karl.rpstourney.webapp.security;

import java.security.AuthProvider;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.RememberMeServices;

import com.justdavis.karl.rpstourney.service.api.auth.game.IGameAuthResource;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;

/**
 * <p>
 * This Spring {@link Configuration} handles most of the configuration related
 * to <a href="http://projects.spring.io/spring-security/">Spring Security</a>.
 * </p>
 * <p>
 * It enables the use of the JSR-250 security annotations on controller methods
 * to secure things: {@link DenyAll}, {@link RolesAllowed}, etc. By default,
 * everything is unsecured. When required, form-based authentication will be
 * used.
 * </p>
 * <p>
 * The actual authentication and authorization is handled with calls to the
 * application's web service. The {@link IGameAuthResource} and
 * {@link IGuestAuthResource} mechanisms are supported.
 * </p>
 */
@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	/*
	 * TODO IGuestAuthResource not yet supported here.
	 */

	/**
	 * The {@link AuthProvider} for {@link IGameAuthResource} logins.
	 */
	@Inject
	private GameLoginAuthenticationProvider gameLoginAuthProvider;

	/**
	 * The {@link RememberMeServices} implementation to be used. (note that
	 * we're cheating a bit here: we both define and use this bean in the same
	 * class).
	 */
	@Inject
	private RememberMeServices rememberMeServices;

	/**
	 * Takes the application's {@link AuthenticationManagerBuilder} bean and
	 * configures it to use the correct authentication mechanisms.
	 */
	@Inject
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		/*
		 * Note: I'm not really sure why this method is used, rather than just
		 * overriding the superclass' configure(AuthenticationManagerBuilder
		 * auth) method, but doing it this way works (and the other way
		 * doesn't). The Spring Security maintainer, Rob Winch, has a GitHub
		 * pull request doing things this way:
		 * https://github.com/jhipster/jhipster-sample-app/pull/2.
		 */

		auth.authenticationProvider(gameLoginAuthProvider);
	}

	/**
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/js/**").permitAll()
			.and().formLogin()
				.loginPage("/login").permitAll()
			.and().logout().permitAll()
			.and().httpBasic()
			.and().rememberMe().rememberMeServices(rememberMeServices)
			.and().anonymous().disable();
	}

	/**
	 * @return the {@link SecurityContextHolderStrategy} that the application is using
	 */
	@Bean
	public SecurityContextHolderStrategy securityContextHolderStrategy() {
		/*
		 * Just exposing this strategy as a bean; Spring Security has a non-bean
		 * mechanism for configuring it.
		 */
		return SecurityContextHolder.getContextHolderStrategy();
	}
}
