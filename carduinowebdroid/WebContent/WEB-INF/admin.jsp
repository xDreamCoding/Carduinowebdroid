<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>CarDuionoDroid Admin</title>

<link rel="icon" href="static/favicon.ico" type="image/x-icon" />
<!--JQuery Import-->
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/ui-lightness/jquery-ui.css" />
<script src="static/jquery-1.9.1.js"></script>
<script src="static/jquery-ui.js"></script>
<script src="static/admin.js"></script>
<script src="static/jquery.dataTables.js"></script>

 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!--Custom Stylesheet-->

<link rel="stylesheet" href="static/style.css" />
<link rel="stylesheet" href="static/table.css" />

<!--Custom jS-->

<script>
	$(function() { $( "#admin_menu" ).menu();});
	
	$(function() {
		$("input[type=button],input[type=submit], button,#admin_edit").button();
	});
</script>

<script>
	$(document).ready(function() {
	  $('#admin_usertbl').dataTable( {
		"iDisplayLength": 20,  
	  } );
	} );
</script>

<style>
.ui-menu { width: 150px; }
</style>

</head>
<body>

<ul id="admin_menu">
<li><a href="admin.jsp?menu=1"><span class="ui-icon ui-icon-person" id="admin_menuicons"></span>User</a></li>
<li><a href="admin.jsp?menu=2"><span class="ui-icon ui-icon-script" id="admin_menuicons"></span>Logging</a></li>
<li><a href="admin.jsp?menu=3"><span class="ui-icon ui-icon-wrench" id="admin_menuicons"></span>Settings</a></li>
<li><a href="main.jsp"><span class="ui-icon ui-icon-wrench" id="admin_menuicons"></span>Back to Main</a></li>
</ul>

<c:set var="result">
	<ct:user par="0" />
</c:set>
<c:set var="result" value="${result-1}" />

<c:if test="${param.menu == 1}">
<table>
	<tr>
		<td>
			<div id="admin_usercontainer">
			<table id="admin_usertbl" class="display">
				<thead><tr><th>UserID</th><th>Nickname</th><th>isAdmin</th><th>Edit User</th></tr></thead>
				<tbody>
				<c:forEach var="i" begin="0" end="${result}">
				<c:set var="string1"><a href="admin.jsp?menu=1&user=${i}">Edit</a></c:set>
					<tr>
						<td><c:out value="${i}" /></td>
						<td><ct:user par="1" num="${i}" /></td>
						<td><ct:user par="2" num="${i}" /></td>
						<td>${string1}</td> 
					</tr>
				</c:forEach>
				</tbody>
			</table>			
			</div>
		</td>
		<td id="admin_add">
				<c:if test="${not empty param.user }">
					<c:set scope="page" var="nickname"><ct:user num="${param.user}" par="1"/></c:set>
					<c:set scope="page" var="isAdmin"><ct:user par="2" num="${param.user}" /></c:set>
					<c:if test="${isAdmin}"><c:set var="isAdmincb">checked="checked"</c:set></c:if>
				</c:if>
			<div id="admin_box">
					<form action="POST">
						<input type="hidden" name="action" value="edituser" />
						<input type="hidden" name="userid" value="${param.user}" />
						<table>
							<tr><u>Edit User</u></tr>
							<tr>
								<td>UserID:</td>
								<td>${param.user}</td>
							</tr>
							<tr>
								<td >Nickname:</td>
								<td><input type="text" name="nickname" value="${nickname}"/></td>
							</tr>
							<tr>
								<td >isAdmin:</td>
								<td><input type="checkbox" name="rights" ${isAdmincb} /></td>
							</tr>
						</table>
						<input type="submit" value="Edit" />
					</form> 
			</div>
			<div id="admin_box">
				<form action="POST">
					<table>
						<tr><u>Add User</u></tr>
						<tr>
							<td>UserID:</td>
							<td><input type="text" placeholder="UserID"></td>
						</tr>
						<tr>
							<td>Nickname:</td>
							<td><input type="text" placeholder="Nickname"></td>
						</tr>
						<tr>
							<td>Password:</td>
							<td><input type="text" placeholder="Password"></td>
						</tr>
					</table>
					<input type="submit" value="Add" />			
				</form>
			</div>
		</td>
	</tr>
</table>

</c:if>
<br />

<c:if test="${param.menu == 2}">
<div id="admin_logcontainer">
<c:set var="logList">
	<ct:getLog />
</c:set>
<c:set var="result" value="${result-1}" />
<table>
	<tr><th>Inhalt vom Log</th></tr>
	<c:forEach items="${logList}" var="s"> 
		<tr>
			<td><c:out value="${s}" /></td>
		</tr>	
	</c:forEach>
</table>
</div>
</c:if>

<c:if test="${param.menu == 3}">

<form method="POST">
	<input type="hidden" name="action" value="connect"/>
	<input id="main_connect" type="submit" value="Connect to Car" />
</form>
<br>

<c:set scope="page" var="carIP"><ct:getConf par="0" /></c:set>
<c:set scope="page" var="dbadress"><ct:getConf par="1" /></c:set>
<c:set scope="page" var="dbpw"><ct:getConf par="2" /></c:set>
<c:set scope="page" var="dbuser"><ct:getConf par="3" /></c:set>
<c:set scope="page" var="drivetime"><ct:getConf par="4" /></c:set>
<c:set scope="page" var="filepath"><ct:getConf par="5" /></c:set>
<c:set scope="page" var="logchat"><ct:getConf par="6" /></c:set>
<c:set scope="page" var="logchattofile"><ct:getConf par="7" /></c:set>
<c:set scope="page" var="loggps"><ct:getConf par="8" /></c:set>
<c:set scope="page" var="loggpsinterval"><ct:getConf par="9" /></c:set>
<c:set scope="page" var="loggpstofile"><ct:getConf par="10" /></c:set>
<c:set scope="page" var="logq"><ct:getConf par="11" /></c:set>
<c:set scope="page" var="logqtofile"><ct:getConf par="12" /></c:set>


<div id="admin_settingscontainer">
<form method="POST">
	<input type="hidden" name="action" value="config" />
	<p><input type="text" name="IP" value="${carIP}"/></p>
	<p><input type="text" name="DBAdress" value="${dbadress}" /></p>
	<p><input type="text" name="DBUser" value="${dbuser}" /></p>
	<p><input type="text" name="DBPw" value="${dbpw}" /></p>
	<p><input type="text" name="drivetime" value="${drivetime}" /></p>
	<p><input type="text" name="filepath" value="${filepath}" /></p>
	<p><input type="text" name="logchat" value="${logchat}" /></p>	
	<p><input type="text" name="logchattofile" value="${logchattofile}" /></p>	
	<p><input type="text" name="loggps" value="${loggps}" /></p>
	<p><input type="text" name="loggps" value="${loggpsinterval}" /></p>
	<p><input type="text" name="loggps" value="${loggpstofile}" /></p>
	<p><input type="text" name="loggps" value="${logq}" /></p>
	<p><input type="text" name="loggps" value="${logqtofile}" /></p>
</form>
</div>
</c:if>
</body>
</html>