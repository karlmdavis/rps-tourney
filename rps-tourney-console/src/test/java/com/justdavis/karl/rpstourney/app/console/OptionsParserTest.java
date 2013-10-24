package com.justdavis.karl.rpstourney.app.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link OptionsParser}.
 */
public final class OptionsParserTest {
	/**
	 * Parses an empty set of arguments.
	 */
	@Test
	public void noOptions() {
		OptionsParser parser = new OptionsParser();
		Options options = parser.parseCommandLineOptions(new String[] {});

		Assert.assertNotNull(options);
		Assert.assertEquals(false, options.isDebugEnabled());
		Assert.assertEquals(false, options.isHelpRequested());
		Assert.assertEquals(3, options.getNumRounds());
	}

	/**
	 * Parses a set of arguments containing all options.
	 */
	@Test
	public void allOptions() {
		OptionsParser parser = new OptionsParser();
		Options options = parser.parseCommandLineOptions(new String[] { "-d",
				"--help", "-r", "1" });

		Assert.assertNotNull(options);
		Assert.assertEquals(true, options.isDebugEnabled());
		Assert.assertEquals(true, options.isHelpRequested());
		Assert.assertEquals(1, options.getNumRounds());
	}

	/**
	 * Makes sure that {@link OptionsParser#printUsage(java.io.PrintStream)}
	 * works as expected.
	 * 
	 * @throws UnsupportedEncodingException
	 *             (should not occur)
	 */
	@Test
	public void printUsage() throws UnsupportedEncodingException {
		OptionsParser parser = new OptionsParser();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		parser.printUsage(new PrintStream(byteArrayOutputStream));

		String usageOutput = byteArrayOutputStream.toString("US-ASCII");
		Assert.assertNotNull(usageOutput);
		Assert.assertNotEquals(0, usageOutput.length());
		Assert.assertTrue(usageOutput.contains("--help"));
	}
}
