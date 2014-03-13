package com.justdavis.karl.rpstourney.webapp;

/**
 * Just contains the {@link #RPSTOURNEY} constant.
 */
public final class XmlNamespace {
	/**
	 * The default XML namespace that should be used by this project's web
	 * application.
	 */
	public static final String RPSTOURNEY_APP = "http://justdavis.com/karl/rpstourney/app/schema/v1";

	/**
	 * A convenience-only alias for
	 * {@link com.justdavis.karl.rpstourney.service.api.XmlNamespace#RPSTOURNEY}
	 * .
	 */
	public static final String RPSTOURNEY_API = com.justdavis.karl.rpstourney.service.api.XmlNamespace.RPSTOURNEY;

	/**
	 * A convenience-only alias for
	 * {@link com.justdavis.karl.misc.datasources.XmlNamespace#JE_DATASOURCES}.
	 */
	public static final String JE_DATASOURCES = com.justdavis.karl.misc.datasources.XmlNamespace.JE_DATASOURCES;
}
