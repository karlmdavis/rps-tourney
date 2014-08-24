<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<spring:message code="game.subtitle" var="subtitle" />
<t:basepage subtitle="${subtitle}">
		<p>Game Session ID: ${gameSessionId}</p>
</t:basepage>
