Development Environment Setup
=============================


## Introduction

This document provides instructions on how to setup a development environment for the RPS Tourney projects. At the moment, it's targeted towards users of Ubuntu systems, though the instructions should be easily adaptable to other Unix environments. Windows users will have quite a bit more work in converting the instructions, though all of the tools used are available cross-platform.

Each of the following sections covers the installation of one of the dependencies required for development.


## Git

The source code is stored in [Git](https://git-scm.com/), and publicly hosted on GitHub here: <https://github.com/karlmdavis/rps-tourney>. On Ubuntu, Git can be installed, as follows:

    $ sudo apt-get install git

Once Git is installed, clone the [rps-tourney](https://github.com/karlmdavis/rps-tourney) repository from GitHub, as follows:

    $ git clone git@github.com:karlmdavis/rps-tourney.git ~/workspaces/rps-tourney/rps-tourney.git

In addition, you'll probably want to grab the [jessentials](https://github.com/karlmdavis/jessentials) code, too, which is a dependency of this project:

    $ git clone git@github.com:karlmdavis/jessentials.git ~/workspaces/rps-tourney/jessentials.git


## Recommended: Use the [devenv.py](./devenv.py) Script to Install Dependencies

This project includes a simple Pyhton [devenv.py](./devenv.py) script that installs the following dependencies:

* Oracle's Java 8 JDK

Run that script as follows (after cloning the `rps-tourney` repo from Git):

    $ cd rps-tourney.git/
    $ ./dev/devenv-install.py

That's it. There should now be an **Eclipse Luna** application launcher available in the Ubuntu dash.


## Java

The Java 8 SDK is required to build the code for this project. Either the Oracle or OpenJDK distribution should be fine.

On Ubuntu Trusty, this should probably be installed via the `webupd8` PPA, as described in the following article: [Install Oracle Java 8 In Ubuntu Or Linux Mint Via PPA Repository [JDK8]](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html).

If you'd prefer not to use that PPA (though it does seem to be fairly well-supported), it's recommended that you just download the JDK from Oracle and unzip it into `~/workspaces/tools/` directory—development with Eclipse doesn't require it to be *installed* on your system, just present.


### SSL Certificates: Gandi Standard

If the <https://justdavis.com/nexus> repository is being used, the Gandi CA certificates that it requires will need to be added to the Java/system truststore. The following commands should accomplish that for OpenJDK:

    $ sudo curl http://crt.gandi.net/GandiStandardSSLCA2.crt -o /usr/local/share/ca-certificates/GandiStandardSSLCA2.crt
    $ sudo update-ca-certificates


## Apache Maven

[Apache Maven](https://maven.apache.org/) is used to build, test, and release this project.

This dependency can be installed via the [devenv.py](./devenv.py) script.


## Eclipse

References:

* [Davis IT: Install Eclipse](https://justdavis.com/karl/it/davis/misc/eclipse.html)

While you can certainly use whatever editor/IDE you want to develop this project, Eclipse is the "default" choice here (per the lead developer, Karl M. Davis). Specifically, Eclipse JavaEE Luna is required.

While Ubuntu does have a somewhat-recent version of Eclipse in its repositories, it's rarely the latest release. On Ubuntu 12.10, the Eclipse in the repositories is 3.8, which is **very** out of date.

This dependency can be installed via the [devenv.py](./devenv.py) script. The script will also install the various Eclipse plugins that are required.


### Troubleshooting: JavaDoc Rendering

References:

* [Stack Overflow: Eclipse Javadoc not rendering correctly](http://stackoverflow.com/questions/14491296/eclipse-javadoc-not-rendering-correctly)

If the JavaDoc displays in Eclipse are rendering everything as plain text with the HTML stripped out, rather than rendering the HTML properly, install the `libwebkitgtk-1.0-0` package as follows and then restart Eclipse:

    $ sudo apt-get install libwebkitgtk-1.0-0


## PostgreSQL

References:

* [Ubuntu Wiki: PostgreSQL](https://help.ubuntu.com/community/PostgreSQL)

This application makes use of a database for its persistent data store. At this time, [PostgreSQL](http://www.postgresql.org/) is the primary database used in production and development (though in-memory [HSQL](http://hsqldb.org/) databases are also used for many of the automated tests). Accordingly, a PostgreSQL database server needs to be available for use during development.

If needed, a PostgreSQL server can be installed as follows:

    $ sudo apt-get install postgresql
    
After installation, run the following command to create a user/role in PostgreSQL tied to your local Ubuntu user account:

    $ sudo -u postgres createuser --superuser yourusername --pwprompt

The projects' integration tests are careful not to assume that every developer will have their database servers available at the exact same URL or with the exact same accounts. Instead, [Maven's resource filtering](http://maven.apache.org/plugins/maven-resources-plugin/examples/filter.html) is used to supply these parameters during the builds. For instance, the `jessentials-misc/src/test/resources/datasource-provisioning-targets.xml` file contains properties that are resolved by the Maven build.

Each developer must edit their `~/.m2/settings.xml` file to contain the necessary properties. Here's a sample snippet:

    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" 
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
      <profiles>
        <profile>
          <!-- This profile sets the properties needed for integration tests that use the 
         com.justdavis.karl.misc.datasources.provisioners API. -->
          <id>justdavis-integration-tests</id>
          <properties>
            <com.justdavis.karl.datasources.provisioner.postgresql.server.url>jdbc:postgresql:postgres</com.justdavis.karl.datasources.provisioner.postgresql.server.url>
            <com.justdavis.karl.datasources.provisioner.postgresql.server.user>karl</com.justdavis.karl.datasources.provisioner.postgresql.server.user>
            <com.justdavis.karl.datasources.provisioner.postgresql.server.password>secretpw</com.justdavis.karl.datasources.provisioner.postgresql.server.password>
          </properties>
        </profile>
      </profiles>
      <activeProfiles>
        <activeProfile>justdavis-integration-tests</activeProfile>
      </activeProfiles>
    </settings>
    
The database schema will be created and populated automatically when the application is first started. See the `/rps-tourney-service-app/com.justdavis.karl.rpstourney.service.app.jpa.DatabaseSchemaInitializer` class for details.


## Tomcat (via Eclipse WTP)

References:

* [Eclipse WTP Tomcat FAQ](https://wiki.eclipse.org/WTP_Tomcat_FAQ)
* [Stack Overflow: Where can I view Tomcat log files in Eclipse?](http://stackoverflow.com/a/7354545/1851299)

During development, it's recommended that the web applications be run in Apache Tomcat via Eclipse's WTP plugin. This is recommended as it's both easy to use and also similar to the production deployment (which also uses Tomcat). The `devenv-install.py` script will create a standalone Tomcat installation for this purpose.

Within Eclipse, the Tomcat instance can be setup as follows:

1. Go to **Window > Show View > Other...**, select **Server > Servers** and click **OK**.
1. Right-click a blank spot in the view, and select **New > Server**.
1. On the *Define a New Server* screen:
    1. Select the **Tomcat v7.0 Server** node.
    1. For *Server runtime environment*, click **Add...**. On the *Tomcat Server* dialog this opens:
        1. Select your `~/workspaces/tools/apache-tomcat-7.0.57` directory as the *Tomcat installation directory*.
            * This was created by the [devenv-install.py](./devenv-install.py) script.
        1. Click **Finish**.
    1. Click **Next**.
1. On the **Add and Remove** screen:
    1. Click **Add All >>**.
    1. Click **Finish**.

Once setup and available, the following should be done to ensure that Tomcat is configured correctly for running the RPS applications:

1. Copy the sample logging properties file from `/rps-tourney-parent/dev/devenv-tomcat-logging.properties` to `/Servers/apache-tomcat-7.0.57 at localhost-config` directory in Eclipse's *Package Explorer*. Right-click the Tomcat server and select **Publish** to apply this configuration change.
2. Open the Tomcat server's run configuration in Eclipse, and configure it as follows:
    1. Add the following VM arguments, each on a separate line:
        * `-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager`
        * `-Djava.util.logging.config.file=${resource_loc:/rps-tourney-parent/dev/tomcat-logging.properties}`
        * `-Dlogback.configurationFile=${resource_loc:/rps-tourney-parent/dev/tomcat-logback.xml}`
        * `-Dsun.io.serialization.extendedDebugInfo=true`
        * `-Drps.service.config.path=${resource_loc:/rps-tourney-parent/rps-tourney-webapp/src/test/resources/rps-service-config-dev.xml}`
        * `-Drps.webapp.config.path=${resource_loc:/rps-tourney-parent/rps-tourney-webapp/src/test/resources/rps-webapp-config-dev.xml}`
    2. Set the working directory to: `${workspace_loc}/.metadata/.plugins/org.eclipse.wst.server.core/tmp0`
