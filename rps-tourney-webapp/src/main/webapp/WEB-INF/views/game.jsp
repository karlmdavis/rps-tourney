<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<spring:message code="game.subtitle" var="metaSubtitle" />
<c:url value="${requestScope['rpstourney.config.baseurl']}/game/${game.id}" var="gameUrl" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<h1><spring:message code="game.subtitle" /></h1>
		<div id="player-controls">
			<div id="player-1-controls">
				<h3>${player1Label}</h3>
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
				<div class="player-score">
					<h4><spring:message code="game.playerScore" /></h4>
					<p class="player-score-value">${player1Score}</p>
				</div>
			</div>
			<div id="player-2-controls">
				<h3>${player2Label}</h3>
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
				<div class="player-score">
					<h4><spring:message code="game.playerScore" /></h4>
					<p class="player-score-value">${player2Score}</p>
				</div>
			</div>
		</div>
		
		<c:if test="${(game.state == 'WAITING_FOR_PLAYER') || (game.state == 'WAITING_FOR_FIRST_THROW')}">
		<div id="game-controls">
			<h2><spring:message code="game.controls" /></h2>
			<c:if test="${!isPlayer && !hasPlayer2}">
			<a id="join-game" href="${gameUrl}/join"><spring:message code="game.join" /></a>
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
			<p>Round: <span id="currentRoundIndex">${fn:length(game.rounds)}</span> of ${game.maxRounds}</p>
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
					<tr id="fakeResultRow">
						<td>1</td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					</c:if>
					<c:forEach items="${game.rounds}" var="round">
					<tr>
						<td>${round.roundIndex + 1}</td>
						<td><c:if test="${isPlayer1 || (round.roundIndex < (fn:length(game.rounds)) - 1)}"><spring:message code="game.roundHistory.${round.throwForPlayer1}" /></c:if></td>
						<td><c:if test="${isPlayer2 || (round.roundIndex < (fn:length(game.rounds)) - 1)}"><spring:message code="game.roundHistory.${round.throwForPlayer2}" /></c:if></td>
						<td><spring:message code="game.roundHistory.${round.result}" /></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
</t:basepage>
