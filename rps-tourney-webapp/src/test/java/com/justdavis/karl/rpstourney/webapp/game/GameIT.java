package com.justdavis.karl.rpstourney.webapp.game;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
			Assert.assertTrue(driver.getCurrentUrl(), driver.getCurrentUrl().contains("game/"));
			Assert.assertFalse(driver.getCurrentUrl(), driver.getCurrentUrl().endsWith("game/"));

			// Spot-check one of the page's elements to ensure it's present.
			Assert.assertEquals(
					String.format("Invalid response: %s: %s", driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("players")).size());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Tests {@link GameController} and {@link GameExceptionHandler} to ensure
	 * that {@link GameNotFoundException}s are converted to 404s.
	 * 
	 * @throws IOException
	 *             (indicates a test failure)
	 */
	@Test
	public void gameNotFound404() throws IOException {
		URL badGameUrl = new URL(ITUtils.buildWebAppUrl("game/foo"));
		HttpURLConnection gameConnection = null;
		try {
			gameConnection = (HttpURLConnection) badGameUrl.openConnection();
			Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), gameConnection.getResponseCode());
		} finally {
			if (gameConnection != null)
				gameConnection.disconnect();
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
			GuestAuthClient authClient = new GuestAuthClient(clientConfig, cookieStore);
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (service): Create the game.
			authClient.loginAsGuest();
			GameView game = gameClient.createGame();

			// Player 2 (webapp): Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s", driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("players")).size());

			// Player 2 (webapp): Join and set max rounds to 1.
			driver.findElement(By.id("join-game")).click();
			driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals("1", driver.findElement(By.className("max-rounds-value")).getText());
			Assert.assertEquals("1", getRoundCounterCurrent(driver));

			// Player 1 (service): Throw rock.
			gameClient.submitThrow(game.getId(), 0, Throw.ROCK);

			// Player 2 (webapp): Throw paper.
			driver.findElement(By.className("throw-paper")).click();
			Assert.assertEquals("0",
					driver.findElement(By.cssSelector("div#player-second p.player-score-value")).getText());
			Assert.assertEquals("1",
					driver.findElement(By.cssSelector("div#player-first p.player-score-value")).getText());
			Assert.assertEquals("Anonymous Player (You) Won!",
					driver.findElement(By.xpath("//tr[@id='result-row']/td[4]")).getText());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Exercises the web application to ensure that game can be played against
	 * an AI opponent.
	 */
	@Test
	public void playGameVsAi() {
		WebDriver player1Driver = null;
		try {
			// Create the Selenium driver that will be used for Player 1.
			player1Driver = new HtmlUnitDriver(true);

			// Create the game.
			player1Driver.get(ITUtils.buildWebAppUrl("game/"));

			// Set max rounds to 1.
			player1Driver.findElement(By.id("max-rounds-down")).click();

			// Request an AI opponent.
			player1Driver.findElement(By.id("opponent-type-ai")).click();
			Select aiIdSelect = new Select(
					player1Driver.findElement(By.cssSelector("form#opponent-selection select#ai-id")));
			aiIdSelect.selectByVisibleText("Easy");
			player1Driver.findElement(By.cssSelector("form#opponent-selection button[type='submit']")).click();

			// Verify that the Player 2 is now correct.
			Assert.assertEquals("Easy",
					player1Driver.findElement(By.cssSelector("div#player-second .player-name")).getText());

			// Verify that, once Player 1 makes a move, Player 2 does, too.
			player1Driver.findElement(By.className("throw-rock")).click();
			Assert.assertEquals("Rock",
					player1Driver.findElement(By.xpath("//tr[@id='round-data-0']/td[2]")).getText());
			String aiThrowText = player1Driver.findElement(By.xpath("//tr[@id='round-data-0']/td[3]")).getText();
			Assert.assertTrue(
					aiThrowText.equals("Rock") || aiThrowText.equals("Paper") || aiThrowText.equals("Scissors"));

			/*
			 * Because the AI for Player 2 will just make random moves, we don't
			 * really want to try and play to the end of the game-- no point.
			 * The web service ITs verify that AI games will play all the way to
			 * the end. We just want to ensure they at least start correctly in
			 * the web interface.
			 */
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
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
			Wait<WebDriver> wait = new WebDriverWait(driver, 20);

			// Create the web service clients that will be used for Player 1.
			ClientConfig clientConfig = ITUtils.createClientConfig();
			CookieStore cookieStore = new CookieStore();
			GuestAuthClient authClient = new GuestAuthClient(clientConfig, cookieStore);
			AccountsClient accountsClient = new AccountsClient(clientConfig, cookieStore);
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (service): Login, set name, and create a game.
			Account player1 = authClient.loginAsGuest();
			player1.setName("foo");
			accountsClient.updateAccount(player1);
			GameView game = gameClient.createGame();

			// Player 2: Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s", driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("players")).size());

			// Player 2 (webapp): Join game.
			driver.findElement(By.id("join-game")).click();

			// Player 2 (webapp): Check Player 1's name.
			Assert.assertEquals("foo", driver.findElement(By.cssSelector("#player-second .player-name")).getText());

			// Player 2 (webapp): Check player name controls' state.
			Assert.assertTrue(driver.findElement(By.cssSelector("#player-first .player-name")).isDisplayed());
			wait.until(ExpectedConditions
					.invisibilityOf(driver.findElement(By.xpath("//form[contains(@class, 'player-name')]"))));

			/*
			 * Player 2 (webapp): Activate the name editor, check the controls'
			 * state.
			 */
			driver.findElement(By.cssSelector("#player-first .player-name")).click();
			Assert.assertTrue(driver.findElement(By.xpath("//form[contains(@class, 'player-name')]")).isDisplayed());
			Assert.assertFalse(driver.findElement(By.cssSelector("#player-first .player-name")).isDisplayed());
			Assert.assertTrue(driver.findElement(By.xpath("//form[contains(@class, 'player-name')]")).isDisplayed());

			// Player 2 (webapp): Update name.
			driver.findElement(By.xpath("//input[@name='inputPlayerName']")).sendKeys("bar");
			driver.findElement(By.cssSelector("div#player-first button.player-name-submit")).click();

			// Player 2 (webapp): Check Player 2's name.
			/*
			 * FIXME Broken due to apparent HtmlUnit bug, where clicking in a
			 * form causes a spurious onblur event to be fired first. See
			 * 2018-02-01 email to the htmlunit-user mailing list for details.
			 */
			// wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#player-first.player-name"),
			// "bar (You)"));
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
			String gameId = driver.getCurrentUrl().substring(driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1 (webapp): Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s", driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("players")).size());

			// Player 1 (webapp): Try to set max rounds to invalid value.
			driver.findElement(By.id("max-rounds-down")).click();
			driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals(1, driver.findElements(By.id("game-warning")).size());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Ensures that the throw controls are hidden before a game starts and
	 * displayed once it does.
	 */
	@Test
	public void hideThrowsBeforeStart() {
		WebDriver player1Driver = null;
		WebDriver player2Driver = null;
		try {
			// Create the Selenium drivers that will be used.
			player1Driver = new HtmlUnitDriver(true);
			player2Driver = new HtmlUnitDriver(false);

			// Player 1 (webapp): Create the game.
			player1Driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = player1Driver.getCurrentUrl().substring(player1Driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1 (webapp): Spot-check the page to ensure it's working.
			player1Driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(String.format("Invalid response: %s: %s", player1Driver.getCurrentUrl(),
					player1Driver.getPageSource()), 1, player1Driver.findElements(By.id("players")).size());

			// Player 1 (webapp): Verify the throw controls are hidden.
			Assert.assertTrue(
					player1Driver.findElement(By.className("player-throws")).getAttribute("class").contains("hidden"));

			// Player 2 (webapp): Spot-check the page to ensure it's working.
			player2Driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(String.format("Invalid response: %s: %s", player2Driver.getCurrentUrl(),
					player2Driver.getPageSource()), 1, player2Driver.findElements(By.id("players")).size());

			// Player 2 (webapp): Join and set max rounds to 1.
			player2Driver.findElement(By.id("join-game")).click();

			// Player 1 (webapp): Verify the throw controls are hidden.
			player1Driver.navigate().refresh();
			Assert.assertFalse(
					player1Driver.findElement(By.className("player-throws")).getAttribute("class").contains("hidden"));
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
			if (player2Driver != null)
				player2Driver.quit();
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
			GuestAuthClient authClient = new GuestAuthClient(clientConfig, cookieStore);
			AccountsClient accountsClient = new AccountsClient(clientConfig, cookieStore);
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (service): Login, set name, and create a game.
			Account player1 = authClient.loginAsGuest();
			player1.setName("foo");
			accountsClient.updateAccount(player1);
			GameView game = gameClient.createGame();

			// Player 2: Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", game.getId()));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s", driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("players")).size());

			// Player 2 (webapp): Join game.
			driver.findElement(By.id("join-game")).click();

			// Player 2 (webapp): Try to submit throw twice.
			driver.findElement(By.className("throw-paper")).click();
			driver.findElement(By.className("throw-paper")).click();
			Assert.assertEquals(1, driver.findElements(By.id("game-warning")).size());
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
			GuestAuthClient authClient = new GuestAuthClient(clientConfig, cookieStore);
			GameClient gameClient = new GameClient(clientConfig, cookieStore);

			// Player 1 (webapp): Create the game.
			driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = driver.getCurrentUrl().substring(driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1 (webapp): Spot-check the page to ensure it's working.
			driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s", driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("players")).size());

			// Player 2 (service): Join and set max rounds.
			authClient.loginAsGuest();
			gameClient.joinGame(gameId);
			gameClient.setMaxRounds(gameId, 3, 1);
			wait.until(ExpectedConditions.not(ExpectedConditions
					.textToBePresentInElementLocated(By.cssSelector("div#player-second .player-name"), "Waiting")));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("max-rounds-value"), "1"));

			// Player 1 (webapp): Throw ROCK.
			driver.findElement(By.className("throw-rock")).click();

			// Player 2 (service): Throw ROCK.
			gameClient.submitThrow(gameId, 0, Throw.ROCK);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//tr[@id='round-data-0']/td[1]"),
					"1"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//tr[@id='round-data-0']/td[2]"),
					"Rock"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//tr[@id='round-data-0']/td[3]"),
					"Rock"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//tr[@id='round-data-0']/td[4]"),
					"(tied)"));

			// Player 1 (webapp): Throw ROCK.
			driver.findElement(By.className("throw-rock")).click();

			// Player 2 (service): Throw PAPER.
			gameClient.submitThrow(gameId, 1, Throw.PAPER);
			wait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.cssSelector("#player-first .player-score-value"), "0"));
			wait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.cssSelector("#player-second .player-score-value"), "1"));
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//tr[@id='result-row']/td[4]"),
					"Anonymous Player Won!"));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".player-throws")));
		} catch (TimeoutException e) {
			/*
			 * If one of these are thrown, the page has the wrong state. We need
			 * to log the page state to help debug the problem.
			 */
			throw new TimeoutException("Test case failed. Current page source:\n" + driver.getPageSource(), e);
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
	 * This is a regression test case for
	 * <a href="https://github.com/karlmdavis/rps-tourney/issues/53">Issue #53:
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
			String gameId = player1Driver.getCurrentUrl().substring(player1Driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1: Spot-check the page to ensure it's working.
			player1Driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(String.format("Invalid response: %s: %s", player1Driver.getCurrentUrl(),
					player1Driver.getPageSource()), 1, player1Driver.findElements(By.id("players")).size());

			// Player 2: Open the game.
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));

			/*
			 * Play through a three-round game, checking the round counts for
			 * both players at every step.
			 */
			player1Wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("round-counter-current"), "1"));
			Assert.assertEquals("1", getRoundCounterCurrent(player2Driver));

			player2Driver.findElement(By.id("join-game")).click();
			player1Wait.until(ExpectedConditions.elementToBeClickable(By.className("throw-rock")));
			Assert.assertEquals("1", getRoundCounterCurrent(player2Driver));

			player1Driver.findElement(By.className("throw-rock")).click();
			player2Driver.findElement(By.className("throw-paper")).click();
			player1Wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("round-counter-current"), "2"));
			Assert.assertEquals("2", getRoundCounterCurrent(player2Driver));

			player1Driver.findElement(By.className("throw-rock")).click();
			player2Driver.findElement(By.className("throw-rock")).click();
			player1Wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("round-counter-current"), "2"));
			Assert.assertEquals("2", getRoundCounterCurrent(player2Driver));

			player1Driver.findElement(By.className("throw-rock")).click();
			player2Driver.findElement(By.className("throw-scissors")).click();
			player1Wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("round-counter-current"), "3"));
			Assert.assertEquals("3", getRoundCounterCurrent(player2Driver));

			player1Driver.findElement(By.className("throw-rock")).click();
			player2Driver.findElement(By.className("throw-paper")).click();
			player1Wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("round-counter-current"), "3"));
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
	 * This is a regression test case for
	 * <a href="https://github.com/karlmdavis/rps-tourney/issues/73">Issue #73:
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
			String gameId = player1Driver.getCurrentUrl().substring(player1Driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 1: Spot-check the page to ensure it's working.
			player1Driver.get(ITUtils.buildWebAppUrl("game", gameId));
			Assert.assertEquals(String.format("Invalid response: %s: %s", player1Driver.getCurrentUrl(),
					player1Driver.getPageSource()), 1, player1Driver.findElements(By.id("players")).size());

			// Player 2: Join the game.
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));
			player2Driver.findElement(By.id("join-game")).click();

			/*
			 * Player 1: Wait for player 2's status to refresh. All this wait is
			 * really needed for is to delay adjusting the rounds until at least
			 * one JS update cycle has completed.
			 */
			player1Wait.until(ExpectedConditions.not(ExpectedConditions
					.textToBePresentInElementLocated(By.cssSelector("div#player-second .player-name"), "Waiting")));

			// Player 1: Increase the max rounds to 7.
			player1Driver.findElement(By.id("max-rounds-up")).click();
			player1Driver.findElement(By.id("max-rounds-up")).click();
			Assert.assertEquals("7", player1Driver.findElement(By.className("max-rounds-value")).getText());

			// Player 1: Increase the max rounds to 7.
			player1Wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("round-counter-current"), "1"));
			Assert.assertEquals("1", getRoundCounterCurrent(player2Driver));

			// Player 2: Refresh and decrease the max rounds to 5.
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));
			player2Driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals("5", player2Driver.findElement(By.className("max-rounds-value")).getText());

			// Player 1: Verify that the max rounds refreshes dynamically.
			player1Wait
					.until(ExpectedConditions.textToBePresentInElementLocated(By.className("max-rounds-value"), "5"));
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
			if (player2Driver != null)
				player2Driver.quit();
		}
	}

	/**
	 * Ensures that the web application correctly returns the game state as
	 * JSON.
	 * 
	 * @throws IOException
	 *             (will be passed through if it occurs, indicating a test
	 *             error)
	 */
	@Test
	public void jsonGameData() throws IOException {
		/*
		 * Use two Selenium players to play through the game a bit, so that the
		 * game state is "interesting" enough to test.
		 */
		WebDriver player1Driver = null;
		WebDriver player2Driver = null;
		String gameId;
		try {
			player1Driver = new HtmlUnitDriver(true);
			Wait<WebDriver> player1Wait = new WebDriverWait(player1Driver, 20);
			player2Driver = new HtmlUnitDriver(false);
			player1Driver.get(ITUtils.buildWebAppUrl("register"));
			player1Driver.findElement(By.id("inputEmail")).sendKeys("foo23@example.com");
			player1Driver.findElement(By.id("inputPassword1")).sendKeys("secret");
			player1Driver.findElement(By.id("inputPassword2")).sendKeys("secret");
			player1Driver.findElement(By.cssSelector("form#register button[type=submit]")).click();
			player1Driver.get(ITUtils.buildWebAppUrl("game/"));
			gameId = player1Driver.getCurrentUrl().substring(player1Driver.getCurrentUrl().lastIndexOf('/') + 1);
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));
			player2Driver.findElement(By.id("join-game")).click();
			player1Driver.navigate().refresh();
			player1Driver.findElement(By.className("throw-rock")).click();
			player2Driver.findElement(By.className("throw-paper")).click();
			player1Wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("round-counter-current"), "2"));
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
			if (player2Driver != null)
				player2Driver.quit();
		}

		/*
		 * Now, pull the game state as JSON data and verify that it looks
		 * correct.
		 */
		URL gameDataUrl = new URL(ITUtils.buildWebAppUrl("game/" + gameId + "/data"));
		InputStream gameDataStream = null;
		Scanner gameDataScanner = null;
		try {
			HttpURLConnection gameDataConnection = (HttpURLConnection) gameDataUrl.openConnection();
			gameDataStream = gameDataConnection.getInputStream();
			gameDataScanner = new Scanner(gameDataStream, StandardCharsets.UTF_8.name());
			gameDataScanner.useDelimiter("\\A");
			String gameDataString = gameDataScanner.hasNext() ? gameDataScanner.next() : null;
			Assert.assertNotNull(gameDataString);
			JsonNode gameDataJson = new ObjectMapper().readTree(gameDataString);
			Assert.assertEquals(gameId, gameDataJson.get("id").asText());
		} finally {
			if (gameDataScanner != null)
				gameDataScanner.close();
			if (gameDataStream != null)
				gameDataStream.close();
		}
	}

	/**
	 * A regression test case for
	 * <a href="https://github.com/karlmdavis/rps-tourney/issues/78">Issue #78:
	 * "You Won" / "You Lost" display wrong: a 3 to 1 win reports "You Lost"</a>
	 * . Verifies that the AJAXy win/loss display is correct when your opponent
	 * makes the final move and loses.
	 */
	@Test
	public void ajaxOpponentLoses() {
		WebDriver player1Driver = null;
		WebDriver player2Driver = null;
		try {
			// Create the Selenium drivers.
			player1Driver = new HtmlUnitDriver(true);
			Wait<WebDriver> player1Wait = new WebDriverWait(player1Driver, 20);
			player2Driver = new HtmlUnitDriver(true);

			// Play the (short) game, with player 2 going last and losing.
			player1Driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = player1Driver.getCurrentUrl().substring(player1Driver.getCurrentUrl().lastIndexOf('/') + 1);
			player1Driver.findElement(By.id("max-rounds-down")).click();
			Assert.assertEquals("1", player1Driver.findElement(By.className("max-rounds-value")).getText());
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));
			player2Driver.findElement(By.id("join-game")).click();
			player1Wait.until(ExpectedConditions.elementToBeClickable(By.className("throw-rock")));
			player1Driver.findElement(By.className("throw-rock")).click();
			player2Driver.findElement(By.className("throw-scissors")).click();

			// Verify that the won/lost displays are correct.
			player1Wait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.cssSelector("div#player-first p.player-score-value"), "1"));
			Assert.assertEquals("0",
					player1Driver.findElement(By.cssSelector("div#player-second p.player-score-value")).getText());
			Assert.assertEquals("Anonymous Player (You) Won!",
					player1Driver.findElement(By.xpath("//tr[@id='result-row']/td[4]")).getText());
			Assert.assertEquals("Anonymous Player Won!",
					player2Driver.findElement(By.xpath("//tr[@id='result-row']/td[4]")).getText());
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
		}
	}

	/**
	 * <p>
	 * Verifies that the AJAXy round history updates are rendered correctly for
	 * non-players.
	 * </p>
	 * <p>
	 * This is also a regression test case for
	 * <a href="https://github.com/karlmdavis/rps-tourney/issues/79">Issue #79:
	 * Round history table updates goofily: rows out of order</a>.
	 * </p>
	 */
	@Test
	public void ajaxObserver() {
		WebDriver observerDriver = null;
		try {
			// Create the Selenium driver.
			observerDriver = new HtmlUnitDriver(true);
			Wait<WebDriver> observerWait = new WebDriverWait(observerDriver, 20);

			// Create the web service clients that will be used for the players.
			ClientConfig clientConfig = ITUtils.createClientConfig();
			CookieStore player1Cookies = new CookieStore();
			CookieStore player2Cookies = new CookieStore();
			GuestAuthClient player1AuthClient = new GuestAuthClient(clientConfig, player1Cookies);
			Account player1Account = player1AuthClient.loginAsGuest();
			AccountsClient player1AccountsClient = new AccountsClient(clientConfig, player1Cookies);
			player1Account.setName("player1");
			player1AccountsClient.updateAccount(player1Account);
			GuestAuthClient player2AuthClient = new GuestAuthClient(clientConfig, player2Cookies);
			Account player2Account = player2AuthClient.loginAsGuest();
			AccountsClient player2AccountsClient = new AccountsClient(clientConfig, player2Cookies);
			player2Account.setName("player2");
			player2AccountsClient.updateAccount(player2Account);
			GameClient player1GameClient = new GameClient(clientConfig, player1Cookies);
			GameClient player2GameClient = new GameClient(clientConfig, player2Cookies);

			/*
			 * Play a game, verifying the observer's round history.
			 */

			GameView game = player1GameClient.createGame();

			observerDriver.get(ITUtils.buildWebAppUrl("game/" + game.getId()));
			player2GameClient.joinGame(game.getId());
			observerWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("#join-controls")));

			player1GameClient.submitThrow(game.getId(), 0, Throw.ROCK);
			player2GameClient.submitThrow(game.getId(), 0, Throw.ROCK);
			observerWait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.xpath("//table[@id='rounds']/tbody/tr[1]/td[4]"), "(tied)"));

			player1GameClient.submitThrow(game.getId(), 1, Throw.ROCK);
			player2GameClient.submitThrow(game.getId(), 1, Throw.PAPER);
			observerWait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.xpath("//table[@id='rounds']/tbody/tr[2]/td[4]"), "player2"));

			player1GameClient.submitThrow(game.getId(), 2, Throw.PAPER);
			player2GameClient.submitThrow(game.getId(), 2, Throw.ROCK);
			observerWait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.xpath("//table[@id='rounds']/tbody/tr[3]/td[4]"), "player1"));

			player1GameClient.submitThrow(game.getId(), 3, Throw.ROCK);
			player2GameClient.submitThrow(game.getId(), 3, Throw.PAPER);
			observerWait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.xpath("//table[@id='rounds']/tbody/tr[4]/td[4]"), "player2"));
			observerWait.until(ExpectedConditions
					.textToBePresentInElementLocated(By.xpath("//table[@id='rounds']/tbody/tr[5]/td[4]"), "player2"));
		} catch (TimeoutException e) {
			/*
			 * If one of these are thrown, the page has the wrong state. We need
			 * to log the page state to help debug the problem.
			 */
			throw new TimeoutException("Test case failed. Current page source:\n" + observerDriver.getPageSource(), e);
		} finally {
			if (observerDriver != null)
				observerDriver.quit();
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
