package com.justdavis.karl.rpstourney.webapp.account;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.justdavis.karl.rpstourney.webapp.ITUtils;

/**
 * Integration tests for both {@link RegisterController} and
 * <code>register.jsp</code>.
 */
public final class RegisterIT {
	/**
	 * Ensures that users can register and successfully view their account
	 * details, when they weren't logged in at all beforehand.
	 */
	@Test
	public void registerWithNoLoginAndViewAccount() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);

			// Register for an account.
			String username = buildRandomEmail();
			driver.get(ITUtils.buildWebAppUrl("register"));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("inputEmail")).size());
			driver.findElement(By.id("inputEmail")).sendKeys(username);
			driver.findElement(By.id("inputPassword1")).sendKeys("secret");
			driver.findElement(By.id("inputPassword2")).sendKeys("secret");
			driver.findElement(
					By.cssSelector("form#register button[type=submit]"))
					.click();

			// Ensure we were redirected to the homepage.
			Assert.assertEquals(ITUtils.buildWebAppUrl(""),
					driver.getCurrentUrl());

			// Attempt to access the account details page.
			driver.get(ITUtils.buildWebAppUrl("account"));
			Assert.assertEquals(ITUtils.buildWebAppUrl("account"),
					driver.getCurrentUrl());
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("account-properties")).size());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Ensures that users can register a login for an existing anonymous
	 * account, without losing their game history, etc.
	 */
	@Test
	public void registerWithAnonymousLogin() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);

			// Create a game (and login as guest).
			driver.get(ITUtils.buildWebAppUrl("game/"));
			String gameUrl = driver.getCurrentUrl();

			// Register for an account.
			String username = buildRandomEmail();
			driver.get(ITUtils.buildWebAppUrl("register"));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("inputEmail")).size());
			driver.findElement(By.id("inputEmail")).sendKeys(username);
			driver.findElement(By.id("inputPassword1")).sendKeys("secret");
			driver.findElement(By.id("inputPassword2")).sendKeys("secret");
			driver.findElement(
					By.cssSelector("form#register button[type=submit]"))
					.click();

			// Ensure we were redirected to the homepage.
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()),
					ITUtils.buildWebAppUrl(""), driver.getCurrentUrl());

			// Verify that the user's game is still there.
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()),
					1,
					driver.findElements(
							By.cssSelector(String.format(
									"table#player-games a[href='%s']", gameUrl)))
							.size());

			// Verify that the account was actually registered.
			driver.get(ITUtils.buildWebAppUrl("account"));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()),
					username, driver.findElement(By.id("emails")).getText());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * @return a random valid-looking email address for use as a fake account
	 */
	private String buildRandomEmail() {
		return String.format("someone%d@example.com", new Random().nextInt());
	}
}
