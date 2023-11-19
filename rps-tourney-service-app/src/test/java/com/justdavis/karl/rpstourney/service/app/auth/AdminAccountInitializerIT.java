package com.justdavis.karl.rpstourney.service.app.auth;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.justdavis.karl.misc.datasources.schema.IDataSourceSchemaManager;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;
import com.justdavis.karl.rpstourney.service.app.SpringBindingsForWebServiceITs;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;
import com.justdavis.karl.rpstourney.service.app.auth.game.IGameLoginIndentitiesDao;
import com.justdavis.karl.rpstourney.service.app.config.IConfigLoader;

/**
 * Integration tests for {@link AdminAccountInitializer}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringBindingsForWebServiceITs.class })
@ActiveProfiles(SpringProfile.INTEGRATION_TESTS_WITH_JETTY)
@WebAppConfiguration
public final class AdminAccountInitializerIT {
	@Inject
	private IGameLoginIndentitiesDao loginsDao;

	@Inject
	private IDataSourceSchemaManager schemaManager;

	@Inject
	private IConfigLoader configLoader;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * Wipes and repopulates the data source schema between tests.
	 */
	@After
	public void wipeSchema() {
		schemaManager.wipeSchema(configLoader.getConfig().getDataSourceCoordinates());
		schemaManager.createOrUpgradeSchema(configLoader.getConfig().getDataSourceCoordinates());
	}

	/**
	 * Ensures that {@link AdminAccountInitializer#contextInitialized(javax.servlet.ServletContextEvent)} is being fired
	 * at application startup, and creating the default admin account, as expected.
	 *
	 * @throws AddressException
	 *             (won't happen; address is hardcoded)
	 */
	@Test
	public void adminAccountCreated() throws AddressException {
		// The admin address is specified in MockConfigLoader.
		GameLoginIdentity adminLogin = loginsDao.find(new InternetAddress("admin@example.com"));
		Assert.assertNotNull(adminLogin);
	}
}
