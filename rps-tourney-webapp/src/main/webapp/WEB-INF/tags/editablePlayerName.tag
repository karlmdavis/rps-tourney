<%@ attribute name="game" required="true" type="com.justdavis.karl.rpstourney.service.api.game.GameView" %>
<%@ attribute name="player" required="true" type="com.justdavis.karl.rpstourney.service.api.game.Player" %>
<%@ attribute name="isCurrentPlayer" required="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>

					<c:url value="${requestScope['rpstourney.config.baseurl']}/game/${game.id}" var="gameUrl" />

					<c:choose>
					<c:when test="${isCurrentPlayer}">
					<h3 class="player-name player-name-current">
						<rps:playerName game="${game}" player="${player}" />
						<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
					</h3>
					<form:form method="POST" action="${gameUrl}/updateName" class="player-name input-group">
						<%-- Note: Bootstrap doesn't support semantic input groups. --%>
						<spring:message code="game.player.name.placeholder" var="playerNamePlaceholder" />
						<input type="text" name="inputPlayerName" placeholder="${playerNamePlaceholder}" value="${player.name}" class="form-control" />
						<span class="input-group-btn">
							<button type="submit" class="player-name-submit btn btn-default">Save</button>
						</span>
					</form:form>
					</c:when>
					<c:otherwise>
					<h3 class="player-name">
						<rps:playerName game="${game}" player="${player}" />
					</h3>
					</c:otherwise>
					</c:choose>
