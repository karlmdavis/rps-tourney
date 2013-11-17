package com.justdavis.karl.rpstourney.webservice;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

/**
 * <p>
 * Hosts the {@link GameApplication} JAX-RS app via an embedded Jetty instance.
 * Uses the configuration in <code>src/main/webapp/WEB-INF/web.xml</code> to
 * configure Jetty.
 * </p>
 * <p>
 * Not designed for production use; should only be used for development/testing.
 * </p>
 */
public final class EmbeddedServer {
	/**
	 * The alias of the self-signed cert that will be created by
	 * {@link #createKeyStoreForTest()}.
	 */
	private static final String CERT_ALIAS = "self_signed_cert";

	/**
	 * The password for the self-signed cert that will be created by
	 * {@link #createKeyStoreForTest()}.
	 */
	private static final String CERT_PASSWORD = "foo";

	/**
	 * The default port that Jetty will run on.
	 */
	public static final int DEFAULT_PORT = 8087;

	private final int port;
	private final boolean enableSsl;
	private Server server;

	/**
	 * Constructs a new {@link EmbeddedServer} instance. Does not start the
	 * server; see {@link #startServer()} for that.
	 * 
	 * @param port
	 *            the port to host Jetty on
	 * @param <code>true</code> to serve HTTPS (with a randomly generated
	 *        self-signed cert), <code>false</code> to serve HTTP (just one or
	 *        the other)
	 */
	public EmbeddedServer(int port, boolean enableSsl) {
		this.port = port;
		this.enableSsl = enableSsl;
	}

	/**
	 * Constructs a new {@link EmbeddedServer} instance. Does not start the
	 * server; see {@link #startServer()} for that.
	 */
	public EmbeddedServer() {
		this(DEFAULT_PORT, false);
	}

	/**
	 * Launches Jetty, configuring it to run the web application configured in
	 * <code>rps-tourney-webservice/src/main/webapp/WEB-INF/web.xml</code>.
	 */
	public synchronized void startServer() {
		if (this.server != null)
			throw new IllegalStateException();

		// Create the Jetty Server instance.
		this.server = new Server(this.port);
		this.server.setStopAtShutdown(true);
		if (enableSsl)
			activateSslOnlyMode();

		// Use the web.xml to configure things.
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");
		this.server.setHandler(webapp);
		/*
		 * NOTE: the above is not robust enough to handle running things if this
		 * code is packaged in an unexploded WAR. It would probably need to try
		 * and load the web.xml as a classpath resource, or something like that.
		 */

		// Start up Jetty.
		try {
			this.server.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Disables HTTP, enables HTTPS.
	 */
	private void activateSslOnlyMode() {
		// Modify the default HTTP config.
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecurePort(this.port);

		// Create the HTTPS config.
		HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
		httpsConfig.addCustomizer(new SecureRequestCustomizer());

		// Create the SslContextFactory to be used, along with the cert.
		SslContextFactory sslContextFactory = new SslContextFactory(true);
		sslContextFactory.setKeyStore(createKeyStoreForTest());
		sslContextFactory.setCertAlias(CERT_ALIAS);
		sslContextFactory.setKeyManagerPassword(CERT_PASSWORD);

		// Apply the config.
		ServerConnector serverConnector = new ServerConnector(this.server,
				new SslConnectionFactory(sslContextFactory,
						HttpVersion.HTTP_1_1.toString()),
				new HttpConnectionFactory(httpsConfig));
		serverConnector.setPort(this.port);
		this.server.setConnectors(new Connector[] { serverConnector });
	}

	/**
	 * @return a {@link KeyStore} with a new, random self-signed key pair in it
	 */
	private static KeyStore createKeyStoreForTest() {
		try {
			// Create a new, random key pair.
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			KeyPair pair = keyGen.generateKeyPair();
			SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo
					.getInstance(pair.getPublic().getEncoded());

			// Create a new self-signed cert.
			X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
					new X500Name("c=foo"), BigInteger.valueOf(new Random()
							.nextInt(1000000)), new Date(
							System.currentTimeMillis()), new Date(
							System.currentTimeMillis()
									+ (1000L * 60 * 60 * 24 * 365 * 100)),
					new X500Name("c=foo"), publicKeyInfo);
			ContentSigner signer = new JcaContentSignerBuilder("Sha256withRSA")
					.build(pair.getPrivate());
			X509CertificateHolder certHolder = certBuilder.build(signer);
			X509Certificate cert = (new JcaX509CertificateConverter())
					.getCertificate(certHolder);

			// Create a KeyStore and shove the cert in there.
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);
			keyStore.setKeyEntry(CERT_ALIAS, pair.getPrivate(),
					CERT_PASSWORD.toCharArray(),
					new java.security.cert.Certificate[] { cert });
			return keyStore;
		} catch (KeyStoreException e) {
			throw new BadCodeMonkeyException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new BadCodeMonkeyException(e);
		} catch (OperatorCreationException e) {
			throw new BadCodeMonkeyException(e);
		} catch (CertificateException e) {
			throw new BadCodeMonkeyException(e);
		} catch (IOException e) {
			throw new BadCodeMonkeyException(e);
		}
	}

	/**
	 * @return the base address that Jetty will be serving from (does not
	 *         include any context paths).
	 */
	public URI getServerBaseAddress() {
		try {
			String protocol = enableSsl ? "https" : "http";
			URI baseAddress = new URI(String.format("%s://localhost:%d/",
					protocol, port));
			return baseAddress;
		} catch (URISyntaxException e) {
			throw new BadCodeMonkeyException();
		}
	}

	/**
	 * Stops Jetty, if it has been started via {@link #startServer()}.
	 */
	public synchronized void stopServer() {
		if (this.server == null)
			return;

		try {
			this.server.stop();
			this.server.join();
		} catch (Exception e) {
			/*
			 * If this fails, we're screwed. If this code was going to be used
			 * in production, it'd probably best to log the error and call
			 * System.exit(...) here.
			 */
			throw new RuntimeException("Unable to stop Jetty", e);
		}
	}

	/**
	 * Creates and starts a {@link EmbeddedServer}. Will run until forcefully
	 * stopped (e.g. <code>ctrl+c</code>).
	 * 
	 * @param args
	 *            (not used)
	 */
	public static void main(String[] args) {
		EmbeddedServer app = new EmbeddedServer();
		app.startServer();
	}
}
