/**
 * @File This is the Javascript file for the main.jsp page.
 * It handles all Javascript calls made. 
 * @author Christoph Braun
 * @author Jenja Dietrich
 * 
 */

/**
 * Handler for chat message length. Controls if the element \e elem exceeds \e
 * limit chars. If so it will just cut the last type char away.
 * 
 * @tparam int limit Defines how many chars are accepted.
 * @tparam string elem Element to control.
 */


/**
 * Handles the dynamic size of the page if the toggle buttons are pressed. Will
 * adjust style sheet according to user wishes what he wants to see
 * (Chat,Queue).
 */

$(function() {
	var stream_width = 60;
	$("#main_chat_speak").button();
	$("#main_close_left").button().click(function() {
		$("#main_table_sidebar_left").hide();
		stream_width += 20;
		$("#main_table_stream").css("width", stream_width + "%");
		$("#main_close_left").hide();
		$("#main_open_left").show();
	});
	$("#main_open_left").button().click(function() {
		$("#main_table_sidebar_left").show();
		stream_width -= 20;
		$("#main_table_stream").css("width", stream_width + "%");
		$("#main_close_left").show();
		$("#main_open_left").hide();
	});
	$("#main_close_right").button().click(function() {
		$("#main_table_sidebar_right").hide();
		stream_width += 20;
		$("#main_table_stream").css("width", stream_width + "%");
		$("#main_close_right").hide();
		$("#main_open_right").show();
	});
	$("#main_open_right").button().click(function() {
		$("#main_table_sidebar_right").show();
		stream_width -= 20;
		$("#main_table_stream").css("width", stream_width + "%");
		$("#main_close_right").show();
		$("#main_open_right").hide();
	});
});

/**
 * jQuery Slider constructor.
 */

$(function() {
	$(".slider-vertical").slider({
		orientation : "vertical",
		range : "min",
		min : 0,
		max : 100,
		value : 60,
		slide : function(event, ui) {
			$("#amount").val(ui.value);
		}
	});
	$("#amount").val($(".slider-vertical").slider("value"));
});

/**
 * jQuery Button constructor.
 */

$(function() {
	$("input[type=submit], button,#mainqsubmit").button();
});

/**
 * Handles keystrokes which made on the clients keyboard. Will only work if the
 * chat is not in focus.
 */
function registerKeys() {
	
	$(document).keydown(function(e) {

		if (!$("#main_chat_textinput").is(":focus")) {
			// h
			if (e.keyCode == 72) {
				$('#main_gadget_button_horn').mousedown();
				return;
			}
			// l
			if (e.keyCode == 76) {
				$('#main_gadget_button_light').mousedown();
				return;
			}
			// up arrow
			if (e.keyCode == 38) {
				$('#main_steering_button_up').mousedown();
				return;
			}
			// down arrow
			if (e.keyCode == 40) {
				$('#main_steering_button_down').mousedown();
				return;
			}
			// left arrow
			if (e.keyCode == 37) {
				$('#main_steering_button_left').mousedown();
				return;
			}
			// right arrow
			if (e.keyCode == 39) {
				$('#main_steering_button_right').mousedown();
				return;
			}
		} else {
			// enter
			if(e.keyCode == 13) {
				$("#main_chat_speak").mousedown();
				return;
			}
		}	
	});
	
	$(document).keyup(function(e) {
		
		if (!$("#main_chat_textinput").is(":focus")) {
			// h
			if (e.keyCode == 72) {
				$('#main_gadget_button_horn')
					.removeClass("ui-state-active")
					.mouseup();
				return;
			}
			// l
			if (e.keyCode == 76) {
				$('#main_gadget_button_light')
					.removeClass("ui-state-active")
					.mouseup();
				return;
			}
			// arrow up
			if (e.keyCode == 38) {
				$('#main_steering_button_up')
					.removeClass("ui-state-active")
					.mouseup();
				return;
			}
			// arrow down
			if (e.keyCode == 40) {
				$('#main_steering_button_down')
					.removeClass("ui-state-active")
					.mouseup();
				return;
			}
			// arrow left
			if (e.keyCode == 37) {
				$('#main_steering_button_left')
					.removeClass("ui-state-active")
					.mouseup();
				return;
			}
			// arrow right
			if (e.keyCode == 39) {
				$('#main_steering_button_right')
					.removeClass("ui-state-active")
					.mouseup();
				return;
			}
		} else {
			// enter
			if(e.keyCode == 13) {
				$("#main_chat_speak")
					.removeClass("ui-state-active")
					.mouseup();
				return;
			}
		}
	});
}


