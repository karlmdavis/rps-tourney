<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<spring:message code="home.subtitle" var="subtitle" />
<spring:message code="logout.submit" var="logoutSubmit" />
<t:basepage subtitle="${subtitle}">
		<p>Hello World!</p>
		<a href="./game/">Start Game</a>
		<sec:authorize access="isAuthenticated()">
			<form method="post" action="/logout">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input type="submit" value="${logoutSubmit}" />
			</form>
		</sec:authorize>
</t:basepage>
