<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--
	/*
	 * See the JavaDoc for
	 * org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer.loginPage(String)
	 * for an explanation of how this view must work.
	 */
--%>
<spring:message code="login.subtitle" var="subtitle" />
<c:url value="${requestScope['rpstourney.config.baseurl']}/login" var="loginFormUrl" />
<t:basepage metaSubtitle="${subtitle}">
		<h1><spring:message code="login.header" /></h1>
		<form method=POST action="${loginFormUrl}" id="login">
			<c:if test="${param.logout != null}">
			<%--
				/*
				 * By default, requests to the /logout URL will redirect here (to the
				 * /login?logout URL), containing the query param logout.
				 */
			--%>
			<div>
				<spring:message code="login.logout" />
			</div>
			</c:if>
			<div class="form-group">
				<label for="username"><spring:message code="login.username.label" /></label>
				<spring:message code="login.username.placeholder" var="usernamePlaceholder" />
				<input type="text" id="username" name="username" placeholder="${usernamePlaceholder}" />
			</div>
			<div class="form-group">
				<label for="password"><spring:message code="login.password.label" /></label>
				<input type="password" id="password" name="password" />
			</div>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<c:if test="${param.error != null}">
			<%--
				/*
				 * Use param.error assuming FormLoginConfigurer#failureUrl contains the 
				 * query parameter error.
				 */
			--%>
			<div id="login-message" class="alert alert-danger" role="alert">
				<spring:message code="login.failed" />
				<c:if test="${SPRING_SECURITY_LAST_EXCEPTION != null}">
				Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
				</c:if>
			</div>
			</c:if>
			<button type="submit"><spring:message code="login.submit" /></button>
		</form>
		<p>
			<spring:message code="login.register.prefix" /><a href="${baseUrl}/register"><spring:message code="login.register.link.text" /></a><spring:message code="login.register.suffix" />
		</p>
</t:basepage>
