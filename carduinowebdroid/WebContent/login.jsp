<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
    <head>
        <title>Carduinodroid Login</title>
       	<link rel="stylesheet" media="screen" href="stylesheets/login.css">
        <link rel="shortcut icon" type="image/png" href="images/favicon.ico">
    </head>
    <body>
	    <div align="center">
			<img src="images/Logofin.png">
			<form action="main" method="post" class="login box">
				<h2>Login</h2>
				<table>
					<tr>
						<td for="nickname">Nickname:</td>
						<td><input name="name" type="text" class="text" /></td>
					</tr>
					<tr>
						<td for="password">Password:</td>
						<td><input name="pw" type="password" class="text" /></td>
					</tr>
				</table>
				<input type="submit" text="Login" class="submit" />
			</form>
			<form action="connect" method="post" class="login box">
				<h2>Connect</h2>
				<table>
					<tr>
						<td for="nickname">IP:</td>
						<td><input name ="ip" type="text" class="text" /></td>
					</tr>
				</table>
				<input type="submit" text="Login" class="submit" />
			</form>
			<p class="label box">Welcome to Carduinodroid. If you want to watch
				another driver, please click on "Look for a driver." We hope you
				enjoy our website.</p>
		</div>    
    </body>
</html>
