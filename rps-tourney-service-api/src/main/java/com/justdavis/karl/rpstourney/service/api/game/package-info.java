/**
 * Contains the JPA {@link javax.persistence.Entity} classes and JAX-RS
 * resources/services involved in actually playing the game.
 */
@XmlSchema(namespace = XmlNamespace.RPSTOURNEY_API, xmlns = {
		@XmlNs(prefix = "rps", namespaceURI = XmlNamespace.RPSTOURNEY_API) }, elementFormDefault = XmlNsForm.QUALIFIED)
package com.justdavis.karl.rpstourney.service.api.game;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import com.justdavis.karl.rpstourney.service.api.XmlNamespace;
