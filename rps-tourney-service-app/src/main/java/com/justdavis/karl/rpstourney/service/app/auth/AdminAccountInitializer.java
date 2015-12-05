package com.justdavis.karl.rpstourney.service.app.auth;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletContextListener;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.SecurityRole;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.auth.game.IGameLoginIndentitiesDao;
import com.justdavis.karl.rpstourney.service.app.auth.game.PasswordUtils;
import com.justdavis.karl.rpstourney.service.app.config.ServiceConfig;

/**
 * This {@link ServletContextListener} runs at application startup to ensure
 * that the application has the expected default {@link SecurityRole#ADMINS}
 * {@link GameLoginIdentity}. If not, it fixes that.
 * 
 * @see ServiceConfig#getAdminAccountConfig()
 */
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Profile({ SpringProfile.PRODUCTION, SpringProfile.INTEGRATION_TESTS_WITH_JETTY })
public class AdminAccountInitializer implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminAccountInitializer.class);

	private final ServiceConfig config;
	private final IGameLoginIndentitiesDao loginsDao;

	/**
	 * Constructs a new {@link AdminAccountInitializer} instance.
	 * 
	 * @param config
	 *            the injected {@link ServiceConfig} to use
	 * @param loginsDao
	 *            the injected {@link IGameLoginIndentitiesDao} to use
	 */
	@Inject
	public AdminAccountInitializer(ServiceConfig config, IGameLoginIndentitiesDao loginsDao) {
		this.config = config;
		this.loginsDao = loginsDao;
	}

	/**
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		GameLoginIdentity adminLogin = loginsDao.find(config.getAdminAccountConfig().getAddress());
		if (adminLogin == null) {
			adminLogin = createAdminLogin();
		} else {
			rectifyAdminEntities(adminLogin);
		}
	}

	/**
	 * @return a new {@link GameLoginIdentity} instance for the application's
	 *         default admin, with associated {@link Account}, etc.
	 */
	private GameLoginIdentity createAdminLogin() {
		InternetAddress emailAddress = config.getAdminAccountConfig().getAddress();
		LOGGER.info("Creating admin login: {}", emailAddress);

		Account account = new Account(SecurityRole.ADMINS, SecurityRole.USERS);
		String password = config.getAdminAccountConfig().getPassword();
		String passwordHash = PasswordUtils.hashPassword(password);
		GameLoginIdentity login = new GameLoginIdentity(account, emailAddress, passwordHash);

		loginsDao.save(login);
		return login;
	}

	/**
	 * Verifies that the specified {@link GameLoginIdentity} has the correct
	 * permissions, username, and password. If not, updates it such that it
	 * does.
	 * 
	 * @param adminLogin
	 *            the admin {@link GameLoginIdentity} to be rectified
	 */
	private void rectifyAdminEntities(GameLoginIdentity adminLogin) {
		boolean somethingWasChanged = false;

		/*
		 * Actually, no need to check the email address, since we found the
		 * login with it.
		 */

		// Handle the password.
		String expectedPassword = config.getAdminAccountConfig().getPassword();
		String expectedPasswordHash = PasswordUtils.hashPassword(expectedPassword);
		if (!expectedPasswordHash.equals(adminLogin.getPasswordHash())) {
			adminLogin.setPasswordHash(expectedPasswordHash);
			LOGGER.warn("Admin account's login password was incorrect.");
			somethingWasChanged = true;
		}

		// Handle the account permissions.
		if (!adminLogin.getAccount().hasRole(SecurityRole.ADMINS)) {
			adminLogin.getAccount().getRoles().add(SecurityRole.ADMINS);
			LOGGER.warn("Admin account's permissions were incorrect.");
			somethingWasChanged = true;
		}

		// Save the login and associated account.
		if (somethingWasChanged) {
			loginsDao.save(adminLogin);
			LOGGER.warn("Updated and saved admin account/login.");
		}
	}
}
