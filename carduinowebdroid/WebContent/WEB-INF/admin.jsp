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

<!--Custom Stylesheet-->

<link rel="stylesheet" href="static/style.css" />
<link rel="stylesheet" href="static/table.css" />

<!--Custom jS-->

<script>
	$(function() { $( "#admin_menu" ).menu();});
	
	$(function() {
		$("input[type=button], button,#admin_edit").button();
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
				<thead><tr><th>UserID</th><th>Nickname</th><th>isAdmin</th></tr></thead>
				<tbody>
				<c:forEach var="i" begin="0" end="${result}">
					<tr>
						<td><c:out value="${i}" /></td>
						<td><ct:user par="1" num="${i}" /></td>
						<td><ct:user par="2" num="${i}" /></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>			
			</div>
		</td>
		<td>
			<a href="#admin_dialog" name="modal">
				<button name="modal" value="Edit User" id="admin_edit" type="button">Edit User</button>
			</a>
			
			<div id="admin_boxes">
				<div id="admin_dialog" class="window">
					<form action="">
						<table>
							<tr>
								<td id="admin_mask_left">Which User? (UserID):</td>
								<td><input id="admin_edit_text" type="text" name="userid" placeholder="UserID" /></td>
							</tr>
							<tr>
								<td id="admin_mask_left">Nickname:</td>
								<td><input id="admin_edit_text" type="text" name="nickname" placeholder="Nickname" /></td>
							</tr>
							<tr>
								<td id="admin_mask_left">Rights:</td>
								<td><input id="admin_edit_text" type="text" name="rights" placeholder="Rights" /></td>
							</tr>
						</table>
					</form> 
					<a href="#"class="close"/>
						<button name="modal" value="Edit" id="admin_mask_edit" type="button">Edit</button>
					</a>
				</div>
				<div id="admin_mask"></div>
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
	<input id="main_connect" type="submit" value="connect" />
</form>
</c:if>
</body>
</html>