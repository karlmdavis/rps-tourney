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
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.webapp.security.WebServiceAccountAuthentication;

/**
 * Unit tests for {@link GameOpponentTag}.
 */
public class GameOpponentTagTest {
	/**
	 * Tests usage of {@link GameOpponentTag} when an opponent can be found, and
	 * that opponent has a name.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withOpponentThatHasAName() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		GameOpponentTag gameOpponentTag = new GameOpponentTag();
		gameOpponentTag.setMockSecurityContext(securityContext);
		gameOpponentTag.setPageContext(pageContext);
		gameOpponentTag.setMessageSource(createMessageSource());

		// Test the tag.
		Account player1Account = new Account();
		securityContext.setAuthentication(new WebServiceAccountAuthentication(
				player1Account));
		Account player2Account = new Account();
		player2Account.setName("foo");
		Game game = new Game(new Player(player1Account));
		game.setPlayer2(new Player(player2Account));
		GameView gameView = new GameView(game, player1Account);
		gameOpponentTag.setGame(gameView);
		gameOpponentTag.doEndTag();
		Assert.assertEquals("<span class=\"PLAYER_2\">foo</span>",
				jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link GameOpponentTag} when the opponent hasn't yet
	 * joined.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withNullOpponent() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		GameOpponentTag gameOpponentTag = new GameOpponentTag();
		gameOpponentTag.setMockSecurityContext(securityContext);
		gameOpponentTag.setPageContext(pageContext);
		gameOpponentTag.setMessageSource(createMessageSource());

		// Test the tag.
		Account player1Account = new Account();
		securityContext.setAuthentication(new WebServiceAccountAuthentication(
				player1Account));
		Game game = new Game(new Player(player1Account));
		GameView gameView = new GameView(game, player1Account);
		gameOpponentTag.setGame(gameView);
		gameOpponentTag.doEndTag();
		Assert.assertEquals(
				"<span class=\"PLAYER_2\">(Waiting for Opponent...)</span>",
				jspWriter.output.toString());
	}

	/**
	 * @return a mock {@link MessageSource}
	 */
	private MessageSource createMessageSource() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("playerName.current.suffix",
				Locale.getDefault(), " (You)");
		messageSource.addMessage("playerName.notJoined", Locale.getDefault(),
				"(Waiting for Opponent...)");
		messageSource.addMessage("playerName.anon", Locale.getDefault(),
				"Anonymous");
		return messageSource;
	}
}
