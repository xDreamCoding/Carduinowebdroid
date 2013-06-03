<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>CarDuinoDroid</title>
<link rel="icon" href="static/favicon.ico" type="image/x-icon" />
<!--JQuery Import-->
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/dot-luv/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<!--Custom Stylesheet-->

<link rel="stylesheet" href="static/style.css" />

<!--Custom jS-->
<script>
(function($) {
    $.fn.extend( {
        limiter: function(limit, elem) {
            $(this).on("keyup focus", function() {
                setCount(this, elem);
            });
            function setCount(src, elem) {
                var chars = src.value.length;
                if (chars > limit) {
                    src.value = src.value.substr(0, limit);
                    chars = limit;
                }
                elem.html( limit - chars );
            }
            setCount($(this)[0], elem);
        }
    });
})(jQuery);
</script>

<script>
$(document).ready( function() {
	var elem = $("#main_chat_chars");
	$("#main_chat_textinput").limiter(256, elem);
}); 
</script>

<script>
$(function() {
	var stream_width=60;
	$( "#main_chat_speak" ).button();
	$( "#main_close_left" ).button().click(function() {
		$( "#main_table_sidebar_left" ).hide();
		stream_width+=20;
		$( "#main_table_stream").css("width", stream_width + "%");
		$( "#main_close_left" ).hide();
		$( "#main_open_left" ).show();
	});
	$( "#main_open_left" ).button().click(function() {
		$( "#main_table_sidebar_left" ).show();
		stream_width-=20;
		$( "#main_table_stream").css("width", stream_width + "%");
		$( "#main_close_left" ).show();
		$( "#main_open_left" ).hide();
	});
		$( "#main_close_right" ).button().click(function() {
		$( "#main_table_sidebar_right" ).hide();
		stream_width+=20;
		$( "#main_table_stream").css("width", stream_width + "%");
		$( "#main_close_right" ).hide();
		$( "#main_open_right" ).show();
	});
	$( "#main_open_right" ).button().click(function() {
		$( "#main_table_sidebar_right" ).show();
		stream_width-=20;
		$( "#main_table_stream").css("width", stream_width + "%");
		$( "#main_close_right" ).show();
		$( "#main_open_right" ).hide();
	});
});
</script>

<script>
$(function() {
	$( ".slider-vertical" ).slider({
		orientation: "vertical",
		range: "min",
		min: 0,
		max: 100,
		value: 60,
		slide: function( event, ui ) {
			$( "#amount" ).val( ui.value );
		}
	});
	$( "#amount" ).val( $( ".slider-vertical" ).slider( "value" ) );
});
</script>

</head>
<body>

<table id="main_table">
	<tr>
    	<td id="main_table_sidebar_left">
        	<div id="main_chat_container">
        	<div id="main_chat">Chat</div>
            <textarea type="text" name="chat" id="main_chat_textinput" maxlength="256"></textarea>
            <div id="main_chat_chars">256</div>
            <button id="main_chat_speak">Chat</button>
          	</div>
        </td>
        <td><button id="main_close_left" class="ui-icon ui-icon-triangle-1-w"></button> <button id="main_open_left" class="ui-icon ui-icon-triangle-1-e"></button></td>
        <td id="main_table_stream">
        
        	<div id="main_stream">Stream</div>
        	<div id="main_controls">
            	<table>
                <div class="slider-vertical" id="main_maxspeed"></div>
            	</table>
            </div>
            
        </td>
        <td><button id="main_close_right" class="ui-icon ui-icon-triangle-1-e"></button> <button id="main_open_right" class="ui-icon ui-icon-triangle-1-w"></button></td>
        <td id="main_table_sidebar_right">	
        
        	<div id="main_q">Q </div>
        
        </td>
    </tr>
</table>

<br>

</body>
</html>