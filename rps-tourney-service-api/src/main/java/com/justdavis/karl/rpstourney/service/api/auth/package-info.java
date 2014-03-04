/**
 * The base package for the web service's authentication API. This API has 
 * several implementations, but is not pluggable. See 
 * {@link com.justdavis.karl.rpstourney.service.api.auth.LoginProvider} for a 
 * listing of the implementations.
 */
@XmlSchema(namespace = XmlNamespace.RPSTOURNEY, xmlns = { @XmlNs(prefix = "rps", namespaceURI = XmlNamespace.RPSTOURNEY) }, elementFormDefault = XmlNsForm.QUALIFIED)
package com.justdavis.karl.rpstourney.service.api.auth;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
