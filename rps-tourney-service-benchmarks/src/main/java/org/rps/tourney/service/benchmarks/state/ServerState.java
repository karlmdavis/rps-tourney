package org.rps.tourney.service.benchmarks.state;

import java.io.IOException;
import java.io.InputStream;
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

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.w3c.dom.Document;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext.NamespaceBinding;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
import com.justdavis.karl.tomcat.ITomcatServer;
import com.justdavis.karl.tomcat.TomcatServerHelper;

/**
 * Stores the {@link ITomcatServer} instance representing the application
 * server/container that is running the web service, along with related objects.
 */
@State(Scope.Benchmark)
public class ServerState {
	public static final String CONTEXT_ROOT_SERVICE = "rps-tourney-service-app";

	private ITomcatServer server;
	private InternetAddress adminAddress;
	private String adminPassword;

	/**
	 * @return the {@link ITomcatServer} instance that the web service is
	 *         running in
	 */
	public ITomcatServer getServer() {
		return server;
	}

	/**
	 * @return the web service's admin email address/login
	 */
	public InternetAddress getAdminAddress() {
		return adminAddress;
	}

	/**
	 * @return the web service's admin password
	 */
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * Initializes {@link ServerState} instances.
	 */
	@Setup
	public void setupServerState() {
		// Read in and parse the service config.
		Path serviceConfigPath = createServiceConfig();
		readServiceConfig(serviceConfigPath);

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
	 * Reads in the specified web service configuration file and initializes
	 * {@link #getAdminAddress()} and {@link #adminPassword} from it.
	 * 
	 * @param serviceConfigPath
	 *            the web service configuration file to read in
	 */
	private void readServiceConfig(Path serviceConfigPath) {
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
	}

	/**
	 * Cleans up {@link ServerState} instances.
	 */
	@TearDown
	public void tearDownServerState() {
		if (this.server != null)
			this.server.release();
	}
}
