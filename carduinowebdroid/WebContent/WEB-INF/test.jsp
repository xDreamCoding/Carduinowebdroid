 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
 <%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>
 
<head></head>
<html><body>

<!--TODO: Driver issues  -->
<sql:setDataSource var="db" user="test" url="sehraf-pi\:3306/carduinodroid" driver="org.mariadb.jdbc.Driver"  password="test" /> 
<sql:query dataSource="${db}" var="result">
SELECT Count(userID) from user
</sql:query>

<c:out value="${result}" />

<table>
	<c:forEach var="i" begin="0" end="${result}">
		<tr>
			<td><c:out value="${i}" /></td>
			<td><ct:user par="1" num="${i}" /></td>
			<td><ct:user par="2" num="${i}" /></td>
		</tr>
	
	</c:forEach>
</table>


</body></html>