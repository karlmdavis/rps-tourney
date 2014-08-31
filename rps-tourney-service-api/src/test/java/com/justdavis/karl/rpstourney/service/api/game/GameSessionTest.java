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
import org.w3c.dom.Node;

import com.justdavis.karl.misc.xml.SimpleNamespaceContext;
import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;
import com.justdavis.karl.rpstourney.service.api.game.GameSession.State;

/**
 * Unit tests for {@link GameSession}. The JAXB tests here also cover
 * {@link Player} and {@link GameRound}.
 */
public final class GameSessionTest {
	/**
	 * Ensures that {@link GameSession} instances can be marshalled when
	 * {@link GameSession#getState()} is {@link State#WAITING_FOR_PLAYER}.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbMarshallingWaiting() throws JAXBException,
			XPathExpressionException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(GameSession.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instances to be converted to XML.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		GameSession gameSession = new GameSession(player1);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(gameSession, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps",
				XmlNamespace.RPSTOURNEY_API));
		Node gameSessionNode = (Node) xpath.evaluate("/rps:gameSession",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(gameSessionNode);
		Node idNode = (Node) xpath.evaluate("/rps:gameSession/rps:id",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertTrue(idNode.getTextContent().length() == 10);
		Node stateNode = (Node) xpath.evaluate("/rps:gameSession/rps:state",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("WAITING_FOR_PLAYER", stateNode.getTextContent());
		Node roundsNode = (Node) xpath.evaluate(
				"/rps:gameSession/rps:maxRounds", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals("3", roundsNode.getTextContent());
		Node player1AccountNode = (Node) xpath.evaluate(
				"/rps:gameSession/rps:player1/rps:humanAccount",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(player1AccountNode);
		Node player1AccountIdNode = (Node) xpath.evaluate(
				"/rps:gameSession/rps:player1/rps:humanAccount/rps:id",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("-1", player1AccountIdNode.getTextContent());
	}

	/**
	 * Ensures that {@link GameSession} instances can be marshalled when
	 * {@link GameSession#getState()} is {@link State#STARTED}.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void jaxbMarshallingStarted() throws JAXBException,
			XPathExpressionException {
		// Create the Marshaller needed.
		JAXBContext jaxbContext = JAXBContext.newInstance(GameSession.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		// Create the instances to be converted to XML.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		GameSession gameSession = new GameSession(player1);
		Account player2Account = new Account();
		Player player2 = new Player(player2Account);
		gameSession.setPlayer2(player2);
		gameSession.submitThrow(0, player1, Throw.ROCK);

		// Convert it to XML.
		DOMResult domResult = new DOMResult();
		marshaller.marshal(gameSession, domResult);

		// Verify the results.
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new SimpleNamespaceContext("rps",
				XmlNamespace.RPSTOURNEY_API));
		Node gameSessionNode = (Node) xpath.evaluate("/rps:gameSession",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(gameSessionNode);
		Node idNode = (Node) xpath.evaluate("/rps:gameSession/rps:id",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertTrue(idNode.getTextContent().length() == 10);
		Node stateNode = (Node) xpath.evaluate("/rps:gameSession/rps:state",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("STARTED", stateNode.getTextContent());
		Node roundsNode = (Node) xpath.evaluate(
				"/rps:gameSession/rps:maxRounds", domResult.getNode(),
				XPathConstants.NODE);
		Assert.assertEquals("3", roundsNode.getTextContent());
		Node player1AccountNode = (Node) xpath.evaluate(
				"/rps:gameSession/rps:player1/rps:humanAccount",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertNotNull(player1AccountNode);
		Node player1AccountIdNode = (Node) xpath.evaluate(
				"/rps:gameSession/rps:player1/rps:humanAccount/rps:id",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("-1", player1AccountIdNode.getTextContent());
		Node throwNode = (Node) xpath.evaluate(
				"/rps:gameSession/rps:rounds/rps:round[1]/rps:throwForPlayer1",
				domResult.getNode(), XPathConstants.NODE);
		Assert.assertEquals("ROCK", throwNode.getTextContent());
	}

	/**
	 * Ensures that {@link GameSession} instances can be unmarshalled.
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
		JAXBContext jaxbContext = JAXBContext.newInstance(GameSession.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		// Get the XML to be converted.
		URL sourceXmlUrl = Thread.currentThread().getContextClassLoader()
				.getResource("sample-xml/gameSession-1.xml");

		// Parse the XML to an object.
		GameSession parsedGameSession = (GameSession) unmarshaller
				.unmarshal(sourceXmlUrl);

		// Verify the results.
		Assert.assertNotNull(parsedGameSession);
		Assert.assertEquals("abcdefghij", parsedGameSession.getId());
		Assert.assertEquals(State.WAITING_FOR_PLAYER,
				parsedGameSession.getState());
		Assert.assertEquals(3, parsedGameSession.getMaxRounds());
		Assert.assertNotNull(parsedGameSession.getPlayer1());
		Assert.assertNotNull(parsedGameSession.getPlayer1().getHumanAccount());
		Assert.assertEquals(42, parsedGameSession.getPlayer1()
				.getHumanAccount().getId());
		Assert.assertNotNull(parsedGameSession.getRounds());
		Assert.assertEquals(1, parsedGameSession.getRounds().size());
		Assert.assertEquals(0, parsedGameSession.getRounds().get(0)
				.getRoundIndex());
		Assert.assertEquals(Throw.ROCK, parsedGameSession.getRounds().get(0)
				.getThrowForPlayer1());
		Assert.assertNull(parsedGameSession.getRounds().get(0)
				.getThrowForPlayer2());
	}

	/**
	 * Ensures that {@link GameSession} instances' state transitions correctly
	 * at the start of a game.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void gameStartStateTransitions() throws JAXBException,
			XPathExpressionException {
		// Create the initial instances.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		GameSession gameSession = new GameSession(player1);
		gameSession.setMaxRounds(41);

		// Verify the initial state.
		Assert.assertEquals(State.WAITING_FOR_PLAYER, gameSession.getState());
		Assert.assertEquals(41, gameSession.getMaxRounds());
		Assert.assertNotNull(gameSession.getPlayer1());
		Assert.assertNull(gameSession.getPlayer2());
		Assert.assertNotNull(gameSession.getRounds());
		Assert.assertEquals(0, gameSession.getRounds().size());

		// Set the second player.
		Account player2Account = new Account();
		Player player2 = new Player(player2Account);
		gameSession.setPlayer2(player2);
		Assert.assertEquals(State.WAITING_FOR_FIRST_THROW,
				gameSession.getState());
		Assert.assertEquals(41, gameSession.getMaxRounds());
		Assert.assertNotNull(gameSession.getPlayer1());
		Assert.assertNotNull(gameSession.getPlayer2());
		Assert.assertNotNull(gameSession.getRounds());
		Assert.assertEquals(1, gameSession.getRounds().size());

		// Submit the first throw.
		gameSession.submitThrow(0, player1, Throw.ROCK);
		Assert.assertEquals(State.STARTED, gameSession.getState());
	}

	/**
	 * Ensures that {@link GameSession} instances' state transitions correctly
	 * as rounds are completed.
	 * 
	 * @throws JAXBException
	 *             (shouldn't be thrown if things are working)
	 * @throws XPathExpressionException
	 *             (shouldn't be thrown if things are working)
	 */
	@Test
	public void gameRoundTransitions() throws JAXBException,
			XPathExpressionException {
		// Create the initial instances.
		Account player1Account = new Account();
		Player player1 = new Player(player1Account);
		GameSession game = new GameSession(player1);
		game.setMaxRounds(41);

		// Set the second player, which should start the game automatically.
		Account player2Account = new Account();
		Player player2 = new Player(player2Account);
		game.setPlayer2(player2);

		// Play the first round.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();

		// Verify that there's now a second round.
		Assert.assertEquals(State.STARTED, game.getState());
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
	}

	/**
	 * Tests a simple one-round game.
	 */
	@Test
	public void simpleGame() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		GameSession game = new GameSession(player1);
		game.setMaxRounds(1);
		game.setPlayer2(player2);

		// Play the first (and only) round.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(1, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player2, game.getWinner());
	}

	/**
	 * Tests a simple three-round game.
	 */
	@Test
	public void multipleRounds() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		GameSession game = new GameSession(player1);
		game.setMaxRounds(3);
		game.setPlayer2(player2);

		// Play the first round.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(1, game.getRounds().get(1).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the second round.
		game.submitThrow(1, player1, Throw.PAPER);
		game.submitThrow(1, player2, Throw.ROCK);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(2, game.getRounds().get(2).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_1_WON, game.getRounds().get(1)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the third round.
		game.submitThrow(2, player1, Throw.ROCK);
		game.submitThrow(2, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(2)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player2, game.getWinner());
	}

	/**
	 * Tests a game with a tied round.
	 */
	@Test
	public void multipleRoundsWithTie() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		GameSession game = new GameSession(player1);
		game.setMaxRounds(3);
		game.setPlayer2(player2);

		// Play the first round. Winner: Player 2.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(1, game.getRounds().get(1).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the second round. Winner: Player 1.
		game.submitThrow(1, player1, Throw.PAPER);
		game.submitThrow(1, player2, Throw.ROCK);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(3, game.getRounds().size());
		Assert.assertEquals(2, game.getRounds().get(2).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_1_WON, game.getRounds().get(1)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the third round. Winner: (none/tie)
		game.submitThrow(2, player1, Throw.ROCK);
		game.submitThrow(2, player2, Throw.ROCK);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(4, game.getRounds().size());
		Assert.assertEquals(3, game.getRounds().get(3).getRoundIndex());
		Assert.assertEquals(Result.TIED, game.getRounds().get(2).getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the fourth round. Winner: Player 1.
		game.submitThrow(3, player1, Throw.SCISSORS);
		game.submitThrow(3, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(4, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_1_WON, game.getRounds().get(3)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player1, game.getWinner());
	}

	/**
	 * Tests a game where a player wins "early" (in less than the maximum amount
	 * of rounds). The game should end as soon as it becomes impossible for the
	 * other player to win, regardless of the number of rounds left.
	 */
	@Test
	public void earlyWinner() {
		// Create the game.
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		GameSession game = new GameSession(player1);
		game.setMaxRounds(3);
		game.setPlayer2(player2);

		// Play the first round. Winner: Player 2.
		game.submitThrow(0, player1, Throw.ROCK);
		game.submitThrow(0, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(1, game.getRounds().get(1).getRoundIndex());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.STARTED, game.getState());

		// Play the second round. Winner: Player 2.
		game.submitThrow(1, player1, Throw.ROCK);
		game.submitThrow(1, player2, Throw.PAPER);
		if (!game.isRoundPrepared())
			game.prepareRound();
		Assert.assertEquals(2, game.getRounds().size());
		Assert.assertEquals(Result.PLAYER_2_WON, game.getRounds().get(0)
				.getResult());
		Assert.assertEquals(State.FINISHED, game.getState());
		Assert.assertEquals(player2, game.getWinner());
	}

	/**
	 * Tests that {@link GameSession#setMaxRounds(int)} throws a
	 * {@link GameConflictException} when it should.
	 */
	@Test(expected = GameConflictException.class)
	public void setMaxRounds_conflicts() {
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		GameSession game = new GameSession(player1);
		game.setPlayer2(player2);
		game.submitThrow(0, player1, Throw.ROCK);

		// Should blow up:
		game.setMaxRounds(1);
	}

	/**
	 * Tests that {@link GameSession#submitThrow(int, Player, Throw)} throws a
	 * {@link GameConflictException} when the wrong round is specified.
	 */
	@Test(expected = GameConflictException.class)
	public void submitThrow_conflictingRound() {
		Player player1 = new Player(new Account());
		Player player2 = new Player(new Account());
		GameSession game = new GameSession(player1);
		game.setPlayer2(player2);

		// Should blow up:
		game.submitThrow(1, player1, Throw.ROCK);
	}
}
