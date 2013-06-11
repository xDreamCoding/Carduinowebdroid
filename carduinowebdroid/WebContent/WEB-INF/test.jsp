 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>
 
<head></head>
<html><body>

<!--TODO: Driver issues  -->
<c:set var="result">
	<ct:user par="0" />
</c:set>
<c:out value="Found ${result} users" />

<c:set var="result" value="${result-1}" /> 
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