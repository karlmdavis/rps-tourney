package com.justdavis.karl.rpstourney.service.app.config;

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

import com.justdavis.karl.misc.datasources.hsql.HsqlCoordinates;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.misc.xml.SimpleNamespaceContext.NamespaceBinding;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
import com.justdavis.karl.rpstourney.service.app.config.GameConfig;

/**
 * Unit tests for {@link GameConfig}.
 */
public final class GameConfigTest {
	/**
	 * Ensures that {@link GameConfig} instances can be marshalled.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbMarshalling() throws JAXBException,
			XPathExpressionException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(GameConfig.class,
				HsqlCoordinates.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		HsqlCoordinates coords = new HsqlCoordinates("jdbc:hsqldb:mem:foo");
		GameConfig config = new GameConfig(coords);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(config, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext(
				new NamespaceBinding("rps", XmlNamespace.RPSTOURNEY),
				new NamespaceBinding("jed", XmlNamespace.JE_DATASOURCES)));
		Node coordsUrlNode = (Node) xpath.evaluate(
				"/rps:gameConfig/jed:hsqlCoordinates/jed:url",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(coordsUrlNode);
		Assert.assertEquals(coords.getUrl(), coordsUrlNode.getTextContent());
	}

	/**
	 * Ensures that {@link GameConfig} instances can be unmarshalled.
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
		JAXBContext jaxbContext = JAXBContext.newInstance(GameConfig.class,
				HsqlCoordinates.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader()
				.getResource("sample-xml/config-1.xml");

		// Parse the XML to an object.
		GameConfig parsedConfig = (GameConfig) unmarshaller
				.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedConfig);

		// Ensure that the auth token is null (should never be included in XML).
		Assert.assertTrue(parsedConfig.getDataSourceCoordinates() instanceof HsqlCoordinates);
		HsqlCoordinates coords = (HsqlCoordinates) parsedConfig
				.getDataSourceCoordinates();
		Assert.assertEquals("jdbc:hsqldb:mem:foo", coords.getUrl());
	}
}
