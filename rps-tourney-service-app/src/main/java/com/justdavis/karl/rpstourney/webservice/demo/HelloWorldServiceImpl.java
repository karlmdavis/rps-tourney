package com.justdavis.karl.rpstourney.webservice.demo;

/**
 * The default implementation of {@link IHelloWorldService}.
 */
public class HelloWorldServiceImpl implements IHelloWorldService {
	/**
	 * @see com.justdavis.karl.rpstourney.webservice.demo.IHelloWorldService#getHelloWorld()
	 */
	@Override
	public String getHelloWorld() {
		return "Hello World!";
	}

	/**
	 * @see com.justdavis.karl.rpstourney.webservice.demo.IHelloWorldService#echo(java.lang.String)
	 */
	@Override
	public String echo(String phrase) {
		return phrase;
	}
}
