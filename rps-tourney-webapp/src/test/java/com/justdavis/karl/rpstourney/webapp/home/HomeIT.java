package com.justdavis.karl.rpstourney.webapp.home;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

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
}
