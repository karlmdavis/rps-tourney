<?xml version="1.0" encoding="UTF-8"?>
<jsp:root
		xmlns:jsp="http://java.sun.com/JSP/Page"
		xmlns:c="http://java.sun.com/jsp/jstl/core"
		version="2.1">
	
	<jsp:directive.page contentType="application/xml" />
	<jsp:output omit-xml-declaration="false" />

	<appInfo xmlns="http://justdavis.com/karl/rpstourney/app/schema/v1">
		<version><c:out value="${model.app_version}" /></version>
	</appInfo>

</jsp:root>