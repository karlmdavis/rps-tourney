package com.justdavis.karl.rpstourney.webapp.home;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * The {@link Controller} for the site's home page.
 */
@Controller
@RequestMapping("/")
public class HomeController {
	/**
	 * @return a {@link ModelAndView} that can be used to render some basic
	 *         information about the application
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getHomePage() {
		Map<String, String> modelProps = new HashMap<>();

		ModelAndView modelAndView = new ModelAndView("home");
		modelAndView.addObject("model", modelProps);

		return modelAndView;
	}
}
