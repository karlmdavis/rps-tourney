package org.rps.tourney.service.benchmarks.state;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.mail.internet.InternetAddress;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext.NamespaceBinding;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
import com.justdavis.karl.tomcat.ITomcatServer;
import com.justdavis.karl.tomcat.TomcatServerHelper;

/**
 * This {@link IServerManager} implementation stands up a local
 * {@link ITomcatServer} for the benchmarks to run against.
 */
final class LocalServerManager implements IServerManager {
	public static final String CONTEXT_ROOT_SERVICE = "rps-tourney-service-app";

	private final InternetAddress adminAddress;
	private final String adminPassword;
	private final ITomcatServer server;

	/**
	 * Constructs a new {@link LocalServerManager}, reading the configuration
	 * from <code>src/main/resources/rps-service-config-benchmarks.xml</code>.
	 */
	public LocalServerManager() {
		Path serviceConfigPath = createServiceConfig();

		// Parse the admin config values from that file
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			xpath.setNamespaceContext(
					new SimpleNamespaceContext(new NamespaceBinding("rps", XmlNamespace.RPSTOURNEY_API)));

			Document doc = db.parse(serviceConfigPath.toFile());
			String adminAddressText = (String) xpath.compile("/rps:serviceConfig/rps:admin/rps:address").evaluate(doc);

			this.adminAddress = new InternetAddress(adminAddressText);
			this.adminPassword = (String) xpath.compile("/rps:serviceConfig/rps:admin/rps:password").evaluate(doc,
					XPathConstants.STRING);
		} catch (Exception e) {
			throw new BadCodeMonkeyException("Unable to read service config.", e);
		}

		// Create and launch a local Tomcat instance.
		// Create, configure, and start Tomcat to run the web service.
		TomcatServerHelper benchmarksServerHelper = new TomcatServerHelper();
		Path tomcatDir = Paths.get(".", "target", "tomcat").toAbsolutePath();
		this.server = benchmarksServerHelper.createLocallyInstalledServer(tomcatDir)
				.addWar(CONTEXT_ROOT_SERVICE, Paths.get(".."),
						FileSystems.getDefault()
								.getPathMatcher("glob:../rps-tourney-service-app/target/rps-tourney-service-app-*.war"))
				.setJavaSystemProperty("rps.service.config.path", serviceConfigPath.toString())
				.setJavaSystemProperty("rps.service.logs.path", tomcatDir.toString()).start();

	}

	/**
	 * @return the {@link Path} to the web service configuration file
	 */
	private static Path createServiceConfig() {
		// Get the path to the config file for the web service to use.
		InputStream configUrl = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("rps-service-config-benchmarks.xml");

		try {
			Path serviceConfigPath = Files.createTempFile("rps-service-config-benchmarks", ".xml");
			serviceConfigPath.toFile().deleteOnExit();
			Files.copy(configUrl, serviceConfigPath, StandardCopyOption.REPLACE_EXISTING);

			return serviceConfigPath;
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#getServiceUrl()
	 */
	@Override
	public URL getServiceUrl() {
		return server.getUrlWithPath(CONTEXT_ROOT_SERVICE);
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#getAdminAddress()
	 */
	@Override
	public InternetAddress getAdminAddress() {
		return adminAddress;
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#getAdminPassword()
	 */
	@Override
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocalServerManager [getServiceUrl()=");
		builder.append(getServiceUrl());
		builder.append(", adminAddress=");
		builder.append(adminAddress);
		builder.append(", adminPassword=");
		builder.append(adminPassword);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @see org.rps.tourney.service.benchmarks.state.IServerManager#tearDown()
	 */
	@Override
	public void tearDown() {
		server.release();
	}
}
