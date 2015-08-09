/* 
 * Hide the player name form when the page loads.
 */
$(document).ready(function() {
	$("form.player-name").hide();
});

/*
 * Swap out the player name label for the editor, when it's clicked.
 */
$(document).on("click", "p.player-name-current", function() {
	$(this).hide();

	$("form.player-name").show();
	$("form.player-name > input").focus().select();
});

/*
 * Watch for and record clicks on this button, to handle blurs of its form
 * correctly.
 */
$(document).on("mousedown", "form.player-name button", function(event) {
	$(this).data("mouseDown", true);
});
$(document).on("mouseup", "form.player-name button", function(event) {
	$(this).data("mouseDown", false);
});

/*
 * Hide the form when it loses focus, unless it's being submitted.
 */
$(document).on("blur", "form.player-name", function(event) {
	// Ignore the blur if it was caused by a submit. The submission will cause a
	// reload that hides it anyways.
	if ($("form.player-name button").data("mouseDown") == true)
		return false;

	$(this).hide();
	$("p.player-name-current").show();
});

/*
 * This function returns the element to display as the result of the specified
 * round in the specified game.
 */
function computeRoundResultElement(gameData, round) {
	/*
	 * Note: This HTML is slightly different from the server-side version. The
	 * server-side version puts the won/lost class in an outer, wrapping
	 * <span/>. The differences don't affect any of the logic or rendering,
	 * though.
	 */

	var resultText = $.i18n.prop('roundResult.none');
	var resultClasses = "";
	if (round.result === 'PLAYER_1_WON') {
		resultText = computePlayerNameText(gameData, gameData.player1);
		if (isUserThisPlayer(gameData, gameData.player1)) {
			resultClasses = "PLAYER_1 won";
		} else if (isUserAPlayer(gameData)) {
			resultClasses = "PLAYER_1 lost";
		} else {
			resultClasses = "PLAYER_1";
		}
	} else if (round.result === 'PLAYER_2_WON') {
		resultText = computePlayerNameText(gameData, gameData.player2);
		if (isUserThisPlayer(gameData, gameData.player2)) {
			resultClasses = "PLAYER_2 won";
		} else if (isUserAPlayer(gameData)) {
			resultClasses = "PLAYER_2 lost";
		} else {
			resultClasses = "PLAYER_2";
		}
	} else if (round.result === 'TIED') {
		resultText = $.i18n.prop('roundResult.tied');
	}

	return '<span class="' + resultClasses + '">' + resultText + '</span>';
}

/*
 * This function returns the text to display as the name of the specified player
 * in the specified game.
 */
function computePlayerNameText(gameData, player) {
	var playerLabel = $.i18n.prop('playerName.notJoined');
	if (player != null) {
		playerLabel = player.name !== null ? player.name : $.i18n
				.prop('playerName.anon');
		if (isUserThisPlayer(gameData, player)) {
			playerLabel = playerLabel
					+ $.i18n.prop('playerName.current.suffix')
		}
	}

	return playerLabel;
}

/*
 * This function returns true if the user that the game data instance was
 * created for is a player in the game, false if not.
 */
function isUserAPlayer(gameData) {
	if (gameData === null) {
		throw "Missing game data.";
	}

	// Check to see if the game data has an associated user.
	if (gameData.viewPlayer === null) {
		return false;
	}

	if (gameData.player1 !== null
			&& isUserThisPlayer(gameData, gameData.player1)) {
		return true;
	} else if (gameData.player2 !== null
			&& isUserThisPlayer(gameData, gameData.player2)) {
		return true;
	} else {
		return false;
	}
}

/*
 * This function returns true if the user that the game data instance was
 * created for is the specified player in the game, false if not.
 */
function isUserThisPlayer(gameData, player) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	if (player === null) {
		throw "Missing player.";
	}

	// Check to see if the game data has an associated user.
	if (gameData.viewPlayer === null) {
		return false;
	}

	return player.humanAccount !== null
			&& player.id === gameData.viewPlayer.id;
}

/*
 * This function returns true if the user that the game data instance was
 * created for won the game, false if not, and throws an error if the game
 * hasn't even completed yet.
 */
function isUserTheWinner(gameData) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	if (gameData.state !== "FINISHED") {
		throw "Game not finished.";
	}

	// Check to see if the game data has an associated user.
	if (gameData.viewPlayer === null) {
		return false;
	}
	
	// Sanity check: Make sure the current player is a human.
	if (gameData.viewPlayer.humanAccount === null) {
		throw "Cyborg detected!";
	}

	// Check to see if the winner was an actual user.
	if (gameData.winner.humanAccount === null) {
		return false;
	}

	return gameData.winner.humanAccount.id === gameData.viewPlayer.humanAccount.id;
}

/*
 * Given a new set of GameView data (as JSON), updates the UI to match the game
 * state.
 */
function processNewGameState(gameData) {
	// Build the URL for the game state, based on the URL for the game page.
	gameUrl = window.location.href;

	// Grab player names/labels for use later.
	var player1Label = gameData.player1.name !== null ? gameData.player1.name
			: $.i18n.prop('playerName.anon');
	if (isUserThisPlayer(gameData, gameData.player1)) {
		player1Label = player1Label + $.i18n.prop('playerName.current.suffix')
	}
	var player2Label = $.i18n.prop('playerName.notJoined');
	if (gameData.player2 != null) {
		player2Label = gameData.player2.name !== null ? gameData.player2.name
				: $.i18n.prop('playerName.anon');
		if (isUserThisPlayer(gameData, gameData.player2)) {
			player2Label = player2Label
					+ $.i18n.prop('playerName.current.suffix')
		}
	}

	// Update Player 1's name.
	$(".PLAYER_1").text(player1Label);

	if (gameData.state != "WAITING_FOR_PLAYER") {
		// Update Player 2's name.
		$(".PLAYER_2").text(player2Label);

		// If the user is a player, unhide the throw controls.
		if (isUserAPlayer(gameData)) {
			$(".player-throws").removeClass("hidden");
		}

		// Remove the "ask someone to join" message and/or the "Join Game"
		// controls.
		$("#join-message").remove();
		$("#join-controls").remove();
	}

	// Update the max round displays and controls.
	$(".max-rounds-value").text(gameData.maxRounds);
	if (gameData.state !== "WAITING_FOR_PLAYER"
			&& gameData.state !== "WAITING_FOR_FIRST_THROW") {
		// If game has started, remove the game controls.
		$("#game-controls").remove();
	} else {
		// If game hasn't started, update the max round controls.
		var roundsDecreaseUrl = gameUrl + "/setMaxRounds?oldMaxRoundsValue="
				+ gameData.maxRounds + "&newMaxRoundsValue="
				+ (gameData.maxRounds - 2);
		;
		var roundsIncreaseUrl = gameUrl + "/setMaxRounds?oldMaxRoundsValue="
				+ gameData.maxRounds + "&newMaxRoundsValue="
				+ (gameData.maxRounds + 2);
		$("#max-rounds-down").attr("href", roundsDecreaseUrl);
		$("#max-rounds-up").attr("href", roundsIncreaseUrl);
	}

	// Update the round history table.
	if (gameData.state !== "WAITING_FOR_PLAYER"
			&& gameData.state !== "WAITING_FOR_FIRST_THROW") {
		// Remove the "fake" result row, if it's there.
		$("#round-data-fake").remove();

		// Update/create the rows for each round.
		for (index = 0; index < gameData.rounds.length; index++) {
			var round = gameData.rounds[index];
			var roundRow = $("#round-data-" + round.roundIndex);

			// Create a new row, if one does not yet exist for the round.
			if (roundRow.length === 0) {
				$('table#rounds > tbody').append(
						'<tr id="round-data-' + round.roundIndex
								+ '"><td /><td /><td /><td /></tr>');
				roundRow = $("#round-data-" + round.roundIndex);
			}

			// Set/update all of the column values in the row for the round.
			roundRow.children("td:nth-child(1)").text(
					round.adjustedRoundIndex + 1);
			var firstPlayerThrow, secondPlayerThrow;
			if (isUserThisPlayer(gameData, gameData.player2)) {
				firstPlayerThrow = round.throwForPlayer2;
				secondPlayerThrow = round.throwForPlayer1;
			} else {
				firstPlayerThrow = round.throwForPlayer1;
				secondPlayerThrow = round.throwForPlayer2;
			}
			var throwForFirstPlayerKey = firstPlayerThrow === null ? 'game.roundHistory.'
					: 'game.roundHistory.' + firstPlayerThrow;
			roundRow.children("td:nth-child(2)").text(
					$.i18n.prop(throwForFirstPlayerKey));
			var throwForSecondPlayerKey = secondPlayerThrow === null ? 'game.roundHistory.'
					: 'game.roundHistory.' + secondPlayerThrow;
			roundRow.children("td:nth-child(3)").text(
					$.i18n.prop(throwForSecondPlayerKey));
			roundRow.children("td:nth-child(4)").empty().append(
					computeRoundResultElement(gameData, round));
		}
	}

	// Determine the order players are displayed in the UI.
	var playerFirst, playerSecond;
	var player1Element, player2Element;
	if (isUserThisPlayer(gameData, gameData.player2)) {
		playerFirst = gameData.player2;
		playerSecond = gameData.player1;
		player1Element = $("#player-second");
		player2Element = $("#player-first");
	} else {
		playerFirst = gameData.player1;
		playerSecond = gameData.player2;
		player1Element = $("#player-first");
		player2Element = $("#player-second");
	}

	// Update the page's title (in the HTML header).
	document.title = '' + computePlayerNameText(gameData, playerFirst) + ' '
			+ $.i18n.prop('gameTitle.versus') + ' '
			+ computePlayerNameText(gameData, playerSecond);

	// Update the scores.
	player1Element.find(".player-score-value").text(gameData.scoreForPlayer1);
	player2Element.find(".player-score-value").text(gameData.scoreForPlayer2);

	if (gameData.state === "FINISHED") {
		// Update the won/lost styling.
		if (gameData.scoreForPlayer1 > gameData.scoreForPlayer2) {
			player1Element.addClass("won");
			player2Element.addClass("lost");
		} else {
			player1Element.addClass("lost");
			player2Element.addClass("won");
		}

		// Create the result row, if it hasn't been already.
		if ($("#result-row").length === 0) {
			var winnerDisplayName = "";
			var winnerNameClass = "";
			var wonOrLostClass = "";

			if (gameData.winner.id === gameData.player1.id) {
				winnerDisplayName = computePlayerNameText(gameData,
						gameData.player1);
				winnerNameClass = "PLAYER_1";
			} else {
				winnerDisplayName = computePlayerNameText(gameData,
						gameData.player2);
				winnerNameClass = "PLAYER_2";
			}

			if (isUserAPlayer(gameData) && isUserTheWinner(gameData)) {
				wonOrLostClass = "won";
			} else if (isUserAPlayer(gameData)) {
				wonOrLostClass = "lost";
			}

			var finalResultCell = '<td class="' + wonOrLostClass
					+ '"><span class="' + winnerNameClass + '">'
					+ winnerDisplayName + '</span>'
					+ $.i18n.prop('game.roundHistory.someoneWonSuffix')
					+ '</td>';
			$('table#rounds > tbody').append(
					'<tr id="result-row"><td /><td /><td />' + finalResultCell
							+ '</tr>');
		}
	}

	// Update the round counter.
	if (gameData.state !== "WAITING_FOR_PLAYER"
			&& gameData.state !== "WAITING_FOR_FIRST_THROW") {
		var currentRound = gameData.rounds[gameData.rounds.length - 1];
		$("#round-counter-current").text(currentRound.adjustedRoundIndex + 1);
	}
}

/*
 * When called, this function will automatically refresh the game state.
 */
function refreshGameState() {
	// Build the URL for the game state, based on the URL for the game page.
	gameUrl = window.location.href;
	gameStateUrl = gameUrl + "/data";

	// Issue an AJAX request for the current game state.
	$.getJSON(gameStateUrl, function(gameData) {
		console.log("Refresh successful. Next interval: %d.", refreshInterval);
		// console.log("Refresh request succeeded: %s",
		// JSON.stringify(gameData));

		// Process the new data.
		processNewGameState(gameData);
	}).fail(function(jqxhr, textStatus, error) {
		console.log("Refresh request failed: %s, %s", textStatus, error);
	});
}

/*
 * This function returns the next refresh/polling interval to use. The interval
 * increases exponentially, up to a maximum of 60 seconds.
 */
function calculateNextRefreshInterval(lastInterval) {
	var maxInterval = 60 * 1000;
	return Math.min(maxInterval, (lastInterval * 1.25));
}

/*
 * Start the polling function that automatically refreshes the game state.
 */
var refreshInterval = 1000;
function refreshGameStateAndLoop() {
	refreshGameState();

	refreshInterval = calculateNextRefreshInterval(refreshInterval);
	setTimeout(refreshGameStateAndLoop, refreshInterval);
}
$(document).ready(function() {
	setTimeout(refreshGameStateAndLoop, refreshInterval);
});
