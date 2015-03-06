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
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
	 * The {@link AuthenticationSuccessHandler} for {@link IGameAuthResource}
	 * logins.
	 */
	@Inject
	private GameLoginSuccessHandler gameLoginSuccessHandler;

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
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
				// Instruct Spring Security to completely ignore these requests.
				.antMatchers("/css/**")
				.antMatchers("/js/**")
				.antMatchers("/i18n/**")
				.antMatchers("/bootstrap-3.2.0/fonts/**");
	}

	/**
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * This is fairly unusual for a Spring Security config (note the lack of
		 * a '.authorizeRequests()' call), but meets this application's need
		 * quite well. All or almost all of the application's authorization is
		 * done programmatically, and the MVC Controllers are all responsible
		 * for redirecting to '/login' as necessary.
		 */
		
		/*
		 * Spring Security's "remember me" services are used as a form of
		 * automatic anonymous-ish login. If users, for example, start a game,
		 * they will be logged in automatically with a new anonymous account
		 * that gets created for them. If the user at some point decides they
		 * want a normal username+password account (perhaps to play the same
		 * games across multiple devices), the anonymous accounts can be
		 * "merged" into the new username+password accounts.
		 */
		gameLoginSuccessHandler.setDefaultTargetUrl("/account");
		http
			.formLogin()
				.loginPage("/login")
				.successHandler(gameLoginSuccessHandler)
				.and()
			.logout()
				.and()
			.httpBasic()
				.and()
			.rememberMe()
				.rememberMeServices(rememberMeServices)
				.key(CustomRememberMeServices.REMEMBER_ME_TOKEN_KEY)
				.and()
			.anonymous().disable();
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
