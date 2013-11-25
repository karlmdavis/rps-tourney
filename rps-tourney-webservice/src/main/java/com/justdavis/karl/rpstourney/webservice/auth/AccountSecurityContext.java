package com.justdavis.karl.rpstourney.webservice.auth;

import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.jaxrs.impl.PropertyHolderFactory;
import org.apache.cxf.jaxrs.impl.PropertyHolderFactory.PropertyHolder;
import org.apache.cxf.message.Message;

/**
 * This {@link SecurityContext} implementation uses {@link Account}s for
 * {@link #getUserPrincipal()} and {@link #isUserInRole(String)}.
 */
public final class AccountSecurityContext implements SecurityContext {
	private final Account account;
	private final boolean secure;

	/**
	 * Constructs a new {@link AccountSecurityContext} instance.
	 * 
	 * @param account
	 *            the {@link Account}/{@link Principal} that has authenticated
	 *            with the application, or <code>null</code> if the user is
	 *            anonymous
	 * @param secure
	 *            <code>true</code> if the connection for the request is
	 *            encrypted (e.g. <code>https</code>), <code>false</code> if
	 *            it's not
	 */
	public AccountSecurityContext(Account account, boolean secure) {
		this.account = account;
		this.secure = secure;
	}

	/**
	 * Constructs a new {@link AccountSecurityContext} instance.
	 * 
	 * @param account
	 *            the {@link Account}/{@link Principal} that has authenticated
	 *            with the application, or <code>null</code> if the user is
	 *            anonymous
	 */
	public AccountSecurityContext(Account account) {
		this(account, false);
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#getUserPrincipal()
	 */
	@Override
	public Account getUserPrincipal() {
		return account;
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#isUserInRole(java.lang.String)
	 */
	@Override
	public boolean isUserInRole(String role) {
		if (account == null)
			return false;

		for (SecurityRole userRole : account.getRoles())
			if (userRole.getId().equals(role))
				return true;
		return false;
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#isSecure()
	 */
	@Override
	public boolean isSecure() {
		return secure;
	}

	/**
	 * @see javax.ws.rs.core.SecurityContext#getAuthenticationScheme()
	 */
	@Override
	public String getAuthenticationScheme() {
		return AuthenticationFilter.AUTH_SCHEME;
	}

	/**
	 * <p>
	 * Allows {@link AccountSecurityContext} instances to be injected into
	 * resources via the {@link Context} annotation, as follows:
	 * </p>
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * &#64;Path("some")
	 * public class SomeResource {
	 *   &#64;Context
	 *   private AccountSecurityContext securityContext;
	 *   
	 *   &#64;GET
	 *   public void foo() {
	 *     ...
	 *   }
	 * }
	 * </blockquote>
	 * </pre>
	 * <p>
	 * FIXME While JAX-RS already allows for the (more general)
	 * {@link SecurityContext} to be injected in this manner, there's a bug in
	 * Apache CXF 2.7 that prevents this from respecting customized
	 * {@link SecurityContext}s that have been set via
	 * {@link ContainerRequestContext#setSecurityContext(SecurityContext)}. From
	 * looking at the patches, it seems that it is fixed in 3.0 (whenever that's
	 * released). Until then, this application will use this custom extension,
	 * which relies on the behavior of
	 * {@link AuthenticationFilter#filter(ContainerRequestContext)}.
	 * </p>
	 * <p>
	 * This extension is specific to Apache CXF; it will not work with other
	 * JAX-RS implementations.
	 * </p>
	 */
	public static final class AccountSecurityContextProvider implements
			ContextProvider<AccountSecurityContext> {
		/**
		 * The {@link ContainerRequestContext#getProperty(String)} key used to
		 * store the {@link AccountSecurityContext} instance for each request.
		 */
		static final String PROP_SECURITY_CONTEXT = AccountSecurityContext.class
				.getName();

		/**
		 * @see org.apache.cxf.jaxrs.ext.ContextProvider#createContext(org.apache.cxf.message.Message)
		 */
		@Override
		public AccountSecurityContext createContext(Message message) {
			/*
			 * Pull (what should be) the the AccountSecurityContext from the
			 * message property.
			 */
			PropertyHolder propHolder = PropertyHolderFactory
					.getPropertyHolder(message);
			Object securityContextObject = propHolder
					.getProperty(PROP_SECURITY_CONTEXT);

			// Sanity check: was the AccountSecurityContext injected?
			if (securityContextObject == null)
				throw new IllegalStateException(String.format(
						"%s not injected by %s", AccountSecurityContext.class,
						AuthenticationFilter.class));
			// Sanity check: was the correct/expected object injected?
			if (!(securityContextObject instanceof AccountSecurityContext))
				throw new IllegalStateException(String.format(
						"Injected security context is wrong type: %s",
						AccountSecurityContext.class));

			return (AccountSecurityContext) securityContextObject;
		}
	}
}