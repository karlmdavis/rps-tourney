package com.justdavis.karl.rpstourney.webservice.auth.guest;

import java.net.URL;
import java.util.UUID;

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
import com.justdavis.karl.misc.xml.XmlConverter;
import com.justdavis.karl.rpstourney.webservice.XmlNamespace;
import com.justdavis.karl.rpstourney.webservice.auth.Account;

/**
 * Unit tests for {@link GuestLoginIdentity}.
 */
public final class GuestLoginIdentityTest {
	/**
	 * Ensures that {@link GuestLoginIdentity} instances can be marshalled.
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
		JAXBContext jaxbContext = JAXBContext
				.newInstance(GuestLoginIdentity.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		UUID randomUuid = UUID.randomUUID();
		GuestLoginIdentity login = new GuestLoginIdentity(new Account(),
				randomUuid);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(login, domResult);

		// Verify the results.
		System.out.println(new XmlConverter().convertToString(domResult
				.getNode()));
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps",
				XmlNamespace.RPSTOURNEY));
		String authTokenText = xpath.evaluate("/rps:guestLogin/@authToken",
				domResult.getNode());
		Assert.assertEquals(randomUuid.toString(), authTokenText);
		Node accountNode = (Node) xpath.evaluate("/rps:guestLogin/rps:account",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(accountNode);
		// TODO test account once that class has been fleshed out
	}

	/**
	 * Ensures that {@link GuestLoginIdentity} instances can be unmarshalled.
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
		JAXBContext jaxbContext = JAXBContext
				.newInstance(GuestLoginIdentity.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader()
				.getResource("sample-xml/guest-login-1.xml");

		// Parse the XML to an object.
		GuestLoginIdentity parsedLogin = (GuestLoginIdentity) unmarshaller
				.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedLogin);
		Assert.assertEquals("e4cf7e62-4896-467d-aef2-897702a5a27f", parsedLogin
				.getAuthToken().toString());
		Assert.assertNotNull(parsedLogin.getAccount());
		// TODO test account once that class has been fleshed out
	}
}
