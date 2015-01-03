package com.justdavis.karl.rpstourney.webapp.game;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

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
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("player-controls")).size());
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
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (service): Create the game.
			authClient.loginAsGuest();
			GameView game = gameClient.createGame();

			// Player 2 (webapp): Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("player-controls")).size());

			// Player 2 (webapp): Join and set max rounds to 1.
			driver.findElement(By.id("join-game")).click();
			driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals("1",
					driver.findElement(By.id("max-rounds-value")).getText());
			Assert.assertEquals("1",
					driver.findElement(By.id("round-counter-max")).getText());
			Assert.assertEquals("1", getRoundCounterCurrent(driver));

			// Player 1 (service): Throw rock.
			gameClient.submitThrow(game.getId(), 0, Throw.ROCK);

			// Player 2 (webapp): Throw paper.
			driver.findElement(
					By.xpath("//div[@id='player-2-controls']//a[@class='throw-paper']"))
					.click();
			Assert.assertEquals(
					"0",
					driver.findElement(
							By.cssSelector("div#player-1-controls p.player-score-value"))
							.getText());
			Assert.assertEquals(
					"1",
					driver.findElement(
							By.cssSelector("div#player-2-controls p.player-score-value"))
							.getText());
			Assert.assertEquals(
					"You Won!",
					driver.findElement(By.xpath("//tr[@id='result-row']/td[4]"))
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

			// Player 2: Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("player-controls")).size());

			// Player 2 (webapp): Join game.
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
			driver.findElement(By.xpath("//input[@name='inputPlayerName']"))
					.sendKeys("bar");
			driver.findElement(
					By.cssSelector("div#player-2-controls button.player-name-submit"))
					.click();

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

	/**
	 * Ensures that the game behaves correctly if the user attempts to set the
	 * number of rounds to an invalid value.
	 */
	@Test
	public void setMaxRoundsToInvalidValue() {
		WebDriver driver = null;
		try {
			// Create the Selenium driver that will be used for Player 1.
			driver = new HtmlUnitDriver(true);

			// Player 1 (webapp): Create the game.
			driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = driver.getCurrentUrl().substring(
					driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1 (webapp): Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("player-controls")).size());

			// Player 1 (webapp): Try to set max rounds to invalid value.
			driver.findElement(By.id("max-rounds-down")).click();
			driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals(1, driver.findElements(By.id("game-warning"))
					.size());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Ensures that the game behaves correctly if the user attempts to submit a
	 * throw before the game starts.
	 */
	@Test
	public void submitThrowBeforeStart() {
		WebDriver driver = null;
		try {
			// Create the Selenium driver that will be used for Player 1.
			driver = new HtmlUnitDriver(true);

			// Player 1 (webapp): Create the game.
			driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = driver.getCurrentUrl().substring(
					driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1 (webapp): Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("player-controls")).size());

			// Player 1 (webapp): Try to submit a throw before game has started.
			driver.findElement(
					By.xpath("//div[@id='player-1-controls']//a[@class='throw-paper']"))
					.click();
			Assert.assertEquals(1, driver.findElements(By.id("game-warning"))
					.size());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Ensures that the game behaves correctly if the user attempts to submit
	 * more than one throw for the same round.
	 */
	@Test
	public void submitThrowTwiceInSameRound() {
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

			// Player 2: Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("player-controls")).size());

			// Player 2 (webapp): Join game.
			driver.findElement(By.id("join-game")).click();

			// Player 2 (webapp): Try to submit throw twice.
			driver.findElement(
					By.xpath("//div[@id='player-2-controls']//a[@class='throw-paper']"))
					.click();
			driver.findElement(
					By.xpath("//div[@id='player-2-controls']//a[@class='throw-paper']"))
					.click();
			Assert.assertEquals(1, driver.findElements(By.id("game-warning"))
					.size());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Verifies that the web application updates the display of in-progress
	 * games via background AJAX refreshes.
	 */
	@Test
	public void ajaxRefresh() {
		WebDriver driver = null;
		try {
			// Create the Selenium driver that will be used for Player 1.
			driver = new HtmlUnitDriver(true);
			Wait<WebDriver> wait = new WebDriverWait(driver, 20);

			// Create the web service clients that will be used for Player 2.
			ClientConfig clientConfig = ITUtils.createClientConfig();
			CookieStore cookieStore = new CookieStore();
			GuestAuthClient authClient = new GuestAuthClient(clientConfig,
					cookieStore);
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (webapp): Create the game.
			driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = driver.getCurrentUrl().substring(
					driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1 (webapp): Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("player-controls")).size());

			// Player 2 (service): Join and set max rounds.
			authClient.loginAsGuest();
			gameClient.joinGame(gameId);
			gameClient.setMaxRounds(gameId, 3, 1);
			wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(
					By.cssSelector("div#player-2-controls h3.player-name"),
					"Not Joined")));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.id("max-rounds-value"), "1"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.id("round-counter-max"), "1"));

			// Player 1 (webapp): Throw ROCK.
			driver.findElement(
					By.xpath("//div[@id='player-1-controls']//a[@class='throw-rock']"))
					.click();

			// Player 2 (service): Throw ROCK.
			gameClient.submitThrow(gameId, 0, Throw.ROCK);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.xpath("//tr[@id='round-data-0']/td[1]"), "1"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.xpath("//tr[@id='round-data-0']/td[2]"), "Rock"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.xpath("//tr[@id='round-data-0']/td[3]"), "Rock"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.xpath("//tr[@id='round-data-0']/td[4]"), "(tied)"));

			// Player 1 (webapp): Throw ROCK.
			driver.findElement(
					By.xpath("//div[@id='player-1-controls']//a[@class='throw-rock']"))
					.click();

			// Player 2 (service): Throw PAPER.
			gameClient.submitThrow(gameId, 1, Throw.PAPER);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.id("player-1-score-value"), "0"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.id("player-2-score-value"), "1"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(
					By.xpath("//tr[@id='result-row']/td[4]"), "You Lost"));
		} catch (TimeoutException e) {
			/*
			 * If one of these are thrown, the page has the wrong state. We need
			 * to log the page state to help debug the problem.
			 */
			throw new TimeoutException(
					"Test case failed. Current page source:\n"
							+ driver.getPageSource(), e);
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * <p>
	 * Ensures that games display the correct round count, with and without
	 * JavaScript.
	 * </p>
	 * <p>
	 * This is a regression test case for <a
	 * href="https://github.com/karlmdavis/rps-tourney/issues/53">Issue #53:
	 * Current round counter is very goofy.</a>.
	 * </p>
	 */
	@Test
	public void currentRoundCounterCorrectness() {
		WebDriver player1Driver = null;
		WebDriver player2Driver = null;
		try {

			// Player 1: Create a driver with JS.
			player1Driver = new HtmlUnitDriver(true);
			Wait<WebDriver> player1Wait = new WebDriverWait(player1Driver, 20);

			// Player 2: Create a driver without JS.
			player2Driver = new HtmlUnitDriver(false);

			// Player 1: Create the game.
			player1Driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = player1Driver.getCurrentUrl().substring(
					player1Driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1: Spot-check the page to ensure it's working.
			player1Driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(String.format("Invalid response: %s: %s",
					player1Driver.getCurrentUrl(),
					player1Driver.getPageSource()), 1, player1Driver
					.findElements(By.id("player-controls")).size());

			// Player 2: Open the game.
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));

			/*
			 * Play through a three-round game, checking the round counts for
			 * both players at every step.
			 */
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(
							By.id("round-counter-current"), "1"));
			Assert.assertEquals("1", getRoundCounterCurrent(player2Driver));

			player2Driver.findElement(By.id("join-game")).click();
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(
							By.id("round-counter-current"), "1"));
			Assert.assertEquals("1", getRoundCounterCurrent(player2Driver));

			player1Driver
					.findElement(
							By.xpath("//div[@id='player-1-controls']//a[@class='throw-rock']"))
					.click();
			player2Driver
					.findElement(
							By.xpath("//div[@id='player-2-controls']//a[@class='throw-paper']"))
					.click();
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(
							By.id("round-counter-current"), "2"));
			Assert.assertEquals("2", getRoundCounterCurrent(player2Driver));

			player1Driver
					.findElement(
							By.xpath("//div[@id='player-1-controls']//a[@class='throw-rock']"))
					.click();
			player2Driver
					.findElement(
							By.xpath("//div[@id='player-2-controls']//a[@class='throw-rock']"))
					.click();
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(
							By.id("round-counter-current"), "2"));
			Assert.assertEquals("2", getRoundCounterCurrent(player2Driver));

			player1Driver
					.findElement(
							By.xpath("//div[@id='player-1-controls']//a[@class='throw-rock']"))
					.click();
			player2Driver
					.findElement(
							By.xpath("//div[@id='player-2-controls']//a[@class='throw-scissors']"))
					.click();
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(
							By.id("round-counter-current"), "3"));
			Assert.assertEquals("3", getRoundCounterCurrent(player2Driver));

			player1Driver
					.findElement(
							By.xpath("//div[@id='player-1-controls']//a[@class='throw-rock']"))
					.click();
			player2Driver
					.findElement(
							By.xpath("//div[@id='player-2-controls']//a[@class='throw-paper']"))
					.click();
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(
							By.id("round-counter-current"), "3"));
			Assert.assertEquals("3", getRoundCounterCurrent(player2Driver));
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
			if (player2Driver != null)
				player2Driver.quit();
		}
	}

	/**
	 * <p>
	 * Ensures that the JavaScript correctly creates the max round controls.
	 * </p>
	 * <p>
	 * This is a regression test case for <a
	 * href="https://github.com/karlmdavis/rps-tourney/issues/73">Issue #73:
	 * Webapp 404s when setting max rounds</a>.
	 * </p>
	 */
	@Test
	public void joinGameAndAlterRounds() {
		WebDriver player1Driver = null;
		WebDriver player2Driver = null;
		try {

			// Player 1: Create driver with JS.
			player1Driver = new HtmlUnitDriver(true);
			Wait<WebDriver> player1Wait = new WebDriverWait(player1Driver, 20);

			// Player 2: Create a driver without JS.
			player2Driver = new HtmlUnitDriver(false);

			// Player 1: Create the game.
			player1Driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = player1Driver.getCurrentUrl().substring(
					player1Driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1: Spot-check the page to ensure it's working.
			player1Driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(String.format("Invalid response: %s: %s",
					player1Driver.getCurrentUrl(),
					player1Driver.getPageSource()), 1, player1Driver
					.findElements(By.id("player-controls")).size());

			// Player 2: Join the game.
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));
			player2Driver.findElement(By.id("join-game")).click();

			/*
			 * Player 1: Wait for player 2's status to refresh. All this wait is
			 * really needed for is to delay adjusting the rounds until at least
			 * one JS update cycle has completed.
			 */
			player1Wait
					.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(
							By.cssSelector("div#player-2-controls h3.player-name"),
							"Not Joined")));

			// Player 1: Increase the max rounds to 7.
			player1Driver.findElement(By.id("max-rounds-up")).click();
			player1Driver.findElement(By.id("max-rounds-up")).click();
			Assert.assertEquals("7",
					player1Driver.findElement(By.id("max-rounds-value"))
							.getText());

			// Player 1: Increase the max rounds to 7.
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(
							By.id("round-counter-current"), "1"));
			Assert.assertEquals("1", getRoundCounterCurrent(player2Driver));

			// Player 2: Refresh and decrease the max rounds to 5.
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));
			player2Driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals("5",
					player2Driver.findElement(By.id("max-rounds-value"))
							.getText());

			// Player 1: Verify that the max rounds refreshes dynamically.
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.id("max-rounds-value"),
							"5"));
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
			if (player2Driver != null)
				player2Driver.quit();
		}
	}

	/**
	 * @param driver
	 *            the {@link WebDriver} to use
	 * @return the value of the <code>#round-counter-current</code> element on
	 *         the specified {@link WebDriver}'s current page
	 */
	private String getRoundCounterCurrent(WebDriver driver) {
		return driver.findElement(By.id("round-counter-current")).getText();
	}
}
