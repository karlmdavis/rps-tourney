package com.justdavis.karl.rpstourney.webapp.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.RememberMeServices;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource;

/**
 * Unit tests for {@link DefaultGuestLoginManager}.
 */
public final class DefaultGuestLoginManagerTest {
	/**
	 * Verifies that
	 * {@link DefaultGuestLoginManager#loginClientAsGuest(HttpServletRequest, HttpServletResponse)}
	 * works as expected.
	 */
	@Test
	public void loginClientAsGuest() {
		// Build the mock objects needed.
		SecurityContext security = new SecurityContextImpl();
		SecurityContextHolderStrategy securityHolder = new MockSecurityContextHolderStrategy(
				security);
		Account mockAccount = new Account();
		IGuestAuthResource guestAuthClient = new MockGuestAuthClient(
				mockAccount);
		MockRememberMeServices rememberMeServices = new MockRememberMeServices();
		HttpServletRequest mockRequest = new MockHttpServletRequest();
		HttpServletResponse mockResponse = new MockHttpServletResponse();

		// Try logging in and verify the results.
		DefaultGuestLoginManager loginManager = new DefaultGuestLoginManager(
				securityHolder, guestAuthClient, rememberMeServices);
		loginManager.loginClientAsGuest(mockRequest, mockResponse);
		Assert.assertNotNull(securityHolder.getContext().getAuthentication());
		Assert.assertEquals(mockAccount, securityHolder.getContext()
				.getAuthentication().getPrincipal());
		Assert.assertNotNull(rememberMeServices.successfulAuthentication);
		Assert.assertEquals(mockAccount,
				rememberMeServices.successfulAuthentication.getPrincipal());
	}

	/**
	 * Verifies that
	 * {@link DefaultGuestLoginManager#loginClientAsGuest(HttpServletRequest, HttpServletResponse)}
	 * fails as expected when called for an already-authenticated request.
	 */
	@Test(expected = BadCodeMonkeyException.class)
	public void loginClientAsGuest_alreadyAuhenticated() {
		// Build the mock objects needed.
		Account mockAccount = new Account();
		SecurityContext security = new SecurityContextImpl();
		Authentication mockAuth = new WebServiceAccountAuthentication(
				mockAccount);
		security.setAuthentication(mockAuth);
		SecurityContextHolderStrategy securityHolder = new MockSecurityContextHolderStrategy(
				security);
		IGuestAuthResource guestAuthClient = new MockGuestAuthClient(null);
		MockRememberMeServices rememberMeServices = new MockRememberMeServices();
		HttpServletRequest mockRequest = new MockHttpServletRequest();
		HttpServletResponse mockResponse = new MockHttpServletResponse();

		// Try logging in, which should go boom.
		DefaultGuestLoginManager loginManager = new DefaultGuestLoginManager(
				securityHolder, guestAuthClient, rememberMeServices);
		loginManager.loginClientAsGuest(mockRequest, mockResponse);
	}

	/**
	 * A mock {@link SecurityContextHolderStrategy} implementation for use in
	 * tests.
	 */
	private static final class MockSecurityContextHolderStrategy implements
			SecurityContextHolderStrategy {
		private final SecurityContext securityContext;

		/**
		 * Constructs a new {@link MockSecurityContextHolderStrategy} instance.
		 * 
		 * @param securityContext
		 *            the value to use for {@link #getContext()}
		 */
		public MockSecurityContextHolderStrategy(SecurityContext securityContext) {
			this.securityContext = securityContext;
		}

		/**
		 * @see org.springframework.security.core.context.SecurityContextHolderStrategy#clearContext()
		 */
		@Override
		public void clearContext() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see org.springframework.security.core.context.SecurityContextHolderStrategy#getContext()
		 */
		@Override
		public SecurityContext getContext() {
			return securityContext;
		}

		/**
		 * @see org.springframework.security.core.context.SecurityContextHolderStrategy#setContext(org.springframework.security.core.context.SecurityContext)
		 */
		@Override
		public void setContext(SecurityContext context) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see org.springframework.security.core.context.SecurityContextHolderStrategy#createEmptyContext()
		 */
		@Override
		public SecurityContext createEmptyContext() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * A mock {@link IGuestAuthResource} client implementation for use in tests.
	 */
	private final class MockGuestAuthClient implements IGuestAuthResource {
		private final Account loginResult;

		/**
		 * Constructs a new {@link MockGuestAuthClient} instance.
		 * 
		 * @param loginResult
		 *            the vaue to return for {@link #loginAsGuest()}
		 */
		public MockGuestAuthClient(Account loginResult) {
			this.loginResult = loginResult;
		}

		/**
		 * @see com.justdavis.karl.rpstourney.service.api.auth.guest.IGuestAuthResource#loginAsGuest()
		 */
		@Override
		public Account loginAsGuest() {
			return loginResult;
		}
	}

	/**
	 * A mock {@link RememberMeServices} implementation for use in tests.
	 */
	private final class MockRememberMeServices implements RememberMeServices {
		private Authentication successfulAuthentication;

		/**
		 * @see org.springframework.security.web.authentication.RememberMeServices#autoLogin(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		@Override
		public Authentication autoLogin(HttpServletRequest request,
				HttpServletResponse response) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see org.springframework.security.web.authentication.RememberMeServices#loginFail(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		@Override
		public void loginFail(HttpServletRequest request,
				HttpServletResponse response) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see org.springframework.security.web.authentication.RememberMeServices#loginSuccess(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse,
		 *      org.springframework.security.core.Authentication)
		 */
		@Override
		public void loginSuccess(HttpServletRequest request,
				HttpServletResponse response,
				Authentication successfulAuthentication) {
			this.successfulAuthentication = successfulAuthentication;
		}
	}
}
