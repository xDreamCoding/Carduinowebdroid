<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>

<c:set var="qlist">
	<ct:getQ par="0" />
</c:set>

<h1>Current driver: <ct:getQ par="1" /></h1>
<c:forEach items="${qlist}" var="s"> 
	<c:out value="${s}" /><br>
</c:forEach>