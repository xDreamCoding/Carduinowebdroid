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

 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>

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
<li><a href="admin.jsp?menu=1"><span class="ui-icon ui-icon-plus" id="admin_menuicons"></span>Create</a></li>
<li><a href="admin.jsp?menu=2"><span class="ui-icon ui-icon-pencil" id="admin_menuicons"></span>Manage</a></li>
<li><a href="admin.jsp?menu=3"><span class="ui-icon ui-icon-minus" id="admin_menuicons"></span>Delete</a></li>
</ul>
</li>
<li><a href="#"><span class="ui-icon ui-icon-script" id="admin_menuicons"></span>Logging</a></li>
<li><a href="#"><span class="ui-icon ui-icon-wrench" id="admin_menuicons"></span>Settings</a></li>
</ul>

<table>
	<tr><td>~Add User foo</td></tr>
	<tr><td>~Edit User foo</td></tr>
</table>

<c:set var="result">
	<ct:user par="0" />
</c:set>
<c:set var="result" value="${result-1}" />

<c:if test="${param.menu == 1}">
<div id="admin_usertb">
<table>
	<tr><th>UserID</th><th>Nickname</th><th>isAdmin</th></tr>
	<c:forEach var="i" begin="0" end="${result}">
		<tr>
			<td id="admin_userdt"><c:out value="${i}" /></td>
			<td id="admin_userdt"><ct:user par="1" num="${i}" /></td>
			<td id="admin_userdt"><ct:user par="2" num="${i}" /></td>
		</tr>
	
	</c:forEach>
</table>
</div>
</c:if>
<br />

<c:if test="${param.menu == 2}">
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
</c:if>

<c:if test="${param.menu == 3}">
LOL
</c:if>
</body>
</html>