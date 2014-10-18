// Hide the player name form when the page loads.
$(document).ready(function () {
	$("form.player-name").hide();
});

// Swap out the player name label for the editor, when it's clicked.
$(document).on("click", "h3.player-name", function () {
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
	$("h3.player-name").show();
});
