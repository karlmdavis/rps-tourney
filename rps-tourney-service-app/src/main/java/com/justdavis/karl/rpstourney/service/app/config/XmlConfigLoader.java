package com.justdavis.karl.rpstourney.service.app.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.springframework.cglib.core.CodeGenerationException;

import com.justdavis.karl.misc.datasources.DataSourceConnectorsManager;
import com.justdavis.karl.misc.exceptions.unchecked.UncheckedJaxbException;

/**
 * <p>
 * This {@link IConfigLoader} implementation loads the application's
 * configuration from a JAX-B-unmarshallable XML file.
 * </p>
 * <p>
 * By default, the application's configuration will be read from a file located
 * at {@link #CONFIG_DEFAULT}, which is in the application's (or container's)
 * current working directory. This, however, can be overridden by specifying a
 * different path via the {@link #CONFIG_PROP} Java system property.
 * </p>
 */
public class XmlConfigLoader implements IConfigLoader {
	/**
	 * The default path that the application's configuration will be read from,
	 * unless overridden via {@link #CONFIG_PROP}.
	 */
	public static final String CONFIG_DEFAULT = "./rps-service-config.xml";

	/**
	 * The Java system property that specifies the path that the application's
	 * configuration will be read from, overriding the default path specified in
	 * {@link #CONFIG_DEFAULT}.
	 * 
	 * @see System#getProperty(String)
	 */
	public static final String CONFIG_PROP = "rps.service.config.path";

	/**
	 * This static field is used as a cache for the application's
	 * {@link ServiceConfig}.
	 */
	private static ServiceConfig cachedConfig = null;

	private final DataSourceConnectorsManager dsConnectorsManager;

	/**
	 * Constructs a new {@link XmlConfigLoader} instance.
	 * 
	 * @param dsConnectorsManager
	 *            the application's {@link DataSourceConnectorsManager}
	 */
	@Inject
	public XmlConfigLoader(DataSourceConnectorsManager dsConnectorsManager) {
		this.dsConnectorsManager = dsConnectorsManager;
	}

	/**
	 * @see com.justdavis.karl.rpstourney.service.app.config.IConfigLoader#getConfig()
	 */
	@Override
	public synchronized ServiceConfig getConfig() {
		/*
		 * This method is marked as synchronized to prevent different parts of
		 * the application from ending up with different config instances. If
		 * this proves to be a bottleneck, we'll want to create a second
		 * IConfigLoader implementation that takes an already-loaded config
		 * value, and set the application to just use that implementation
		 * (pre-loading the config via this implementation at startup).
		 */

		// Do we already have a cached copy available?
		if (cachedConfig != null)
			return cachedConfig;

		// Find and open a stream to the config file.
		InputStream configFileStream = retrieveConfigFile();

		try {
			// Create the List of classes that might be unmarshalled.
			List<Class<?>> jaxbClasses = new LinkedList<>();
			jaxbClasses.add(ServiceConfig.class);
			jaxbClasses.addAll(dsConnectorsManager.getCoordinatesTypes());

			// Create the Unmarshaller needed.
			JAXBContext jaxbContext = JAXBContext.newInstance(jaxbClasses.toArray(new Class<?>[jaxbClasses.size()]));
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			// Try to unmarshall the config file.
			ServiceConfig parsedConfig = (ServiceConfig) unmarshaller.unmarshal(configFileStream);

			// Cache the config for future calls.
			cachedConfig = parsedConfig;

			return cachedConfig;
		} catch (UnmarshalException e) {
			throw new ServiceConfigException("Unable to parse configuration.", e);
		} catch (JAXBException e) {
			throw new UncheckedJaxbException(e);
		}
	}

	/**
	 * Note: Integration tests may want to override this method.
	 * 
	 * @return an {@link InputStream} for the application's configuration file
	 */
	protected InputStream retrieveConfigFile() {
		// Try to grab the overridden or the default file.
		InputStream configFileStream = retrieveConfigFile_overridden();
		if (configFileStream == null)
			configFileStream = retrieveConfigFile_default();

		// If no file was found, throw an explanatory error.
		if (configFileStream == null)
			throw new ServiceConfigException(String.format(
					"A configuration file must be available" + " either at the default path ('%s')"
							+ " or at the path specified by the '%s'" + " Java system property.",
					CONFIG_DEFAULT, CONFIG_PROP));

		return configFileStream;
	}

	/**
	 * @return an {@link InputStream} for the file available at
	 *         {@link #CONFIG_DEFAULT}, or <code>null</code> if no such file
	 *         exists
	 */
	private static InputStream retrieveConfigFile_default() {
		try {
			// Is a file available at the default path?
			Path configPath = FileSystems.getDefault().getPath(CONFIG_DEFAULT);
			if (Files.exists(configPath) && Files.isRegularFile(configPath))
				return new BufferedInputStream(Files.newInputStream(configPath));

			// No file exists at the default path.
			return null;
		} catch (IOException e) {
			throw new ServiceConfigException("Unable to open the configuration file.", e);
		}
	}

	/**
	 * @return an {@link InputStream} for the file specified by
	 *         {@link #CONFIG_PROP}, or <code>null</code> if that property
	 *         wasn't set
	 * @throws ServiceConfigException
	 *             A {@link CodeGenerationException} will be thrown if the
	 *             {@link #CONFIG_PROP} property has been set to a value that
	 *             does not point to an actual file.
	 */
	private static InputStream retrieveConfigFile_overridden() throws ServiceConfigException {
		try {
			// If the property isn't set, bail out.
			String configFilePropValue = System.getProperty(CONFIG_PROP);
			if (configFilePropValue == null)
				return null;

			// Is a file available at that path?
			Path configPath = FileSystems.getDefault().getPath(configFilePropValue);
			if (!Files.exists(configPath) || !Files.isRegularFile(configPath))
				throw new ServiceConfigException(String.format(
						"The path specified by the %s property does " + "not point to a valid file: '%s'.", CONFIG_PROP,
						configFilePropValue));

			// Return a stream for the file.
			return new BufferedInputStream(Files.newInputStream(configPath));
		} catch (IOException e) {
			throw new ServiceConfigException("Unable to open the configuration file.", e);
		}
	}
}
