<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>
<spring:message code="home.subtitle" var="metaSubtitle" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<h1><spring:message code="home.h1" /></h1>
		<p><spring:message code="home.intro.1" /></p>
		
		<h2><spring:message code="home.games.h2" /></h2>
		<a id="createGame" href="${requestScope['rpstourney.config.baseurl']}/game/"><spring:message code="home.games.create" /></a>
		
		<sec:authorize access="isAuthenticated()">
		<c:if test="${not empty games}">
		<table id="playerGames">
			<thead>
				<tr>
					<th><spring:message code="home.games.id.label" /></th>
					<th><spring:message code="home.games.lastThrowTimestamp.label" /></th>
					<th><spring:message code="home.games.state.label" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${games}" var="game">
				<tr>
					<td><a href="${requestScope['rpstourney.config.baseurl']}/game/${game.id}">${game.id}</a></td>
					<td><rps:temporal value="${game.lastThrowTimestamp}" format="DEFAULT" /></td>
					<td><spring:message code="home.games.game.state.${game.state}" /></td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		</c:if>
		</sec:authorize>
		
		<%-- Disabling logout until username & password authentication is fully supported: registration and login are missing. --%>
		<%-- <sec:authorize access="isAuthenticated()"> --%>
		<%-- 	<form method="post" action="/logout"> --%>
		<%-- 		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> --%>
		<%--		<spring:message code="logout.submit" var="logoutSubmit" /> --%>
		<%-- 		<input type="submit" value="${logoutSubmit}" /> --%>
		<%-- 	</form> --%>
		<%-- </sec:authorize> --%>
</t:basepage>
