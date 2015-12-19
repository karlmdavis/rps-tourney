package com.justdavis.karl.rpstourney.service.app.auth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * {@link AuthorizationFilter} instances can be registered for specific JAX-RS
 * resources, and will ensure that the {@link SecurityContext} of users
 * requesting the resource is in at least one of the required roles.
 * </p>
 * <p>
 * This filter will typically be dynamically registered by
 * {@link AuthorizationFilterFeature}, but only for those webservice methods and
 * classes marked with {@link DenyAll} and/or {@link RolesAllowed} annotations.
 * </p>
 */
@Priority(Priorities.AUTHORIZATION)
public final class AuthorizationFilter implements ContainerRequestFilter {
	private final String[] rolesAllowed;

	/**
	 * Constructs a new instance of the {@link AuthorizationFilter}.
	 * 
	 * @param rolesAllowed
	 *            the whitelist of {@link SecurityContext} roles that are
	 *            allowed to access resources "guarded" by the
	 *            {@link AuthorizationFilter} ( <code>null</code> values/entries
	 *            are ignored)
	 */
	public AuthorizationFilter(String... rolesAllowed) {
		this.rolesAllowed = rolesAllowed;
	}

	/**
	 * <strong>Only for use in testing.</strong>
	 * 
	 * @return the array of role IDs that was passed to
	 *         {@link #AuthorizationFilter(String...)}
	 */
	String[] getRolesAllowed() {
		return Arrays.copyOf(rolesAllowed, rolesAllowed.length);
	}

	/**
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		/*
		 * Grab the SecurityContext, which should have been setup by the
		 * AuthenticationFilter. FIXME Grabbing it from a property is a
		 * workaround for a bug in CXF 2.7. See AccountSecurityContextProvider
		 * for details.
		 */
		SecurityContext securityContext = requestContext.getSecurityContext();
		// SecurityContext securityContext = (SecurityContext) requestContext
		// .getProperty(AccountSecurityContextProvider.PROP_SECURITY_CONTEXT);

		// Is the user in any of the allowed roles? If so, we're done.
		for (String roleAllowed : rolesAllowed)
			if (securityContext.isUserInRole(roleAllowed))
				return;

		// The user is not authorized. Abort the request now.
		requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
	}

	/**
	 * This {@link DynamicFeature} will register {@link AuthorizationFilter} (as
	 * appropriate) for JAX-RS webservice methods and classes that are marked up
	 * with any of the {@link PermitAll}, {@link DenyAll}, and/or
	 * {@link RolesAllowed} annotations.
	 */
	public static final class AuthorizationFilterFeature implements DynamicFeature {
		private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilterFeature.class);

		/**
		 * @see javax.ws.rs.container.DynamicFeature#configure(javax.ws.rs.container.ResourceInfo,
		 *      javax.ws.rs.core.FeatureContext)
		 */
		@Override
		public void configure(ResourceInfo resourceInfo, FeatureContext context) {
			/*
			 * This method will be called once for every webservice method that
			 * is registered with the JAX-RS application. The Configurable can
			 * be used to register ContainerRequestFilter (or other related
			 * features, e.g. ContainerResponseFilter) implementations, "tying"
			 * them to the specific webservice methods. This is opposed to
			 * registering/declaring them in the JAX-RS application globally,
			 * where the features would be active for all webservice methods.
			 */

			/*
			 * Evaluate the security annotations on the webservice method &
			 * class. This has to be done here, as AuthFilter.filter(...)
			 * doesn't have access to the info.
			 */
			Class<?> webServiceClass = resourceInfo.getResourceClass();
			RolesAllowed rolesAllowedForClass = webServiceClass.getAnnotation(RolesAllowed.class);
			Method webServiceMethod = resourceInfo.getResourceMethod();
			boolean permitAllForMethod = webServiceMethod.isAnnotationPresent(PermitAll.class);
			boolean denyAll = webServiceMethod.isAnnotationPresent(DenyAll.class);
			RolesAllowed rolesAllowedForMethod = webServiceMethod.getAnnotation(RolesAllowed.class);

			/*
			 * The overriding rules specified for these annotations (in their
			 * JavaDoc) are a bit complex, but in general, method-level
			 * annotations always override class-level ones.
			 */
			RolesAllowed rolesAllowed = rolesAllowedForMethod != null ? rolesAllowedForMethod : rolesAllowedForClass;

			// Create the AuthFilter (if needed).
			AuthorizationFilter authFilter;
			if (denyAll) {
				authFilter = new AuthorizationFilter();
			} else if (rolesAllowed != null && !permitAllForMethod) {
				authFilter = new AuthorizationFilter(rolesAllowed.value());
			} else {
				authFilter = null;
			}

			// Register the AuthFilter (if it was created).
			if (authFilter != null) {
				context.register(authFilter);
				LOGGER.debug("An {} instance was registered for the {} JAX-RS method.", AuthorizationFilter.class,
						webServiceMethod);
			}
		}
	}
}
