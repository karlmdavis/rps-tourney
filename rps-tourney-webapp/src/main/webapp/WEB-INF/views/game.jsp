<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<spring:message code="game.subtitle" var="metaSubtitle" />
<c:url value="${requestScope['rpstourney.config.baseurl']}/game/${game.id}" var="gameUrl" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<div id="currentRound">
			<h2><spring:message code="game.currentRound" /></h2>
			<div id="playerControls">
				<div id="player1Controls">
					<h3>${player1Label}</h3>
					<p id="player1Status"></p>
					<a id="player1ThrowRock" href="${gameUrl}/playThrow?throwToPlay=ROCK"><spring:message code="game.throw.rock" /></a>
					<a id="player1ThrowPaper" href="${gameUrl}/playThrow?throwToPlay=PAPER"><spring:message code="game.throw.paper" /></a>
					<a id="player1ThrowScissors" href="${gameUrl}/playThrow?throwToPlay=SCISSORS"><spring:message code="game.throw.scissors" /></a>
				</div>
				<div id="player2Controls">
					<h3>${player2Label}</h3>
					<p id="player2Status"></p>
					<a id="player2ThrowRock" href="${gameUrl}/playThrow?throwToPlay=ROCK"><spring:message code="game.throw.rock" /></a>
					<a id="player2ThrowPaper" href="${gameUrl}/playThrow?throwToPlay=PAPER"><spring:message code="game.throw.paper" /></a>
					<a id="player2ThrowScissors" href="${gameUrl}/playThrow?throwToPlay=SCISSORS"><spring:message code="game.throw.scissors" /></a>
				</div>
			</div>
			<div id="playerScores">
				<div id="player1Score">
					<p><spring:message code="game.playerScore" /></p>
					<p id="player1ScoreValue">${player1Score}</p>
				</div>
				<div id="player2Score">
					<p><spring:message code="game.playerScore" /></p>
					<p id="player2ScoreValue">${player2Score}</p>
				</div>
			</div>
		</div>
		
		<c:if test="${(game.state == 'WAITING_FOR_PLAYER') || (game.state == 'WAITING_FOR_FIRST_THROW')}">
		<div id="gameControls">
			<h2>Game Controls</h2>
			
			<c:if test="${isPlayer}">
			<div id="maxRoundControls">
				<h3>Max Rounds</h3>
				<a id="maxRoundsDown" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds - 2}"><spring:message code="game.maxRounds.down" /></a>
				<spring:message code="game.maxRounds.label" />
				<input id="maxRoundsValue" type="text" value="${game.maxRounds}" />
				<a id="maxRoundsUp" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds + 2}"><spring:message code="game.maxRounds.up" /></a>
			</div>
			</c:if>
			<c:if test="${!isPlayer && !hasPlayer2}">
			<a id="joinLink" href="${gameUrl}/join"><spring:message code="game.join" /></a>
			</c:if>
		</div>
		</c:if>

		<div id="roundHistory">
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
						<td><spring:message code="game.roundHistory.${round.throwForPlayer1}" /></td>
						<td><spring:message code="game.roundHistory.${round.throwForPlayer2}" /></td>
						<td><spring:message code="game.roundHistory.${round.result}" /></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
</t:basepage>
