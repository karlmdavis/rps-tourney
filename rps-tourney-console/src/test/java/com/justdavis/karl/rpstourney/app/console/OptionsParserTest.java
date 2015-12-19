package com.justdavis.karl.rpstourney.app.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Test;

import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * Unit tests for {@link OptionsParser}.
 */
public final class OptionsParserTest {
	/**
	 * Parses an empty set of arguments.
	 * 
	 * @throws URISyntaxException
	 *             (won't happen; address is hardcoded)
	 */
	@Test
	public void noOptions() throws URISyntaxException {
		OptionsParser parser = new OptionsParser();
		Options options = parser.parseCommandLineOptions(new String[] {});

		Assert.assertNotNull(options);
		Assert.assertEquals(false, options.isOnline());
		Assert.assertEquals(new URI(Options.DEFAULT_SERVER), options.getServerUri());
		Assert.assertEquals(null, options.getEmailAddress());
		Assert.assertEquals(null, options.getPassword());
		Assert.assertEquals(null, options.getGameUri());
		Assert.assertEquals(BuiltInAi.THREE_SIDED_DIE_V1, options.getAiOpponent());
		Assert.assertEquals(false, options.isDebugEnabled());
		Assert.assertEquals(false, options.isHelpRequested());
		Assert.assertEquals(3, options.getNumRounds());
	}

	/**
	 * Parses a set of arguments containing all network play options.
	 * 
	 * @throws URISyntaxException
	 *             (won't happen; addresses are hardcoded)
	 * @throws AddressException
	 *             (won't happen; addresses are hardcoded)
	 */
	@Test
	public void allNetworkPlayOptions() throws URISyntaxException, AddressException {
		OptionsParser parser = new OptionsParser();
		String[] optionsArray = new String[] { "--server", "https://example.com/foo", "--user", "bar@example.com",
				"--password", "12345", "--game", "https://example.com/fizz", "--ai", "Hard", "-d", "--help", "-r",
				"1" };
		Options options = parser.parseCommandLineOptions(optionsArray);

		Assert.assertNotNull(options);
		Assert.assertEquals(false, options.isOnline());
		Assert.assertEquals(new URI(optionsArray[1]), options.getServerUri());
		Assert.assertEquals(new InternetAddress(optionsArray[3]), options.getEmailAddress());
		Assert.assertEquals(optionsArray[5], options.getPassword());
		Assert.assertEquals(new URI(optionsArray[7]), options.getGameUri());
		Assert.assertEquals(BuiltInAi.META_WIN_STAY_LOSE_SHIFT_V1, options.getAiOpponent());
		Assert.assertEquals(true, options.isDebugEnabled());
		Assert.assertEquals(true, options.isHelpRequested());
		Assert.assertEquals(1, options.getNumRounds());
	}

	/**
	 * Parses a set of arguments containing all options.
	 */
	@Test
	public void allOptions() {
		OptionsParser parser = new OptionsParser();
		Options options = parser.parseCommandLineOptions(new String[] { "-d", "--help", "-r", "1" });

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
