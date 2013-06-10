 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>
<head></head>
<html><body>
<table>
	<c:forEach var="i" begin="0" end="2">
		<tr>
			<td><c:out value="${i}" /></td>
			<td><ct:user par="1" num="${i}" /></td>
			<td><ct:user par="2" num="${i}" /></td>
		</tr>
	
	</c:forEach>
</table>

</body></html>