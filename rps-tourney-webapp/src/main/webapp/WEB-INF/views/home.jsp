<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<spring:message code="home.subtitle" var="subtitle" />
<t:basepage subtitle="${subtitle}">
		<p>Hello World!</p>
</t:basepage>
