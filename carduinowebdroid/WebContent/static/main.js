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
(function($) {
	$.fn.extend({
		limiter : function(limit, elem) {
			$(this).on("keyup focus", function() {
				setCount(this, elem);
			});
			function setCount(src, elem) {
				var chars = src.value.length;
				if (chars > limit) {
					src.value = src.value.substr(0, limit);
					chars = limit;
				}
				elem.html(limit - chars);
			}
			setCount($(this)[0], elem);
		}
	});
})(jQuery);

/**
 * Limiter call.
 */
$(document).ready(function() {
	var elem = $("#main_chat_chars");
	$("#main_chat_textinput").limiter(256, elem);
});

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
$(document).keydown(function(e) {

	if (!$("#main_chat_textinput").is(":focus")) {
		if (e.keyCode == 72) {
			$('#main_gadget_button_horn').mousedown();
			$('#main_gadget_button_horn').click("h");
			return false;
		}
		if (e.keyCode == 76) {
			$('#main_gadget_button_light').mousedown();
			$('#main_gadget_button_light').click("l");
			return false;
		}
		if (e.keyCode == 37) {
			$('#main_steering_button_left').mousedown();
			$('#main_steering_button_left').click("a");
			return false;
		}
		if (e.keyCode == 38) {
			$('#main_steering_button_up').mousedown();
			$('#main_steering_button_up').click("w");
			return false;
		}
		if (e.keyCode == 39) {
			$('#main_steering_button_right').mousedown();
			$('#main_steering_button_right').click("d");
			return false;
		}
		if (e.keyCode == 40) {
			$('#main_steering_button_down').mousedown();
			$('#main_steering_button_down').click("s");
			return false;
		}
	} else {
		if(e.keyCode == 13) {
			$("#main_chat_speak").mousedown();
			$("#main_chat_speak").click();
			return false;
		}
	}

});

$(document).keyup(function(e) {
	if (e.keyCode == 37) {
		$('#main_steering_button_left').removeClass("ui-state-active");
		return false;
	}
	if (e.keyCode == 38) {
		$('#main_steering_button_up').removeClass("ui-state-active");
		return false;
	}
	if (e.keyCode == 39) {
		$('#main_steering_button_right').removeClass("ui-state-active");
		return false;
	}
	if (e.keyCode == 40) {
		$('#main_steering_button_down').removeClass("ui-state-active");
		return false;
	}
	if (e.keyCode == 72) {
		$('#main_gadget_button_horn').removeClass("ui-state-active");
		return false;
	}
	if (e.keyCode == 76) {
		$('#main_gadget_button_light').removeClass("ui-state-active");
		return false;
	}
	if(e.keyCode == 13) {
		$("#main_chat_speak").removeClass("ui-state-active");
		return false;
	}
});
