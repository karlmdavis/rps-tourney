package com.justdavis.karl.rpstourney.account;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.justdavis.karl.rpstourney.webapp.ITUtils;
import com.justdavis.karl.rpstourney.webapp.account.AccountController;
import com.justdavis.karl.rpstourney.webapp.account.RegisterController;

/**
 * Integration tests for {@link RegisterController}, {@link AccountController},
 * and <code>login.jsp</code>.
 */
public final class AuthenticationIT {
	/**
	 * Ensures that attempts to login with non-existing accounts don't succeed.
	 */
	@Test
	public void loginFailsForInvalidAccount() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);
			driver.get(ITUtils.buildWebAppUrl("login"));

			// Spot-check the page to ensure it loaded correctly.
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("username")).size());
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 0,
					driver.findElements(By.id("login-message")).size());

			// Fill in a (bad) username & password, click Login.
			driver.findElement(By.id("username")).sendKeys("fake@example.com");
			driver.findElement(By.id("password")).sendKeys(
					"nottherightpassword");
			driver.findElement(By.cssSelector("form#login button[type=submit]"))
					.click();

			// Make sure we're still on the login page with an error.
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("login-message")).size());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Ensures that users who login from an existing anonymous account have
	 * their game history, etc. merged into the account that they are logging
	 * into.
	 */
	@Test
	public void loginFromAnonymousAccount() {
		WebDriver driverA = null;
		WebDriver driverB = null;
		try {
			// Register for an account and create a game.
			driverA = new HtmlUnitDriver(true);
			driverA.get(ITUtils.buildWebAppUrl("register"));
			driverA.findElement(By.id("inputEmail"))
					.sendKeys("foo@example.com");
			driverA.findElement(By.id("inputPassword1")).sendKeys("secret");
			driverA.findElement(By.id("inputPassword2")).sendKeys("secret");
			driverA.findElement(
					By.cssSelector("form#register button[type=submit]"))
					.click();
			Assert.assertEquals(ITUtils.buildWebAppUrl(""),
					driverA.getCurrentUrl());
			driverA.get(ITUtils.buildWebAppUrl("game/"));

			// In a separate session, create an anonymous account and a game.
			driverB = new HtmlUnitDriver(true);
			driverB.get(ITUtils.buildWebAppUrl("game/"));

			// Now have that separate session login to the first account.
			driverB.get(ITUtils.buildWebAppUrl("login"));
			driverB.findElement(By.id("username")).sendKeys("foo@example.com");
			driverB.findElement(By.id("password")).sendKeys("secret");
			driverB.findElement(
					By.cssSelector("form#login button[type=submit]")).click();
			Assert.assertEquals(ITUtils.buildWebAppUrl("account"),
					driverB.getCurrentUrl());

			/*
			 * Verify that the login worked and that the account is now
			 * associated with both games.
			 */
			driverB.get(ITUtils.buildWebAppUrl(""));
			Assert.assertEquals(
					2,
					driverB.findElements(
							By.cssSelector("table#player-games tbody tr"))
							.size());
		} finally {
			if (driverA != null)
				driverA.quit();
		}
	}

	/**
	 * Ensures that the sign in/account control that's part of the pages'
	 * template works correctly.
	 */
	@Test
	public void signInAndAccountControl() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);

			// Go to the homepage and check for the Sign In control.
			driver.get(ITUtils.buildWebAppUrl("/"));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("sign-in")).size());

			// Create a game (and login as guest).
			driver.get(ITUtils.buildWebAppUrl("game/"));

			// Ensure the Sign In control is still there.
			driver.get(ITUtils.buildWebAppUrl("/"));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("sign-in")).size());

			// Register for an account.
			String username = buildRandomEmail();
			driver.get(ITUtils.buildWebAppUrl("register"));
			driver.findElement(By.id("inputEmail")).sendKeys(username);
			driver.findElement(By.id("inputPassword1")).sendKeys("secret");
			driver.findElement(By.id("inputPassword2")).sendKeys("secret");
			driver.findElement(
					By.cssSelector("form#register button[type=submit]"))
					.click();

			/*
			 * Ensure the Sign In control was replaced with the current account
			 * control.
			 */
			driver.get(ITUtils.buildWebAppUrl("/"));
			Assert.assertEquals(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()), 1,
					driver.findElements(By.id("signed-in")).size());

			// Set the current user's name.
			driver.get(ITUtils.buildWebAppUrl("/account"));
			driver.findElement(By.id("inputName")).sendKeys("Foo");
			driver.findElement(
					By.cssSelector("form#account-properties button[type=submit]"))
					.click();

			// Verify that the account control has the name in it.
			Assert.assertTrue(
					String.format("Invalid response: %s: %s",
							driver.getCurrentUrl(), driver.getPageSource()),
					driver.findElement(By.id("signed-in")).getText()
							.contains("Foo"));
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
