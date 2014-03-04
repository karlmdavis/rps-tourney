/**
 * Contains 
 * {@link com.justdavis.karl.rpstourney.webservice.auth.game.GameAuthResourceImpl}, 
 * which allows users to login via game-specific accounts associated with an 
 * email address.
 */
@XmlSchema(namespace = XmlNamespace.RPSTOURNEY, xmlns = { @XmlNs(prefix = "rps", namespaceURI = XmlNamespace.RPSTOURNEY) }, elementFormDefault = XmlNsForm.QUALIFIED)
package com.justdavis.karl.rpstourney.webservice.auth.game;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import com.justdavis.karl.rpstourney.service.api.XmlNamespace;

