<%@ attribute name="metaSubtitle" required="false" %>
<%@ attribute name="metaDescription" required="false" %>
<%@ attribute name="header" fragment="true" required="false" %>
<%@ attribute name="bodyscripts" fragment="true" required="false" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="rps" uri="http://justdavis.com/karl/rpstourney/app/jsp-tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><spring:message code="template.title.prefix" /><c:if test="${not empty metaSubtitle}">: ${metaSubtitle}</c:if></title>
		<c:if test="${empty metaDescription}">
			<spring:message code="template.meta.description.default" var="metaDescription" />
		</c:if>
		<meta name="description" content="${metaDescription}">
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<link rel="stylesheet" href="${requestScope['rpstourney.config.baseurl']}/css/rps.css">
		<script src="${requestScope['rpstourney.config.baseurl']}/js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
		
		<script>
			var baseUrl = "${requestScope['rpstourney.config.baseurl']}";
		</script>
		
		<jsp:invoke fragment="header" />
	</head>
	<body>
	
		<div class="navbar navbar-default navbar-static-top" role="navigation">
			<div class="container">
				<div class="navbar-header">
					<button type="button" id="nav-collapse-toggle" class="navbar-toggle collapsed"
							data-toggle="collapse" data-target="#navbar-collapse-area"
							aria-expanded="false">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="${requestScope['rpstourney.config.baseurl']}"><spring:message code="template.title.prefix" /></a>
				</div>
				<div class="collapse navbar-collapse" id="navbar-collapse-area">
					<t:accountControl />
				</div>
			</div>
		</div>

		<div id="page-content">
			<jsp:doBody />
		</div>

		<footer id="page-footer">
			<div class="container">
				<jsp:useBean id="date" class="java.util.Date" />
				<p>
					&copy; <a href="https://justdavis.com/karl/">Karl M. Davis</a>, <fmt:formatDate value="${date}" pattern="yyyy" />.
					Check this project out at <a href="https://github.com/karlmdavis/rps-tourney">github.com/karlmdavis/rps-tourney</a>.
				</p>
			</div>
		</footer>

		<script src="//ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script>window.jQuery || document.write('<script src="${requestScope['rpstourney.config.baseurl']}/js/vendor/jquery-3.3.1.min.js"><\/script>')</script>
		<script src="${requestScope['rpstourney.config.baseurl']}/js/rps.js"></script>
		
		<jsp:invoke fragment="bodyscripts" />

		<!-- Global site tag (gtag.js) - Google Analytics (Google Analytics 4 property) -->
		<script async src="https://www.googletagmanager.com/gtag/js?id=G-ZJYQ96CT0K"></script>
		<script>
		  window.dataLayer = window.dataLayer || [];
		  function gtag(){dataLayer.push(arguments);}
		  gtag('js', new Date());
		  gtag('config', 'G-ZJYQ96CT0K');
		</script>
	</body>
</html>
