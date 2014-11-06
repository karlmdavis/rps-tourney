package com.justdavis.karl.rpstourney.webapp.game;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.justdavis.karl.rpstourney.service.api.auth.Account;
import com.justdavis.karl.rpstourney.service.api.game.GameView;
import com.justdavis.karl.rpstourney.service.api.game.Throw;
import com.justdavis.karl.rpstourney.service.client.CookieStore;
import com.justdavis.karl.rpstourney.service.client.auth.AccountsClient;
import com.justdavis.karl.rpstourney.service.client.auth.guest.GuestAuthClient;
import com.justdavis.karl.rpstourney.service.client.config.ClientConfig;
import com.justdavis.karl.rpstourney.service.client.game.GameClient;
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
	public void createNewController() {
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
					driver.findElement(By.id("player-controls")));
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Uses {@link GameController} and {@link GameClient} to ensure that a game
	 * can be played via the web application.
	 */
	@Test
	public void playShortController() {
		WebDriver driver = null;
		try {
			// Create the Selenium driver that will be used for Player 2.
			driver = new HtmlUnitDriver(true);

			// Create the web service clients that will be used for Player 1.
			ClientConfig clientConfig = ITUtils.createClientConfig();
			CookieStore cookieStore = new CookieStore();
			GuestAuthClient authClient = new GuestAuthClient(clientConfig,
					cookieStore);
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (service): Create the game.
			authClient.loginAsGuest();
			GameView game = gameClient.createGame();

			// Player 2 (webapp): Join and set max rounds to 1.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			driver.findElement(By.id("join-game")).click();
			driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals("1",
					driver.findElement(By.id("max-rounds-value")).getText());

			// Player 1 (service): Throw rock.
			gameClient.submitThrow(game.getId(), 0, Throw.ROCK);

			// Player 2 (webapp): Throw paper.
			driver.findElement(
					By.xpath("//div[@id='player-2-controls']//a[@class='throw-paper']"))
					.click();
			Assert.assertEquals(
					"0",
					driver.findElement(
							By.xpath("//div[@id='player-1-controls']//p[@class='player-score-value']"))
							.getText());
			Assert.assertEquals(
					"1",
					driver.findElement(
							By.xpath("//div[@id='player-2-controls']//p[@class='player-score-value']"))
							.getText());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Uses {@link GameController} and {@link GameClient} to ensure that players
	 * can change their names.
	 */
	@Test
	public void updateName() {
		WebDriver driver = null;
		try {
			// Create the Selenium driver that will be used for Player 2.
			driver = new HtmlUnitDriver(true);

			// Create the web service clients that will be used for Player 1.
			ClientConfig clientConfig = ITUtils.createClientConfig();
			CookieStore cookieStore = new CookieStore();
			GuestAuthClient authClient = new GuestAuthClient(clientConfig,
					cookieStore);
			AccountsClient accountsClient = new AccountsClient(clientConfig,
					cookieStore);
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (service): Login, set name, and create a game.
			Account player1 = authClient.loginAsGuest();
			player1.setName("foo");
			accountsClient.updateAccount(player1);
			GameView game = gameClient.createGame();

			// Player 2 (webapp): Join game.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			driver.findElement(By.id("join-game")).click();

			// Player 2 (webapp): Check Player 1's name.
			Assert.assertEquals(
					"foo",
					driver.findElement(
							By.xpath("//div[@id='player-1-controls']//h3"))
							.getText());

			// Player 2 (webapp): Check player name controls' state.
			Assert.assertTrue(driver.findElement(
					By.xpath("//div[@id='player-2-controls']//h3"))
					.isDisplayed());
			Assert.assertFalse(driver.findElement(
					By.xpath("//form[contains(@class, 'player-name')]"))
					.isDisplayed());

			/*
			 * Player 2 (webapp): Activate the name editor, check the controls'
			 * state.
			 */
			driver.findElement(By.xpath("//div[@id='player-2-controls']//h3"))
					.click();
			Assert.assertTrue(driver.findElement(
					By.xpath("//form[contains(@class, 'player-name')]"))
					.isDisplayed());
			Assert.assertFalse(driver.findElement(
					By.xpath("//div[@id='player-2-controls']//h3"))
					.isDisplayed());
			Assert.assertTrue(driver.findElement(
					By.xpath("//form[contains(@class, 'player-name')]"))
					.isDisplayed());

			// Player 2 (webapp): Update name.
			driver.findElement(By.xpath("//input[@name='currentPlayerName']"))
					.sendKeys("bar");
			driver.findElement(By.id("player-2-name-submit")).click();

			// Player 2 (webapp): Check Player 2's name.
			Assert.assertEquals(
					"bar",
					driver.findElement(
							By.xpath("//div[@id='player-2-controls']//h3"))
							.getText());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}
}
