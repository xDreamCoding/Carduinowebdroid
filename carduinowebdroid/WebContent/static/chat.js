/**
 * @File This is the Javascript file for the chat.
 * @author Sven-Leonhard Weiler
 * 
 */

$(function() {
	
	/**
	 * Register .append(newPart) function to be able to add text to the chat textarea.
	 */
	$.fn.append = function (newPart) {
		   return this.each(function(){ $(this).val( $(this).val() + newPart); });
		 };
		
	
	$("#main_chat").append("Chat Script loaded!" + "\n");
	
	
	/**
	 * Initialize and define websocket.
	 */
	var ws = new WebSocket("ws://"+ location.host + "/carduinowebdroid/chat");
	
	ws.onopen = function(){
		$("#main_chat").append("Connected!" + "\n");
	};
	ws.onclose = function(){
		$("#main_chat").append("Closed!" + "\n");
	};
	ws.onmessage = function(message){
		$("#main_chat").append(message.data + "\n");
	};
	ws.onerror = function(){
		$("#main_chat").append("Error!" + "\n");
	};
	
	
	/**
	 * Send chat_input to server.
	 */
	function postToServer(){
		$("#main_chat").append("Sent!" + "\n");
		ws.send($("#main_chat_textinput").val());
		$("#main_chat_textinput").val("");
	};
	
	/**
	 * Register postToServer on Chat_button.click event.
	 */
	$("#main_chat_speak").click(postToServer);
	
	/**
	 * Close websocket connection.
	 */
	function closeConnect(){
		ws.close();
	};
});