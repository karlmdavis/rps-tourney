<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>
<spring:message code="account.subtitle" var="metaSubtitle" />
<c:url value="${requestScope['rpstourney.config.baseurl']}" var="baseUrl" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<h1><spring:message code="account.h1" /></h1>
		<form:form method="POST" action="${baseUrl}/account/update" id="account-properties">
			<div class="form-group">
				<label for="inputCreationTimestamp"><spring:message code="account.created.label" /></label>
				<p id="inputCreationTimestamp">
					<span id="account-created-pretty"><rps:temporal value="${account.createdTimestamp}" format="PRETTY_TIME" /></span>
					<span id="account-created-date">(<rps:temporal value="${account.createdTimestamp}" format="ISO_DATE" />)</span>
				</p>
			</div>
			<div class="form-group">
				<label for="inputName"><spring:message code="account.name.label" /></label>
				<spring:message code="account.name.placeholder" var="namePlaceholder" />
				<input type="text" id="inputName" name="inputName" value="${account.name}" placeholder="${namePlaceholder}">
			</div>
			<button type="submit"><spring:message code="account.submit" /></button>
		</form:form>
</t:basepage>
