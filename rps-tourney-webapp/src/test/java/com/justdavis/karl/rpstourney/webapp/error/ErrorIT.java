package com.justdavis.karl.rpstourney.webapp.error;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.http.HttpStatus;

import com.justdavis.karl.rpstourney.webapp.ITUtils;

/**
 * Integration tests for both {@link ErrorController} and <code>error-*.jsp</code>.
 */
public final class ErrorIT {
	/**
	 * Tests {@link ErrorController#goBoom()}.
	 */
	@Test
	public void server500Errors() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);
			driver.get(ITUtils.buildWebAppUrl("/error/go-boom"));

			// Spot-check one of the page's elements to ensure it's present.
			Assert.assertEquals(driver.getPageSource(), "Oops!", driver.findElement(By.tagName("h1")).getText());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	/**
	 * Tests {@link ErrorController} for {@link HttpStatus#NOT_FOUND} errors.
	 */
	@Test
	public void server404Errors() {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver(true);
			driver.get(ITUtils.buildWebAppUrl("/notarealpage"));

			// Spot-check one of the page's elements to ensure it's present.
			Assert.assertEquals(driver.getPageSource(), "Page Not Found",
					driver.findElement(By.tagName("h1")).getText());
		} finally {
			if (driver != null)
				driver.quit();
		}
	}
}
