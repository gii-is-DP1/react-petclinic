<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="active" required="true" rtexprvalue="true" %>
<%@ attribute name="url" required="true" rtexprvalue="true" %>
<%@ attribute name="title" required="false" rtexprvalue="true" %>
<%@ attribute name="dropdown" required="false" rtexprvalue="true" %>
<li class="${active ? 'active' : ''} ${dropdown ? 'dropdown' : ''}">
    <a href="<spring:url value="${url}" htmlEscape="true" />"
       title="${fn:escapeXml(title)}" class="${dropdown ? 'dropdown-toggle' : ''}" 
       ${dropdown ? 'data-toggle="dropdown"' : ''}>
       <c:if test="${!dropdown}">
        <jsp:doBody/>
       </c:if>
       <c:if test="${dropdown}">
       		<span class="glyphicon glyphicon-chevron-down"></span>
       		<span>${title}</span>        	 
    	</c:if>
    </a>
    <c:if test="${dropdown}">
        <jsp:doBody/>
    </c:if>
</li>
