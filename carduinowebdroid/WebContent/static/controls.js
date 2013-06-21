/**
 * @File This is the Javascript file for the controls.
 * @author Sven-Leonhard Weiler
 * @param identifier To seperate between chat messages and control messages serverside.
 */
identifierControl = "Co%:";
/**
 * Send to server. Append s to start control and e to end control.
 */
function controlToServer(msg) {
	$("#main_chat").append("Controller: " + msg + "\n");
	ws.send(identifierControl + msg);
}

/**
 * Register controlToServer to buttons.
 * @param control message identifier
 */
function registerControls() {
	$('#main_gadget_button_horn').mousedown(function() {
		controlToServer("hs");
	});
	$('#main_gadget_button_light').mousedown(function() {
		controlToServer("ls");
	});
	$('#main_steering_button_left').mousedown(function() {
		controlToServer("as");
	});
	$('#main_steering_button_up').mousedown(function() {
		controlToServer("ws");
	});
	$('#main_steering_button_right').mousedown(function() {
		controlToServer("ds");
	});
	$('#main_steering_button_down').mousedown(function() {
		controlToServer("ss");
	});
	$('#main_gadget_button_horn').mouseup(function() {
		controlToServer("he");
	});
	$('#main_gadget_button_light').mouseup(function() {
		controlToServer("le");
	});
	$('#main_steering_button_left').mouseup(function() {
		controlToServer("ae");
	});
	$('#main_steering_button_up').mouseup(function() {
		controlToServer("we");
	});
	$('#main_steering_button_right').mouseup(function() {
		controlToServer("de");
	});
	$('#main_steering_button_down').mouseup(function() {
		controlToServer("se");
	});
}
