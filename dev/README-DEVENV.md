Development Environment Setup
=============================


## Introduction

This document provides instructions on how to setup a development environment for the RPS Tourney projects. At the moment, it's targeted towards users of Ubuntu systems, though the instructions should be easily adaptable to other Unix environments. Windows users will have quite a bit more work in converting the instructions, though all of the tools used are available cross-platform.


## Getting the Source

The source code is stored in [Git](https://git-scm.com/), and publicly hosted on GitHub here: <https://github.com/karlmdavis/rps-tourney>. On Ubuntu, Git can be installed, as follows:

    $ sudo apt-get install git

Once Git is installed, clone the [rps-tourney](https://github.com/karlmdavis/rps-tourney) repository from GitHub, as follows:

    $ git clone git@github.com:karlmdavis/rps-tourney.git ~/workspaces/rps-tourney/rps-tourney.git

In addition, you'll probably want to grab the [jessentials](https://github.com/karlmdavis/jessentials) code, too, which is a dependency of this project:

    $ git clone git@github.com:karlmdavis/jessentials.git ~/workspaces/rps-tourney/jessentials.git


## Installing the Development Tools

### SDKMAN!: Install Manager for Java, Maven, and Tomcat

It is recommended that folks install [SDKMAN!](https://sdkman.io) and use it to install and manage the required development tools that it supports: Java, Maven, and Tomcat.

You can see the versions of these dependencies currently in use by inspecting this project's `[.sdkmanrc](./sdkmanrc)` file.

```shell-session
$ curl -s "https://get.sdkman.io" | bash
$ source "/home/karl/.sdkman/bin/sdkman-init.sh"
$ sdk env install
```

Then, the tools can be activated (given preference in your `PATH`) by running this command in an active terminal session:

```shell-session
$ sdk env
```

Or, alternatively, you can set `sdkman_auto_env=true` to automatically activate the required tools when you `cd` into the project's directory.


### Eclipse

The "Java EE" edition of Eclipse, version 2023-09, should be installed and used for development of this project.

Obviously, other IDEs _could_ be used, but that specific version of Eclipse is known to work.


#### Troubleshooting: JavaDoc Rendering

References:

* [Stack Overflow: Eclipse Javadoc not rendering correctly](http://stackoverflow.com/questions/14491296/eclipse-javadoc-not-rendering-correctly)

If the JavaDoc displays in Eclipse are rendering everything as plain text with the HTML stripped out, rather than rendering the HTML properly, install the `libwebkitgtk-1.0-0` package as follows and then restart Eclipse:

    $ sudo apt-get install libwebkitgtk-1.0-0


### PostgreSQL

References:

* [How To Install and Use Docker on Ubuntu 22.04](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-22-04)

This application makes use of a database for its persistent data store. At this time, [PostgreSQL](http://www.postgresql.org/) is the primary database used in production and development (though in-memory [HSQL](http://hsqldb.org/) databases are also used for many of the automated tests). Accordingly, a PostgreSQL database server needs to be available for use during development.

The simplest way to acquire and launch the necessary PostgreSQL server is via Docker. On Ubuntu 22.04, you should follow the instructions here to install and configure an up to date version of Docker: [How To Install and Use Docker on Ubuntu 22.04](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-22-04).

Once Docker is installed, a DB server for this project can be launched as follows:

```shell-session
$ docker run --name postgresql-rps -e POSTGRES_PASSWORD=rockpaperscissors -d -p 5432:5432 postgres
```

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
            <com.justdavis.karl.datasources.provisioner.postgresql.server.user>postgres</com.justdavis.karl.datasources.provisioner.postgresql.server.user>
            <com.justdavis.karl.datasources.provisioner.postgresql.server.password>rockpaperscissors</com.justdavis.karl.datasources.provisioner.postgresql.server.password>
          </properties>
        </profile>
      </profiles>
      <activeProfiles>
        <activeProfile>justdavis-integration-tests</activeProfile>
      </activeProfiles>
    </settings>
    
The database schema will be created and populated automatically when the application is first started. See the `/rps-tourney-service-app/com.justdavis.karl.rpstourney.service.app.jpa.DatabaseSchemaInitializer` class for details.


### Tomcat (via Eclipse WTP)

References:

* [Eclipse WTP Tomcat FAQ](https://wiki.eclipse.org/WTP_Tomcat_FAQ)
* [Stack Overflow: Where can I view Tomcat log files in Eclipse?](http://stackoverflow.com/a/7354545/1851299)

During development, it's recommended that the web applications be run in Apache Tomcat via Eclipse's WTP plugin. This is recommended as it's both easy to use and also similar to the production deployment (which also uses Tomcat). The SDKMAN! instructions above should have installed a Tomcat server for this purpose.

Within Eclipse, the Tomcat instance can be setup as follows:

1. Go to **Window > Show View > Other...**, select **Server > Servers** and click **OK**.
1. Right-click a blank spot in the view, and select **New > Server**.
1. On the *Define a New Server* screen:
    1. Select the **Tomcat v8.5 Server** node.
        * Note: If *Server name* and *Server runtime environment* aren't yet visible here, click **Next** and adjust the instructions below a bit. (Those fields aren't available until at least one runtime has been configured for the server type.)
    1. For *Server name*, enter "`apache-tomcat-8.5.94 at localhost`".
    1. For *Server runtime environment*, click **Add...**. On the *Tomcat Server* dialog this opens:
        1. For *Name*, enter "`apache-tomcat-8.5.94`".
        1. Select the directory output by running `sdk home tomcat 8.5.94` from a terminal as the *Tomcat installation directory*.
            * This was created by SDKMAN!.
        1. Click **Finish**.
    1. Click **Next**.
1. On the **Add and Remove** screen:
    1. Click **Finish**.

Once setup and available, the following should be done to ensure that Tomcat is configured correctly for running the RPS applications:

1. Copy the sample logging properties file from `/rps-tourney-parent/dev/tomcat-logging.properties` to `/Servers/apache-tomcat-8.5.94 at localhost-config` directory in Eclipse's *Package Explorer*. Right-click the Tomcat server and select **Publish** to apply this configuration change.
2. Open the Tomcat server's run configuration in Eclipse, and configure it as follows:
    1. Add the following VM arguments, each on a separate line:
        ```
        -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
        -Djava.util.logging.config.file=${resource_loc:/rps-tourney-parent/dev/tomcat-logging.properties}
        -Dlogback.configurationFile=${resource_loc:/rps-tourney-parent/dev/tomcat-logback.xml}
        -Dsun.io.serialization.extendedDebugInfo=true
        -Drps.service.config.path=${resource_loc:/rps-tourney-parent/rps-tourney-webapp/src/test/resources/rps-service-config-dev.xml}
        -Drps.webapp.config.path=${resource_loc:/rps-tourney-parent/rps-tourney-webapp/src/test/resources/rps-webapp-config-dev.xml}
        ```
    2. Set the working directory to: `${workspace_loc}/.metadata/.plugins/org.eclipse.wst.server.core/tmp0`
3. Configure the HTTP port that Tomcat will use:
    1. Switch to the *Servers* view in Eclipse.
    2. Right-click **apache-tomcat-8.5.94 at localhost**, and select **Open**.
    3. Set *Ports > HTTP/1.1* to `9093`, then click **Save** and close the editor.


## Working with the Projects in Eclipse

Once all of the required tools are installed, the projects should be imported into Eclipse, as follows:

1. Start Eclipse and create a new Eclipse workspace at `~/workspaces/rps-tourney/`.
1. Import the projects into Eclipse:
    1. Select **File > Import...**.
    1. Select the **Maven > Existing Maven Projects** node, then click **Next**.
    1. Select the `~/workspaces/rps-tourney/` directory as the *Root Directory*.
    1. Select all of the `rps-tourney-*` projects via **Select All**, then click **Finish**.

Configure the `rps-tourney-service-app` and `rps-tourney-webapp` projects to run in Tomcat (via Eclipse WTP), as follows:

1. Switch to the *Servers* view in Eclipse.
1. Right-click **apache-tomcat-8.5.94 at localhost**, and select **Add and Remove...**.
1. Click **Add All**, then click **Finish**.

Once configured, Tomcat can be run, as follows:

1. Switch to the *Servers* view in Eclipse.
1. Right-click **apache-tomcat-8.5.94 at localhost**, and select **Start**.
1. Switch to the **Console** view, and wait for the applications to finish launching.
1. Access the web application at <http://localhost:9093/rps-tourney-webapp/>.


### Troubleshooting: Missing JS and CSS in Web App

Using wr04j with Eclipse technically requires the [m2e-wr04j](https://github.com/jbosstools/m2e-wro4j) plugin to be installed in Eclipse. Unfortunately, the latest version of Eclipse doesn't support running this plugin with a Java 8 JDK, which the RPS project currently requires.

Without this plugin installed, the webapp build published to Tomcat will not include any of the CSS or JS compiled by wr04j. A workaround is available:

1. Right-click the `rps-tourney-webapp` project in Eclipse and select **Properties**.
1. Select the **Deployment Assembly** section.
1. Click **Add...**.
1. Select **Folder** and then click **Next >**.
1. Select the `target/rps-tourney-webapp-2.0.0-SNAPSHOT` folder and click **Finish**.
1. Click **Apply and Close**.
1. Clean and Republish the project in Tomcat.

This workaround will need to be performed every time the Eclipse project configuration is updated by Maven.
