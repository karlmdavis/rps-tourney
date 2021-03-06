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
<sec:authentication var="principal" property="principal" />
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
				<h2><span class="col"><spring:message code="game.throws.h2" /></span></h2>
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
				<div id="max-round-controls">
					<div id="max-rounds-label-row" class="row">
						<div class="col"><label><spring:message code="game.maxRounds.label" /></label></div>
					</div>
					<div id="max-rounds-controls-outer" class="row">
						<div class="col">
							<div id="max-rounds-controls-inner" class="row">
								<div class="col"><a id="max-rounds-down" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds - 2}"><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></a></div>
								<div class="col"><span class="max-rounds-value">${game.maxRounds}</span></div>
								<div class="col"><a id="max-rounds-up" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds + 2}"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></div>
							</div>
						</div>
					</div>
				</div>
				<form:form method="POST" action="${gameUrl}/inviteOpponent" id="opponent-selection">
					<div class="col">
						<label><spring:message code="game.inviteOpponent.label" /></label>
						<div class="radio">
							<label>
								<input type="radio" name="opponentType" id="opponent-type-friend" value="friend" checked />
								<span class="control-indicator">&#xf10c;</span>
								<spring:message code="game.inviteOpponent.friend" />
							</label>
						</div>
						<div class="radio">
							<label>
								<input type="radio" name="opponentType" id="opponent-type-ai" value="ai" />
								<span class="control-indicator">&#xf10c;</span>
								<spring:message code="game.inviteOpponent.ai" />
							</label>
						</div>
						<div id="opponent-type-friend-panel">
							<div id="join-message" class="alert alert-info" role="alert">
								<p><spring:message code="game.join.message.player1" /></p>
								<p>
									<strong><spring:message code="game.join.message.player1.url" /></strong>
									<a href="${gameUrl}">${gameUrl}</a>
								</p>
							</div>
						</div>
						<div id="opponent-type-ai-panel">
							<div id="ai-id-group">
								<label for="ai-id"><spring:message code="game.inviteOpponent.ai.difficulty" /></label>
								<select name="playerId" id="ai-id">
									<c:forEach items="${aiPlayers}" var="aiPlayer">
									<c:choose>
									<c:when test="${not empty aiPlayer.name}">
									<option value="${aiPlayer.id}">${aiPlayer.name}</option>
									</c:when>
									<c:when test="${not empty aiPlayer.builtInAi}">
									<option value="${aiPlayer.id}"><spring:message code="players.ai.name.${aiPlayer.builtInAi.displayNameKey}" /></option>
									</c:when>
									</c:choose>
									</c:forEach>
								</select> 
							</div>
							<button type="submit"><spring:message code="game.inviteOpponent.submit" /></button>
						</div>
					</div>
				</form:form>
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
					<div id="max-rounds-label-row" class="row">
						<div class="col"><label><spring:message code="game.maxRounds.label" /></label></div>
					</div>
					<div id="max-rounds-controls-outer" class="row">
						<div class="col">
							<div id="max-rounds-controls-inner" class="row">
								<div class="col"><a id="max-rounds-down" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds - 2}"><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></a></div>
								<div class="col"><span class="max-rounds-value">${game.maxRounds}</span></div>
								<div class="col"><a id="max-rounds-up" href="${gameUrl}/setMaxRounds?oldMaxRoundsValue=${game.maxRounds}&newMaxRoundsValue=${game.maxRounds + 2}"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></div>
							</div>
						</div>
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
							<th><rps:playerName game="${game}" player="${firstPlayer}" /></th>
							<th><rps:playerName game="${game}" player="${secondPlayer}" /></th>
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
							<c:choose>
							<c:when test="${firstPlayer == game.player1}">
							<td><spring:message code="game.roundHistory.${round.throwForPlayer1}" /></td>
							<td><spring:message code="game.roundHistory.${round.throwForPlayer2}" /></td>
							</c:when>
							<c:otherwise>
							<td><spring:message code="game.roundHistory.${round.throwForPlayer2}" /></td>
							<td><spring:message code="game.roundHistory.${round.throwForPlayer1}" /></td>
							</c:otherwise>
							</c:choose>
							<td><rps:roundResult game="${game}" round="${round}" /></td>
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
							<c:when test="${isUserTheLoser}">
							<td class="lost">
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
