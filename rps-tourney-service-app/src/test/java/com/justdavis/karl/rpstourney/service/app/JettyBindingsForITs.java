package com.justdavis.karl.rpstourney.service.app;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.justdavis.karl.misc.jetty.EmbeddedServer;
import com.justdavis.karl.rpstourney.service.app.GameServiceApplicationInitializer.AppSpringConfig;

/**
 * <p>
 * This Spring {@link Configuration} should be used for integration tests that
 * require the web service to actually be running and responding to requests. It
 * adds a bean that will start a Jetty instance along with the Spring
 * {@link ApplicationContext}, and stop it when the context is stopped.
 * </p>
 * <p>
 * It also {@link Import}s the following additional {@link Configuration}s:
 * {@link AppConfigBindingsForITs} and {@link AppSpringConfig} (which itself
 * imports others).
 * </p>
 */
@Configuration
@Import({ SpringBindingsForITs.class, AppSpringConfig.class })
@Profile(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
public class JettyBindingsForITs {
	/**
	 * @param springContext
	 *            the Spring {@link ApplicationContext} that this bean is being
	 *            created in/for
	 * @return an {@link EmbeddedServer} bean that will start up a Jetty
	 *         container along with the Spring {@link ApplicationContext}, and
	 *         will stop it when the {@link ApplicationContext} is destroyed
	 */
	@Bean(initMethod = "startServer", destroyMethod = "stopServer")
	public EmbeddedServer embeddedServer(ApplicationContext springContext) {
		Map<String, Object> webAppAttributes = new HashMap<>();
		webAppAttributes.put(
				GameServiceApplicationInitializer.SPRING_PARENT_CONTEXT,
				springContext);

		EmbeddedServer embeddedServer = new EmbeddedServer(
				EmbeddedServer.RANDOM_PORT, false, webAppAttributes);
		return embeddedServer;
	}
}
