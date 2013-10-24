package com.justdavis.karl.rpstourney.app.console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * Unit tests for {@link ConsoleApp}.
 * </p>
 * <p>
 * These tests should probably include a simulated game. However, that's tough
 * to do as the app's API doesn't (and probably shouldn't) allow for the AI
 * opponent to be specified. Without that, there's no way to ensure that the
 * game will end in the expected number of rounds (hypothetically, it could keep
 * tieing forever).
 * </p>
 */
public final class ConsoleAppTest {
	/**
	 * Runs the app with the "<code>--help</code>" flag.
	 * 
	 * @throws UnsupportedEncodingException
	 *             (should not occur)
	 */
	@Test
	public void helpRequested() throws UnsupportedEncodingException {
		String humanInput = "";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				humanInput.getBytes("US-ASCII"));

		ConsoleApp app = new ConsoleApp();
		app.runApp(new String[] { "--help" }, new PrintStream(outputStream),
				inputStream);

		String appOutput = outputStream.toString("US-ASCII");
		Assert.assertNotNull(appOutput);
		Assert.assertNotEquals(0, appOutput.length());
		Assert.assertTrue(appOutput.contains("-r"));
	}
}
