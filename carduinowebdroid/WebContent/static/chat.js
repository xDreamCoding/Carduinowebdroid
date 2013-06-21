/**
 * @File This is the Javascript file for the chat.
 * @author Sven-Leonhard Weiler
 * 
 */
identifierChat = "Ch%:";
/**
 * Register .append(newPart) function to be able to add text to the chat
 * textarea.
 */
$(function() {

	$.fn.extend({
		append : function(newPart) {
			return this.each(function() {
				$(this).val($(this).val() + newPart);
			});
		}
	});

});

/**
 * Register postToServer on Chat_button.click event.
 */
function registerChat() {
	$("#main_chat_speak").click(postToServer);
	if($("#main_chat_textinput").is(":focus"))
		$("#main_chat_textinput").val('');
}

/**
 * Handle incoming messages.
 */
function chatHandleMessage(message) {
	$("#main_chat").append(message.data + "\n");
    $('#main_chat').scrollTop($('#main_chat')[0].scrollHeight);
}

/**
 * Send chat_input to server.
 */
function postToServer() {
	ws.send(identifierChat + $("#main_chat_textinput").val());
}