/**
 * @File This is the Javascript file for the Websocketconnection.
 * @author Sven-Leonhard Weiler
 * 
 */

/**
 * Initialize and define Websocket.
 */
function initializeWebsocket() {

	ws = new WebSocket("ws://"+ location.host + "/carduinowebdroid/websocket");
	
	ws.onopen = function(){
		$("#main_chat").append("Websocket Connected!" + "\n");
	};
	ws.onclose = function(){
		$("#main_chat").append("Websocket Closed!" + "\n");
	};
	ws.onmessage = function(message){
		chatHandleMessage(message);
	};
	ws.onerror = function(){
		$("#main_chat").append("Websocket Error!" + "\n");
	};
}
	
/**
 * Close Websocketconnection.
 */
function closeConnect(){
	ws.close();
}
