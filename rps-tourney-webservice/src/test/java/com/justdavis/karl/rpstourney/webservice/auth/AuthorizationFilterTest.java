package com.justdavis.karl.rpstourney.webservice.auth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.webservice.auth.AccountSecurityContext.AccountSecurityContextProvider;
import com.justdavis.karl.rpstourney.webservice.auth.AuthorizationFilter.AuthorizationFilterFeature;

/**
 * Unit tests for {@link AuthorizationFilter} and
 * {@link AuthorizationFilterFeature}.
 */
public class AuthorizationFilterTest {
	/**
	 * Tests {@link AuthorizationFilterFeature} against a resource with the
	 * {@link RolesAllowed} annotation just on its class.
	 */
	@Test
	public void configureForRoles() {
		// Create the Configurable to test against.
		MockConfigurable configurable = new MockConfigurable();

		// Run against the appropriate MockResource method.
		AuthorizationFilterFeature feature = new AuthorizationFilterFeature();
		feature.configure(new MockResourceInfo(MockResource.class, "foo"),
				configurable);

		// Verify that the expected AuthorizationFilter was registered.
		Assert.assertEquals(1, configurable.providers.size());
		AuthorizationFilter filter = (AuthorizationFilter) configurable.providers
				.get(0);
		Assert.assertArrayEquals(new String[] { "bob", "frank" },
				filter.getRolesAllowed());
	}

	/**
	 * Tests {@link AuthorizationFilterFeature} against a resource with the
	 * {@link RolesAllowed} annotation on both its class and method.
	 */
	@Test
	public void configureForOverriddenRoles() {
		// Create the Configurable to test against.
		MockConfigurable configurable = new MockConfigurable();

		// Run against the appropriate MockResource method.
		AuthorizationFilterFeature feature = new AuthorizationFilterFeature();
		feature.configure(new MockResourceInfo(MockResource.class, "bar"),
				configurable);

		// Verify that the expected AuthorizationFilter was registered.
		Assert.assertEquals(1, configurable.providers.size());
		AuthorizationFilter filter = (AuthorizationFilter) configurable.providers
				.get(0);
		Assert.assertArrayEquals(new String[] { "sue" },
				filter.getRolesAllowed());
	}

	/**
	 * Tests {@link AuthorizationFilterFeature} against a resource with the
	 * {@link PermitAll} annotation on its method.
	 */
	@Test
	public void configureForPermitAll() {
		// Create the Configurable to test against.
		MockConfigurable configurable = new MockConfigurable();

		// Run against the appropriate MockResource method.
		AuthorizationFilterFeature feature = new AuthorizationFilterFeature();
		feature.configure(new MockResourceInfo(MockResource.class, "bizz"),
				configurable);

		// Verify that the expected AuthorizationFilter was registered.
		Assert.assertEquals(0, configurable.providers.size());
	}

	/**
	 * Tests {@link AuthorizationFilterFeature} against a resource with the
	 * {@link DenyAll} annotation on its method.
	 */
	@Test
	public void configureForDenyAll() {
		// Create the Configurable to test against.
		MockConfigurable configurable = new MockConfigurable();

		// Run against the appropriate MockResource method.
		AuthorizationFilterFeature feature = new AuthorizationFilterFeature();
		feature.configure(new MockResourceInfo(MockResource.class, "buzz"),
				configurable);

		// Verify that the expected AuthorizationFilter was registered.
		Assert.assertEquals(1, configurable.providers.size());
		AuthorizationFilter filter = (AuthorizationFilter) configurable.providers
				.get(0);
		Assert.assertArrayEquals(new String[] {}, filter.getRolesAllowed());
	}

	/**
	 * Tests {@link AuthorizationFilter} to see if its denies requests, when
	 * expected.
	 * 
	 * @throws IOException
	 *             (not expected to be thrown)
	 */
	@Test
	public void filterToDeny() throws IOException {
		// Create the request to test against.
		MockContainerRequestContext requestContext = new MockContainerRequestContext();
		AccountSecurityContext securityContext = new AccountSecurityContext(
				null);
		requestContext.setSecurityContext(securityContext);
		requestContext.setProperty(
				AccountSecurityContextProvider.PROP_SECURITY_CONTEXT,
				securityContext);

		// Run the filter.
		AuthorizationFilter authFilter = new AuthorizationFilter("foo");
		authFilter.filter(requestContext);

		// Verify the result in the request.
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), requestContext
				.getAbortResponse().getStatus());
	}

	/**
	 * Tests {@link AuthorizationFilter} to see if its allows requests, when
	 * expected.
	 * 
	 * @throws IOException
	 *             (not expected to be thrown)
	 */
	@Test
	public void filterToAllow() throws IOException {
		// Create the request to test against.
		MockContainerRequestContext requestContext = new MockContainerRequestContext();
		Account account = new Account(UUID.randomUUID(), SecurityRole.USERS);
		AccountSecurityContext securityContext = new AccountSecurityContext(
				account);
		requestContext.setSecurityContext(securityContext);
		requestContext.setProperty(
				AccountSecurityContextProvider.PROP_SECURITY_CONTEXT,
				securityContext);

		// Run the filter.
		AuthorizationFilter authFilter = new AuthorizationFilter(
				SecurityRole.USERS.getId());
		authFilter.filter(requestContext);

		// Verify the result in the request.
		Assert.assertNull(requestContext.getAbortResponse());
	}

	/**
	 * A mock resource(ish) class to test {@link AuthorizationFilter} against.
	 */
	@RolesAllowed({ "bob", "frank" })
	private static final class MockResource {
		@SuppressWarnings("unused")
		public String foo() {
			return null;
		}

		@RolesAllowed({ "sue" })
		public String bar() {
			return null;
		}

		@PermitAll
		public String bizz() {
			return null;
		}

		@DenyAll
		public String buzz() {
			return null;
		}
	}

	/**
	 * A mock {@link ResourceInfo} implementation for use in tests.
	 */
	private static final class MockResourceInfo implements ResourceInfo {
		private final Class<?> resourceClass;
		private final Method resourceMethod;

		/**
		 * Constructs a new {@link MockResourceInfo} implementation.
		 * 
		 * @param resourceClass
		 *            the value to use for {@link #getResourceClass()}
		 * @param resourceMethodName
		 *            the name of the {@link Method} in the specified
		 *            {@link Class} to use for {@link #getResourceMethod()}
		 */
		public MockResourceInfo(Class<?> resourceClass,
				String resourceMethodName) {
			this.resourceClass = resourceClass;

			try {
				Method resourceMethod = resourceClass
						.getMethod(resourceMethodName);
				this.resourceMethod = resourceMethod;
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException(e);
			} catch (SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * @see javax.ws.rs.container.ResourceInfo#getResourceClass()
		 */
		@Override
		public Class<?> getResourceClass() {
			return resourceClass;
		}

		/**
		 * @see javax.ws.rs.container.ResourceInfo#getResourceMethod()
		 */
		@Override
		public Method getResourceMethod() {
			return resourceMethod;
		}
	}

	/**
	 * A mock {@link Configurable} implementation for use in tests.
	 */
	private static final class MockConfigurable implements Configurable {
		private final List<Object> providers = new LinkedList<>();

		/**
		 * @see javax.ws.rs.core.Configurable#getProperties()
		 */
		@Override
		public Map<String, Object> getProperties() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#getProperty(java.lang.String)
		 */
		@Override
		public Object getProperty(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#setProperties(java.util.Map)
		 */
		@Override
		public Configurable setProperties(Map<String, ?> properties) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#setProperty(java.lang.String,
		 *      java.lang.Object)
		 */
		@Override
		public Configurable setProperty(String name, Object value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#getFeatures()
		 */
		@Override
		public Collection<Feature> getFeatures() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#getProviderClasses()
		 */
		@Override
		public Set<Class<?>> getProviderClasses() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#getProviderInstances()
		 */
		@Override
		public Set<Object> getProviderInstances() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Class)
		 */
		@Override
		public Configurable register(Class<?> providerClass) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Class, int)
		 */
		@Override
		public Configurable register(Class<?> providerClass, int bindingPriority) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Class,
		 *      java.lang.Class[])
		 */
		@Override
		public <T> Configurable register(Class<T> providerClass,
				@SuppressWarnings("unchecked") Class<? super T>... contracts) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Class, int,
		 *      java.lang.Class[])
		 */
		@Override
		public <T> Configurable register(Class<T> providerClass,
				int bindingPriority,
				@SuppressWarnings("unchecked") Class<? super T>... contracts) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Object)
		 */
		@Override
		public Configurable register(Object provider) {
			this.providers.add(provider);
			return this;
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Object, int)
		 */
		@Override
		public Configurable register(Object provider, int bindingPriority) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Object,
		 *      java.lang.Class[])
		 */
		@Override
		public <T> Configurable register(Object provider,
				@SuppressWarnings("unchecked") Class<? super T>... contracts) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.ws.rs.core.Configurable#register(java.lang.Object, int,
		 *      java.lang.Class[])
		 */
		@Override
		public <T> Configurable register(Object provider, int bindingPriority,
				@SuppressWarnings("unchecked") Class<? super T>... contracts) {
			throw new UnsupportedOperationException();
		}
	}
}
