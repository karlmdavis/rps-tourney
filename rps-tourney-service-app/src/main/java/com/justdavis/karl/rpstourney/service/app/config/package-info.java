/**
 * Provides classes related to the web service app's configuration.
 */
@XmlSchema(namespace = XmlNamespace.RPSTOURNEY_API, xmlns = {
		@XmlNs(prefix = "rps", namespaceURI = XmlNamespace.RPSTOURNEY_API) }, elementFormDefault = XmlNsForm.QUALIFIED)
package com.justdavis.karl.rpstourney.service.app.config;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
