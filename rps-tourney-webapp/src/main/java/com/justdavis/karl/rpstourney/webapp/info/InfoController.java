package com.justdavis.karl.rpstourney.webapp.info;

import java.io.IOException;
import java.util.Properties;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.justdavis.karl.misc.exceptions.unchecked.UncheckedIoException;
import com.justdavis.karl.rpstourney.webapp.XmlNamespace;

/**
 * Just a simple {@link Controller} that returns some information about the web
 * application. Intended for use mostly just as an "is it up?" canary.
 */
@Controller
@RequestMapping("/info")
public class InfoController {
	private static String projectVersion = null;

	/**
	 * @return always just returns the {@link String} "<code>OK</code>"
	 */
	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String ping() {
		return "OK";
	}

	/**
	 * @return a {@link ModelAndView} that can be used to render some basic
	 *         information about the application
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public ModelAndView getAppInfo() {
		/*
		 * While this view could be rendered as marshalled XML, it acts as a
		 * better "canary" if it uses JSP to produce the XML, instead. So, it
		 * does.
		 */

		ModelAndView modelAndView = new ModelAndView("app-info");
		modelAndView.addObject("namespace", XmlNamespace.RPSTOURNEY_APP);
		modelAndView.addObject("app.version", getProjectVersion());
		return modelAndView;
	}

	/**
	 * @return the application project's version string
	 */
	private static String getProjectVersion() {
		if (projectVersion == null)
			projectVersion = readInProjectVersion();

		return projectVersion;
	}

	/**
	 * @return the application project's version string, as read in from the
	 *         <code>src/main/resources/project-version.properties</code> file
	 */
	private static String readInProjectVersion() {
		Properties projectVersionProps = new Properties();
		try {
			projectVersionProps.load(Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("project-version.properties"));
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}

		return projectVersionProps.getProperty("project.version");
	}
}
