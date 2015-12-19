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
 * Unit tests for {@link GameTitleTag}.
 */
public class GameTitleTagTest {
	/**
	 * Tests usage of {@link GameTitleTag} when {@link GameView#getPlayer1()} is
	 * the current user and {@link GameView#getPlayer2()} is <code>null</code>.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withPlayer1AsUserAndPlayer2AsNull() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		GameTitleTag gameTitleTag = new GameTitleTag();
		gameTitleTag.setMockSecurityContext(securityContext);
		gameTitleTag.setMessageSource(createMessageSource());
		gameTitleTag.setPageContext(pageContext);

		// Test the tag.
		Account player1Account = new Account();
		securityContext.setAuthentication(new WebServiceAccountAuthentication(player1Account));
		Game game = new Game(new Player(player1Account));
		GameView gameView = new GameView(game, null);
		gameTitleTag.setGame(gameView);
		gameTitleTag.doEndTag();
		Assert.assertEquals(
				"<span class=\"PLAYER_1\">Anonymous Player (You)</span>"
						+ " vs. <span class=\"PLAYER_2\">(Waiting for Opponent...)</span>",
				jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link GameTitleTag} when {@link GameView#getPlayer1()}
	 * exists but has no name and {@link GameView#getPlayer2()} is the current
	 * user.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withPlayer1AnonAndPlayer2AsUser() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		GameTitleTag gameTitleTag = new GameTitleTag();
		gameTitleTag.setMockSecurityContext(securityContext);
		gameTitleTag.setMessageSource(createMessageSource());
		gameTitleTag.setPageContext(pageContext);

		// Test the tag.
		Account player1Account = new Account();
		Account player2Account = new Account();
		securityContext.setAuthentication(new WebServiceAccountAuthentication(player2Account));
		Game game = new Game(new Player(player1Account));
		game.setPlayer2(new Player(player2Account));
		GameView gameView = new GameView(game, null);
		gameTitleTag.setGame(gameView);
		gameTitleTag.doEndTag();
		Assert.assertEquals("<span class=\"PLAYER_2\">Anonymous Player (You)</span>"
				+ " vs. <span class=\"PLAYER_1\">Anonymous Player</span>", jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link GameTitleTag} when both {@link Player}s have a
	 * display name, and neither are the current user.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withOtherNamedPlayers() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		GameTitleTag gameTitleTag = new GameTitleTag();
		gameTitleTag.setMockSecurityContext(securityContext);
		gameTitleTag.setMessageSource(createMessageSource());
		gameTitleTag.setPageContext(pageContext);

		// Test the tag.
		Account player1Account = new Account();
		player1Account.setName("Foo");
		Account player2Account = new Account();
		player2Account.setName("Bar");
		Game game = new Game(new Player(player1Account));
		game.setPlayer2(new Player(player2Account));
		GameView gameView = new GameView(game, null);
		gameTitleTag.setGame(gameView);
		gameTitleTag.doEndTag();
		Assert.assertEquals("<span class=\"PLAYER_1\">Foo</span>" + " vs. <span class=\"PLAYER_2\">Bar</span>",
				jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link GameTitleTag} when it's set to just output to a
	 * variable.
	 * 
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void renderToVariable() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		GameTitleTag gameTitleTag = new GameTitleTag();
		gameTitleTag.setMockSecurityContext(securityContext);
		gameTitleTag.setMessageSource(createMessageSource());
		gameTitleTag.setPageContext(pageContext);
		gameTitleTag.setVar("fizz");

		// Test the tag.
		Account player1Account = new Account();
		player1Account.setName("Foo");
		Account player2Account = new Account();
		player2Account.setName("Bar");
		Game game = new Game(new Player(player1Account));
		game.setPlayer2(new Player(player2Account));
		GameView gameView = new GameView(game, null);
		gameTitleTag.setGame(gameView);
		gameTitleTag.doEndTag();
		Assert.assertEquals("Foo vs. Bar", pageContext.getAttribute("fizz"));
	}

	/**
	 * @return a mock {@link MessageSource}
	 */
	private MessageSource createMessageSource() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("gameTitle.versus", Locale.getDefault(), "vs.");
		messageSource.addMessage("playerName.current.suffix", Locale.getDefault(), " (You)");
		messageSource.addMessage("playerName.notJoined", Locale.getDefault(), "(Waiting for Opponent...)");
		messageSource.addMessage("playerName.anon", Locale.getDefault(), "Anonymous Player");
		return messageSource;
	}
}
