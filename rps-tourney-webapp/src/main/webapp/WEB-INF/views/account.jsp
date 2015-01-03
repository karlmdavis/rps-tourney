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
		<form:form method="POST" action="${baseUrl}/account/update">
			<div class="form-group">
				<label for="inputCreationTimestamp">Created</label>
				<p id="inputCreationTimestamp" class="form-control-static">
					<span id="account-created-pretty"><rps:temporal value="${account.createdTimestamp}" format="PRETTY_TIME" /></span>
					<span id="account-created-date">(<rps:temporal value="${account.createdTimestamp}" format="ISO_DATE" />)</span>
				</p>
			</div>
			<div class="form-group">
				<label for="inputName">Name</label>
				<input type="text" id="inputName" name="inputName" class="form-control" value="${account.name}" placeholder="Anonymous">
			</div>
			<button type="submit" class="btn btn-default">Submit</button>
		</form:form>
</t:basepage>
