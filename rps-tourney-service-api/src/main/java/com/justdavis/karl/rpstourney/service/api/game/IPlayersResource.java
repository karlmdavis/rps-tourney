package com.justdavis.karl.rpstourney.service.api.game;

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
	 * The {@link Path} for {@link #getPlayersForBuiltInAis()}.
	 */
	public static final String SERVICE_PATH_BUILT_IN_AIS = "/builtInAis";

	/**
	 * The {@link Path} for {@link #getPlayerForBuiltInAi(BuiltInAi)}.
	 */
	public static final String SERVICE_PATH_BUILT_IN_AI = "/builtInAi";

	/**
	 * @return the {@link Player} records for the active {@link BuiltInAi}s
	 *         (where {@link BuiltInAi#isRetired()} are <code>false</code>)
	 */
	@GET
	@Path(SERVICE_PATH_BUILT_IN_AIS)
	@Produces(MediaType.TEXT_XML)
	Set<Player> getPlayersForBuiltInAis();

	/**
	 * @param ai
	 *            the {@link BuiltInAi} constant to get the {@link Player} for
	 * @return the {@link Player} record for the specified {@link BuiltInAi}
	 */
	@GET
	@Path(SERVICE_PATH_BUILT_IN_AI)
	@Produces(MediaType.TEXT_XML)
	Player getPlayerForBuiltInAi(@QueryParam("ai") BuiltInAi ai);
}