// Hide the player name form when the page loads.
$(document).ready(function () {
	$("form.player-name").hide();
});

// Swap out the player name label for the editor, when it's clicked.
$(document).on("click", "p.player-name-current", function () {
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
	$("p.player-name-current").show();
});

// This function returns the element to display as the result of the specified round in the specified game.
function computeRoundResultElement(gameData, round) {
	var resultText = $.i18n.prop('game.roundHistory.result.none');
	var resultClass = "";
	if (round.result === 'PLAYER_1_WON') {
		resultText = computePlayerNameText(gameData, gameData.player1);
		resultClass = "PLAYER_1";
	}
	else if (round.result === 'PLAYER_2_WON') {
		resultText = computePlayerNameText(gameData, gameData.player2);
		resultClass = "PLAYER_2";
	}
	else if (round.result === 'TIED') {
		resultText = $.i18n.prop('game.roundHistory.result.tied');
	}
	
	return '<span class="' + resultClass + '">' + resultText + '</span>';
}

// This function returns the text to display as the name of the specified player in the specified game.
function computePlayerNameText(gameData, player) {
	var playerLabel = $.i18n.prop('game.player.notJoined');
	if(player != null) {
		playerLabel = player.name !== null ? player.name : $.i18n.prop('game.player.anonymous');
		if(isUserThisPlayer(gameData, player)) {
			playerLabel = playerLabel + $.i18n.prop('game.player.current.suffix')
		}
	}
	
	return playerLabel;
}

//This function returns true if the user that the game data instance was created for 
//is a player in the game, false if not.
function isUserAPlayer(gameData) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	
	// Check to see if the game data has an associated user.
	if (gameData.viewUser === null) {
		return false;
	}
	
	if(gameData.player1 !== null && isUserThisPlayer(gameData, gameData.player1)) {
		return true;
	}
	else if(gameData.player2 !== null && isUserThisPlayer(gameData, gameData.player2)) {
		return true;
	}
	else {
		return false;
	}
}

// This function returns true if the user that the game data instance was created for 
// is the specified player in the game, false if not.
function isUserThisPlayer(gameData, player) {
	if (gameData === null) {
		throw "Missing game data.";
	}
	if (player === null) {
		throw "Missing player.";
	}
	
	// Check to see if the game data has an associated user.
	if (gameData.viewUser === null) {
		return false;
	}
	
	return player.humanAccount !== null && player.humanAccount.id === gameData.viewUser.id;
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
	gameUrl = window.location.href;
	gameStateUrl = gameUrl + "/data";
	
	// Issue an AJAX request for the current game state.
	$.getJSON(gameStateUrl, function(gameData) {
		console.log("Refresh successful. Next interval: %d.", refreshInterval);
		// console.log("Refresh request succeeded: %s", JSON.stringify(gameData));
		
		// Grab player names/labels for use later.
		var player1Label = gameData.player1.name !== null ? gameData.player1.name : $.i18n.prop('game.player.anonymous');
		if(isUserThisPlayer(gameData, gameData.player1)) {
			player1Label = player1Label + $.i18n.prop('game.player.current.suffix')
		}
		var player2Label = $.i18n.prop('game.player.notJoined');
		if(gameData.player2 != null) {
			player2Label = gameData.player2.name !== null ? gameData.player2.name : $.i18n.prop('game.player.anonymous');
			if(isUserThisPlayer(gameData, gameData.player2)) {
				player2Label = player2Label + $.i18n.prop('game.player.current.suffix')
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
			
			// Remove the "ask someone to join" message and/or the "Join Game" controls.
			$("#join-message").remove();
			$("#join-controls").remove();
		}
		
		// Update the max round displays and controls.
		$(".max-rounds-value").text(gameData.maxRounds);
		if (gameData.state !== "WAITING_FOR_PLAYER" && gameData.state !== "WAITING_FOR_FIRST_THROW") {
			// If game has started, remove the game controls.
			$("#game-controls").remove();
		}
		else {
			// If game hasn't started, update the max round controls.
			var roundsDecreaseUrl = gameUrl + "/setMaxRounds?oldMaxRoundsValue=" + gameData.maxRounds + "&newMaxRoundsValue=" + (gameData.maxRounds - 2);;
			var roundsIncreaseUrl = gameUrl + "/setMaxRounds?oldMaxRoundsValue=" + gameData.maxRounds + "&newMaxRoundsValue=" + (gameData.maxRounds + 2);
			$("#max-rounds-down").attr("href", roundsDecreaseUrl);
			$("#max-rounds-up").attr("href", roundsIncreaseUrl);
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
				roundRow.children("td:nth-child(4)").empty().append(computeRoundResultElement(gameData, round));
			}
		}
		
		// Determine the order players are displayed in the UI.
		var player1Element, player2Element;
		var player1HistoryColumn, player2HistoryColumn;
		if (isUserThisPlayer(gameData, gameData.player2)) {
			player1Element = $("#player-second");
			player2Element = $("#player-first");
			player1HistoryColumn = 3;
			player2HistoryColumn = 2;
		}
		else {
			player1Element = $("#player-first");
			player2Element = $("#player-second");
			player1HistoryColumn = 2;
			player2HistoryColumn = 3;
		}
		
		// Update the scores.
		player1Element.find(".player-score-value").text(gameData.scoreForPlayer1);
		player2Element.find(".player-score-value").text(gameData.scoreForPlayer2);
		
		if (gameData.state === "FINISHED") {
			// Update the won/lost styling.
			if (gameData.scoreForPlayer1 > gameData.scoreForPlayer2) {
				player1Element.addClass("won");
				player2Element.addClass("lost");
			}
			else {
				player1Element.addClass("lost");
				player2Element.addClass("won");
			}
			
			// Create the result row, if it hasn't been already.
			if ($("#result-row").length === 0) {
				var winnerDisplayName = "";
				var winnerDisplayClass = "";
				if (gameData.winner.id === gameData.player1.id) {
					winnerDisplayName = computePlayerNameText(gameData, gameData.player1);
					winnerDisplayClass = "PLAYER_1";
				}
				else {
					winnerDisplayName = computePlayerNameText(gameData, gameData.player2);
					winnerDisplayClass = "PLAYER_2";
				}
				
				var finalResultCell = '<td><span class="' 
						+ winnerDisplayClass + '">' 
						+ winnerDisplayName + '</span>'
						+ $.i18n.prop('game.roundHistory.someoneWonSuffix')
						+ '</td>';
				$('table#rounds > tbody').append('<tr id="result-row"><td /><td /><td />' + finalResultCell + '</tr>');
			}
		}
		
		// Update the round counter.
		if (gameData.state !== "WAITING_FOR_PLAYER" && gameData.state !== "WAITING_FOR_FIRST_THROW") {
			var currentRound = gameData.rounds[gameData.rounds.length - 1];
			$("#round-counter-current").text(currentRound.adjustedRoundIndex + 1);
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

