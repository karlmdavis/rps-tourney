package com.justdavis.karl.rpstourney.webapp.error;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * The {@link Controller} for the site's error pages.
 */
@Controller
@RequestMapping("/error")
public class ErrorController {
	/**
	 * @param httpRequest
	 *            the {@link HttpServletRequest} that resulted in the error being rendered
	 * @return a {@link ModelAndView} that can be used to render an application error
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
		ModelAndView modelAndView = new ModelAndView();

		// Set the view based on the HTTP error code.
		String viewName;
		if (getErrorCode(httpRequest) == HttpStatus.NOT_FOUND.value())
			viewName = "error-notFound";
		else
			viewName = "error-default";
		modelAndView.setViewName(viewName);

		return modelAndView;
	}

	/**
	 * Always throws an {@link Exception} when requested. Useful for testing purposes -- to verify that the application
	 * is handling errors as expected.
	 */
	@RequestMapping(value = "go-boom", method = RequestMethod.GET)
	public void goBoom() {
		throw new BadCodeMonkeyException();
	}

	/**
	 * @param httpRequest
	 *            the {@link HttpServletRequest} that resulted in the error being rendered
	 * @return the {@link HttpStatus#value()} of the error being rendered
	 */
	private static int getErrorCode(HttpServletRequest httpRequest) {
		return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
	}
}
