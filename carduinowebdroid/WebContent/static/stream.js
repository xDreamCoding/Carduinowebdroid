/**
 * @File This is the Javascript file for the Websocketconnection.
 * @author Vincenz Vogel
 * @author Sven-Leonhard Weiler
 */
function streamHandleMessage(message) {
	data = message.data;
	msg = data.substring(identifierImageFrame.length, data.length);
	
	var img = new Image();
	var cnv = document.getElementById('main_canvas');
	
	canvas.width = $("#main_stream").width();
	canvas.height = $("#main_stream").height();
	  
	img.src = 'data:image/png;base64,'+msg;
	img.onload = function(){
		cnv.getContext('2d').drawImage(img,0,0);
	};
}