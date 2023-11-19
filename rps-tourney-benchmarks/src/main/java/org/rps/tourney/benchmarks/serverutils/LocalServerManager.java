package org.rps.tourney.benchmarks.serverutils;

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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext.NamespaceBinding;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
import com.justdavis.karl.tomcat.ITomcatServer;
import com.justdavis.karl.tomcat.TomcatServerHelper;

/**
 * This {@link IServerManager} implementation stands up a local {@link ITomcatServer} for the benchmarks to run against.
 */
final class LocalServerManager implements IServerManager {
	public static final String CONTEXT_ROOT_SERVICE = "rps-tourney-service-app";
	public static final String CONTEXT_ROOT_WEBAPP = "rps-tourney-webapp";

	// FIXME this should come from somewhere else
	private static final String RPS_WEBAPP_XML_NS = "http://justdavis.com/karl/rpstourney/app/schema/v1";

	private final InternetAddress adminAddress;
	private final String adminPassword;
	private final ITomcatServer server;

	/**
	 * Constructs a new {@link LocalServerManager}, reading the configuration from
	 * <code>src/main/resources/rps-service-config-benchmarks.xml</code>.
	 */
	public LocalServerManager() {
		Path tomcatDir = Paths.get(".", "target", "tomcat").toAbsolutePath();
		TomcatServerHelper benchmarksServerHelper = new TomcatServerHelper();
		this.server = benchmarksServerHelper.createLocallyInstalledServer(tomcatDir);

		Path serviceConfigPath = createServiceConfig();
		Path webappConfigPath = createWebappConfig();

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

		// Create, configure, and start Tomcat to run the WARs.
		this.server
				.addWar(CONTEXT_ROOT_SERVICE, Paths.get(".."),
						FileSystems.getDefault()
								.getPathMatcher("glob:../rps-tourney-service-app/target/rps-tourney-service-app-*.war"))
				.setJavaSystemProperty("rps.service.config.path", serviceConfigPath.toString())
				.setJavaSystemProperty("rps.service.logs.path", tomcatDir.toString())
				.addWar(CONTEXT_ROOT_WEBAPP, Paths.get(".."),
						FileSystems.getDefault()
								.getPathMatcher("glob:../rps-tourney-webapp/target/rps-tourney-webapp-*.war"))
				.setJavaSystemProperty("rps.webapp.config.path", webappConfigPath.toString())
				.setJavaSystemProperty("rps.webapp.logs.path", tomcatDir.toString()).start();
	}

	/**
	 * @return the {@link Path} to the web service configuration file
	 */
	private static Path createServiceConfig() {
		// Get the path to the config file for the web service to use.
		InputStream configStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("rps-service-config-benchmarks.xml");

		try {
			Path serviceConfigPath = Files.createTempFile("rps-service-config-benchmarks", ".xml");
			serviceConfigPath.toFile().deleteOnExit();
			Files.copy(configStream, serviceConfigPath, StandardCopyOption.REPLACE_EXISTING);

			return serviceConfigPath;
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}

	/**
	 * @return the {@link Path} to the web application configuration file
	 */
	private Path createWebappConfig() {
		// Get the path to the template config file for the web service to use.
		InputStream configStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("rps-webapp-config-benchmarks.xml");

		try {
			// Read it in as a DOM.
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(configStream);

			// Edit the /appConfig/baseUrl entry in it.
			Element baseUrlElement = (Element) doc.getDocumentElement()
					.getElementsByTagNameNS(RPS_WEBAPP_XML_NS, "baseUrl").item(0);
			baseUrlElement.setTextContent(getWebAppUrl().toString());

			// Edit the /appConfig/clientServiceRoot entry in it.
			Element serviceRootElement = (Element) doc.getDocumentElement()
					.getElementsByTagNameNS(RPS_WEBAPP_XML_NS, "clientServiceRoot").item(0);
			serviceRootElement.setTextContent(getServiceUrl().toString());

			// Create a temp file for the config (mark it to be deleted at JVM
			// exit).
			Path webappConfigPath = Files.createTempFile("rps-service-webapp-benchmarks", ".xml");
			webappConfigPath.toFile().deleteOnExit();

			// Write out the DOM to the temp file.
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);
			Result result = new StreamResult(webappConfigPath.toFile());
			transformer.transform(source, result);

			return webappConfigPath;
		} catch (ParserConfigurationException | SAXException | TransformerException e) {
			throw new BadCodeMonkeyException(e);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}

	/**
	 * @see org.rps.tourney.benchmarks.serverutils.IServerManager#getServiceUrl()
	 */
	@Override
	public URL getServiceUrl() {
		return server.getUrlWithPath(CONTEXT_ROOT_SERVICE);
	}

	/**
	 * @see org.rps.tourney.benchmarks.serverutils.IServerManager#getWebAppUrl()
	 */
	@Override
	public URL getWebAppUrl() {
		return server.getUrlWithPath(CONTEXT_ROOT_WEBAPP);
	}

	/**
	 * @see org.rps.tourney.benchmarks.serverutils.IServerManager#getAdminAddress()
	 */
	@Override
	public InternetAddress getAdminAddress() {
		return adminAddress;
	}

	/**
	 * @see org.rps.tourney.benchmarks.serverutils.IServerManager#getAdminPassword()
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
	 * @see org.rps.tourney.benchmarks.serverutils.IServerManager#tearDown()
	 */
	@Override
	public void tearDown() {
		server.release();
	}
}
