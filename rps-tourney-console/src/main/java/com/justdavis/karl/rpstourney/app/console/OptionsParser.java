package com.justdavis.karl.rpstourney.app.console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerRegistry;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.misc.resources.ResourcePath;

/**
 * Parses command line arguments into {@link Options} instances.
 */
final class OptionsParser {
	/**
	 * <p>
	 * Parses the specified {@link String} array of command line options and returns an {@link Options} instance
	 * representing them, or <code>null</code> if they could not be parsed.
	 * </p>
	 * <p>
	 * <strong>Side Effects:</strong> If the command line arguments could not be parsed successfully, this method will
	 * also print out a (hopefully) helpful error message and the application usage text, all to {@link System#err}.
	 * </p>
	 *
	 * @param args
	 *            the command line arguments passed to the application when it was launched
	 * @return an {@link Options} instance representing the command line arguments passed to the application when it was
	 *         launched, or <code>null</code> if they could not be parsed
	 */
	Options parseCommandLineOptions(String[] args) {
		Options options = new Options();
		OptionHandlerRegistry.getRegistry().registerHandler(InternetAddress.class, InternetAddressOptionHandler.class);
		CmdLineParser optionsParser = new CmdLineParser(options);
		try {
			optionsParser.parseArgument(args);
		} catch (CmdLineException e) {
			/*
			 * This exception will be thrown if the command line options cannot be parsed correctly.
			 */

			// Print the error and usage.
			System.err.println(e.getMessage());
			printUsage(System.err);
		}

		return options;
	}

	/**
	 * Prints out help text regarding the application's command line options.
	 *
	 * @param outputStream
	 *            the {@link PrintStream} to write to, typically {@link System#out} or {@link System#err}
	 */
	void printUsage(PrintStream outputStream) {
		// Create a throw-away parser to use here.
		CmdLineParser optionsParser = new CmdLineParser(new Options());

		// Print out the usage.
		outputStream.print("Usage: java -jar ");
		outputStream.print(OptionsParser.getExpectedOutputJarName());
		outputStream.print(" [OPTION]...\n\n");
		outputStream.println("Options:");
		optionsParser.printUsage(outputStream);
		outputStream.flush();
	}

	/**
	 * @return the name of the JAR that this project is expected to produce
	 */
	private static String getExpectedOutputJarName() {
		/*
		 * To pull this off, I've created a .properties resource file that Maven should filter, dropping the output
		 * JAR's name into it. This method just loads that properties file and pulls the filtered value out from it.
		 */

		// Get the path to the resource.
		ResourcePath jarDetailsPath = new ResourcePath(ConsoleApp.class, "jar-details.properties");

		// Load & parse the resource.
		Properties jarDetailsProps = new Properties();
		try {
			jarDetailsProps
					.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(jarDetailsPath.getPath()));
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}

		return jarDetailsProps.getProperty("project.build.finalName");
	}
}
