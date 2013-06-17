$(function() {
	
	$.fn.append = function (newPart) {
		   return this.each(function(){ $(this).val( $(this).val() + newPart); });
		 };
		
	
	$("#main_chat").append("Chat Script loaded!" + "\n");
 
	var ws = new WebSocket("ws://localhost:8080/carduinowebdroid/chat");
	
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
	
	
	
	function postToServer(){
		$("#main_chat").append("Sent!" + "\n");
		ws.send($("#main_chat_textinput").val());
		$("#main_chat_textinput").val("");
	};
	
	$("#main_chat_speak").click(postToServer);
	
	function closeConnect(){
		ws.close();
	};
});