package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.security.core.context.SecurityContextImpl;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.Game;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.webapp.security.WebServiceAccountAuthentication;

/**
 * Unit tests for {@link PlayerNameTag}.
 */
public class PlayerNameTagTest {
	/**
	 * Tests usage of {@link PlayerNameTag} when the {@link Player} has no name, is set as
	 * {@link GameView#getPlayer1()}, and is the current user.
	 *
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withNoNameAsPlayer1AndAsUser() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		PlayerNameTag playerNameTag = new PlayerNameTag();
		playerNameTag.setMockSecurityContext(securityContext);
		playerNameTag.setMessageSource(createMessageSource());
		playerNameTag.setPageContext(pageContext);

		// Test the tag.
		Account player1Account = new Account();
		securityContext.setAuthentication(new WebServiceAccountAuthentication(player1Account));
		Game game = new Game(new Player(player1Account));
		GameView gameView = new GameView(game, game.getPlayer1());
		playerNameTag.setGame(gameView);
		playerNameTag.setPlayer(game.getPlayer1());
		playerNameTag.doEndTag();
		Assert.assertEquals("<span class=\"PLAYER_1\">Anonymous (You)</span>", jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link PlayerNameTag} when the {@link Player} has a name and is set as both
	 * {@link GameView#getPlayer1()} {@link GameView#getPlayer2()}.
	 *
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	@Ignore("Skipped until games support playing against self")
	public void withNameAsPlayer1AndPlayer2() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		PlayerNameTag playerNameTag = new PlayerNameTag();
		playerNameTag.setMockSecurityContext(securityContext);
		playerNameTag.setMessageSource(createMessageSource());
		playerNameTag.setPageContext(pageContext);

		// Test the tag.
		Account player1Account = new Account();
		player1Account.setName("foo");
		Game game = new Game(new Player(player1Account));
		game.setPlayer2(game.getPlayer1());
		GameView gameView = new GameView(game, null);
		playerNameTag.setGame(gameView);
		playerNameTag.setPlayer(game.getPlayer1());
		playerNameTag.doEndTag();
		Assert.assertEquals("<span class=\"PLAYER_1 PLAYER_2\">foo</span>", jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link PlayerNameTag} when it's set to render out as a not-yet-joined
	 * {@link GameView#getPlayer2()}.
	 *
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withNullPlayer2() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		PlayerNameTag playerNameTag = new PlayerNameTag();
		playerNameTag.setMockSecurityContext(securityContext);
		playerNameTag.setMessageSource(createMessageSource());
		playerNameTag.setPageContext(pageContext);

		// Test the tag.
		Account player1Account = new Account();
		Game game = new Game(new Player(player1Account));
		GameView gameView = new GameView(game, null);
		playerNameTag.setGame(gameView);
		playerNameTag.setPlayer(game.getPlayer2());
		playerNameTag.doEndTag();
		Assert.assertEquals("<span class=\"PLAYER_2\">(Waiting for Opponent...)</span>", jspWriter.output.toString());
	}

	/**
	 * Tests usage of {@link PlayerNameTag} when it's set to render out as a {@link GameView#getPlayer2()} that is a
	 * {@link BuiltInAi}.
	 *
	 * @throws IOException
	 *             (indicates a problem with the test setup)
	 * @throws JspException
	 *             (indicates a problem with the test setup)
	 */
	@Test
	public void withAiPlayer2() throws JspException, IOException {
		// Create the mock objects to use.
		SecurityContextImpl securityContext = new SecurityContextImpl();
		MockJspWriter jspWriter = new MockJspWriter();
		MockPageContext pageContext = new MockPageContext(jspWriter);

		// Create the tag to test.
		PlayerNameTag playerNameTag = new PlayerNameTag();
		playerNameTag.setMockSecurityContext(securityContext);
		playerNameTag.setMessageSource(createMessageSource());
		playerNameTag.setPageContext(pageContext);

		// Test the tag.
		Account player1Account = new Account();
		Game game = new Game(new Player(player1Account));
		game.setPlayer2(new Player(BuiltInAi.ONE_SIDED_DIE_PAPER));
		GameView gameView = new GameView(game, null);
		playerNameTag.setGame(gameView);
		playerNameTag.setPlayer(game.getPlayer2());
		playerNameTag.doEndTag();
		Assert.assertEquals("<span class=\"PLAYER_2\">Always Paper</span>", jspWriter.output.toString());
	}

	/**
	 * @return a mock {@link MessageSource}
	 */
	private MessageSource createMessageSource() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("playerName.current.suffix", Locale.getDefault(), " (You)");
		messageSource.addMessage("playerName.notJoined", Locale.getDefault(), "(Waiting for Opponent...)");
		messageSource.addMessage("playerName.anon", Locale.getDefault(), "Anonymous");
		messageSource.addMessage("players.ai.name.oneSidedDiePaper", Locale.getDefault(), "Always Paper");
		return messageSource;
	}
}
