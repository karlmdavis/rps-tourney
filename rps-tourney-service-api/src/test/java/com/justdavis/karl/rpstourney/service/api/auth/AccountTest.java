package com.justdavis.karl.rpstourney.service.api.auth;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
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
import org.w3c.dom.Node;

import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;

/**
 * Unit tests for {@link Account}.
 */
public final class AccountTest {
	/**
	 * Ensures that {@link Account#getAuthToken(java.util.UUID)} works as
	 * expected.
	 */
	@Test
	public void getAuthTokenForUuid() {
		Account accountWithNoTokens = new Account();
		Assert.assertNull(null);
		Assert.assertNull(accountWithNoTokens.getAuthToken(UUID.randomUUID()));

		Account accountWith1Token = new Account();
		AuthToken authToken = new AuthToken(accountWith1Token,
				UUID.randomUUID(), Instant.now());
		accountWith1Token.getAuthTokens().add(authToken);
		Assert.assertNull(accountWith1Token.getAuthToken(UUID.randomUUID()));
		Assert.assertEquals(authToken,
				accountWith1Token.getAuthToken(authToken.getToken()));
	}

	/**
	 * Ensures that {@link Account#getAuthToken()} works as expected.
	 */
	@Test
	public void getAuthToken() {
		Account accountWithNoTokens = new Account();
		Assert.assertNull(accountWithNoTokens.getAuthToken());

		Account accountWith1Token = new Account();
		AuthToken authToken = new AuthToken(accountWith1Token,
				UUID.randomUUID(), Instant.now());
		accountWith1Token.getAuthTokens().add(authToken);
		Assert.assertEquals(authToken, accountWith1Token.getAuthToken());
	}

	/**
	 * Ensures that {@link Account#isValidAuthToken(UUID)} works as expected.
	 */
	@Test
	public void isValidToken() {
		Account accountWith1Token = new Account();
		AuthToken authToken = new AuthToken(accountWith1Token,
				UUID.randomUUID(), Instant.now());
		accountWith1Token.getAuthTokens().add(authToken);
		Assert.assertFalse(accountWith1Token.isValidAuthToken(null));
		Assert.assertFalse(accountWith1Token.isValidAuthToken(UUID.randomUUID()));
		Assert.assertTrue(accountWith1Token.isValidAuthToken(authToken
				.getToken()));
	}

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
				XmlNamespace.RPSTOURNEY_API));
		Node accountNode = (Node) xpath.evaluate("/rps:account",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(accountNode);
		Node idNode = (Node) xpath.evaluate("/rps:account/rps:id",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("-1", idNode.getTextContent());
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
		Assert.assertEquals(42, parsedAccount.getId());
		Assert.assertEquals(new HashSet<>(Arrays.asList(SecurityRole.USERS)),
				parsedAccount.getRoles());
	}

	/**
	 * Tests {@link Account#equals(Object)} and {@link Account#hashCode()}.
	 * 
	 * @throws SecurityException
	 *             (won't happen)
	 * @throws NoSuchFieldException
	 *             (won't happen)
	 * @throws IllegalAccessException
	 *             (won't happen)
	 * @throws IllegalArgumentException
	 *             (won't happen)
	 */
	@Test
	public void equalsAndHashCode() throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		Account accountA = new Account();

		Assert.assertEquals(accountA, accountA);
		Assert.assertEquals(accountA.hashCode(), accountA.hashCode());

		Account accountB = new Account();

		Assert.assertNotEquals(accountA, accountB);

		/*
		 * The logic for Account.equals(...) is different for persisted vs.
		 * non-persisted objects, and we want to cover that logic with this
		 * test. To avoid having to involve the DB here (and actually persist
		 * things), we'll cheat and set the fields' values via reflection.
		 */
		Field accountIdField = Account.class.getDeclaredField("id");
		accountIdField.setAccessible(true);

		Account accountC = new Account();
		accountIdField.set(accountC, 3);

		Assert.assertEquals(accountC, accountC);
		Assert.assertNotEquals(accountC, accountA);
		Assert.assertNotEquals(accountA, accountC);

		Account accountD = new Account();
		accountIdField.set(accountD, 4);

		Assert.assertNotEquals(accountD, accountC);
	}
}
