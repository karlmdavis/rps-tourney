package com.justdavis.karl.rpstourney.service.app.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.justdavis.karl.rpstourney.service.api.game.Player;
import com.justdavis.karl.rpstourney.service.api.game.ai.BuiltInAi;
import com.justdavis.karl.rpstourney.service.app.SpringProfile;

/**
 * <p>
 * Ensures that {@link Player} records exist for all active {@link BuiltInAi}s (where {@link BuiltInAi#isRetired()} is
 * <code>false</code>).
 * </p>
 * <p>
 * This Spring Bean is registered as an {@link ApplicationListener} for {@link ContextRefreshedEvent}s, and will call
 * {@link #initializeAiPlayers()} when that event is fired.
 * </p>
 */
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Profile({ SpringProfile.PRODUCTION, SpringProfile.INTEGRATION_TESTS_WITH_JETTY })
public class AiPlayerInitializer implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AiPlayerInitializer.class);

	private final IPlayersDao playersDao;

	/**
	 * Constructs a new {@link AiPlayerInitializer} instance.
	 *
	 * @param playersDao
	 *            the {@link IPlayersDao} to use
	 */
	@Inject
	public AiPlayerInitializer(IPlayersDao playersDao) {
		this.playersDao = playersDao;
	}

	/**
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		initializeAiPlayers();
	}

	/**
	 * Ensures that {@link Player} records exist for all active {@link BuiltInAi}s (where {@link BuiltInAi#isRetired()}
	 * is <code>false</code>).
	 */
	public void initializeAiPlayers() {
		// First, figure out which AIs we're looking for.
		Set<BuiltInAi> activeAis = new HashSet<>();
		for (BuiltInAi ai : BuiltInAi.values()) {
			if (!ai.isRetired())
				activeAis.add(ai);
		}

		initializeAiPlayers(activeAis.toArray(new BuiltInAi[activeAis.size()]));
	}

	/**
	 * <p>
	 * Ensures that {@link Player} records exist for the specified {@link BuiltInAi}s.
	 * </p>
	 * <p>
	 * This method is mostly intended for use in tests, as it allows the use of non-active {@link BuiltInAi}s, such as
	 * {@link BuiltInAi#ONE_SIDED_DIE_ROCK}.
	 * </p>
	 *
	 * @param ais
	 *            the {@link BuiltInAi}s to ensure that {@link Player}s exist for
	 */
	@Transactional
	public void initializeAiPlayers(BuiltInAi... ais) {
		// Sanity checks.
		if (ais == null)
			throw new IllegalArgumentException();
		if (ais.length <= 0)
			throw new IllegalArgumentException();

		// Grab the matching Players that already exist (if any).
		Set<Player> aiPlayers = playersDao.findPlayerForBuiltInAi(ais);
		LOGGER.debug("Found the following AI Players: {}", aiPlayers);
		Map<BuiltInAi, Player> aiPlayersMap = new HashMap<>();
		for (Player aiPlayer : aiPlayers)
			aiPlayersMap.put(aiPlayer.getBuiltInAi(), aiPlayer);

		// Create any missing Players.
		for (BuiltInAi activeAi : ais) {
			if (!aiPlayersMap.containsKey(activeAi)) {
				// A Player is needed for this BuiltInAi.
				Player aiPlayer = new Player(activeAi);
				playersDao.save(aiPlayer);
				LOGGER.info("Created AI Player: {}", aiPlayer);
			}
		}
	}
}
