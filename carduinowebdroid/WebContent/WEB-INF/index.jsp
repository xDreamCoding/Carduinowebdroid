<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE HTML>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>CarDuinoDroid</title>
<link rel="icon" href="static/favicon.ico" type="image/x-icon" />
<!--JQuery Import-->
<link rel="stylesheet" href="static/jquery-ui-1.10.3.custom/css/ui-lightness/jquery-ui-1.10.3.custom.css" />
<script src="static/jquery-1.9.1.js"></script>
<script src="static/jquery-ui.js"></script>

<!--Custom Stylesheet-->

<link rel="stylesheet" href="static/style.css" />

<!--Custom jS-->

<script src="static/index.js"></script>
</head>
<body>

<div id="index_logo">

	<img src="static/logo.png" />
       
</div>

<br>

<div id="index_loginbox">
<form method="POST">
	<input type="hidden" name="action" value="login" />
	<h1>Login</h1>
    	<table id="index_logintable">
        	<tr>
            	<td for="nickname">UserID:</td>
                <td><input type="text" name="loginName" placeholder="Nickname"/></td>
            </tr>
            <tr>
            	<td for="password">Password:</td>
                <td><input type="password" name="password" placeholder="Password"/></td>
            </tr>
       </table>
       <input id="index_buttonsubmit" type="submit" value="Login" />
</form>
</div>

<br>

<div id="index_guestbox">
    <form method="POST">
    	<input type="hidden" name="action" value="watchDriver" />
        <input type="submit" value="Watch a Driver" />       
    </form> 
</div>

<br>

<div id="index_note">
  Welcome to Carduinodroid. If you want to watch another driver, please click on "Watch a driver." We hope you enjoy our website.
</div>
<br>
<div id="index_footer">
<a href="impress.jsp">Impress</a> | <a href="about.jsp" >About</a>
</div>

</body>
</html>