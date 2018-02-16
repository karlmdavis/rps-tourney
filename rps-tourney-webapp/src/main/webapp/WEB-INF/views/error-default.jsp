<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>
<spring:message code="error.default.subtitle" var="metaSubtitle" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<h1><spring:message code="error.default.subtitle" /></h1>
		<p class="lead"><spring:message code="error.default.lead" /></p>
		<blockquote><p><spring:message code="error.default.poem" /></p></blockquote>
		<p><spring:message code="error.default.trailingText" /></p>
</t:basepage>
