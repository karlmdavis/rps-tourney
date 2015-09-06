package com.justdavis.karl.rpstourney.service.api.game;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;

/**
 * Implementations of this service allow users to find other {@link Player}s.
 */
@Path(IPlayersResource.SERVICE_PATH)
public interface IPlayersResource {
	/**
	 * The {@link Path} that this this resource class' methods will be hosted
	 * at.
	 */
	public static final String SERVICE_PATH = "/players";

	/**
	 * The {@link Path} for {@link #getPlayersForBuiltInAis(java.util.List)}.
	 */
	public static final String SERVICE_PATH_BUILT_IN_AIS = "/builtInAis";

	/**
	 * @param ais
	 *            the {@link BuiltInAi}s to find the {@link Player} records for
	 * @return the {@link Player} records for the specified {@link BuiltInAi}s
	 */
	@GET
	@Path(SERVICE_PATH_BUILT_IN_AIS)
	@Produces(MediaType.TEXT_XML)
	Set<Player> getPlayersForBuiltInAis(@QueryParam("ais") List<BuiltInAi> ais);
}