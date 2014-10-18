<%@ attribute name="metaSubtitle" required="false" %>
<%@ attribute name="metaDescription" required="false" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
	</head>
	<body>

		<div id="page-content">
			<jsp:doBody />
		</div>

		<div id="page-footer" class="container">
			<footer>
				<p>&copy; <a href="https://justdavis.com/karl/">Karl M. Davis</a>, 2014</p>
			</footer>
		</div>

		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
		<script>window.jQuery || document.write('<script src="${requestScope['rpstourney.config.baseurl']}/js/vendor/jquery-1.11.0.min.js"><\/script>')</script>
		<script src="${requestScope['rpstourney.config.baseurl']}/js/rps.js"></script>

		<!-- Google Analytics: UA-43685799-2 is the rpstourney.com Tracking ID. -->
		<script>
			(function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
			function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
			e=o.createElement(i);r=o.getElementsByTagName(i)[0];
			e.src='//www.google-analytics.com/analytics.js';
			r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
			ga('create','UA-43685799-2');ga('send','pageview');
		</script>
	</body>
</html>
