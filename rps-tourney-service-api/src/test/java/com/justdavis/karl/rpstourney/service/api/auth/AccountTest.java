package com.justdavis.karl.rpstourney.service.api.auth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
import com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity;

/**
 * Unit tests for {@link Account}.
 */
public final class AccountTest {
	/**
	 * Ensures that {@link Account#getAuthToken(java.util.UUID)} works as expected.
	 */
	@Test
	public void getAuthTokenForUuid() {
		Account accountWithNoTokens = new Account();
		Assert.assertNull(null);
		Assert.assertNull(accountWithNoTokens.getAuthToken(UUID.randomUUID()));

		Account accountWith1Token = new Account();
		AuthToken authToken = new AuthToken(accountWith1Token, UUID.randomUUID());
		accountWith1Token.getAuthTokens().add(authToken);
		Assert.assertNull(accountWith1Token.getAuthToken(UUID.randomUUID()));
		Assert.assertEquals(authToken, accountWith1Token.getAuthToken(authToken.getToken()));
	}

	/**
	 * Ensures that {@link Account#getAuthToken()} works as expected.
	 */
	@Test
	public void getAuthToken() {
		Account accountWithNoTokens = new Account();
		Assert.assertNull(accountWithNoTokens.getAuthToken());

		Account accountWith1Token = new Account();
		AuthToken authToken = new AuthToken(accountWith1Token, UUID.randomUUID());
		accountWith1Token.getAuthTokens().add(authToken);
		Assert.assertEquals(authToken, accountWith1Token.getAuthToken());
	}

	/**
	 * Ensures that {@link Account#isValidAuthToken(UUID)} works as expected.
	 */
	@Test
	public void isValidToken() {
		Account accountWith1Token = new Account();
		AuthToken authToken = new AuthToken(accountWith1Token, UUID.randomUUID());
		accountWith1Token.getAuthTokens().add(authToken);
		Assert.assertFalse(accountWith1Token.isValidAuthToken(null));
		Assert.assertFalse(accountWith1Token.isValidAuthToken(UUID.randomUUID()));
		Assert.assertTrue(accountWith1Token.isValidAuthToken(authToken.getToken()));
	}

	/**
	 * Ensures that {@link Account} instances can be marshalled.
	 *
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 * @throws AddressException
	 *             (won't be thrown: address is hardcoded)
	 */
	@Test
	public void jaxbMarshalling() throws JAXBException, XPathExpressionException, AddressException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(Account.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		Account account = new Account();
		account.setName("foo");
		account.getAuthTokens().add(new AuthToken(account, UUID.randomUUID()));
		account.getLogins().add(new GameLoginIdentity(account, new InternetAddress("foo@example.com"), "secret"));

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(account, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps", XmlNamespace.RPSTOURNEY_API));
		Node accountNode = (Node) xpath.evaluate("/rps:account", domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(accountNode);
		Node idNode = (Node) xpath.evaluate("/rps:account/rps:id", domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("-1", idNode.getTextContent());
		Node nameNode = (Node) xpath.evaluate("/rps:account/rps:name", domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("foo", nameNode.getTextContent());
		Node rolesNode = (Node) xpath.evaluate("/rps:account/rps:roles", domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(rolesNode);
		Node roleNode = (Node) xpath.evaluate("/rps:account/rps:roles/rps:role[1]", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNotNull(roleNode);
		Assert.assertEquals(SecurityRole.USERS.toString(), roleNode.getTextContent());
		Node tokensNode = (Node) xpath.evaluate("/rps:account/rps:authTokens", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNull(tokensNode);
		Node loginsNode = (Node) xpath.evaluate("/rps:account/rps:logins", domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(loginsNode);
		Node loginNode = (Node) xpath.evaluate("/rps:account/rps:logins/rps:login[1]", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNotNull(loginNode);
		Node loginAccountNode = (Node) xpath.evaluate("/rps:account/rps:logins/rps:login[1]/rps:account",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNull(loginAccountNode);
		Node loginEmailNode = (Node) xpath.evaluate("/rps:account/rps:logins/rps:login[1]/rps:emailAddress",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(loginEmailNode);
		Node loginPasswordNode = (Node) xpath.evaluate("/rps:account/rps:logins/rps:login[1]/rps:passwordHash",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNull(loginPasswordNode);
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
	public void jaxbUnmarshalling() throws JAXBException, XPathExpressionException {
		// Create the Unmarshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(Account.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader().getResource("sample-xml/account-1.xml");

		// Parse the XML to an object.
		Account parsedAccount = (Account) unmarshaller.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedAccount);
		Assert.assertEquals(42, parsedAccount.getId());
		Assert.assertEquals("foo", parsedAccount.getName());
		Assert.assertEquals(new HashSet<>(Arrays.asList(SecurityRole.USERS)), parsedAccount.getRoles());
		Assert.assertEquals(2, parsedAccount.getLogins().size());
		Assert.assertSame(parsedAccount, parsedAccount.getLogins().get(0).getAccount());
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
	public void equalsAndHashCode()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Account accountA = new Account();

		Assert.assertEquals(accountA, accountA);
		Assert.assertEquals(accountA.hashCode(), accountA.hashCode());

		Account accountB = new Account();

		Assert.assertNotEquals(accountA, accountB);

		/*
		 * The logic for Account.equals(...) is different for persisted vs. non-persisted objects, and we want to cover
		 * that logic with this test. To avoid having to involve the DB here (and actually persist things), we'll cheat
		 * and set the fields' values via reflection.
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

	/**
	 * Verifies that {@link Account}s can be properly serialized and deserialized.
	 *
	 * @throws IOException
	 *             Might be thrown if serialization or deserialization fails.
	 * @throws ClassNotFoundException
	 *             Might be thrown if serialization or deserialization fails.
	 */
	@Test
	public void serialization() throws IOException, ClassNotFoundException {
		// Create a mock Account.
		Account account = new Account(SecurityRole.USERS);
		account.setName("foo");
		AuthToken authToken = new AuthToken(account, UUID.randomUUID());
		account.getAuthTokens().add(authToken);

		// Run the Account through serialization and deserialization.
		ByteArrayOutputStream bytesOutStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutStream = new ObjectOutputStream(bytesOutStream);
		objectOutStream.writeObject(account);
		objectOutStream.close();
		ByteArrayInputStream bytesInStream = new ByteArrayInputStream(bytesOutStream.toByteArray());
		ObjectInputStream objectInStream = new ObjectInputStream(bytesInStream);
		Account accountCopy = (Account) objectInStream.readObject();
		objectInStream.close();

		// Verify the deserialized CookieStore.
		Assert.assertEquals(account.hasId(), accountCopy.hasId());
		Assert.assertEquals(account.getName(), accountCopy.getName());
		Assert.assertEquals(account.getRoles(), accountCopy.getRoles());
		Assert.assertEquals(account.getAuthTokens(), accountCopy.getAuthTokens());
	}
}
