<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>
<spring:message code="error.notFound.subtitle" var="metaSubtitle" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<h1><spring:message code="error.notFound.subtitle" /></h1>
		<p class="lead"><spring:message code="error.notFound.lead" /></p>
		<blockquote><p><spring:message code="error.notFound.poem" /></p></blockquote>
		<p><spring:message code="error.notFound.trailingText" /></p>
</t:basepage>
