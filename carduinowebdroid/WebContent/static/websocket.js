/**
 * @File This is the Javascript file for the Websocketconnection.
 * @author Sven-Leonhard Weiler
 * @author Vincenz Vogel
 */
identifierHeartbeat = "Hb%:";
/**
 * Initialize and define Websocket.
 */
function initializeWebsocket() {
	
	var frame = 0;
	ws = new WebSocket("ws://"+ location.host + "/carduinowebdroid/websocket");
	ws.binaryType = "arraybuffer"; //Binärtyp auf arraybuffer setzen
	
	ws.onopen = function(){
		$("#main_chat").append("Websocket Connected!" + "\n");
		sendBinaery();
	};
	ws.onclose = function(){
		$("#main_chat").append("Websocket Closed!" + "\n");
		// try reconnect after 2 sek
		window.setTimeout(initializeWebsocket, 2000);
	};
	ws.onmessage = function(message){
		
		
		/* author Vincenz Vogel */
		console.log("Daten empfangen " + message.data.byteLength);
    	//Datentyp ermitteln
  	    if (message.data instanceof ArrayBuffer) {
  	    	var canvas = document.getElementById('CarStream');
  	    	var ctx = canvas.getContext('2d');
  	    	ctx.fillStyle = "rgb(255, 255, 200)";
  	        ctx.fillRect(0, 0, 1080, 720);
  	      	console.log( "Daten = " + message.data);
  	      	
  	      	var IntBuffer = new Uint8Array( message.data);  
  	   		// Bildbreite und Höhe ermitteln byte[] -> int
  	      	var B = 0;     
  	      	for (var i = 0; i < 4; i++) {
  	          	B |= ((IntBuffer[3-i] & 0xff) << (i << 3));
  	      	}
  	      	console.log( "Bildbreite = " + B);
  	      	var H = 0;     
	      	for (var i = 0; i < 4; i++) {
	          	H |= ((IntBuffer[7-i] & 0xff) << (i << 3));
	      	}
	      	console.log( "Bildhöhe = " + H);
	      	
  	      	var img = new Image();
  	    	var img = ctx.getImageData(0, 0, B, H);
  	    	
  	    	console.log("Dummy Image erzeugt");
  	    	console.log( "Messagelänge = "  + message.data.byteLength );
  	    	console.log( "IntBufLänge = "  + IntBuffer.byteLength);
  	    	
  	    	var i = 8;
  	    	for (var y = 0; y < H; y++) {  
  	    		for (var x = 0; x < B; x++) {
  	    	    	var idx = (x + y * B) * 4;
  	    	    	img.data[idx + 0] = IntBuffer[i + 0]; // & 0xff;
  	    	    	img.data[idx + 1] = (IntBuffer[i + 1]); // << 8) & 0xff00;
  	    	    	img.data[idx + 2] = (IntBuffer[i + 2]); // << 16) & 0xff0000;
  	    	    	img.data[idx + 3] = 255; //IntBuffer[i + 3] & 0xff000000;
  	    	    	i = i + 4;
  	    	    }
  	    	}
  	    	  	    	
  	    	console.log("Daten auf Image verschoben i = " + i);
  	    	// Bildmitte berechnen
  	    	var bx = Math.floor(canvas.width / 2);
  	    	bx = Math.floor(bx - B/2);
  	    	if (bx < 0) { bx = 0; };
  	    	console.log("Bildmitte B = " + bx);
  	    	var by = Math.floor(canvas.height / 2);
  	    	by = Math.floor(by - H/2);
  	    	if (by < 0) { by = 0; };
  	    	console.log("Bildmitte H = " + by);
  	    	ctx.putImageData( img, bx, 10);
  	    	frame = frame + 1;
  	    	ctx.strokeText(frame, 20, 20, 40)
  	    	ctx.restore();
  	    	// nächstes Bild anfordern
  	    	sendBinaery();
  	    }
  	    else if (message.data instanceof Blob) {
  	    	alert("Blob");
  	    }
  	    else if (typeof (message.data === "string")) {
  	    	if(message.data.match(identifierChat + "*"))
  				chatHandleMessage(message);
  			if(message.data.match(identifierControl + "*"))
  				controlHandleMessage(message);
  	    	//alert("string");
  	    }
  	    
  	  	
		
	};
	ws.onerror = function(){
		$("#main_chat").append("Websocket Error!" + "\n");
	};
		
	function sendBinaery() {
    	var message = new ArrayBuffer(9);
        var dataViewMessage = new DataView(message);

        dataViewMessage.setInt8(0, 25); // einen Binärwert generieren
        
        //message per Websocket wegschicken
        ws.send(message);	
        console.log("DataView verschickt");
    }
}
	
/**
 * Close Websocketconnection.
 */
function closeConnect(){
	ws.close();
}
