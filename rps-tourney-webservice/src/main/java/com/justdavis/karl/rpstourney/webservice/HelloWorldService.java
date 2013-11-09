package com.justdavis.karl.rpstourney.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A simple JAX-RS web service. Just intended as a proof of concept.
 */
@Path("/helloworld/")
public final class HelloWorldService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getHelloWorld() {
		return "Hello World!";
	}

	@GET
	@Path("/echo/{phrase}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String echo(@PathParam("phrase") String phrase) {
		return phrase;
	}
}
