/**
 * @File This is the Javascript file for the controls.
 * @author Sven-Leonhard Weiler
 * 
 */

$(function() {
	/**
	 * Initialize and define websocket.
	 */
	var wscon = new WebSocket("ws://"+ location.host + "/carduinowebdroid/controller");
	
	wscon.onopen = function(){
		$("#main_chat").append("Controller Connected!" + "\n");
	};
	wscon.onclose = function(){
		$("#main_chat").append("Controller Closed!" + "\n");
	};
	wscon.onmessage = function(message){
		$("#main_chat").append("Controller: " + message.data + "\n");
	};
	wscon.onerror = function(){
		$("#main_chat").append("Controller Error!" + "\n");
	};
	
	
	/**
	 * Send to server.
	 */
	function controlToServer(msg){
		$("#main_chat").append("Controller: " + msg + "\n");
		wscon.send(msg);
	};
	
	/**
	 * Register controlToServer to buttons.
	 */
	$('#main_gadget_button_horn').click(function() {controlToServer("h")});
	$('#main_gadget_button_light').click(function() {controlToServer("l")});
	$('#main_steering_button_left').click(function() {controlToServer("a")});
	$('#main_steering_button_up').click(function() {controlToServer("w")});
	$('#main_steering_button_right').click(function() {controlToServer("d")});
	$('#main_steering_button_down').click(function() {controlToServer("s")});
	
	/**
	 * Close websocket connection.
	 */
	function closeConnect(){
		wschat.close();
	};
});