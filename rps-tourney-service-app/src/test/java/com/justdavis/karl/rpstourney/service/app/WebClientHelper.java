package com.justdavis.karl.rpstourney.service.app;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.Cookie;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * Utility methods to modify and work with {@link WebClient} instances.
 */
public final class WebClientHelper {
	/*
	 * These methods may or may not be being used at the moment, but they have all been used at one point or another
	 * while trying things out and/or debugging issues, and they took forever to write. Deleting them would probably not
	 * be a great idea.
	 */

	/**
	 * Enables/disables the {@link WebClient}'s session maintenance, which will persist cookies and other state between
	 * web client calls. Defaults to <code>false</code>.
	 *
	 * @param client
	 *            the {@link WebClient} to modify
	 * @param maintainSession
	 *            <code>true</code> to enable session maintenance, <code>false</code> to disable it
	 */
	public static void enableSessionMaintenance(WebClient client, boolean maintainSession) {
		WebClient.getConfig(client).getRequestContext().put(org.apache.cxf.message.Message.MAINTAIN_SESSION,
				maintainSession);
	}

	/**
	 * Enables/disables the {@link WebClient}'s "permissive SSL trust" mode, which will accept any/all server SSL
	 * certificates, regardless of their validity. Defaults to <code>false</code>.
	 *
	 * @param client
	 *            the {@link WebClient} to modify
	 * @param permissiveSslTrustMode
	 *            <code>true</code> to enable "permissive SSL trust", <code>false</code> to disable it
	 */
	public static void enablePermissiveSslTrustMode(WebClient client, boolean permissiveSslTrustMode) {
		// Grab the WebClient's config.
		ClientConfiguration clientConfig = WebClient.getConfig(client);

		// Grab/create the TLSClientParameters being used.
		HTTPConduit clientHttpConduit = (HTTPConduit) clientConfig.getConduit();
		TLSClientParameters tlsParams;
		if (clientHttpConduit.getTlsClientParameters() != null) {
			tlsParams = clientHttpConduit.getTlsClientParameters();
		} else {
			tlsParams = new TLSClientParameters();
			clientHttpConduit.setTlsClientParameters(tlsParams);
		}

		// If "permissive SSL trust" is enabled, set a custom X509TrustManager.
		tlsParams.setDisableCNCheck(permissiveSslTrustMode);
		if (permissiveSslTrustMode)
			tlsParams.setTrustManagers(new TrustManager[] { new PermissiveX509TrustManager() });
		else
			tlsParams.setTrustManagers(new TrustManager[] {});
	}

	/**
	 * Copies the {@link Cookie}s from one {@link WebClient} to another.
	 *
	 * @param sourceClient
	 *            the {@link WebClient} to copy from
	 * @param targetClient
	 *            the {@link WebClient} to copy to
	 */
	public static void copyCookies(WebClient sourceClient, WebClient targetClient) {
		HTTPConduit sourceConduit = WebClient.getConfig(sourceClient).getHttpConduit();
		HTTPConduit targetConduit = WebClient.getConfig(targetClient).getHttpConduit();
		targetConduit.getCookies().putAll(sourceConduit.getCookies());
	}

	/**
	 * <p>
	 * <strong>Do not use this class in production!</strong>
	 * </p>
	 * <p>
	 * This {@link X509TrustManager} implementation can be used by clients, and will accept <em>all</em> server
	 * certificates, regardless of their actual validity.
	 * </p>
	 */
	private static final class PermissiveX509TrustManager implements X509TrustManager {
		/**
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// This class is only used by clients.
			throw new UnsupportedOperationException();
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// Nothing to do; this class is only used by clients.
			return new X509Certificate[] {};
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
		 *      java.lang.String)
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// Do nothing; trust everything.
			// DO NOT USE IN PRODUCTION!
		}
	}
}
