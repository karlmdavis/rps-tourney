// Hide the player name form when the page loads.
$(document).ready(function () {
	$("form.player-name").hide();
});

// Swap out the player name label for the editor, when it's clicked.
$(document).on("click", "h3.player-name-current", function () {
	$(this).hide();
	
	$("form.player-name").show();
	$("form.player-name > input").focus().select();
});

// Watch for and record clicks on this button, to handle blurs of its form correctly.
$(document).on("mousedown", "form.player-name button", function(event){
	$(this).data("mouseDown", true);
});
$(document).on("mouseup", "form.player-name button", function(event){
	$(this).data("mouseDown", false);
});

// Hide the form when it loses focus, unless it's being submitted.
$(document).on("blur", "form.player-name", function (event) {
	// Ignore the blur if it was caused by a submit. The submission will cause a reload that hides it anyways.
	if($("form.player-name button").data("mouseDown") == true)
		return false;
	
	$(this).hide();
	$("h3.player-name-current").show();
});

// This function returns the text to display as the result of the specified round in the specified game.
function computeRoundResultText(gameData, round) {
	var roundResult = $.i18n.prop('game.roundHistory.result.none');
	if (round.result === 'PLAYER_1_WON') {
		roundResult = gameData.player1.name !== null ? gameData.player1.name : $.i18n.prop('game.player1.label');
	}
	else if (round.result === 'PLAYER_2_WON') {
		roundResult = gameData.player2.name !== null ? gameData.player2.name : $.i18n.prop('game.player2.label');
	}
	else if (round.result === 'TIED') {
		roundResult = $.i18n.prop('game.roundHistory.result.tied');
	}
	
	return roundResult;
}

// This function returns true if the user that the game data instance was created for 
// is a player in the game, false if not.
function isUserAPlayer(gameData) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	if (gameData.state !== "FINISHED") {
		throw "Game not finished.";
	}
	
	// Check to see if the game data has an associated user.
	if (gameData.viewUser === null) {
		return false;
	}
	
	return gameData.viewUser.id === gameData.player1.id || gameData.viewUser.id === gameData.player2.id;
}

// This function returns true if the user that the game data instance was created for 
// won the game, false if not, and throws an error if the game hasn't even completed yet.
function isUserTheWinner(gameData) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	if (gameData.state !== "FINISHED") {
		throw "Game not finished.";
	}
	
	// Check to see if the game data has an associated user.
	if (gameData.viewUser === null) {
		return false;
	}
	
	// Check to see if the winner was an actual user.
	if (gameData.winner.humanAccount === null) {
		return false;
	}
	
	return gameData.winner.humanAccount.id === gameData.viewUser.id;
}

// This function returns true if "Player 1" in the specified game data instance won the 
// game, false if not, and throws an error if the game hasn't even completed yet.
function isPlayer1TheWinner(gameData) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	if (gameData.state !== "FINISHED") {
		throw "Game not finished.";
	}
	
	return gameData.winner.id === gameData.player1.id;
}

// This function returns true if "Player 2" in the specified game data instance won the 
// game, false if not, and throws an error if the game hasn't even completed yet.
function isPlayer2TheWinner(gameData) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	if (gameData.state !== "FINISHED") {
		throw "Game not finished.";
	}
	
	return gameData.winner.id === gameData.player2.id;
}

// When called, this function will automatically refresh the game state.
function refreshGameState() {
	// Build the URL for the game state, based on the URL for the game page.
	gameStateUrl = window.location.href + "/data";
	
	// Issue an AJAX request for the current game state.
	$.getJSON(gameStateUrl, function(gameData) {
		console.log("Refresh successful. Next interval: %d.", refreshInterval);
		//console.log("Refresh request succeeded: %s", JSON.stringify(gameData));
		
		// Update Player 1's name.
		var player1NameLabel = $("div#player-1-controls h3.player-name");
		if (gameData.player1.name !== null) {
			player1NameLabel.text(gameData.player1.name);
		}
		
		// Update Player 2's name.
		if (gameData.state != "WAITING_FOR_PLAYER") {
			var player2NameLabel = $("div#player-2-controls h3.player-name");
			
			// Does Player 2 have a name?
			if (gameData.player2.name !== null) {
				player2NameLabel.text(gameData.player2.name);
			}
			else {
				player2NameLabel.text($.i18n.prop('game.player2.label'));
			}
			
			// Remove the "ask someone to join" message.
			$("#join-message").remove();
		}
		
		// Update the max round controls.
		if (gameData.state !== "WAITING_FOR_PLAYER" && gameData.state !== "WAITING_FOR_FIRST_THROW") {
			// If game has started, remove the max round controls.
			$("#max-round-controls").remove();
		}
		else {
			// If game hasn't started, update the max round controls.
			$("#max-rounds-value").text(gameData.maxRounds);
			var roundsDecreaseUrl = gameStateUrl + "/setMaxRounds?oldMaxRoundsValue=" + gameData.maxRounds + "&newMaxRoundsValue=" + (gameData.maxRounds - 2);;
			var roundsIncreaseUrl = gameStateUrl + "/setMaxRounds?oldMaxRoundsValue=" + gameData.maxRounds + "&newMaxRoundsValue=" + (gameData.maxRounds + 2);
			$("#max-rounds-down").attr("href", roundsDecreaseUrl);
			$("#max-rounds-up").attr("href", roundsIncreaseUrl);
		}
		
		// Update score.
		var player1ScoreElement = $("#player-1-score-value");
		var player2ScoreElement = $("#player-2-score-value");
		if (gameData.state !== "WAITING_FOR_PLAYER" && gameData.state !== "WAITING_FOR_FIRST_THROW") {
			player1ScoreElement.text(gameData.scoreForPlayer1);
			player2ScoreElement.text(gameData.scoreForPlayer2);
		}
		if (gameData.state === "FINISHED") {
			if (gameData.scoreForPlayer1 > gameData.scoreForPlayer2) {
				player1ScoreElement.addClass("won");
				player2ScoreElement.addClass("lost");
			}
			else {
				player1ScoreElement.addClass("lost");
				player2ScoreElement.addClass("won");
			}
			
			// Create the result row, if it hasn't been already.
			if ($("#result-row").length === 0) {
				// Computer the "Foo Won/Lost" display text.
				var finalResultCell;
				if (isUserAPlayer(gameData) && isUserTheWinner(gameData)) {
					finalResultCell = '<td class="won">' + $.i18n.prop("game.roundHistory.userWon") + '</td>';
				}
				else if (isUserAPlayer(gameData) && !isUserTheWinner(gameData)) {
					finalResultCell = '<td class="lost">' + $.i18n.prop("game.roundHistory.userLost") + '</td>';
				}
				else if (isPlayer1TheWinner(gameData)) {
					finalResultCell = gameData.winner.name + $.i18n.prop("game.roundHistory.someoneWonSuffix");
				}

				$('table#rounds > tbody').append('<tr id="result-row"><td /><td /><td />' + finalResultCell + '</tr>');
			}
		}
		
		// Update the round counter.
		$("#round-counter-max").text(gameData.maxRounds);
		if (gameData.state !== "WAITING_FOR_PLAYER" && gameData.state !== "WAITING_FOR_FIRST_THROW") {
			var currentRound = gameData.rounds[gameData.rounds.length - 1];
			$("#round-counter-current").text(currentRound.adjustedRoundIndex + 1);
		}

		// Update the round history table.
		if (gameData.state !== "WAITING_FOR_PLAYER" && gameData.state !== "WAITING_FOR_FIRST_THROW") {
			// Remove the "fake" result row, if it's there.
			$("#round-data-fake").remove();
			
			// Update/create the rows for each round.
			for(index = 0; index < gameData.rounds.length; index++) {
				var round = gameData.rounds[index];
				var roundRow = $("#round-data-" + round.roundIndex);
				
				// Create a new row, if one does not yet exist for the round.
				if (roundRow.length === 0) {
					$('table#rounds > tbody').append('<tr id="round-data-' + round.roundIndex + '"><td /><td /><td /><td /></tr>');
					roundRow = $("#round-data-" + round.roundIndex);
				}
				
				// Set/update all of the column values in the row for the round.
				roundRow.children("td:nth-child(1)").text(round.adjustedRoundIndex + 1);
				var throwForPlayer1Key = round.throwForPlayer1 === null ? 'game.roundHistory.' : 'game.roundHistory.' + round.throwForPlayer1;
				roundRow.children("td:nth-child(2)").text($.i18n.prop(throwForPlayer1Key));
				var throwForPlayer2Key = round.throwForPlayer2 === null ? 'game.roundHistory.' : 'game.roundHistory.' + round.throwForPlayer2;
				roundRow.children("td:nth-child(3)").text($.i18n.prop(throwForPlayer2Key));
				roundRow.children("td:nth-child(4)").text(computeRoundResultText(gameData, round));
			}
		}
	}).fail(function(jqxhr, textStatus, error) {
		console.log("Refresh request failed: %s, %s", textStatus, error);
	});
}

// This function returns the next refresh/polling interval to use. The interval increases 
// exponentially, up to a maximum of 60 seconds.
function calculateNextRefreshInterval(lastInterval) {
	var maxInterval = 60 * 1000;
	return Math.min(maxInterval, (lastInterval * 1.25));
}

// Start the polling function that automatically refreshes the game state.
var refreshInterval = 1000;
function refreshGameStateAndLoop() {
	refreshGameState();
	
	refreshInterval = calculateNextRefreshInterval(refreshInterval);
	setTimeout(refreshGameStateAndLoop, refreshInterval);
}
$(document).ready(function () {
	setTimeout(refreshGameStateAndLoop, refreshInterval);
});

