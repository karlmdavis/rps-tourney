package com.justdavis.karl.rpstourney.webapp.game;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.justdavis.karl.rpstourney.service.api.game.GameSession;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameSessionClient;
import com.justdavis.karl.rpstourney.webapp.ITUtils;

/**
 * Integration tests for both {@link GameController} and <code>game.jsp</code>.
 */
public final class GameIT {
	/**
	 * Tests
	 * {@link GameController#createNewGame(java.security.Principal, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * .
	 */
	@Test
	public void createNewGame() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);
			driver.get(ITUtils.buildWebAppUrl("game/"));

			// Make sure the request was redirected to "game/{gameid}".
			Assert.assertTrue(driver.getCurrentUrl(), driver.getCurrentUrl()
					.contains("game/"));
			Assert.assertFalse(driver.getCurrentUrl(), driver.getCurrentUrl()
					.endsWith("game/"));

			// Spot-check one of the page's elements to ensure it's present.
			Assert.assertNotNull(driver.getPageSource(),
					driver.findElement(By.id("currentRound")));
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Uses {@link GameController} and {@link GameSessionClient} to ensure that
	 * a game can be played via the web application.
	 */
	@Test
	public void playShortGame() {
		WebDriver driver = null;
		try {
			// Create the Selenium driver that will be used for Player 2.
			driver = new HtmlUnitDriver(true);

			// Create the web service clients that will be used for Player 1.
			ClientConfig clientConfig = ITUtils.createClientConfig();
			CookieStore cookieStore = new CookieStore();
			GuestAuthClient authClient = new GuestAuthClient(clientConfig,
					cookieStore);
			GameSessionClient gameClient = new GameSessionClient(clientConfig,
					cookieStore);

			// Player 1 (service): Create the game.
			authClient.loginAsGuest();
			GameSession game = gameClient.createGame();

			// Player 2 (webapp): Join and set max rounds to 1.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			driver.findElement(By.id("joinLink")).click();
			driver.findElement(By.id("maxRoundsDown")).click();
			Assert.assertEquals("1", driver
					.findElement(By.id("maxRoundsValue")).getAttribute("value"));

			// Player 1 (service): Throw rock.
			gameClient.submitThrow(game.getId(), 0, Throw.ROCK);

			// Player 2 (webapp): Throw paper.
			driver.findElement(By.id("player2ThrowPaper")).click();
			Assert.assertEquals("0",
					driver.findElement(By.id("player1ScoreValue")).getText());
			Assert.assertEquals("1",
					driver.findElement(By.id("player2ScoreValue")).getText());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}
}
