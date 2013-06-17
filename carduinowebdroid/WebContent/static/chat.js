/**
 * @File This is the Javascript file for the chat.
 * @author Sven-Leonhard Weiler
 * 
 */

/**
 * Register .append(newPart) function to be able to add text to the chat
 * textarea.
 */
$(function() {

	$.fn.append = function(newPart) {
		return this.each(function() {
			$(this).val($(this).val() + newPart);
		});
	};

});

/**
 * Register postToServer on Chat_button.click event.
 */
function registerChat() {
	$("#main_chat_speak").click(postToServer);

}

/**
 * Handle incoming messages.
 */
function chatHandleMessage(message) {
	$("#main_chat").append(message.data + "\n");
}

/**
 * Send chat_input to server.
 */
function postToServer() {
	ws.send($("#main_chat_textinput").val());
}