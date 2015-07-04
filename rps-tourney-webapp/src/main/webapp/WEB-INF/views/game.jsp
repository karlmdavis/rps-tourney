<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>
<rps:gameTitle game="${game}" var="metaSubtitle" />
<c:url value="${requestScope['rpstourney.config.baseurl']}/game/${game.id}" var="gameUrl" />
<t:basepage metaSubtitle="${metaSubtitle}">
	<jsp:attribute name="bodyscripts">
		<script src="${requestScope['rpstourney.config.baseurl']}/js/game.min.js"></script>
	</jsp:attribute>
	<jsp:body>
			<h1><rps:gameTitle game="${game}" /></h1>
			<p id="created">Game created <rps:temporal value="${game.createdTimestamp}" format="PRETTY_TIME" />.</p>
			<c:if test="${not empty warningType}">
			<div id="game-warning" class="alert alert-warning" role="alert">
				<spring:message code="game.warning.${warningType}" />
			</div>
			</c:if>
			<div id="players">
				<div class="col">
					<c:choose>
					<c:when test="${game.state == 'FINISHED' && firstPlayer == game.winner}">
						<c:set var="firstPlayerWonLostClass" value="won" />
						<c:set var="secondPlayerWonLostClass" value="lost" />
					</c:when>
					<c:when test="${game.state == 'FINISHED' && secondPlayer == game.winner}">
						<c:set var="firstPlayerWonLostClass" value="lost" />
						<c:set var="secondPlayerWonLostClass" value="won" />
					</c:when>
					</c:choose>
					<div id="player-first" class="${firstPlayerWonLostClass}">
						<t:editablePlayerName game="${game}" player="${firstPlayer}" />
						<p class="player-score-value">
							${firstPlayerScore}
						</p><p class="player-score-suffix">
							<spring:message code="game.scores.suffix.a" />
							<span class="max-rounds-value">${game.maxRounds}</span>
							<spring:message code="game.scores.suffix.b" />
						</p>
					</div>
				</div>
				<div class="col">
					<div id="player-second" class="${secondPlayerWonLostClass}">
						<t:editablePlayerName game="${game}" player="${secondPlayer}" />
						<p class="player-score-value">
							${secondPlayerScore}
						</p><p class="player-score-suffix">
							<spring:message code="game.scores.suffix.a" />
							<span class="max-rounds-value">${game.maxRounds}</span>
							<spring:message code="game.scores.suffix.b" />
						</p>
					</div>
				</div>
			</div>

			<c:if test="${!isPlayer || (game.state == 'WAITING_FOR_PLAYER')}">
				<c:set var="throwsVisibility" value="hidden"/>
			</c:if>
			<div class="player-throws ${throwsVisibility}">
				<h2><span class="col">Choose Your Throw</span></h2>
				<div class="row">
					<div class="col"><a class="throw-rock" href="${gameUrl}/playThrow?throwToPlay=ROCK"><spring:message code="game.throw.rock" /></a></div>
					<div class="col"><a class="throw-paper" href="${gameUrl}/playThrow?throwToPlay=PAPER"><spring:message code="game.throw.paper" /></a></div>
					<div class="col"><a class="throw-scissors" href="${gameUrl}/playThrow?throwToPlay=SCISSORS"><spring:message code="game.throw.scissors" /></a></div>
				</div>
			</div>

			<c:choose>
			<c:when test="${isPlayer && (game.state == 'WAITING_FOR_PLAYER')}">
			<div id="game-controls">
				<h2><spring:message code="game.controls" /></h2>
				<div id="join-message" class="alert alert-info" role="alert">
					<p><spring:message code="game.join.message.player1" /></p>
					<p>
						<strong><spring:message code="game.join.message.player1.url" /></strong>
						<a href="${gameUrl}">${gameUrl}</a>
					</p>
				</div>
				<div id="max-round-controls">
					<div class="row">
						<div class="col"><span id="max-rounds-label"><spring:message code="game.maxRounds.label" /></span></div>
					</div>
					<div class="row">
						<div class="col"><a id="max-rounds-down" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds - 2}"><spring:message code="game.maxRounds.down" /></a></div>
						<div class="col"><span class="max-rounds-value">${game.maxRounds}</span></div>
						<div class="col"><a id="max-rounds-up" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds + 2}"><spring:message code="game.maxRounds.up" /></a></div>
					</div>
				</div>
			</div>
			</c:when>
			<c:when test="${!isPlayer && (game.state == 'WAITING_FOR_PLAYER')}">
			<div id="game-controls">
				<h2><spring:message code="game.controls" /></h2>
				<div id="join-controls">
					<div class="alert alert-info" role="alert"><spring:message code="game.join.message.other" /></div>
					<div class="row">
						<div class="col"><a id="join-game" href="${gameUrl}/join"><spring:message code="game.join" /></a></div>
					</div>
				</div>
			</div>
			</c:when>
			<c:when test="${isPlayer && (game.state == 'WAITING_FOR_FIRST_THROW')}">
			<div id="game-controls">
				<h2><spring:message code="game.controls" /></h2>
				<div id="max-round-controls">
					<div class="row">
						<div class="col"><span id="max-rounds-label"><spring:message code="game.maxRounds.label" /></span></div>
					</div>
					<div class="row">
						<div class="col"><a id="max-rounds-down" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds - 2}"><spring:message code="game.maxRounds.down" /></a></div>
						<div class="col"><span class="max-rounds-value">${game.maxRounds}</span></div>
						<div class="col"><a id="max-rounds-up" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds + 2}"><spring:message code="game.maxRounds.up" /></a></div>
					</div>
				</div>
			</div>
			</c:when>
			</c:choose>
	
			<div id="round-history">
				<h2><spring:message code="game.roundHistory" /></h2>
				<p>Round: <span id="round-counter-current">${currentAdjustedRoundIndex + 1}</span><spring:message code="game.roundHistory.counter.separator" /><span class="max-rounds-value">${game.maxRounds}</span></p>
				<table id="rounds">
					<thead>
						<tr>
							<th><spring:message code="game.roundHistory.index" /></th>
							<th><rps:playerName game="${game}" player="${game.player1}" /></th>
							<th><rps:playerName game="${game}" player="${game.player2}" /></th>
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
							<td><rps:playerName game="${game}" player="${game.player1}" /></td>
							</c:when>
							<c:when test="${round.result == 'PLAYER_2_WON'}">
							<td><rps:playerName game="${game}" player="${game.player2}" /></td>
							</c:when>
							<c:when test="${round.result == 'TIED'}">
							<td><spring:message code="game.roundHistory.result.tied" /></td>
							</c:when>
							<c:otherwise>
							<td><spring:message code="game.roundHistory.result.none" /></td>
							</c:otherwise>
							</c:choose>
						</tr>
						</c:forEach>
						<c:if test="${not empty game.winner}">
						<tr id="result-row">
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<c:choose>
							<c:when test="${isUserTheWinner}">
							<td class="won">
							</c:when>
							<c:otherwise>
							<td>
							</c:otherwise>
							</c:choose>
								<rps:playerName game="${game}" player="${game.winner}" />
								<spring:message code="game.roundHistory.someoneWonSuffix" />
							</td>
						</tr>
						</c:if>
					</tbody>
				</table>
			</div>
	</jsp:body>
</t:basepage>
