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

/**
 * Unit tests for {@link ServiceConfig}.
 */
public final class ServiceConfigTest {
	/**
	 * Ensures that {@link ServiceConfig} instances can be marshalled.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbMarshalling() throws JAXBException, XPathExpressionException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(ServiceConfig.class, HsqlCoordinates.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		HsqlCoordinates coords = new HsqlCoordinates("jdbc:hsqldb:mem:foo");
		AdminAccountConfig adminAccountConfig = new AdminAccountConfig("admin@example.com", "password");
		ServiceConfig config = new ServiceConfig(coords, adminAccountConfig);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(config, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext(new NamespaceBinding("rps", XmlNamespace.RPSTOURNEY_API),
				new NamespaceBinding("jed", XmlNamespace.JE_DATASOURCES)));
		Node coordsUrlNode = (Node) xpath.evaluate("/rps:serviceConfig/jed:hsqlCoordinates/jed:url",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(coordsUrlNode);
		Assert.assertEquals(coords.getUrl(), coordsUrlNode.getTextContent());
		Node adminAddressNode = (Node) xpath.evaluate("/rps:serviceConfig/rps:admin/rps:address", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNotNull(adminAddressNode);
		Assert.assertEquals(adminAccountConfig.getAddress().toString(), adminAddressNode.getTextContent());
	}

	/**
	 * Ensures that {@link ServiceConfig} instances can be unmarshalled.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbUnmarshalling() throws JAXBException, XPathExpressionException {
		// Create the Unmarshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(ServiceConfig.class, HsqlCoordinates.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader().getResource("sample-xml/config-1.xml");

		// Parse the XML to an object.
		ServiceConfig parsedConfig = (ServiceConfig) unmarshaller.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedConfig);

		// Ensure that the auth token is null (should never be included in XML).
		Assert.assertTrue(parsedConfig.getDataSourceCoordinates() instanceof HsqlCoordinates);
		HsqlCoordinates coords = (HsqlCoordinates) parsedConfig.getDataSourceCoordinates();
		Assert.assertEquals("jdbc:hsqldb:mem:foo", coords.getUrl());
		Assert.assertEquals("admin@example.com", parsedConfig.getAdminAccountConfig().getAddress().toString());
		Assert.assertEquals("password", parsedConfig.getAdminAccountConfig().getPassword());
	}
}
