package com.justdavis.karl.rpstourney.app.console.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultResourceBundleLoader}.
 */
public class DefaultResourceBundleLoaderTest {
	/**
	 * Verifies {@link DefaultResourceBundleLoader} works as expected.
	 */
	@Test
	public void normalUsage() {
		DefaultResourceBundleLoader bundleLoader = new DefaultResourceBundleLoader(Locale.ENGLISH);
		ResourceBundle bundle = bundleLoader.getBundle();

		Assert.assertNotNull(bundle);
		Assert.assertEquals(bundle.getString("players.ai.name.threeSidedDie"), "Easy");
	}
}
