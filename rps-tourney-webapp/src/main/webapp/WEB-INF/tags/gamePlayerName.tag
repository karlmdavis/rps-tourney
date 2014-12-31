<%@ attribute name="gameUrl" required="true" %>
<%@ attribute name="playerName" required="true" %>
<%@ attribute name="isCurrentPlayer" required="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

					<c:choose>
					<c:when test="${isCurrentPlayer}">
					<h3 class="player-name player-name-current">
						${playerName}
						<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
					</h3>
					<form:form method="POST" action="${gameUrl}/updateName" class="player-name input-group">
						<%-- Note: Bootstrap doesn't support semantic input groups. --%>
						<spring:message code="game.player.name.placeholder" var="playerNamePlaceholder" />
						<input type="text" name="currentPlayerName" placeholder="${playerNamePlaceholder}" value="${playerName}" class="form-control" />
						<span class="input-group-btn">
							<button type="submit" class="player-name-submit btn btn-default">Save</button>
						</span>
					</form:form>
					</c:when>
					<c:otherwise>
					<h3 class="player-name">${playerName}</h3>
					</c:otherwise>
					</c:choose>
