package com.justdavis.karl.rpstourney.service.app.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.datasources.hsql.HsqlConnector;
import com.justdavis.karl.misc.datasources.hsql.HsqlCoordinates;

/**
 * Unit tests for {@link XmlConfigLoader}.
 */
public final class XmlConfigLoaderTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	/**
	 * Ensures that {@link XmlConfigLoader#getConfig()} correctly loads files
	 * from the default path.
	 * 
	 * @throws IOException
	 *             (would indicate a problem with the test code itself)
	 */
	@Test
	public void loadFromDefaultPath() throws IOException {
		/*
		 * Sanity check: We're going to create a config file in the working
		 * directory, so lets make sure that there isn't one there already.
		 */
		Path defaultConfigPath = FileSystems.getDefault().getPath(XmlConfigLoader.CONFIG_DEFAULT);
		if (Files.exists(defaultConfigPath))
			throw new IllegalStateException();

		try {
			// Copy the sample config file to the working directory.
			InputStream sampleConfigStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("sample-xml/config-1.xml");
			Files.copy(sampleConfigStream, defaultConfigPath);

			// Use XmlConfigLoader to load the config, then verify it.
			XmlConfigLoader configLoader = new XmlConfigLoader(new DataSourceConnectorsManager(new HsqlConnector()));
			ServiceConfig config = configLoader.getConfig();
			Assert.assertNotNull(config);
			Assert.assertTrue(config.getDataSourceCoordinates() instanceof HsqlCoordinates);
			HsqlCoordinates coords = (HsqlCoordinates) config.getDataSourceCoordinates();
			Assert.assertEquals("jdbc:hsqldb:mem:foo", coords.getUrl());
		} finally {
			// Need to ensure we delete the file we've created.
			Files.deleteIfExists(defaultConfigPath);
		}
	}

	/**
	 * Ensures that {@link XmlConfigLoader#getConfig()} correctly loads files
	 * from an override path value.
	 * 
	 * @throws IOException
	 *             (would indicate a problem with the test code itself)
	 */
	@Test
	public void loadFromOverridePath() throws IOException {
		/*
		 * Sanity check: We're going to set the config file system property, so
		 * let's make sure it doesn't already have a value.
		 */
		if (System.getProperties().containsKey(XmlConfigLoader.CONFIG_PROP))
			throw new IllegalStateException();

		try {
			// Set the property.
			File tempConfigFile = tempFolder.newFile();
			System.setProperty(XmlConfigLoader.CONFIG_PROP, tempConfigFile.getAbsolutePath());

			// Copy the sample config file to the temp file.
			InputStream sampleConfigStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("sample-xml/config-1.xml");
			Files.copy(sampleConfigStream, tempConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			// Use XmlConfigLoader to load the config, then verify it.
			XmlConfigLoader configLoader = new XmlConfigLoader(new DataSourceConnectorsManager(new HsqlConnector()));
			ServiceConfig config = configLoader.getConfig();
			Assert.assertNotNull(config);
			Assert.assertTrue(config.getDataSourceCoordinates() instanceof HsqlCoordinates);
			HsqlCoordinates coords = (HsqlCoordinates) config.getDataSourceCoordinates();
			Assert.assertEquals("jdbc:hsqldb:mem:foo", coords.getUrl());
		} finally {
			// Need to ensure we unset the config property.
			System.getProperties().remove(XmlConfigLoader.CONFIG_PROP);
		}
	}

	/**
	 * Ensures that {@link XmlConfigLoader#getConfig()} correctly caches loaded
	 * files.
	 * 
	 * @throws IOException
	 *             (would indicate a problem with the test code itself)
	 */
	@Test
	public void caching() throws IOException {
		/*
		 * Sanity check: We're going to create a config file in the working
		 * directory, so lets make sure that there isn't one there already.
		 */
		Path defaultConfigPath = FileSystems.getDefault().getPath(XmlConfigLoader.CONFIG_DEFAULT);
		if (Files.exists(defaultConfigPath))
			throw new IllegalStateException();

		try {
			// Copy the sample config file to the working directory.
			InputStream sampleConfigStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("sample-xml/config-1.xml");
			Files.copy(sampleConfigStream, defaultConfigPath);

			// Use XmlConfigLoader to load the config twice.
			XmlConfigLoader configLoader = new XmlConfigLoader(new DataSourceConnectorsManager(new HsqlConnector()));
			ServiceConfig config1 = configLoader.getConfig();
			ServiceConfig config2 = configLoader.getConfig();
			Assert.assertNotNull(config1);
			Assert.assertSame(config1, config2);
		} finally {
			// Need to ensure we delete the file we've created.
			Files.deleteIfExists(defaultConfigPath);
		}
	}
}
