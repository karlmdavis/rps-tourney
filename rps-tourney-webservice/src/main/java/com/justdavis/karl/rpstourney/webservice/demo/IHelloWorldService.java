package com.justdavis.karl.rpstourney.webservice.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A simple JAX-RS web service. Just intended as a proof of concept.
 */
@Path("/helloworld/")
public interface IHelloWorldService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	String getHelloWorld();

	@GET
	@Path("/echo/{phrase}/")
	@Produces(MediaType.TEXT_PLAIN)
	String echo(@PathParam("phrase") String phrase);
}