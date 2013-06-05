<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>CarDuionoDroid Admin</title>

<link rel="icon" href="static/favicon.ico" type="image/x-icon" />
<!--JQuery Import-->
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/ui-lightness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<!--Custom Stylesheet-->

<link rel="stylesheet" href="static/style.css" />

<!--Custom jS-->

<script>
	$(function() { $( "#admin_menu" ).menu();});
</script>

<style>
.ui-menu { width: 150px; }
</style>

</head>
<body>

<ul id="admin_menu">
<li>
<a href="#"><span class="ui-icon ui-icon-person" id="admin_menuicons"></span>User</a>
<ul>
<li><a href="#"><span class="ui-icon ui-icon-plus" id="admin_menuicons"></span>Create</a></li>
<li><a href="#"><span class="ui-icon ui-icon-pencil" id="admin_menuicons"></span>Manage</a></li>
<li><a href="#"><span class="ui-icon ui-icon-minus" id="admin_menuicons"></span>Delete</a></li>
</ul>
</li>
<li><a href="#"><span class="ui-icon ui-icon-script" id="admin_menuicons"></span>Logging</a></li>
<li><a href="#"><span class="ui-icon ui-icon-wrench" id="admin_menuicons"></span>Settings</a></li>

</ul>

</body>
</html>