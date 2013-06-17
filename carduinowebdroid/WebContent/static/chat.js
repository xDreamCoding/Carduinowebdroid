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
	
	/**
	 * Initialize and define websocket.
	 */
	var wschat = new WebSocket("ws://"+ location.host + "/carduinowebdroid/chat");
	
	wschat.onopen = function(){
		$("#main_chat").append("Chat Connected!" + "\n");
	};
	wschat.onclose = function(){
		$("#main_chat").append("Chat Closed!" + "\n");
	};
	wschat.onmessage = function(message){
		$("#main_chat").append(message.data + "\n");
	};
	wschat.onerror = function(){
		$("#main_chat").append("Chat Error!" + "\n");
	};
	
	
	/**
	 * Send chat_input to server.
	 */
	function postToServer(){
		wschat.send($("#main_chat_textinput").val());
	};
	
	/**
	 * Register postToServer on Chat_button.click event.
	 */
	$("#main_chat_speak").click(postToServer);
	
	/**
	 * Close websocket connection.
	 */
	function closeConnect(){
		wschat.close();
	};
});