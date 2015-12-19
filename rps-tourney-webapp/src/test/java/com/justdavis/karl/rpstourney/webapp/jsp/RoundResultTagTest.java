package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.security.core.context.SecurityContextImpl;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameRound;
import com.justdavis.karl.rpstourney.service.api.game.GameRound.Result;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.webapp.security.WebServiceAccountAuthentication;

/**
 * Unit tests for {@link RoundResultTag}.
 */
public class RoundResultTagTest {
	/**
	 * Tests usage of {@link RoundResultTag} when the current user won the
	 * {@link GameRound}.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withUserAsWinner() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the mock data to use.
		Account player1Account = new Account();
		securityContext.setAuthentication(new WebServiceAccountAuthentication(player1Account));
		Game game = new Game(new Player(player1Account));
		game.setPlayer2(new Player(new Account()));
		game.submitThrow(0, game.getPlayer1(), Throw.ROCK);
		game.submitThrow(0, game.getPlayer2(), Throw.SCISSORS);
		GameView gameView = new GameView(game, game.getPlayer1());

		// Create the tag to test.
		RoundResultTag roundResultTag = new RoundResultTag();
		roundResultTag.setMockSecurityContext(securityContext);
		roundResultTag.setMessageSource(createMessageSource());
		roundResultTag.setPageContext(pageContext);
		roundResultTag.setGame(gameView);
		roundResultTag.setRound(gameView.getRounds().get(0));

		// Test the tag.
		roundResultTag.doEndTag();
		Assert.assertEquals(
				"<span class=\"won\">" + "<span class=\"PLAYER_1\">Anonymous Player (You)</span>" + "</span>",
				jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link RoundResultTag} when the current user is just an
	 * observer and {@link GameRound#getResult()} is {@link Result#TIED}.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withUserAsObserver() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the mock data to use.
		Game game = new Game(new Player(new Account()));
		game.setPlayer2(new Player(new Account()));
		game.submitThrow(0, game.getPlayer1(), Throw.ROCK);
		game.submitThrow(0, game.getPlayer2(), Throw.ROCK);
		GameView gameView = new GameView(game, null);

		// Create the tag to test.
		RoundResultTag roundResultTag = new RoundResultTag();
		roundResultTag.setMockSecurityContext(securityContext);
		roundResultTag.setMessageSource(createMessageSource());
		roundResultTag.setPageContext(pageContext);
		roundResultTag.setGame(gameView);
		roundResultTag.setRound(gameView.getRounds().get(0));

		// Test the tag.
		roundResultTag.doEndTag();
		Assert.assertEquals("(tied)", jspWriter.output.toString());
	}

	/**
	 * @return a mock {@link MessageSource}
	 */
	private MessageSource createMessageSource() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("playerName.current.suffix", Locale.getDefault(), " (You)");
		messageSource.addMessage("playerName.anon", Locale.getDefault(), "Anonymous Player");
		messageSource.addMessage("roundResult.tied", Locale.getDefault(), "(tied)");
		return messageSource;
	}
}
