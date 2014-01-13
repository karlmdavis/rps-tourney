Development Environment Setup
=============================


## Introduction

This document provides instructions on how to setup a development environment for the RPS Tourney projects. At the moment, it's targeted towards users of Ubuntu systems, though the instructions should be easily adaptable to other Unix environments. Windows users will have quite a bit more work in converting the instructions, though all of the tools used are cross-platform.

Each of the following sections covers the installation of a set of dependencies required for development.


## Git

TODO


## Java

TODO


## Apache Maven

TODO


## Eclipse Kepler

References:

* [Davis IT: Install Eclipse](https://justdavis.com/karl/it/davis/misc/eclipse.html)

While you can certainly use whatever editor/IDE you want to develop this project, Eclipse is the "default" choice here (per the lead developer, Karl M. Davis). Specifically, Eclipse Kepler (4.3) is required.

While Ubuntu does have a somewhat-recent version of Eclipse in its repositories, it's rarely the latest release. On Ubuntu 12.10, the Eclipse in the repositories is 3.8.

The `eclipse-kepler-sr1-install.sh` script provided with this project will download and install Eclipse. Run it as follows:

    $ cd rps-tourney.git/
    $ sudo dev/eclipse-kepler-sr1-install.sh

That's it. There should now be an **Eclipse Kepler** application launcher available.


### Eclipse Plugins/Features

References:

* [Stack Overflow: How do you automate the installation of Eclipse plugins with command line?](http://stackoverflow.com/questions/7163970/how-do-you-automate-the-installation-of-eclipse-plugins-with-command-line)
* [Eclipse: Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplersr1), see the *Detailed features list*

The above script will install the "Eclipse IDE for Java Developers Eclipse IDE for Java Developers" distribution of Eclipse, which is lacking a number of plugins used in the development of this project. The `eclipse-kepler-sr1-install-plugins.sh` script will install those plugins. Run it as follows:

    $ cd rps-tourney.git/
    $ sudo dev/eclipse-kepler-sr1-install-plugins.sh


#### Troubleshooting: JavaDoc Rendering

References:

* [Stack Overflow: Eclipse Javadoc not rendering correctly](http://stackoverflow.com/questions/14491296/eclipse-javadoc-not-rendering-correctly)

If the JavaDoc displays in Eclipse are rendering everything as plain text with the HTML stripped out, rather than rendering the HTML properly, install the `libwebkitgtk-1.0-0` package as follows and then restart Eclipse:

    $ sudo apt-get install libwebkitgtk-1.0-0

1. Download the "Eclipse IDE for Java Developers" distribution:

        $ wget http://mirrors.ibiblio.org/pub/mirrors/eclipse/technology/epp/downloads/release/kepler/SR1/eclipse-java-kepler-SR1-linux-gtk-x86_64.tar.gz

1. Unpack and "install" that to `/usr/local/`:

        $ sudo mkdir -p /usr/local/eclipse
        $ tar -xzf eclipse-java-kepler-SR1-linux-gtk-x86_64.tar.gz
        $ sudo mv eclipse/ /usr/local/eclipse/eclipse-java-kepler-SR1-linux-gtk-x86_64/
        $ sudo chmod a+W /usr/local/eclipse/eclipse-java-kepler-SR1-linux-gtk-x86_64/


### PostgreSQL

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
