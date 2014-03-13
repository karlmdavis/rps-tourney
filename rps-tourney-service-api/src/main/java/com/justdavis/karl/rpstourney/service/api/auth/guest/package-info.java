/**
 * Contains the types related to the guest authentication code, which persists
 * user data across requests and sessions, but doesn't require an explicit 
 * login.
 */
@XmlSchema(namespace = XmlNamespace.RPSTOURNEY_API, xmlns = { @XmlNs(prefix = "rps", namespaceURI = XmlNamespace.RPSTOURNEY_API) }, elementFormDefault = XmlNsForm.QUALIFIED)
package com.justdavis.karl.rpstourney.service.api.auth.guest;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import com.justdavis.karl.rpstourney.service.api.XmlNamespace;

