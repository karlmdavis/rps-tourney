package com.justdavis.karl.rpstourney.webservice.auth;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
		JAXBContext jaxbContext = JAXBContext
				.newInstance(MockRootElement.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		Account account = new Account();
		MockRootElement root = new MockRootElement(account);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(root, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps",
				XmlNamespace.RPSTOURNEY));
		Node accountNode = (Node) xpath.evaluate("/rps:root/rps:account",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(accountNode);
		// TODO test account once that class has been fleshed out
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
		JAXBContext jaxbContext = JAXBContext
				.newInstance(MockRootElement.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader()
				.getResource("sample-xml/account-1.xml");

		// Parse the XML to an object.
		MockRootElement parsedRoot = (MockRootElement) unmarshaller
				.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedRoot.getAccount());
		// TODO test account once that class has been fleshed out
	}

	/**
	 * A mock class that just stores an {@link Account} instance. As this is a
	 * JAX-B {@link XmlRootElement}, can be used to test {@link Account}'s
	 * support for JAX-B.
	 */
	@XmlRootElement(name = "root")
	private static final class MockRootElement {
		@XmlElement
		private final Account account;

		/**
		 * This no-arg/default constructor is required by JAX-B.
		 */
		@SuppressWarnings("unused")
		private MockRootElement() {
			this.account = null;
		}

		/**
		 * Constructs a new {@link MockRootElement} instance.
		 * 
		 * @param account
		 *            the value to use for {@link #getAccount()}
		 */
		public MockRootElement(Account account) {
			this.account = account;
		}

		/**
		 * @return the {@link Account} stored in this {@link MockRootElement}
		 */
		public Account getAccount() {
			return account;
		}
	}
}
