<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>

<c:set var="qlist">
	<ct:getQ />
</c:set>

<c:forEach items="${qlist}" var="s"> 
	<c:out value="${s}" /><br>
</c:forEach>