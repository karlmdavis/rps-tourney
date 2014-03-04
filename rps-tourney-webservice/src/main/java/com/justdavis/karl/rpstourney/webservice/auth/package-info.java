/**
 * <p>
 * This is the base package for the webservice's authentication API.
 * </p>
 */
@XmlSchema(namespace = XmlNamespace.RPSTOURNEY, xmlns = { @XmlNs(prefix = "rps", namespaceURI = XmlNamespace.RPSTOURNEY) }, elementFormDefault = XmlNsForm.QUALIFIED)
package com.justdavis.karl.rpstourney.webservice.auth;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import com.justdavis.karl.rpstourney.service.api.XmlNamespace;

