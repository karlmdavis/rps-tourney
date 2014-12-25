package com.justdavis.karl.rpstourney.webapp.home;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.justdavis.karl.rpstourney.service.api.game.State;
import com.justdavis.karl.rpstourney.webapp.ITUtils;

/**
 * Integration tests for both {@link HomeController} and <code>home.jsp</code>.
 */
public final class HomeIT {
	/**
	 * Tests {@link HomeController#getHomePage()} for an unauthenticated user.
	 */
	@Test
	public void home_unauthenticated() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);
			driver.get(ITUtils.buildWebAppUrl("/"));

			// Spot-check one of the page's elements to ensure it's present.
			Assert.assertNotNull(driver.getPageSource(),
					driver.findElement(By.id("create-game")));
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Tests {@link HomeController#getHomePage()} for an authenticated user that
	 * has at least one game to be displayed.
	 */
	@Test
	public void home_withGame() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);

			// Create a game (and login as guest).
			driver.get(ITUtils.buildWebAppUrl("game/"));

			// Go to the home page.
			driver.get(ITUtils.buildWebAppUrl("/"));

			// Spot-check one of the page's elements to ensure it's present.
			Assert.assertNotNull(driver.getPageSource(),
					driver.findElement(By.id("player-games")));
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Ensures that the home page doesn't throw errors when it displays a game
	 * at the {@link State#WAITING_FOR_FIRST_THROW} step. This is a regression
	 * test case for <a
	 * href="https://github.com/karlmdavis/rps-tourney/issues/66">Error on home
	 * page after new game has been joined:
	 * "No message found under code 'home.games.game.state.WAITING_FOR_FIRST_THROW'..."
	 * </a>.
	 */
	@Test
	public void home_withGameBeforeFirstThrow() {
		WebDriver player1Driver = null;
		WebDriver player2Driver = null;
		try {

			// Create the Selenium drivers for each player.
			player1Driver = new HtmlUnitDriver(false);
			player2Driver = new HtmlUnitDriver(false);

			// Player 1: Create a game.
			player1Driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameId = player1Driver.getCurrentUrl().substring(
					player1Driver.getCurrentUrl().lastIndexOf('/') + 1);

			// Player 2: Join the game.
			player2Driver.get(ITUtils.buildWebAppUrl("game/" + gameId));
			player2Driver.findElement(By.id("join-game")).click();

			// Player 1: Go to the home page.
			player1Driver.get(ITUtils.buildWebAppUrl("/"));

			// Player 1: Spot-check one of the page's elements to ensure it's
			// present.
			Assert.assertNotNull(player1Driver.getPageSource(),
					player1Driver.findElement(By.id("player-games")));
		} finally {
			if (player1Driver != null)
				player1Driver.quit();
			if (player2Driver != null)
				player2Driver.quit();
		}
	}
}
