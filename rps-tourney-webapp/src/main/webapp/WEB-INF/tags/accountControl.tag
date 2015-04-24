<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>

				<sec:authentication var="principal" property="principal" />
				<c:choose>
				<c:when test="${(not empty principal) && !principal.anonymous}">
				<p id="signed-in" class="navbar-text navbar-right">
					Signed in as 
					<a href="${requestScope['rpstourney.config.baseurl']}/account" class="navbar-link">
					<rps:accountName />
					</a>
				</p>
				</c:when>
				<c:otherwise>
				<a href="${requestScope['rpstourney.config.baseurl']}/login" id="sign-in" class="btn btn-default navbar-btn navbar-right">
					<spring:message code="template.signin.label" />
				</a>
				</c:otherwise>
				</c:choose>
