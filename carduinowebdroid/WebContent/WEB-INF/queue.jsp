<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>

<c:set var="qlist">
	<ct:getQ par="0" />
</c:set>

<div style="font-size:20px;"><b>Current driver: <ct:getQ par="1" />
<span id="counter" style="font-weight:500; font-size:20px; padding:0px 2px;">10</span></b></div>
<c:forEach items="${qlist}" var="s"> 
	<c:out value="${s}" /><br>
</c:forEach>