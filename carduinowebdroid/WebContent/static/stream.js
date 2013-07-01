/**
 * @File This is the Javascript file for the Websocketconnection.
 * @author Vincenz Vogel
 * @author Sven-Leonhard Weiler
 */
function streamHandleMessage(message) {
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
    
    canvas.width = B;
    canvas.height = H;
    var img = ctx.getImageData(0, 0, B, H);
    
    console.log("Dummy Image erzeugt");
    console.log( "Messagelänge = "  + message.data.byteLength );
    console.log( "IntBufLänge = "  + IntBuffer.byteLength);
    
    var i = 8;
    for (var y = 0; y < H; y++) {  
      for (var x = 0; x < B; x++) {
          var idx = (x + y * B) * 4;
          img.data[idx + 0] = IntBuffer[i + 0]; // & 0xff;
          img.data[idx + 1] = (IntBuffer[i + 1]); // << 8) &
													// 0xff00;
          img.data[idx + 2] = (IntBuffer[i + 2]); // << 16) &
													// 0xff0000;
          img.data[idx + 3] = 255; // IntBuffer[i + 3] &
										// 0xff000000;
          i = i + 3;
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
    ctx.strokeText(frame, 20, 20, 40);
    ctx.restore();
}