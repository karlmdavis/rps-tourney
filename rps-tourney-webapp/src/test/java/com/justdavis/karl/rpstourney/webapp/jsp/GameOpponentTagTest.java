package com.justdavis.karl.rpstourney.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.junit.Assert;
import org.junit.Test;
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
		Assert.assertEquals("foo", jspWriter.output.toString());
	}
}
