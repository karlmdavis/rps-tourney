package com.justdavis.karl.rpstourney.webapp.config;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext.NamespaceBinding;
import com.justdavis.karl.rpstourney.webapp.XmlNamespace;

/**
 * Unit tests for {@link AppConfig}.
 */
public final class AppConfigTest {
	/**
	 * Ensures that {@link AppConfig} instances can be marshalled.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 * @throws MalformedURLException
	 *             (using static URLs; shouldn't happen)
	 */
	@Test
	public void jaxbMarshalling() throws JAXBException,
			XPathExpressionException, URISyntaxException, MalformedURLException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(AppConfig.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		URL baseUrl = new URL("https://example.com/");
		URL clientServiceRoot = new URL("https://example.com/service");
		AppConfig config = new AppConfig(baseUrl, clientServiceRoot);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(config, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext(
				new NamespaceBinding("rps", XmlNamespace.RPSTOURNEY_APP)));
		Node clientServiceUriNode = (Node) xpath.evaluate(
				"/rps:appConfig/rps:clientServiceRoot", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNotNull(clientServiceUriNode);
		Assert.assertEquals(clientServiceRoot.toString(),
				clientServiceUriNode.getTextContent());
	}

	/**
	 * Ensures that {@link AppConfig} instances can be unmarshalled.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbUnmarshalling() throws JAXBException,
			XPathExpressionException {
		// Create the Unmarshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(AppConfig.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader()
				.getResource("sample-xml/config-1.xml");

		// Parse the XML to an object.
		AppConfig parsedConfig = (AppConfig) unmarshaller
				.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedConfig);
		Assert.assertNotNull(parsedConfig.getBaseUrl());
		Assert.assertEquals("https://example.com/", parsedConfig.getBaseUrl()
				.toString());
		Assert.assertNotNull(parsedConfig.getClientServiceRoot());
		Assert.assertEquals("https://example.com/service", parsedConfig
				.getClientServiceRoot().toString());
	}
}
