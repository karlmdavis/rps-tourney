Production Environment Setup
=============================


## Introduction

This document provides instructions on how to setup the production environment for the RPS Tourney projects. As of 2014-12-29, the applications production deployments are on [eddings.justdavis.com](https://justdavis.com/karl/it/davis/servers/eddings.html).

Each of the following sections covers the installation of a set of dependencies required for development.


## Prerequisites

The following separate guides cover the required prerequisites for the production environment:

* [Eddings DNS Server](https://justdavis.com/karl/it/davis/servers/eddings/dns.html)
* [Eddings Web Server](https://justdavis.com/karl/it/davis/servers/eddings/web.html)
* [Eddings Tomcat Server](https://justdavis.com/karl/it/davis/servers/eddings/tomcat.html)


## Apache Tomcat Deployment

The application WARs themselves are hosted in an Apache Tomcat 7 server runnings on `eddings`. This Tomcat server only serves to `localhost`, so all traffic to the applications must be proxied in.

TODO: detail setup (this was done before I started this guide)


## Apache HTTPD Deployment

As mentioned above, the Java applications on `eddings` are only directly accessible via `localhost`. The Apache HTTPD server running on `eddings` is used to proxy in traffic to these applications.

The following `/etc/apache2/sites-available/rpstourney-com.conf` virtual host configuration file is used for the application:

    <VirtualHost *:443>
    	ServerName rpstourney.com
    	
    	DocumentRoot /var/apache2/rpstourney.com/www/
    	<Directory /var/apache2/rpstourney.com/www/>
    		Require all granted
    	</Directory>
    	
    	LogLevel warn
    	ErrorLog /var/apache2/rpstourney.com/logs/error_log
    	TransferLog /var/apache2/rpstourney.com/logs/access_log
    	
    	ServerAdmin webmaster@justdavis.com
    	
    	Options FollowSymLinks
    	
    	# Configure SSL for this virtual host (derived from /etc/apache2/sites-available/default-ssl)
    	SSLEngine on
    	SSLCertificateFile /etc/ssl/certs/rpstourney.com-2014-12-30.crt
    	SSLCertificateKeyFile /etc/ssl/private/rpstourney.com.key
    	SSLCertificateChainFile /etc/ssl/certs/GandiStandardSSLCA2.pem
    	<FilesMatch "\.(cgi|shtml|phtml|php)$">
    		SSLOptions +StdEnvVars
    	</FilesMatch>
    	<Directory /usr/lib/cgi-bin>
    		SSLOptions +StdEnvVars
    	</Directory>
    	BrowserMatch "MSIE [2-6]" \
    		nokeepalive ssl-unclean-shutdown \   
    		downgrade-1.0 force-response-1.0
    	# MSIE 7 and newer should be able to use keepalive
    	BrowserMatch "MSIE [17-9]" ssl-unclean-shutdown
    	
    	# Disable SSL v2 and v3 to prevent POODLE attacks.
    	SSLProtocol all -SSLv2 -SSLv3
    	
    	# Jenkins requires this when running behind a proxy. Reference: https://wiki.jenkins-ci.org/display/JENKINS/Running+Jenkins+behind+Apache
    	AllowEncodedSlashes NoDecode
    	
    	# Configure mod_proxy to be used for proxying URLs on this site to other URLs/ports on this server.
    	ProxyRequests Off
    	ProxyVia Off
    	ProxyPreserveHost On
    	<Proxy *>
    	        AddDefaultCharset off
    	        Order deny,allow
    	        Allow from all
    	</Proxy>
    	
    	# Proxy the Java web application running at http://localhost:8080/rps-tourney-service-app
    	<Location /api>
    		ProxyPass http://localhost:8080/rps-tourney-service-app
    		ProxyPassReverse http://localhost:8080/rps-tourney-service-app
    		ProxyPassReverse http://rpstourney.com/rps-tourney-service-app
    		SetEnv proxy-nokeepalive 1
    	</Location>
    	
    	# Proxy the Java web application running at http://localhost:8080/rps-tourney-webapp
    	<Location />
    		ProxyPass http://localhost:8080/rps-tourney-webapp/
    		ProxyPassReverse http://localhost:8080/rps-tourney-webapp/
    		ProxyPassReverse http://rpstourney.com/rps-tourney-webapp/
    		ProxyPassReverse https://rpstourney.com/rps-tourney-webapp/
    		SetEnv proxy-nokeepalive 1
    	</Location>
    </VirtualHost>

The following `/etc/apache2/sites-available/rpstourney-com.conf` virtual host configuration file is used to redirect everything to HTTPS:

    <VirtualHost *:80>
    	ServerName rpstourney.com
    	
    	LogLevel warn
    	ErrorLog /var/apache2/rpstourney.com/logs/error_log
    	TransferLog /var/apache2/rpstourney.com/logs/access_log
    	
    	ServerAdmin webmaster@justdavis.com
    	
    	# Redirect all HTTP (port 80) traffic for this virtual host to HTTPS.
    	# Reference: https://wiki.apache.org/httpd/RedirectSSL
    	Redirect permanent / https://rpstourney.com/
    </VirtualHost>
	

### SSL Certificate

A 3-year basic (non-wildcard) SSL was acquired for <https://rpstourney.com> from [gandi.net](https://www.gandi.net), who also happens to be the registrar for the site. The cert was acquired on 2014-12-29 and will expire on 2017-12-30.

A private key and certificate signing request were generated for this cert on `eddings`, as follows:

    $ sudo openssl req -nodes -newkey rsa:2048 -sha256 -keyout /etc/ssl/private/rpstourney.com.key -out /etc/ssl/certs/rpstourney.com-2014-12-30.csr

When prompted, the `openssl` questions were answered as follows:

* Country Name (2 letter code) [AU]: `US`
* State or Province Name (full name) [Some-State]: (blank)
* Locality Name (eg, city) []: (blank)
* Organization Name (eg, company) [Internet Widgits Pty Ltd]: (blank)
* Organizational Unit Name (eg, section) []: (blank)
* Common Name (e.g. server FQDN or YOUR name) []: `rpstourney.com`
* Email Address []: (blank)
* A challenge password []: (randomly-generated and saved in `/afs/justdavis.com/user/karl/id/passwords-karl.kdbx`)
* An optional company name []: (blank)

The CSR was then uploaded via Gandi's web interface, during the SSL certificate purchase process. Please note that, per [gandi.net: Generating Your CSR](http://wiki.gandi.net/en/ssl/csr#sha-2_certificate_request), the issued certificate will also cover the `www.rpstourney.com` subdomain.

Once the certificate was issued, it was saved as `/etc/ssl/certs/rpstourney.com-2014-12-30.crt` on `eddings`. Gandi's intermediate CA certificate was also saved on `eddings` as `/etc/ssl/certs/GandiStandardSSLCA2.pem`. Please note that the "Comodo Cross-Signed Certificate" was manually appended to `GandiStandardSSLCA2.pem`, per the instructions at [Gandi Wiki: Retrieving the Gandi Intermediate Certificate](http://wiki.gandi.net/en/ssl/intermediate).
