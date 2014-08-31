<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<spring:message code="home.subtitle" var="metaSubtitle" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<h1><spring:message code="home.h1" /></h1>
		<p><spring:message code="home.intro.1" /></p>
		
		<h2><spring:message code="home.games.h2" /></h2>
		<a id="createGame" href="./game/"><spring:message code="home.games.create" /></a>
		
		<%-- Disabling logout until username & password authentication is fully supported: registration and login are missing. --%>
		<%-- <sec:authorize access="isAuthenticated()"> --%>
		<%-- 	<form method="post" action="/logout"> --%>
		<%-- 		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> --%>
		<%--		<spring:message code="logout.submit" var="logoutSubmit" /> --%>
		<%-- 		<input type="submit" value="${logoutSubmit}" /> --%>
		<%-- 	</form> --%>
		<%-- </sec:authorize> --%>
</t:basepage>
