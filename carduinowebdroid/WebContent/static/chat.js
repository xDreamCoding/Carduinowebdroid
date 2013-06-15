$(function() {
	$("#main_chat").textcontent += "Chat Script loaded!" + "\n";
	var ws = new WebSocket("ws://localhost:8080/carduinodroid/chat");
	ws.onopen = function(){
		$("#main_chat").textcontent += "Connected!" + "\n";
	};
	ws.onmessage = function(message){
		$("#main_chat").textcontent += message.data + "\n";
	};
	$("#main_chat_speak").onclick = function postToServer(){
		$("#main_chat").textcontent += "Sent!" + "\n";
		ws.send($("#main_chat_textinput").value);
		$("#main_chat_textinput").value = "";
	};
	function closeConnect(){
		ws.close();
	};
});