<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>
<spring:message code="register.subtitle" var="metaSubtitle" />
<c:url value="${requestScope['rpstourney.config.baseurl']}" var="baseUrl" />
<t:basepage metaSubtitle="${metaSubtitle}">
		<h1><spring:message code="register.h1" /></h1>
		<form:form method="POST" action="${baseUrl}/register" id="register">
			<div class="form-group">
				<label for="inputEmail"><spring:message code="register.email.label" /></label>
				<spring:message code="register.email.placeholder" var="emailPlaceholder" />
				<input type="text" id="inputEmail" name="inputEmail" placeholder="${emailPlaceholder}" />
			</div>
			<div class="form-group">
				<label for="inputPassword1"><spring:message code="register.password1.label" /></label>
				<input type="password" id="inputPassword1" name="inputPassword1" />
			</div>
			<div class="form-group">
				<label for="inputPassword2"><spring:message code="register.password2.label" /></label>
				<input type="password" id="inputPassword2" name="inputPassword2" />
			</div>
			<c:if test="${not empty messageType}">
			<div id="register-message" class="alert alert-danger" role="alert">
				<spring:message code="register.message.${messageType}" />
			</div>
			</c:if>
			<button type="submit"><spring:message code="register.submit" /></button>
		</form:form>
</t:basepage>
