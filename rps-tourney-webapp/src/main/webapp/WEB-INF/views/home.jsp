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
		<p><spring:message code="home.intro.2" /></p>
		<p><a id="create-game" href="${requestScope['rpstourney.config.baseurl']}/game/"><spring:message code="home.games.create" /></a></p>
		
		<h2><spring:message code="home.games.h2" /></h2>
		<c:choose>
		<c:when test="${empty games}">
		<p id="player-games-empty"><spring:message code="home.games.empty" /></p>
		</c:when>
		<c:otherwise>
		<table id="player-games">
			<thead>
				<tr>
					<th><spring:message code="home.games.id.label" /></th>
					<th><spring:message code="home.games.opponent.label" /></th>
					<th><spring:message code="home.games.lastThrowTimestamp.label" /></th>
					<th><spring:message code="home.games.state.label" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${games}" var="game">
				<tr>
					<td><a class="game-view" href="${requestScope['rpstourney.config.baseurl']}/game/${game.id}"><spring:message code="home.games.view" /></a></td>
					<td><rps:gameOpponent game="${game}" /></td>
					<td><rps:temporal value="${game.lastThrowTimestamp}" format="PRETTY_TIME" /></td>
					<td><spring:message code="home.games.game.state.${game.state}" /></td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		</c:otherwise>
		</c:choose>
		
		<%-- Disabling logout until username & password authentication is fully supported: registration and login are missing. --%>
		<%-- <sec:authorize access="isAuthenticated()"> --%>
		<%-- 	<form method="post" action="/logout"> --%>
		<%-- 		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> --%>
		<%--		<spring:message code="logout.submit" var="logoutSubmit" /> --%>
		<%-- 		<input type="submit" value="${logoutSubmit}" /> --%>
		<%-- 	</form> --%>
		<%-- </sec:authorize> --%>
</t:basepage>
