package com.justdavis.karl.rpstourney.webservice.auth;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

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
import com.justdavis.karl.rpstourney.webservice.XmlNamespace;

/**
 * Unit tests for {@link Account}.
 */
public final class AccountTest {
	/**
	 * Ensures that {@link Account} instances can be marshalled.
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
		JAXBContext jaxbContext = JAXBContext.newInstance(Account.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		Account account = new Account();

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(account, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps",
				XmlNamespace.RPSTOURNEY));
		Node accountNode = (Node) xpath.evaluate("/rps:account",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(accountNode);
		Node rolesNode = (Node) xpath.evaluate("/rps:account/rps:roles",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(rolesNode);
		Node roleNode = (Node) xpath.evaluate(
				"/rps:account/rps:roles/rps:role[1]", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNotNull(roleNode);
		Assert.assertEquals(SecurityRole.USERS.toString(),
				roleNode.getTextContent());
	}

	/**
	 * Ensures that {@link Account} instances can be unmarshalled.
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
		JAXBContext jaxbContext = JAXBContext.newInstance(Account.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader()
				.getResource("sample-xml/account-1.xml");

		// Parse the XML to an object.
		Account parsedAccount = (Account) unmarshaller.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedAccount);
		Assert.assertEquals(new HashSet<>(Arrays.asList(SecurityRole.USERS)),
				parsedAccount.getRoles());
	}
}
