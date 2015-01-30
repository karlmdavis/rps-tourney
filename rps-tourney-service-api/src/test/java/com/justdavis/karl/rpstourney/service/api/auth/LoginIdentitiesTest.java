package com.justdavis.karl.rpstourney.service.api.auth;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
import com.justdavis.karl.rpstourney.service.api.auth.guest.GuestLoginIdentity;

/**
 * Unit tests for {@link LoginIdentities}.
 */
public final class LoginIdentitiesTest {
	/**
	 * Ensures that {@link LoginIdentities} instances can be marshalled.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 * @throws AddressException
	 *             (won't be thrown: address is hardcoded)
	 */
	@Test
	public void jaxbMarshalling() throws JAXBException,
			XPathExpressionException, AddressException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext
				.newInstance(LoginIdentities.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instance to be converted to XML.
		List<ILoginIdentity> logins = new ArrayList<>();
		Account account = new Account();
		account.setName("foo");
		GuestLoginIdentity guestLogin = new GuestLoginIdentity(account);
		logins.add(guestLogin);
		GameLoginIdentity gameLogin = new GameLoginIdentity(account,
				new InternetAddress("foo@example.com"), "secret");
		logins.add(gameLogin);
		LoginIdentities loginsWrapper = new LoginIdentities(logins);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(loginsWrapper, domResult);

		// Verify the list.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps",
				XmlNamespace.RPSTOURNEY_API));
		Node loginsNode = (Node) xpath.evaluate("/rps:logins",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(loginsNode);
		Assert.assertEquals(2, loginsNode.getChildNodes().getLength());

		// Verify the GuestLoginIdentity.
		Node guestLoginNode = (Node) xpath.evaluate(
				"/rps:logins//rps:guestLogin", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNotNull(guestLoginNode);
		Assert.assertEquals("foo", xpath.evaluate("//rps:account/rps:name",
				guestLoginNode, XPathConstants.STRING));

		// Verify the GameLoginIdentity.
		Node gameLoginNode = (Node) xpath.evaluate(
				"/rps:logins//rps:gameLogin", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertNotNull(gameLoginNode);
		Assert.assertEquals("foo", xpath.evaluate("//rps:account/rps:name",
				gameLoginNode, XPathConstants.STRING));

		/*
		 * Ensure that GameLoginIdentity isn't leaking password hashes
		 * unintentionally.
		 */
		Assert.assertNull(xpath.evaluate("//rps:passwordHash", gameLoginNode,
				XPathConstants.NODE));
	}
}
