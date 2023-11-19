package com.justdavis.karl.rpstourney.webapp;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * Unit tests for <code>src/main/webapp/WEB-INF/i18n/messages.properties</code>.
 */
public final class MessagesTest {
	/**
	 * Verifies that all of the {@link BuiltInAi#getDisplayNameKey()} messages that should exist, do exist.
	 */
	@Test
	public void aiNames() {
		MessageSource messages = buildMessageSource();
		for (BuiltInAi ai : BuiltInAi.values()) {
			if (ai.isRetired())
				continue;

			String aiNameKey = "players.ai.name." + ai.getDisplayNameKey();
			Assert.assertNotEquals("Missing message for key: " + aiNameKey, "MISSING",
					messages.getMessage(aiNameKey, new Object[] {}, "MISSING", Locale.getDefault()));
		}
	}

	/**
	 * @return a {@link MessageSource} containing all of this project's messages
	 */
	private static MessageSource buildMessageSource() {
		try {
			// Have to jump through some hoops to find the file path.
			URL projectVersionUrl = Thread.currentThread().getContextClassLoader()
					.getResource("project.version.properties");
			Path projectVersionPath = Paths.get(projectVersionUrl.toURI());
			Path i18nPath = projectVersionPath.getParent().getParent().getParent().resolve("src").resolve("main")
					.resolve("webapp").resolve("WEB-INF").resolve("i18n");
			if (!Files.exists(i18nPath))
				throw new BadCodeMonkeyException("Directory not found: " + i18nPath.toAbsolutePath());
			URL i18nUrl = i18nPath.toUri().toURL();

			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
			messageSource.setBundleClassLoader(new URLClassLoader(new URL[] { i18nUrl }));
			messageSource.setBasename("messages");
			messageSource.setFallbackToSystemLocale(false);
			return messageSource;
		} catch (URISyntaxException e) {
			// Won't happen.
			throw new BadCodeMonkeyException(e);
		} catch (MalformedURLException e) {
			// Won't happen.
			throw new BadCodeMonkeyException(e);
		}
	}
}
