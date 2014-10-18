package com.justdavis.karl.rpstourney.service.api.auth;

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
import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeFormatter;
import org.w3c.dom.Node;

import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;

/**
 * Unit tests for {@link AuthToken}.
 */
public final class AuthTokenTest {
	/**
	 * Ensures that {@link AuthToken} instances can be marshalled.
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
		JAXBContext jaxbContext = JAXBContext.newInstance(AuthToken.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		Account account = new Account();
		AuthToken authToken = new AuthToken(account, UUID.randomUUID());

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(authToken, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps",
				XmlNamespace.RPSTOURNEY_API));
		Node tokenNode = (Node) xpath.evaluate("/rps:authToken",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(tokenNode);
		Node accountNode = (Node) xpath.evaluate("/rps:authToken/rps:account",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(accountNode);
		Node idNode = (Node) xpath.evaluate(
				"/rps:authToken/rps:account/rps:id", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals("0", idNode.getTextContent());
		Node tokenValueNode = (Node) xpath.evaluate("/rps:authToken/rps:token",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals(authToken.getToken().toString(),
				tokenValueNode.getTextContent());
		Node timestampNode = (Node) xpath.evaluate(
				"/rps:authToken/rps:createdTimestamp", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals(DateTimeFormatter.ISO_INSTANT.format(authToken
				.getCreatedTimestamp()), timestampNode.getTextContent());
	}

	/**
	 * Ensures that {@link AuthToken} instances can be unmarshalled.
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
		JAXBContext jaxbContext = JAXBContext.newInstance(AuthToken.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader()
				.getResource("sample-xml/authToken-1.xml");

		// Parse the XML to an object.
		AuthToken parsedToken = (AuthToken) unmarshaller
				.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedToken);
		Assert.assertEquals(42, parsedToken.getAccount().getId());
		Assert.assertEquals("f211aae3-c46f-47da-ae6c-445f5281c4ee", parsedToken
				.getToken().toString());
		Assert.assertEquals(Instant.parse("2007-12-03T10:15:30Z"),
				parsedToken.getCreatedTimestamp());
	}
}
