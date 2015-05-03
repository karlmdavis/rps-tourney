<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spring:message code="game.subtitle" var="metaSubtitle" />
<c:url value="${requestScope['rpstourney.config.baseurl']}/game/${game.id}" var="gameUrl" />
<t:basepage metaSubtitle="${metaSubtitle}">
	<jsp:attribute name="bodyscripts">
		<script src="${requestScope['rpstourney.config.baseurl']}/js/game.min.js"></script>
	</jsp:attribute>
	<jsp:body>
			<h1><spring:message code="game.subtitle" /></h1>
			<c:if test="${not empty warningType}">
			<div id="game-warning" class="alert alert-warning" role="alert">
				<spring:message code="game.warning.${warningType}" />
			</div>
			</c:if>
			<div id="player-controls">
				<div id="player-1-controls">
					<t:gamePlayerName gameUrl="${gameUrl}" playerName="${player1Label}" isCurrentPlayer="${isPlayer1}" />
					<div class="player-throws">
						<c:choose>
						<c:when test="${isPlayer1}">
						<a class="throw-rock" href="${gameUrl}/playThrow?throwToPlay=ROCK"><spring:message code="game.throw.rock" /></a>
						<a class="throw-paper" href="${gameUrl}/playThrow?throwToPlay=PAPER"><spring:message code="game.throw.paper" /></a>
						<a class="throw-scissors" href="${gameUrl}/playThrow?throwToPlay=SCISSORS"><spring:message code="game.throw.scissors" /></a>
						</c:when>
						<c:otherwise>
						<span class="throw-rock"><spring:message code="game.throw.rock" /></span>
						<span class="throw-paper"><spring:message code="game.throw.paper" /></span>
						<span class="throw-scissors"><spring:message code="game.throw.scissors" /></span>
						</c:otherwise>
						</c:choose>
					</div>
					<c:choose>
					<c:when test="${isPlayer1TheWinner}">
					<c:set var="player1ScoreClass" value="won" />
					</c:when>
					<c:when test="${isPlayer1TheLoser}">
					<c:set var="player1ScoreClass" value="lost" />
					</c:when>
					</c:choose>
					<div class="player-score">
						<h4><spring:message code="game.playerScore" /></h4>
						<p id="player-1-score-value" class="player-score-value ${player1ScoreClass}">${game.scoreForPlayer1}</p>
					</div>
				</div>
				<div id="player-2-controls">
					<t:gamePlayerName gameUrl="${gameUrl}" playerName="${player2Label}" isCurrentPlayer="${isPlayer2}" />
					<div class="player-throws">
						<c:choose>
						<c:when test="${isPlayer2}">
						<a class="throw-rock" href="${gameUrl}/playThrow?throwToPlay=ROCK"><spring:message code="game.throw.rock" /></a>
						<a class="throw-paper" href="${gameUrl}/playThrow?throwToPlay=PAPER"><spring:message code="game.throw.paper" /></a>
						<a class="throw-scissors" href="${gameUrl}/playThrow?throwToPlay=SCISSORS"><spring:message code="game.throw.scissors" /></a>
						</c:when>
						<c:otherwise>
						<span class="throw-rock"><spring:message code="game.throw.rock" /></span>
						<span class="throw-paper"><spring:message code="game.throw.paper" /></span>
						<span class="throw-scissors"><spring:message code="game.throw.scissors" /></span>
						</c:otherwise>
						</c:choose>
					</div>
					<c:choose>
					<c:when test="${isPlayer2TheWinner}">
					<c:set var="player2ScoreClass" value="won" />
					</c:when>
					<c:when test="${isPlayer2TheLoser}">
					<c:set var="player2ScoreClass" value="lost" />
					</c:when>
					</c:choose>
					<div class="player-score">
						<h4><spring:message code="game.playerScore" /></h4>
						<p id="player-2-score-value" class="player-score-value ${player2ScoreClass}">${game.scoreForPlayer2}</p>
					</div>
				</div>
			</div>
			
			<c:if test="${(game.state == 'WAITING_FOR_PLAYER') || (game.state == 'WAITING_FOR_FIRST_THROW')}">
			<div id="game-controls">
				<h2><spring:message code="game.controls" /></h2>
				<c:if test="${isPlayer && !hasPlayer2}">
				<div id="join-message" class="alert alert-success" role="alert"><spring:message code="game.join.message.player1" /></div>
				</c:if>
				<c:if test="${!isPlayer && !hasPlayer2}">
				<div id="join-controls">
					<div class="alert alert-info" role="alert"><spring:message code="game.join.message.other" /></div>
					<a id="join-game" href="${gameUrl}/join"><spring:message code="game.join" /></a>
				</div>
				</c:if>
				<c:if test="${isPlayer}">
				<div id="max-round-controls">
					<span id="max-rounds-label"><spring:message code="game.maxRounds.label" /></span>
					<a id="max-rounds-down" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds - 2}"><spring:message code="game.maxRounds.down" /></a>
					<span id="max-rounds-value">${game.maxRounds}</span>
					<a id="max-rounds-up" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds + 2}"><spring:message code="game.maxRounds.up" /></a>
				</div>
				</c:if>
			</div>
			</c:if>
	
			<div id="round-history">
				<h2><spring:message code="game.roundHistory" /></h2>
				<p>Round: <span id="round-counter-current">${currentAdjustedRoundIndex + 1}</span><spring:message code="game.roundHistory.counter.separator" /><span id="round-counter-max">${game.maxRounds}</span></p>
				<table id="rounds">
					<thead>
						<tr>
							<th><spring:message code="game.roundHistory.index" /></th>
							<th>${player1Label}</th>
							<th>${player2Label}</th>
							<th><spring:message code="game.roundHistory.result" /></th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${empty game.rounds}">
						<tr id="round-data-fake">
							<td>1</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						</c:if>
						<c:forEach items="${game.rounds}" var="round">
						<tr id="round-data-${round.roundIndex}">
							<td>${round.adjustedRoundIndex + 1}</td>
							<td><spring:message code="game.roundHistory.${round.throwForPlayer1}" /></td>
							<td><spring:message code="game.roundHistory.${round.throwForPlayer2}" /></td>
							<c:choose>
							<c:when test="${round.result == 'PLAYER_1_WON'}">
							<c:set var="roundResultText" value="${player1Label}" />
							</c:when>
							<c:when test="${round.result == 'PLAYER_2_WON'}">
							<c:set var="roundResultText" value="${player2Label}" />
							</c:when>
							<c:when test="${round.result == 'TIED'}">
							<spring:message code="game.roundHistory.result.tied" var="roundResultText" />
							</c:when>
							<c:otherwise>
							<spring:message code="game.roundHistory.result.none" var="roundResultText" />
							</c:otherwise>
							</c:choose>
							<td>${roundResultText}</td>
						</tr>
						</c:forEach>
						<c:if test="${not empty game.winner}">
						<tr id="result-row">
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<c:choose>
							<c:when test="${isUserTheWinner}">
							<td class="won"><spring:message code="game.roundHistory.userWon" /></td>
							</c:when>
							<c:when test="${isUserTheLoser}">
							<td class="lost"><spring:message code="game.roundHistory.userLost" /></td>
							</c:when>
							<c:otherwise>
							<td>${winnerLabel}<spring:message code="game.roundHistory.someoneWonSuffix" /></td>
							</c:otherwise>
							</c:choose>
						</tr>
						</c:if>
					</tbody>
				</table>
			</div>
	</jsp:body>
</t:basepage>
