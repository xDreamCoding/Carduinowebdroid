/**
 * @File This is the Javascript file for the controls.
 * @author Sven-Leonhard Weiler
 * @param identifier To seperate between chat messages and control messages serverside.
 */
identifier = "C%:";
/**
 * Send to server.
 */
function controlToServer(msg) {
	$("#main_chat").append("Controller: " + msg + "\n");
	ws.send(identifier + msg);
}

/**
 * Register controlToServer to buttons.
 * @param control message identifier
 */
function registerControls() {
	$('#main_gadget_button_horn').click(function() {
		controlToServer("h");
	});
	$('#main_gadget_button_light').click(function() {
		controlToServer("l");
	});
	$('#main_steering_button_left').click(function() {
		controlToServer("a");
	});
	$('#main_steering_button_up').click(function() {
		controlToServer("w");
	});
	$('#main_steering_button_right').click(function() {
		controlToServer("d");
	});
	$('#main_steering_button_down').click(function() {
		controlToServer("s");
	});
}
