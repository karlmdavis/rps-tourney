RPS Tourney Architecture Overview
=================================


## Introduction

This document provides an overview of the architecture of the RPS Tourney applications. It is not intended to be comprehensive, but is instead meant to provide a base level of understanding that can be built upon by looking through the code.


## System Architecture

The following diagram illustrates the applications' deployed system architecture:

    +------------+
    | PostgreSQL | [1]
    |  Database  |
    +-----^------+
          |
          |           +-------------+
      [3] |     [6]   |     Web     | [4]
          |   +-------> Application |
          |   |       +-------------+
          |   |
     +----v---v+
     |   Web   |
     | Service |
     +--------^+
      [2]     |
              |       +--------------+
              +------->   Console    | [5]
                [7]   | Applications |
                      +--------------+

(This diagram was created with [Asciiflow](http://www.asciidraw.com).)

Diagram callouts:

1. The DB is defined by and managed with [Liquibase](http://www.liquibase.org/).
    * DB Definition: [liquibase-change-log.xml](../rps-tourney-service-app/src/main/resources/liquibase-change-log.xml)
    * [DatabaseSchemaInitializer.java](../rps-tourney-service-app/src/main/java/com/justdavis/karl/rpstourney/service/app/jpa/DatabaseSchemaInitializer.java)
2. The web service is built from the [rps-tourney-service-app](../rps-tourney-service-app/) module.
3. The web service interacts with the database primarily via JPA-based DAOs.
    * For example: [AccountsDaoImpl.java](../rps-tourney-service-app/src/main/java/com/justdavis/karl/rpstourney/service/app/auth/AccountsDaoImpl.java) and [GamesDaoImpl.java](../rps-tourney-service-app/src/main/java/com/justdavis/karl/rpstourney/service/app/game/GamesDaoImpl.java)
4. The web application is built from the [rps-tourney-webapp](../rps-tourney-webapp/) module.
5. The console application is built from the [rps-tourney-console](../rps-tourney-console/) module.
6. The web application communicates with the web service via the [rps-tourney-service-client](../rps-tourney-service-client/) library/module.
7. Not yet implemented; the console application is currently only single-player and standalone.


## Notable Frameworks/Libraries Being Used

The following list notes some of the frameworks/libraries that are being used in this application. Please note that many of these libraries were chosen because I wanted to learn more about them, not necessarily because I believe they're the best tool for the job (I've learned that some of them definitely aren't).

1. [Spring Framework: Core](http://projects.spring.io/spring-framework/): Spring is used for dependency injection.
2. [Liquibase](http://www.liquibase.org/): The database schema is defined, populated, and upgraded by Liquibase.
3. [Hibernate](http://hibernate.org/): The JPA frontend for Hibernate is being used in the [rps-tourney-service-app](../rps-tourney-service-app/) module to manage all CRUD operations.
4. [Apache CXF](http://cxf.apache.org/): CXF provides the JAX-RS server implementation used in the [rps-tourney-service-app](../rps-tourney-service-app/) module. It also provides the JAX-RS client implementation used in the [rps-tourney-service-client](../rps-tourney-service-client/) module.
5. [Spring Framework: Web MVC](http://projects.spring.io/spring-framework/): The Spring Web MVC framework is used by the [rps-tourney-webapp](../rps-tourney-webapp/) module, along with JSP, to host the web application.
6. [Spring Security](http://projects.spring.io/spring-security): The Spring Security framework is used by the [rps-tourney-webapp](../rps-tourney-webapp/) module to handle authentication and authorization.
7. [jQuery](http://jquery.com/): jQuery is used in the web application to provide dynamic updates and other functionality.
8. [Bootstrap](http://getbootstrap.com/): Bootstrap provides the base template for the web application.
