package com.justdavis.karl.rpstourney.service.api.game;

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
import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeFormatter;
import org.w3c.dom.Node;

import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
import com.justdavis.karl.rpstourney.service.api.auth.Account;

/**
 * Unit tests for {@link GameView}.
 */
public final class GameViewTest {
	/**
	 * Ensures that {@link GameView} instances can be marshalled when
	 * {@link GameView#getState()} is {@link State#WAITING_FOR_PLAYER}.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbMarshallingWaiting() throws JAXBException, XPathExpressionException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(GameView.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instances to be converted to XML.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		Game game = new Game(player1);
		GameView gameView = new GameView(game, player1);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(gameView, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps", XmlNamespace.RPSTOURNEY_API));
		Node gameNode = (Node) xpath.evaluate("/rps:gameView", domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(gameNode);
		Node idNode = (Node) xpath.evaluate("/rps:gameView/rps:id", domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals(game.getId(), idNode.getTextContent());
		Node timestampNode = (Node) xpath.evaluate("/rps:gameView/rps:createdTimestamp", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals(DateTimeFormatter.ISO_INSTANT.format(game.getCreatedTimestamp()),
				timestampNode.getTextContent());
		Node stateNode = (Node) xpath.evaluate("/rps:gameView/rps:state", domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("WAITING_FOR_PLAYER", stateNode.getTextContent());
		Node roundsNode = (Node) xpath.evaluate("/rps:gameView/rps:maxRounds", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals("3", roundsNode.getTextContent());
		Node player1AccountNode = (Node) xpath.evaluate("/rps:gameView/rps:player1/rps:humanAccount",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(player1AccountNode);
		Node player1AccountIdNode = (Node) xpath.evaluate("/rps:gameView/rps:player1/rps:humanAccount/rps:id",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("0", player1AccountIdNode.getTextContent());
	}

	/**
	 * Ensures that {@link GameView} instances can be marshalled when
	 * {@link GameView#getState()} is {@link State#STARTED}.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbMarshallingStarted() throws JAXBException, XPathExpressionException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(GameView.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instances to be converted to XML.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		Game game = new Game(player1);
		Account player2Account = new Account();
		Player player2 = new Player(player2Account);
		game.setPlayer2(player2);
		game.submitThrow(0, player1, Throw.ROCK);
		GameView gameView = new GameView(game, player1);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(gameView, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps", XmlNamespace.RPSTOURNEY_API));
		Node gameNode = (Node) xpath.evaluate("/rps:gameView", domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(gameNode);
		Node idNode = (Node) xpath.evaluate("/rps:gameView/rps:id", domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals(game.getId(), idNode.getTextContent());
		Node timestampNode = (Node) xpath.evaluate("/rps:gameView/rps:createdTimestamp", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals(DateTimeFormatter.ISO_INSTANT.format(game.getCreatedTimestamp()),
				timestampNode.getTextContent());
		Node stateNode = (Node) xpath.evaluate("/rps:gameView/rps:state", domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("STARTED", stateNode.getTextContent());
		Node roundsNode = (Node) xpath.evaluate("/rps:gameView/rps:maxRounds", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals("3", roundsNode.getTextContent());
		Node player1AccountNode = (Node) xpath.evaluate("/rps:gameView/rps:player1/rps:humanAccount",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(player1AccountNode);
		Node player1AccountIdNode = (Node) xpath.evaluate("/rps:gameView/rps:player1/rps:humanAccount/rps:id",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("0", player1AccountIdNode.getTextContent());
		Node throwNode = (Node) xpath.evaluate("/rps:gameView/rps:rounds/rps:round[1]/rps:throwForPlayer1",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("ROCK", throwNode.getTextContent());
	}

	/**
	 * Ensures that {@link GameView} instances can be unmarshalled.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbUnmarshalling() throws JAXBException, XPathExpressionException {
		// Create the Unmarshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(GameView.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader().getResource("sample-xml/game-view-1.xml");

		// Parse the XML to an object.
		GameView parsedGameView = (GameView) unmarshaller.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedGameView);
		Assert.assertEquals("abcdefghij", parsedGameView.getId());
		Assert.assertEquals(Instant.parse("2007-12-03T10:15:30Z"), parsedGameView.getCreatedTimestamp());
		Assert.assertEquals(State.WAITING_FOR_PLAYER, parsedGameView.getState());
		Assert.assertEquals(3, parsedGameView.getMaxRounds());
		Assert.assertEquals(0, parsedGameView.getScoreForPlayer1());
		Assert.assertEquals(0, parsedGameView.getScoreForPlayer2());
		Assert.assertNotNull(parsedGameView.getPlayer1());
		Assert.assertNotNull(parsedGameView.getPlayer1().getHumanAccount());
		Assert.assertEquals(42, parsedGameView.getPlayer1().getHumanAccount().getId());
		Assert.assertNotNull(parsedGameView.getRounds());
		Assert.assertEquals(1, parsedGameView.getRounds().size());
		Assert.assertEquals(0, parsedGameView.getRounds().get(0).getRoundIndex());
		Assert.assertEquals(0, parsedGameView.getRounds().get(0).getAdjustedRoundIndex());
		Assert.assertEquals(Throw.ROCK, parsedGameView.getRounds().get(0).getThrowForPlayer1());
		Assert.assertNull(parsedGameView.getRounds().get(0).getThrowForPlayer2());
		Assert.assertEquals(7, parsedGameView.getViewPlayer().getId());
	}

	/**
	 * Ensures that {@link GameView} filters incomplete rounds, as expected.
	 */
	@Test
	public void roundFiltering() {
		// Create the game to be filtered
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		Game game = new Game(player1);
		Account player2Account = new Account();
		Player player2 = new Player(player2Account);
		game.setPlayer2(player2);
		game.submitThrow(0, player1, Throw.ROCK);

		// Make sure that the first round is filtered as expected.
		GameView gameViewForPlayer1 = new GameView(game, player1);
		GameView gameViewForPlayer2 = new GameView(game, player2);
		GameView gameViewForNullPlayer = new GameView(game, null);
		Assert.assertNotNull(gameViewForPlayer1.getRounds().get(0).getThrowForPlayer1());
		Assert.assertNull(gameViewForPlayer2.getRounds().get(0).getThrowForPlayer1());
		Assert.assertNull(gameViewForNullPlayer.getRounds().get(0).getThrowForPlayer1());

		/*
		 * Move to the next round, have Player 2 go first, and verify that the
		 * rounds are filtered as expected.
		 */
		game.submitThrow(0, player2, Throw.PAPER);
		game.submitThrow(1, player2, Throw.SCISSORS);
		gameViewForPlayer1 = new GameView(game, player1);
		gameViewForPlayer2 = new GameView(game, player2);
		gameViewForNullPlayer = new GameView(game, null);
		Assert.assertNotNull(gameViewForNullPlayer.getRounds().get(0).getThrowForPlayer1());
		Assert.assertNotNull(gameViewForNullPlayer.getRounds().get(0).getThrowForPlayer2());
		Assert.assertNull(gameViewForPlayer1.getRounds().get(1).getThrowForPlayer2());
	}
}
