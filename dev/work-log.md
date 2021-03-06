Work Log
========
*Tracking Progress on the RPS Tourney Project*


## Introduction

This log just tracks my daily progress on the project. I'm hopeful that this will be a better mechanism for keeping myself motivated than trying to ensure daily commits, which ends up causing odd work-in-progress commits. That's bad practice.

This file should never be committed along with other files; it should always be updated by itself. This will prevent any weird merge problems.


## Daily Log

### 2013-11-04, Monday

* Created this work log file.
* Created the [2.0 milestone](https://github.com/karlmdavis/rps-tourney/issues?milestone=2&state=open) and the initial set of issues for it.
    * 30 minutes work.
* Added justdavis.com-nexus as a mirror of central in settings.xml.
    * 5 minutes work.
* Researched Java REST frameworks. Played with Jersey.
    * 1 hr work.

### 2013-11-05, Tuesday

* Researched Java REST frameworks. Played with Apache CXF. Mostly fiddled with embedding Jetty.
    * 1.5 hr work.

### 2013-11-06, Wednesday

* Tried to get Eclipse's WTP/JavaEE support installed.
    * 1.0 hr work.
* Installed Eclipse Kepler, installed WTP and other plugins, got m2e working correctly for JAX-RS project.
    * 1.5 hr work. (after midnight)

### 2013-11-07, Thursday

* Got a basic "Hello World" and echo service running. Still needs to be cleaned up & committed.
    * 2.0 hr work.
* Spent some time cleaning things up and starting to draft rps-tourney-webservice/dev/README-DEV.md.
    * 1.0 hr work. (after midnight)

### 2013-11-08, Friday

* Documented references in rps-tourney-webservice/dev/README-DEV.md.
    * 0.25 hr work.
* Cleaned up the "Hello World" web service. Added in tests for it.
    * 1.5 hr work.

### 2013-11-10, Sunday

* Read up on cookies.
    * 0.75 hr work.
* Read up on Apache CXF 3.0 and JAX-RS 2.0.
    * 0.25 hr work.
* Started creating a login service for guests.
    * 0.25 hr work.

### 2013-11-11, Monday

* Worked on the guest login service a bit.
    * 0.25 hr work.

### 2013-11-12, Tuesday

* Worked on the authentication system: documented, diagrammed, and whiteboarded. Next steps: add GUID library, flesh out stub classes, and then implement persistence.
    * 1.0 hr work.

### 2013-11-13, Wednesday

* Added GUID library (stole from "Perfect Note" project).
    * 10 min work.
* Worked on fleshing out stub classes. Still more to do there.
    * 0.75 hr work.

### 2013-11-14, Thursday

* Removed the GUID library and implemented all of GuestAuthService (not tested).
    * 0.75 hr work.
* Started writing tests. Didn't get very far.
    * 0.25 hr work.

### 2013-11-15, Friday

* Created tests for GuestLoginIdentity and GuestAuthService. Took forever to debug SocketException.
    * 2.5 hr work.

### 2013-11-16, Saturday

* Got XML namespaces working correctly in JAX-B and tests.
    * 1.5 hr work.
* Cleaned things up and commited the guest logins support.
    * 0.5 hr work.
* Reworked guest logins, added AccountService. Ended up on a huge tangent here where I was getting a cookie error that I thought was caused by the ITs not using SSL, so I went and added SSL support to everything. Turns out that wasn't the problem. Oops. I cleaned up the SSL-enabling code anyways, though, and just set that mode to disabled for now.
    * 8.5 hr work.
* Started on the GameLoginService, which allows login via email address and password.
    * 0.5 hr work.

### 2013-11-17, Sunday

* Fixed borked commit from last night. 
    * 5 min work.
* Added SonarQube rules for TODOs, FIXMEs, and XXXs. Only applies to non-test code. 
    * 0.5 hr work.
* Implemented GameAuthService. No tests yet.
    * 1.0 hr work.
* Added tests for GameAuthService. Also created InternetAddressReader.
    * 2.0 hr work.

### 2013-11-18, Monday

* 1.25h: Read about JAX-RS security. 
    * Read about JAAS, which doesn't sound like a good fit here. It's more for applications that want to allow their security to be customizable at deployment time. Seems like overkill for this.
    * Read about JAX-RS filters, which are likely the best choice. I can use them to populate a SecurityContext, and possibly also to drive the javax.annotation.security annotations, which'd be slick.

### 2013-11-19, Tuesday

* 0.5h: Read about XML Signatures, SAML, and OAuth 2 support in CXF. 
    * None of these really apply here. They're all more for enterprise systems that need to authenticate between themselves.

### 2013-11-20, Wednesday

* 0.75h: Looked at making use of DynamicFeature.
    * It looks like CXF's copy of this from the JAX-RS milestone release is significantly out of date.
    * Two choices:
        1. Try and figure out how to make the old API work anyways.
        1. Switch to a SNAPSHOT 3.0 release of CXF.
    * I think I'm better off just dealing with the old API.
        * No way to tell how close they are to releasing 3.0.
        * Pre-releases tend to cause more problems than they solve.

### 2013-11-21, Thursday

* 0.5h: Figured out how to use DynamicFeature.

### 2013-11-22, Friday

* 1.0h: Implemented AuthorizationFilter.
* 0.25h: Learned about @BindingPriority, and stubbed out AuthenticationFilter.

### 2013-11-23, Saturday

* 4.0h: Implemented AuthenticationFilter.
    * Started refactoring other services to leverage it.
    * Working through updating & fixing tests.
    * (Kind of guessing on time here, was off & on all day.)
* 1.0h: Worked on this for another hour or so (after midnight).
    * Discovered that CXF's ContainerRequestContextImpl.getSecurityContext() is broken beyond use.

### 2013-11-24, Sunday

* 0.5h: Looking into ContainerRequestContextImpl.getSecurityContext() bug.
    * Found a commit on trunk (3.0-SNAPSHOT) that fixes it: <https://fisheye6.atlassian.com/changelog/cxf?cs=1482410>
* 0.5h: Read up on Spring Security and Apache Shiro.
    * Spring Security seems to be tightly coupled to the rest of Spring, and with it itself. Don't think it'd be easy to integrate without dragging along the rest of Spring.
    * Apache Shiro doesn't look too bad. Not sure, but it might make it difficult to operate statelessly, as it seems to be big on session management.
* 4.0h: Hacked around the bug in CXF's ContainerRequestContextImpl.getSecurityContext().
    * Created unit tests for AuthenticationFilter and AuthorizationFilter.
    * (Kind of guessing on time here, was off & on all day.)
* 1.0h: Refactored/cleaned up the other services.
    * Leveraged AuthenticationFilter and AuthorizationFilter.
    * Tried to make the webservice methods' API less JAX-RS-specific.
* 0.25h: [commit 5386d1556e: Issue #13: Created the EmbeddedServerResource JUnit @ClassRule.](https://github.com/karlmdavis/rps-tourney/commit/5386d1556e56fbd168031a9862f836aee1a5ef1e)
* 0.25h: [commit 7851b057e9: Issue #13: Refactored HelloWorldService.](https://github.com/karlmdavis/rps-tourney/commit/7851b057e991306bf705e50339a9f34e587a603d)

### 2013-11-25, Monday

* 0.5h: Added Hibernate as a dependency.
    * Had to add the JBoss repo to Nexus, as the latest releases of Hibernate aren't in Central.
    * Found the [Hibernate Getting Started Guide](http://docs.jboss.org/hibernate/orm/4.2/quickstart/en-US/html/) for the previous release.
    * Looks like Hibernate/JPA also allow for bytecode enhancement, so I may still need to configure some Maven plugins for that.
* 0.25h: Started reading through the [Hibernate Getting Started Guide](http://docs.jboss.org/hibernate/orm/4.2/quickstart/en-US/html/).
    * Read as far as Chapter 4, but didn't quite get the code running yet (need to get `persistence.xml` file correct).

### 2013-11-26, Tuesday

* 1.0h: Tried to figure out how to supply a DataSource to Hibernate (via JPA) programmatically.
    * Looks like I'll have to create a DataSource instance, stick it somewhere in JNDI, and use the `non-jta-data-source` property in `persistence.xml`.

### 2013-11-27, Wednesday

* 0.5h: Read through Oracle's JNDI tutorial.
    * Need to figure out how to stick a DataSource in there, for JPA to pick it up.

### 2013-11-28, Thursday

* 0.5h: JNDI is stupid. It's looking like my best option is to create a class that can produce both DataSources and the `javax.persistence.jdbc.*` properties for JPA. Obnoxious.

### 2013-11-29, Friday

* 0.5h: Got a simple JPA example running in Scratch.java.

### 2013-11-30, Saturday

* 1.0h: Did a lot of random reading about JPA DAOs and the JPA MetaModel.
    * Still haven't figured out MetaModels, but they sound wonderful.
* 0.5h: Enabled org.hibernate:hibernate-jpamodelgen annotation processing.
    * Need to document the installation of m2e-apt from the m2e marketplace (<https://community.jboss.org/en/tools/blog/2012/05/20/annotation-processing-support-in-m2e-or-m2e-apt-100-is-out>).

### 2013-12-01, Sunday

* 0.1h: Documented the installation of m2e-apt in `eclipse-kepler-sr1-install-plugins.sh`.
* 0.75h: Read about detached objects in JPA, J2EE vs. Tomcat, and the [JPA Lifecycle](http://java.boot.by/scbcd5-guide/ch06.html).
* 4.0h: Created an initial/stub version of `GameAppServiceInit`, which will more-or-less replace GameApplication with Spring-injected resources.
    * Took a ridiculous amount of reading. Came across the following useful resources:
        * [Spring framework without XML... At all!](http://nurkiewicz.blogspot.com/2011/01/spring-framework-without-xml-at-all.html)
        * [REST with JAX-RS: Part 1 - Spring Java Config](http://www.halyph.com/2013/10/rest-with-jax-rs-part-1-spring-java.html)
        * [Bug 404176 - Jetty's AnnotationConfiguration class does not scan non-jar resources on the classpath (except under WEB-INF/classes)](https://bugs.eclipse.org/bugs/show_bug.cgi?id=404176)
        * [embed Jetty + Spring + Apache CXF](http://www.sql.ru/forum/1061100/embed-jetty-spring-apache-cxf)
        * [Spring 3.1 WebApplicationInitializer & Embedded Jetty 8 AnnotationConfiguration](http://t3572.codeinpro.us/q/51501dabe8432c042614285b)

### 2013-12-02, Monday

* 0.25h: Started to flesh out `GameAppServiceInit` to replace the config that was being handled by `GameApplication`.
    * Didn't get very far. Don't have enough documentation on how to use `JAXRSServerFactoryBean`. Should probably post a StackOverflow question on it.

### 2013-12-03, Tuesday

* 1.0h: Worked on fleshing out `GameAppServiceInit` some more.
    * Got much farther. Currently hung up on how to register DynamicFeatures.

### 2013-12-04, Wednesday

(didn't work; too distracted by reading)

### 2013-12-05, Thursday

* 0.5h: Finished fleshing out `GameAppServiceInit`.

### 2013-12-06, Friday

* 0.25h: Cleaned up the switch-to-Spring-code and committed it.
* 0.5h: Tried to debug intermittent test failures.
    * Several failed on Jenkins, and in Eclipse I can make `GuestAuthServiceIT.createLogin()` fail pretty often.
    * Adding a sleep(5s) to the Jetty startup didn't help.

### 2013-12-07, Saturday

* 0.25h: Solved the intermittent test failures.
    * Turns out to have been a bean ordering/dependency issue.
* 0.25h: Fixed the Sonar "Unused Imports" rule. It now checks JavaDoc for references, too.
    * There was actually a setting for this in the rule. Just had to enable it.
* 0.5h: Started creating the `IDataSourceConnector` and `IConfigLoader` code.

### 2013-12-08, Sunday

* 0.5h: Worked on the `IDataSourceConnector` and `IConfigLoader` code some more.
    * Fleshed out the comments, got (most of?) the method stubs in place.

### 2013-12-09, Monday

(didn't work; too burned out by day job)

### 2013-12-10, Tuesday

* 0.5h: Implemented `HsqlConnector`. Still need to write tests for it.

### 2013-12-11, Wednesday

* 0.25h: Wrote unit tests for `HsqlConnector`.
* 1.0h: Worked on setting up the application's config mechanisms.
    * Need to flesh out `GameConfig`.
    * Need to figure out how to use Spring in tests.
    * Need to flesh out `JpaSpringConfig` such that `EntityManager` instances can be injected.
    * Then, need to retrofit the existing web services to use JPA.

### 2013-12-12, Thursday

* 1.0h: Wrote `GameConfigTest`'s JAX-B tests (and actually got JAX-B working for it).
    * Turns out, that @XmlElementRef makes things much easier.

### 2013-12-13, Friday

(didn't work; too burned out by day job)

### 2013-12-14, Saturday

* 1.25h: Worked on `XmlConfigLoader`.

### 2013-12-15, Sunday

* 0.75h: Finished `XmlConfigLoader` and its unit tests.
* 0.75h: Worked on the Spring configuration, and started trying to mock it a bit for the ITs.
* 1.5h: Figured out how to get Spring's component scanning and mocking/overriding for ITs working.
* 3.0h: Worked on getting Spring injecting JAX-RS resources. Surprisingly painful.
    * Got that fixed, but now having trouble injecting @Context objects along with Spring components.
        * I think I'll end up having to inject @Context instances via method injection. Bother.

### 2013-12-16, Monday

* 1.0h: Worked on injecting @Context objects along with Spring components.
    * Tried injecting @Context instances via method injection. Didn't work.
    * Seems to be trying to instantiate the bean at startup, before a request has been made, even though the bean is request scoped.
    * Think I need to go the "minimum reproducible testcase" route and post a plea for help on Stack Overflow.
    * Might also give up on the non-XML approach.
* 0.5h: Created the `rps-tourney-cxf-sandbox` project and got it mostly running.
    * Still need to try out Spring Context and Component injection in it.

### 2013-12-17, Tuesday

* 0.5h: Got Spring Context and Component injection for request-scoped beans working in `rps-tourney-cxf-sandbox`.
    * Turns out, I'd missed the mention of `<jaxrs:serviceFactories/>` on [CXF: JAX-RS : Services Configuration](http://cxf.apache.org/docs/jaxrs-services-configuration.html#JAXRSServicesConfiguration-ConfiguringJAXRSservicesincontainerwithSpringconfigurationfile.).

### 2013-12-18, Wednesday

* 0.5h: Worked on translating my success in `rps-tourney-cxf-sandbox` back to the "real" project.
    * Got `AccountServiceIT` passing again.
    * Still need to ensure that constructor injection for non-Context objects is working.
    * Still need to retrofit the rest of the resources that way.

### 2013-12-19, Thursday

* 0.5h: Worked on getting injection of `EntityManager` instances to work.
    * Gah, turns out that JPA doesn't support constructor injection at all. Need to use the `@PersistenceUnit` or `@PersistenceContext` annotations.

### 2013-12-20, Friday

* 0.5h: Still working on JPA injection.
    * FFS, it looks like Spring doesn't support use of any injected `@Component`s: EMFs are initialized in an earlier phase than autowiring.
    * I'm not sure, but I may have to go with Spring's configuration data loading mechanism (whatever that is).

### 2013-12-21, Saturday

* 0.5h: Got JPA injection working.
    * In @Configuration @Bean bean methods, you have to use method parameter injection of any resources. Otherwise, Spring doesn't seem to be smart enough to follow the dependency chain correctly.

### 2013-12-22, Sunday

* 2.0h: Started adding in actual persistence to the web service methods.
    * Turned into a bit of a mess. Probably will need to go with DAOs to cut down on the code noise.

### 2013-12-23, Monday

* 0.25h: Read some more about Spring's magic JpaRepository implementations.
    * Not really impressed with the idea. Too much magic. Also: doesn't support detachment.

### 2013-12-24, Tuesday

* 0.25h: Fixed `AccountTest`, added a couple of method defs to `IAccountsDao`, and some other minor work.

### 2013-12-25, Wednesday

* 0.5h: Moved `GameAuthService` to JPA, at least to the point where things look right and compile.
    * Left the DAOs unimplemented and the tests are still not compiling.

### 2013-12-26, Thursday

* 1.5h: Spent some time reading about and enabling Spring transactions.
    * Probably isn't working right, as I'll need the ITs running to test that out.
* 1.0h: Fixed up a number of the unit tests.

### 2013-12-27, Friday

(mostly spent my free time reading)

* 0.05h: Fixed another unit test: `AuthenticationFilterTest`.

### 2013-12-28, Saturday

* 1.75h: Spent time trying to debug the `AccountServicesIT` errors.
    * It's really a problem with everything, though. Spring is unable to inject @Context AccountSecurityContext instances.
    * Haven't figured out the solution, but it doesn't look it's even trying to run the Authentication filter at all.
    * From what I could see from the CXF code, even if that was running, I still might be having the same problem. Not 100% on that, though.
    * Last thing I tried was bumping to the CXF 3.0 milestone. That's caused some compile errors that I haven't had a chance to sort out, yet.

### 2013-12-29, Sunday

(spent the day visiting family in PA)

* 0.05h: Fixed the `src/main/java` compile errors caused by the CXF 3.0 switch.
    * Haven't looked at the `src/test/java` compile problems it caused, though.

### 2013-12-30, Monday

* 1.25h: Made progress on getting `AccountServicesIT` to pass, but still not there.
    * Fixed the `src/test/java` compile errors.
    * Got `AuthenticationFilter` running again.
        * Looks like I'll have to inject `@Context SecurityContext` instances, rather than `@Context AccountSecurityContext` instances. Not 100% on that, but seems likely.
    * Implemented a bit more of the JPA stuff.
        * Next step is probably to write tests for the DAOs. Looks like they may not be working as expected.

### 2013-12-31, Tuesday

* 2.5h: Got `AccountServicesIT` to pass.
    * Learned that Hibernate doesn't intelligently merge annotations and `orm.xml`: any attribute specified in `orm.xml` will completely replace the annotation-specified properties.
    * Had to fix the JPA dialect/vendor specified in Spring, from H2 to HSQL.
        * Will need to make that dynamic, somehow.
    * Modified the default fetch plan for `Account` instances to pull in everything.

### 2014-01-01, Wednesday

(spent most of the day reading)

* 0.05h: Fixed `AuthorizationFilterTest`.

### 2014-01-02, Thursday

(spent most of the day reading)

### 2014-01-03, Friday

(spent most of the day reading)

### 2014-01-04, Saturday

* 0.25h: Fixed `GuestLoginIdentityTest` and `GuestAuthServiceTest`.

### 2014-01-05, Sunday

* 1.0h: Tried to figure out how to get Spring DI working in JUnit tests with embedded Jetty.
    * The Jetty @Rule being used conflicts with Spring Test's `SpringJUnit4ClassRunner`, as they both end up trying to start a server.
* 0.5h: More time spent reading about the above problem.
* 1.25h: Still more time working on the above problem.
    * Made some progress, though. Created the `SpringJettyConfig` Spring config class, which will launch the `EmbeddedServer`.
    * Problem now is that the `EmbeddedServer` creates a separate Spring context and things get stuck in a loop.
    * Thought: `AnnotationConfigWebApplicationContext.setParent(ApplicationContext)` might be a solution.

### 2014-01-06, Monday

* 1.5h: Got Spring DI working in JUnit tests with embedded Jetty.
    * My idea last night was correct: I did have to set the parent Spring `ApplicationContext`.
    * So far, this is just for `GuestAuthServiceIT`; still need to fix the other tests.
* 0.5h: Fixed `GameLoginIdentityTest` and `GameAuthServiceTest`. Also implemented most of `GameLoginIdentitiesDaoImpl`.
* 0.5h: Worked on getting `GameAuthServiceIT` passing.
    * Fixed the compile errors, but not the test failures.
    * Still implementing `GameLoginIdentitiesDaoImpl.find(InternetAddress)`.
    * Looks like I need to add support to Hibernate for persisting `InternetAddress` objects.

### 2014-01-07, Tuesday

* 2.0h: Worked on getting `GameAuthServiceIT` passing.
    * Created `InternetAddressUserType`, a custom Hibernate type mapping.
    * Finished implementing `GameLoginIdentitiesDaoImpl`.
    * Stuck on a test error involving URL-encoding of values sent to the web service. May need to switch to the new JAX-RS 2.0 client API.

### 2014-01-08, Wednesday

* 1.0h: Got `GameAuthServiceIT` and `mvn clean verify` passing.
    * Had to add `@FormParam` annotations to `GameAuthService`. Not sure why these weren't needed before with CXF 2.x, but whatever.
    * Had to fix up some minor dependency issues with the POM.
* 0.35h: Created `AccountsDaoImplTest` and `GameLoginIdentitiesDaoImplTest`.
    * Need to create tests for the rest of the methods in the DAOs (beyond just `save(...)`).
    * Need to add DB "cleanup" code to the DAO tests.

### 2014-01-09, Thursday

* 0.7h: Fleshed out the DAO tests the rest of the way.
* 0.05h: Addressed the `TODO`s, `FIXME`s, etc. that needed to be cleaned up before a (first) commit.
    * I left a couple in because I want them as part of the history. They'll need to be cleaned up in a second commit.
* 1.0h: Fixed the SQL type of `AuthToken.creationTimestamp` via the [Jadira Usertypes library](http://jadira.sourceforge.net/).
* 0.55h: Switched from `orm.xml` to annotations for JPA/Hibernate.
* 0.5h: Committed the changes for Issue #13.
* 1.5h: Created Issue #24 and Issue #25.

### 2014-01-10, Friday

* 0.5h: Created the `PostgreSqlConnector` and `PostgreSqlCoordinates` classes.
    * Done, except the `PostgreSqlConnectorTest.createDataSource()` test case still needs to be implemented, once a provisioner is available.

### 2014-01-11, Saturday

* 1.0h: Created the `IDataSourceProvisioner` API and the `HsqlProvisioner` implementation for it.
    * Now need to create a PostgreSQL implementation.
* 3.0h: Created the `PostgreSqlProvisioner` implementation and a bunch of the helper/util code that will actually be used by tests.
    * Still need to point the tests at an actual PostgreSQL server and verify everything works.
    * Unit tests are still needed for the helper/utility code, and possibly other things as well.
* 1.5h: Got `PostgreSqlProvisionerIT` passing.
    * Not sure why yet, but I have to pass in the DB name to be provisioned in all lower case, or I can't connect to it after it's created (though I can drop it).
* 2.0h: Wrote tests for `DataSourceProvisionersManager` and friends, got them passing.
    * Created a PostgreSQL account on `eddings` to get the tests passing on Jenkins, as well.
    * Now just need to start on adding PostgreSQL support to RPS; almost all of the util code is complete.
* 0.5h: Worked on converting `AccountsDaoImplIT` to use the new provisioning framework.

### 2014-01-12, Sunday

* 1.0h: Finished converting `AccountsDaoImplIT` to use the new provisioning framework, and added PostgreSQL tests to it.
    * Had to add nested quotes to all of the JPA table/column annotations. Otherwise, HSQL uppercases everything by default and PostgreSQL lowercases everything. Made it impossible to write tests that query for specific column metadata.
    * The 'try {} finally {}' code in there is kind of a disaster now. Need to come up with a way to clean it up.
* 3.0h: Still trying to clean up the 'try {} finally {}' code in `AccountsDaoImplIT`. Going in circles on it.
* 1.5h: Got `AccountsDaoImplIT` and the other DAO ITs running against PostgreSQL and cleaned up.
    * Created `DaoTestHelper` to clean up the worst of the 'try {} finally {}' mess. It's not perfect, but it's a large improvement.
    * Eventually, I should work on not having to drop the DBs and recreate the EMF for each test case, as this is painfully slow.

### 2014-01-13, Monday

* 1.0h: Started work on `LiquibaseSchemaManager`.

### 2014-01-14, Tuesday

* 0.05h: Worked on `LiquibaseSchemaManager` a little bit.

### 2014-01-15, Wednesday

* 0.5h: Worked on `LiquibaseSchemaManager` a bit.
    * It looks like it might be running correctly against HSQL. Need to flesh out the test some, though.

### 2014-01-16, Thursday

* 0.25h: Worked on `LiquibaseSchemaManagerTest` a bit.
    * Not passing. My best guess is that I need to somehow specify which changesets to run.

### 2014-01-17, Friday

* 1.0h: Worked on `LiquibaseSchemaManagerTest` a bit.
    * Fixed one problem: now passing in just the changeset path, rather than an uninitialized `ChangeSet` instance.
    * Ran into a bug around table/column name case with HSQL, though: [CORE-1721](https://liquibase.jira.com/browse/CORE-1721).

### 2014-01-18, Saturday

* 1.5h: Fixed [CORE-1721](https://liquibase.jira.com/browse/CORE-1721) and issued a pull request for it.
* 2.5h: Worked on adding in schema provisioning to the webservice startup and tests.
    * Had to split up the Spring configuration some, some that the DAO tests aren't initializing JPA twice.
    * Ended up having to provision the schema via Spring bean initialization, rather than webapp context initialization (which would have been cleaner). Unfortunately, though, Spring insists on initializing the JPA EMF as part of its context creation; that can't be lazy-loaded. Since we're asking Hibernate to verify that the DB schema is correct, this fails if provisioning isn't run prior to that.
    * Fleshed out the Liquibase changelog for the webservice.
* 3.0h: Completed [Issue #25](https://github.com/karlmdavis/rps-tourney/issues/25).
    * Had to hack around a number of bugs/issues in Hibernate/Liquibase around object casing and cross-platform ID columns.

### 2014-01-19, Sunday

* 0.75h: Started reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.3.

### 2014-01-20, Monday

* 0.5h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.3.3.

### 2014-01-21, Tuesday

* 0.5h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.3.4.

### 2014-01-22, Wednesday

* 0.25h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.5.2.
    * Also skimmed the [AngularJS](http://angularjs.org/) site's front page a bit, just to get a feel for it.

### 2014-01-23, Thursday

* 0.2h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.6.

### 2014-01-24, Friday

* 0.1h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.9.

### 2014-01-25, Saturday

* 0.25h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.12.
* 0.25h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.16.

### 2014-01-26, Sunday

* 0.05h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Made it up to section 16.16.2.

### 2014-01-27, Monday

* 0.5h: Reading through the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) guide. Still in section 16.16.2.
    * Created a stub project. Doesn't build, as I still need to work out the WAR dependency issue.

### 2014-01-28, Tuesday

* 0.25h: Played around a little bit with the JAX-RS `Client` API.

### 2014-01-29, Wednesday

* 0.10h: Read up some more on the `Client` API.
    * Any resources passed in (e.g. service interfaces) are just relative to the parent `Client` target.
    * `Client` instances may be expensive to create and dispose. Not sure how best to account for that in the webapp design yet.

### 2014-01-30, Thursday

* 0.25h: Created the `rps-tourney-service-api` project stub.
    * Will need to interface-ify the web service project's resources and pull the interfaces into this new project.
    * Given a future Android project, it probably makes sense to also create an `rps-tourney-service-client` project and put the `Client` code in there. This assumes, though, that there's a JAX-RS 2.0 implementation for Android. 

### 2014-01-31, Friday

(didn't work on this; traveling from Chapel Hill to Fort Meade)

### 2014-02-01, Saturday

* 0.5h: Started pulling out interfaces to drop into `rps-tourney-service-api`.

### 2014-02-02, Sunday

* 2.0h: Finished pulling out interfaces into `rps-tourney-service-api` and got the build working again.
    * Next step: Fix up the interfaces that return `Response` instances to return legit entities instead. Mostly, I think this is the login-related methods.
        * Probably want to use request attributes and a filter to set the outgoing cookies.
    * Also need to fix up the references to `AuthTokenCookieHelper` and the `-webapp` projects's `web.xml` reference to the entry point.
    * Also need to rename all of the `*Service*` classes to match their new interfaces.

### 2014-02-03, Monday

* 2.25h: Worked on fixing the login methods to not return `Response` instances.
    * Think I'm mostly done, but the `AuthenticationFilter` isn't quite finished and at least some of the ITs are failing.

### 2014-02-04, Tuesday

* 0.5h: Worked on fixing the login methods to not return `Response` instances.
    * Fixed all of the tests.
    * Think I'm mostly done with this, but the `AuthenticationFilter` needs comments still.

### 2014-03-03, Monday

* Note: Did not work on project for last month, due to very busy work schedule.
* 0.25h: Reviewed commit and work logs to refresh my memory, and then added missing Javadoc comments to `AuthenticationFilter`.
* 0.5h: Comitted the changes and then refactored the project, class, and package names for consistency.

### 2014-03-04, Tuesday

* 0.1h: Created the `GameWebApplicationInitializer` class and started filling it in. Need to go through the MVC docs again, until I've at least got a "Hello World" in place.
* Still need to:
    * Fix whatever the Jenkins build failure is.

### 2014-03-05, Wednesday

* 0.25h: Got the Jenkins build working again. The local Git repo was corrupted, and it had a problem with Sonar timing out (it's running really slowly). Haven't fixed the Sonar problem, as it worked the second time through.

### 2014-03-06, Thursday

* 0.5h: Worked towards a "Hello World" MVC app a bit more. Got the Spring config and controller in place, I think. Need to get a view wired up, and then hook up Jetty or whatever.

### 2014-03-07, Friday

(Didn't work on anything: got too stuck in a book.)

### 2014-03-08, Saturday

* 0.25h: Skimmed through the Spring MVC documentation on choices for view technologies.
* 0.25h: Created the JSP view for my Hello World sample.
* Still need to wire up Jetty, then test and debug things.

### 2014-03-09, Sunday

* 1.25h: Moved `EmbeddedServer` into `jessentials-misc`.
* 0.5h: Trying to get the "Hello World" sample to work.
* 2.0h: Got the "Hello World" sample to work: had to explicitly add a JSP servlet to the application.
    * This only seems to work if the JSP servlet is also configured as the default/fallback servlet for the MVC servlet. Would like to figure out how to avoid that, as I'd also like to be able to serve CSS, etc. using the real `DefaultServlet`.

### 2014-03-10, Monday

* 0.75h: Fixed the servlet mappings such that Spring MVC, JSPs, and static files should now all be handled correctly.
    * Still need to go through and commit things, but I think [Issue #18](https://github.com/karlmdavis/rps-tourney/issues/18) is now complete.

### 2014-03-11, Tuesday

* 2.0h: Re-did the servlet mappings: `GameWebApplicationInitializer` no longer registers the JSP and default servlets itself. That's now handled in `EmbeddedServer`, as really, it's the container's responsibility to provide those servlets. If I hadn't done this, I'd be trying to install Jetty's `DefaultServlet` class in, for example, Tomcat, which is silly.

### 2014-03-12, Wednesday

* 0.15h: Created a unit test for `EmbeddedServer`, and then commented it out, once I realized I wouldn't be able to run it (requires Java 7).
* 0.15h: Committed a bunch of little things, but not the "Hello World" sample itself. Still need tests for it.
* 3.5h: Replaced my "Hello World" sample with the more useful `InfoController` and view. Created ITs for the controller.
    * No ITs for the views itself (yet).
    * Committed this and closed [Issue #18](https://github.com/karlmdavis/rps-tourney/issues/18).

### 2014-03-13, Thursday

* 0.75h: Worked on getting JaCoCo and Sonar to play nicely for integration tests. Not yet done.

### 2014-03-14, Friday

* 1.0h: Got integration test coverage reporting in Sonar for `jessentials`.

### 2014-03-15, Saturday

* 0.2h: Got integration test coverage reporting in Sonar for `rps-tourney`. Closed [Issue #27](https://github.com/karlmdavis/rps-tourney/issues/27).
* 1.0h: Got integration tests running in parallel for `rps-tourney-service-app`. Closed [Issue #26](https://github.com/karlmdavis/rps-tourney/issues/26).

### 2014-03-16, Sunday

* 0.5h: Re-opened [Issue #26](https://github.com/karlmdavis/rps-tourney/issues/26) to fix some problems, fixed them, and closed it again.

### 2014-03-17, Monday

* 0.2h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Started research.
    * Looks like Tomcat would perform better (according to some quick Google searches), but Jetty is already running on `eddings` to host Nexus. Probably makes more sense to stick with it for now.
    * I think the best way to automate this is with a script that can pull the WARs to deploy from Nexus, and allows me to specify the version, password, and target (dev vs. prod).

### 2014-03-18, Tuesday

* 1.25h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued research, and started implementing.
    * Started creating a Maven project that will allow use of Cargo to deploy things.
    * Started drafting the Jetty configuration docs. Will need to install the Cargo deployer WAR and configure Jetty auth.

### 2014-03-19, Wednesday

* 2.0h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued implementing.
    * Got Jetty configured with the Cargo Deployer, and secured it.
    * Got the deployment script working (though it still has some TODOs).
    * However, the WARs themselves aren't working on Jetty. My guess is that Jetty 6 may not support Servlet 3.0.
* 2.0h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued implementing.
    * Yup, Jetty 6 doesn't support Servlet 3.0. Later versions of Jetty aren't available in the Ubuntu 12.04 repos.
    * Tomcat 7 is, however, and does support Servlet 3.0 (though not 3.1, but I don't yet need that).
    * Couldn't get Tomcat working with Kerberos.

### 2014-03-20, Thursday

* 2.0h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued implementing.
    * Spent a lot more time trying to get Kerberos working. Failed.
    * Got LDAP working, instead.
    * Got sidetracked trying to add some new LDAP groups, clean up old groups, and set LDAP security correctly.

### 2014-03-21, Friday

* 2.0h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued implementing.
    * Still trying to get LDAP security configured correctly.
        * Doesn't look like `dynlist` can be used to convert a `posixGroup` into a `groupOfNames`. Need a Plan B.

### 2014-03-22, Saturday

* 4.0h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued implementing.
    * Got LDAP's security configured in a much more sane way, if not quite the way I'd wanted.
    * Got Nexus redeployed in Tomcat.
    * Got the RPS web applications deployed in Tomcat.
    * Still need to uninstall Jetty.
* 1.75h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued implementing.
    * Researching logging in Tomcat.
    * Configured logback correctly (I think) for `rps-rourney-service-app`.
* 2.0h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16): Continued implementing.
    * Renamed `GameConfig` and other classes in `rps-tourney-service-app` so that they aren't confused with similar classes in `rps-tourney-webapp`.
    * Worked on the logging configuration of the deployed webapps.
    * Tried to create an `rps` PostgreSQL DB for the deployed webapps to use. Didn't succeed.

### 2014-03-23, Sunday

* 3.0h: [Issue #16](https://github.com/karlmdavis/rps-tourney/issues/16) and [Issue #19](https://github.com/karlmdavis/rps-tourney/issues/19): Finished.
    * Fixed Puppet on `eddings` so that the `rps` PostgreSQL DB could be created.
    * Cleaned up the WARs' dependencies so that the deploy correctly and cleanly.
    * Cleaned up and committed things.
* 0.5h: [Issue #28](https://github.com/karlmdavis/rps-tourney/issues/28): Found and fixed.
* 1.0h: [Issue #29](https://github.com/karlmdavis/rps-tourney/issues/29): Found and fixed.
    * Posted <http://stackoverflow.com/q/22596769/1851299> to try and resolve one minor complaint I have with my solution.

### 2014-03-24, Monday

* 0.1h: Registered `rpstourney.com` as a domain name.
    * I checked, but none of the following were available: `rps.com`, `rpsbattle.com`, `rpsarena.com`.

### 2014-03-25, Tuesday

* 0.1h: Configured DNS for `rpstourney.com`.

### 2014-03-26, Wednesday

* 0.1h: Deployed fix for [Issue #29](https://github.com/karlmdavis/rps-tourney/issues/29) (as I'd apparently forgotten to do so earlier).
* 0.4h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Checked out and started configuring [HTML5 Boilerplate](http://html5boilerplate.com/).
    * Started work on getting a homepage for the application in place.

### 2014-03-27, Thursday

* 1.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Broke out the page's (currently very boring) template into a taglib.
        * Useful reference: [Stack Overflow: What's the difference between including files with JSP include directive, JSP include action and using JSP Tag Files?](http://stackoverflow.com/questions/14580120/whats-the-difference-between-including-files-with-jsp-include-directive-jsp-in)
        * Useful reference: [Stack Overflow: JSP tricks to make templating easier?](http://stackoverflow.com/questions/1296235/jsp-tricks-to-make-templating-easier/3257426#3257426)
    * Got basic internationalization working.

### 2014-03-28, Friday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Created a basic `HomeControllerIT`.
    * Committed things.

### 2014-03-29, Saturday

* 1.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started reading about Spring Security and trying to implement it. Didn't get very far.

### 2014-03-30, Sunday

* 1.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Read some more about Spring Security. For now, I think it makes sense to just get "game login" authentication working. The anonymous authentication I've built will be quite a bit more complicated to make work with this framework.

### 2014-03-31, Monday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Read about OAuth2 with Spring Security.
* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * OAuth2 was a random red herring: even if I wanted to implement it, I don't know that I want to add Spring Security to my CXF web service application to do so. Something to maybe consider later.
    * I need to implement a custom `AuthenticationProvider`, and then provide a login form. I think that's all I'll need for right now, though I might end up having to futz with "Remember Me" services.

### 2014-04-01, Tuesday

* 0.35h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Created a stub `AuthenticationProvider` implementation and tried to get it working. Didn't quite succeed: nothing seems to be prompting for login.

### 2014-04-02, Wednesday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Did a quick test and discovered that my `SecurityWebApplicationInitializer` is not being run.
* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Fixed my `SecurityWebApplicationInitializer`.

### 2014-04-03, Thursday

* 1.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Tried to figure out how `@EnableGlobalMethodSecurity` would work with `@EnableWebMvcSecurity`. I'm not sure, but I think that perhaps it won't.
* 1.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Found this excellent tutorial on using `@EnableGlobalMethodSecurity` with Spring MVC: [Designing and Implementing a Web Application with Spring](http://spring.io/guides/tutorials/web/6/).
    * Got things prompting for login when/where they should.
    * Don't quite have the login/logout page working yet, though.
* 1.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Got login and logout working.
    * Next up: actually implement `GameLoginAuthenticationProvider` and add tests.

### 2014-04-04, Friday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Cleaned up comments on the new classes.
    * Started fleshing out `GameLoginAuthenticationProvider`.
* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Cleaned up comments on the new classes.
    * Started fleshing out `GameLoginAuthenticationProvider`.

### 2014-04-05, Saturday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started creating `GameAuthClient` in the new `rps-tourney-service-client` project.
* 1.25h: [Issue #13](https://github.com/karlmdavis/rps-tourney/issues/13): Game logins service.
    * Hadn't ever tested logins with game logins, and they were broken. Fixed this.
* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * How do I want to handle testing the `-service-client` project?
        * All that I could do with unit tests is verify its behavior when the service is down. Seems silly to bother.
        * Integration tests require a running instance of `-service-app` to connect to.
        * Do I want to have a `test-jar` dependency on `-service-app` and test things that way? Those dependencies are always such a giant PITA.
        * The only alternative, though, is to move all of those ITs to a separate `-service-its` project. That project would need to have the ITs for both the client and server.
        * Maybe that's not such a bad idea... Given that you can't really test the service without a client and vice-versa, maybe those just belong together.
        * It'd be kind of nice if I design things such that the tests could also be run against an instance of the service in production.
            * I'd have to be super careful to ensure that the tests don't leave test data laying around, though, and I wouldn't be able to wipe the DB in them ever.
            * I'd also have to come up with a way to hide the test data: don't want to throw off stats or pollute things with a bunch of test game sessions, for instance.
            * I think I'll leave that out for now. It's something to implement later if it turns out I really want it.
        * I should probably create a separate branch for this effort.

### 2014-04-06, Sunday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started creating the new `rps-tourney-service-its` project and moving the `-service-app` ITs into it.
        * Will have to either use the Cargo plugin, or split the `-service-app` project into a separate WAR and JAR: WAR dependencies don't end up on the classpath or pull in transitive dependencies.
        * I think it's best to go with Cargo, though I'll have to see if JaCoCo and Sonar work correctly with it.

### 2014-04-07, Monday

* 0.3h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started trying to wire up Cargo or the Tomcat plugin for ITs.

### 2014-04-08, Tuesday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Changed course: decided to try and leave the ITs in `-service-app`, and to use the `-service-client` classes there to test the server side and client side together. Not sure that JaCoCo and Sonar will "play nice" with this idea, but definitely seems to be worth a shot.

### 2014-04-09, Wednesday

* 0.75h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Working on refactoring the `-service-app` ITs to use the `-service-client` classes.
        * Currently stuck on how to handle login cookies between requests with the client. They really represent session state (on the server side), so I feel like I ought to use some sort of AOP proxy to inject them on every call. It looks like Spring's `@Scope(...)` annotation makes this relatively painless to do.

### 2014-04-10, Thursday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Working on refactoring the `-service-app` ITs to use the `-service-client` classes.
        * Started implementing the `CookieStore` to preserve auth cookies between requests. Stopped before I had things working.

### 2014-04-11, Friday

* 0.35h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Working on refactoring the `-service-app` ITs to use the `-service-client` classes.
        * Got `GameAuthResourceImplIT` working.

### 2014-04-12, Saturday

* 1.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Finished refactoring the `-service-app` ITs to use the `-service-client` classes.
        * Got the rest of the client  API classes created. Added a couple of unit tests for them.
        * Committed all of this: [rps-tourney:85e5d07d37](https://github.com/karlmdavis/rps-tourney/commit/85e5d07d377e617d30664364cc5a22e3beb4762d).
* 0.75h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Started building out `GameSession`, etc. classes (with JPA support this time).

### 2014-04-13, Sunday

* 1.5h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Wasn't very motivated or productive: kept letting myself get distracted.
    * Worked on the JPA classes some more, and started designing web services.

### 2014-04-14, Monday

* 0.5h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Refactored `GameSession` to make it mutable, in the ways it'll need to be to support the game's workflow. Didn't finish.

### 2014-04-15, Tuesday

(Was busy with work and forgot to work on side project. Blew my 38 day streak.)

### 2014-04-16, Wednesday

* 0.3h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Completed the `GameSession` refactoring. Still need to check the schema and `GameRound`.

### 2014-04-17, Thursday

* 0.3h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Worked on fleshing out `Player`.
    * Updated the schema to match the new classes.
    * Next steps:
        * XML attributes for the new model classes.
        * Tests for the new model classes.
        * DAO(s) for the new model classes.
        * Flesh out `IGameSessionResource` and its implementations.

### 2014-04-18, Friday

* 0.2h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Fleshed out `IGameSessionResource`.

### 2014-04-19, Saturday

* 1.0h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Added XML attributes for the new model classes.
    * Added some tests for the new model classes.
    * Started adding in some more gameplay logic to model classes.
    * Next steps:
        * Finish adding in gameplay logic to model classes: determining winners, making sure players are distinct, etc.
        * DAO(s) for the new model classes.
        * Create the `IGameSessionResource` implementations.

### 2014-04-20, Sunday

* 1.0h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Finished (I think) adding in gameplay logic to model classes: determining winners, making sure players are distinct, etc.
    * Next steps:
        * Updating the tests for the model classes to cover the new gameplay logic.
        * DAO(s) for the new model classes.
        * Create the `IGameSessionResource` implementations.

### 2014-04-21, Monday

* 1.0h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Updated the tests for the model classes to cover the new gameplay logic.
    * Next steps:
        * DAO(s) for the new model classes.
        * Create the `IGameSessionResource` implementations.

### 2014-04-22, Tuesday

* 0.75h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Started the `IGameSessionResource` server implementation.
    * Next steps:
        * Complete the `IGameSessionResource` implementations.
        * DAO(s) for the new model classes.

### 2014-04-23, Wednesday

* 0.3h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Completed the `IGameSessionResource` server implementation, or at least a first pass on it.
    * Next steps:
        * Create the `IGameSessionResource` client implementation.
        * DAO(s) for the new model classes.
        * Write a lot of tests, being sure to thoroughly investigate thread safety.

### 2014-04-24, Thursday

* 0.35h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Thought about synchronization/locking for the `IGameSessionResource` server implementation.
        * Added an extra parameter to `setMaxRounds(...)` to help prevent issues.
        * If the objects are all retrieved as part of a pessimistic transaction, I think things will work out.
        * All of the fields either require the correct "old" value to be passed in and verified, or only allow a one-time write.
        * If `setMaxRounds(...)` is in a pessimistic transaction, it should now be fine.
        * Does `submitThrow(...)` need a CHECK constraint in the DB to ensure it's safe? I think so. Alternatively, I could put it in a pessimistic transaction, but that would prevent users from simultaneously submitting throws, which would be not-great.
* 0.3h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Created the `IGameSessionResource` client implementation.
    * Started creating the ITs.
    * Next steps:
        * DAO(s) for the new model classes.
        * Write a lot of tests, being sure to thoroughly investigate thread safety.

### 2014-04-25, Friday

* 0.75h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Worked on the ITs, and fixed bugs that they exposed.
    * Next steps:
        * DAO(s) for the new model classes.
        * Write a lot of tests, being sure to thoroughly investigate thread safety.

### 2014-04-26, Saturday

* 4.0h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Worked on the ITs, and fixed bugs that they exposed.
    * Spent a lot of time tracking down problems with concurrency. Was very confused about some problems until I learned about Hibernate's `@DynamicUpdate(...)` setting, which is `false` by default, which is the opposite of the way things worked with JDO/DataNucleus.
    * Next steps:
        * DAO(s) for the new model classes.
        * Write a lot of tests, being sure to thoroughly investigate thread safety.

### 2014-04-27, Sunday

* 1.25h: Added support to the web service for logging requests/responses (with an attempt to exclude passwords).
    * Opened [CXF-5714: org.apache.cxf.interceptor.LoggingMessage doesn't have getId() property](https://issues.apache.org/jira/browse/CXF-5714).
* 4.0h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Worked on the ITs, and fixed bugs that they exposed.
    * Next steps:
        * Write the trigger guard for Postgres.
        * Add a `CHECK` constraint to prevent calls to `setMaxRounds(...)` from succeeding once the game has started.
        * DAO(s) for the new model classes.
    * Estimates:
        * [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14)
            * Remaining DB constraints: 3h
            * Remaining tests: 2h
            * Cleaning up and committing code: 2h
            * Rework once the web application is in place: 4h

### 2014-04-28, Monday

* 0.5h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Wrote the trigger guard for Postgres.
* 0.5h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Thought about adding a `CHECK` constraint to prevent calls to `setMaxRounds(...)` from succeeding once the game has started.
        * Thing is, though, it wouldn't help. Unless there's a separate method to mark a game as STARTED, after the first throw is the earliest that this can be blocked off.
        * Don't need to worry about clients with stale data, as the web service always gets a new copy of the `GameSession`.
    * Started on the tests for the new model classes' DAOs.
    * Next steps:
        * Finish the tests for the new model classes' DAOs.

### 2014-04-29, Tuesday

* 0.75h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Trying to get `findByIdWithLock(...)` working.
        * Pessimistic locking just deadlocks everything, even for reads.
        * Optimistic locking seems to always be turned on, even when I wish it weren't.
        * The `version` column also seems to be throwing some really odd errors.
* 2.0h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Trying to get `findByIdWithLock(...)` working.
        * The `version` column also seems to be throwing some really odd errors.
            * Lesson learned: never mark `@Version` columns as not-nullable.
            * Also ended up opening this bug against Hibernate: [Hibernate ORM: HHH-9149: Error when using mixed-case column name on an @Version column](https://hibernate.atlassian.net/browse/HHH-9149).
    * Next steps:
        * Finish the tests for the new model classes' DAOs.

### 2014-04-30, Wednesday

* 0.25h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Trying to get `findByIdWithLock(...)` working.
        * The `version` column also seems to be throwing some really odd errors.
            * Added a workaround for the [Hibernate ORM: HHH-9149: Error when using mixed-case column name on an @Version column](https://hibernate.atlassian.net/browse/HHH-9149) bug.
    * Next steps:
        * Decide if optimistic locking is a good design choice.
        * Finish the tests for the new model classes' DAOs.

### 2014-05-01, Thursday

* 0.5h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Trying to get `findByIdWithLock(...)` working.
        * The `version` column also seems to be throwing some really odd errors.
            * Oh, FFS: even without the inner quotes on the column name, I still can't get the `@Version` column working in Postgres. Works fine in HSQL. Such BS.
            * Added a switch to the schema to workaround that stupid problem, too.
        * Still not sure if I actually want to have locking. Need to think about it some more.
    * Next steps:
        * Decide if optimistic locking is a good design choice.
        * Finish the tests for the new model classes' DAOs.

### 2014-05-02, Friday

* 0.5h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Trying to get `findByIdWithLock(...)` working.
        * Consider the following scenario: three requests come in at once: `setMaxRounds(1)`, a first throw for player 1, and a first throw for player 2. Breaks the game.
        * What would be the "right" thing to do in that scenario? Ideally, the `setMaxRounds` would fail, because the submit throws held some sort of shared lock. Unfortunately, no such "shared lock" construct exists.
        * Instead, I think I really do need a "start game" flag for both players. Then, the worst thing that happens is that the number of rounds changes just before anyone can make a move.
    * Next steps:
        * Add in the model fields and service methods to support a required "please start the game" request from both players.
        * Finish the tests for the new model classes' DAOs.

### 2014-05-03, Saturday

* 1.75h: [Issue #14](https://github.com/karlmdavis/rps-tourney/issues/14): Adding methods to web service to enable game play.
    * Tried adding in the model fields and service methods to support a required "please start the game" request from both players.
        * Hold on... in my scenario from earlier, with the concurrent `setMaxRounds(...)` and both pla players' first moves... that actually **wouldn't** break the game. Neither of the two throws would see that the round is over, so their call to `prepareRound(...)` wouldn't do anything. When the clients next call it manually, everything would work out.
        * These extra fields and methods aren't needed. Undid my work here.
    * Got the existing tests passing.
    * Added in the rest of the tests needed for the new model classes and their DAOs.
    * Committed all of my changes on this issue and marked it closed.
        * Wouldn't be surprised if I end up re-opening it later, but that's good enough for now.
* 3.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started working on `GameController` a bit. Didn't get too far, as I wanted to try things out manually and see how they looked.
    * Copy-pasted-hacked in the config loading code for the web app.
    * Trying to figure out how to start Jetty for manual testing, with a mock Spring configuration.

### 2014-05-04, Sunday

* 2.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Estimates:
        * 4h: Figure out how to start Spring & Jetty for manual testing.
        * 2h: Fix web app authentication (it's probably broken).
        * 2h: Either switch to guest auth in the web app or add in "create account" form.
        * 3h: Get the "create game" form and redirect working.
        * 2h: Get the submit throw controls working.
        * 3h: Add in the score-keeping and winner/loser logic.
        * 3h: Add in jQuery and use it to refresh the page.
    * Trying to figure out how to start Jetty for manual testing, with a mock Spring configuration.
        * Think I've got this worked out. Ended up splitting the Spring bindings into separate profiles, rather than relying on overrides. Overrides were too non-deterministic, as you can't control the load order when using classpath scanning.

### 2014-05-05, Monday

(Forgot to work on this stuff, as I got sidetracked upgrading my phone's OS.)

### 2014-05-06, Tuesday

* 1.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Created `ServiceAppJettyLauncher` to launch the web service for manual testing.
    * Did some miscellaneous refactoring.

### 2014-05-07, Wednesday

* 1.0h: Working to resolve issues with build server CPU being pegged, which keeps killing CI builds for this project.
    * Not sure exactly what the issue was, but there were a couple of `java` processes or threads on `piers` (not sure which) that kept running even after stopping Zimbra, and didn't seem to have a command line. After `kill -9` for those, and restarting Zimbra, things seem saner, at least for now. Interestingly though, the Jenkins build wasn't any noticably faster than previous ones. Maybe that's because I'd just restarted Jenkins and Sonar so they were cold?
* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Fixed the "Start Game" link so that it points to the correct path.
    * Can't test any further than without getting auth actually up and running. Recommend biting the bullet and enabling guest auth. Ugh.

### 2014-05-08, Thursday

(Forgot to work on this stuff, as I was busy getting ready for my trip to Tucson.)

### 2014-05-09, Friday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Tried to find examples of guest auth strategies similar to mine for Spring Security. No luck.
    * Think I need to explore "auto login" examples with Spring Security, instead.

### 2014-05-10, Saturday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Actually, I'm thinking that Spring's pre-auth mechanisms might do the trick:
        * Just go ahead and start logging in folks as guest on every request.
        * Or perhaps, I use pre-auth just to setup Spring Security when users have already logged in as guest. Instead of always logging folks in when they're un-authenticated, I could make this a manual fallback in my controllers' code when they perform some action that requires an account. This approach might scale a bit better. Means I won't be using `@Role` annotations for much, though.

### 2014-05-11, Sunday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Not feeling very productive, but started on the "auto guest login if user does something that requires an account" code.

### 2014-05-29, Thursday

* Haven't touched this in a while. Was too busy while in Tucson hanging out with folks, and was then too busy catching up on work.
* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Spent time reading through Spring Security's "Remember Me" code again. I think that guest logins need to be implemented as a custom `AbstractRememberMeServices` implementation.

### 2014-05-30, Friday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Can't use `AbstractRememberMeServices` or any subclass of it, as it requires a `UserDetailsService`. Think I need to write my own `RememberMeServices` implementation from scratch, instead.

### 2014-05-31, Saturday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started (just barely) the creation of a `CustomRememberMeServices` implementation.

### 2014-06-01, Sunday

* 1.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Implemented `CustomRememberMeServices.autoLogin(...)`.

### 2014-06-16, Monday

* Haven't touched this in a while. Was busy looking for a place to live in Baltimore and trying to get caught up at work.
* 0.75h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Partially implemented `CustomRememberMeServices.loginSuccess(...)`.

### 2014-06-17, Tuesday

* 1.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Added an `IAccountsResource.selectOrCreateAuthToken()` method for it to use.

### 2014-06-18, Wednesday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * May have finished the first-pass implementation. Didn't test it at all, though.

### 2014-06-19, Thursday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Started to maybe implement an integration test. Need to think about how to host or mock the web service for these.

### 2014-06-20, Friday

* 0.2h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * After some research, I've concluded the best thing to do for ITs is the following:
            1. Use the `maven-dependency-plugin` to copy the web service WAR needed for integration tests.
            2. Update the embedded Jetty code to support launching an extra WAR.
        * I may also have to poke at the web service some to allow for HSQL configuration, wiping, etc. Need to think about that some.

### 2014-06-20, Saturday

* 0.75h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Added code to `EmbeddedServer` for hosting additional WARs. Haven't tested it yet.

### 2014-06-21, Sunday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Having `EmbeddedServer` require a packed WAR makes running the tests in Eclipse a lot less convenient. Should I also try to support relative references to the sibling project?
            * How would I know when to use a packed WAR vs. a sibling project? I suppose I could just try the project if the WAR file isn't present, or vice-versa.
            * I think I should give this a shot. Likely won't even require changes to the `EmbeddedServer` code-- just its comments.

### 2014-07-08, Tuesday

* Hadn't touched this for a couple of weeks as we were busy moving.
* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Banged on `CustomerRememberMeServicesIT` a bit. Need to setup Spring profiles, bindings, etc. for webapp ITs.

### 2014-07-09, Wednesday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Moved my screwy 'EmbeddedServer` config into `JettyBindingsForITs` and started creating `EnvironmentIT`.

### 2014-07-10, Thursday

* 0.75h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Created the `/status/ping` service (and friends) that `EnvironmentIT` and other such use cases can use to verify that the web service is up & running. Need to commit it.

### 2014-07-15, Tuesday

* Was busy trying to get caught up on stuff at work-- worked the weekend.
* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Committed a couple of things, including the new `IServiceStatusResource` and friends.

### 2014-07-16, Wednesday

* 0.2h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Fleshed out `EnvironmentIT` and the bindings for it a bit. Now getting an interesting bean-creation `ClassNotFoundException`.

### 2014-07-17, Thursday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Working through the test failures and compilation failures from `EnvironmentIT`. Latest problem looks like my bean configs might just be incomplete.

### 2014-07-18, Friday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Debugged many of the bean config issues exposed by `EnvironmentIT`. Now seem to be onto more fundamental issues with the Spring Security configuration itself.

### 2014-07-19, Saturday

* 0.75h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Determined that the `SecurityConfig` throws errors if I inject a `GameLoginAuthenticationProvider` into it. Not sure how to solve it yet.
        * Looks like `JettyBindingsForITs` isn't configuring the web service WAR correctly. Thinking about it, it makes sense: I'm just pointing it at a folder-- there's no classpath or anything. Will need to point it to a WAR instead, sadly.

### 2014-07-20, Sunday

* 2.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Wow, what a mess...
        * Switched `JettyBindingsForITs` to load the actual must-already-be-built WAR file from its sibling project. If m2eclipse is alreadt building an exploded WAR somewhere, I might be able to avoid this, but for right now, this is conceptually simpler.
        * Was horrified to discover that I had an old Git stash sitting there, with half of the changes I've been in the middle of (re-)implementing. Went through and popped that, cleaned things up as best I could. I'm sure it's still a bit of a mess, though.
        * There's another, older stash still there that it looks like I probably don't need, but I didn't want to mess with it right now.
        * Pushed some non-webapp commits to lower the amount of uncommitted stuff I've got floating around right now. Checked and ensured that everything committed builds cleanly. Haven't pushed it to GitHub yet, though.

### 2014-07-21, Monday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Poked at the `EnvironmentIT` failure some. Didn't get very far, though it looks like Jetty may not be actually starting the context for the additional WAR. Need to test it manually.

### 2014-07-22, Tuesday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Fixed `EmbeddedServer` to handle WARs correctly (I think), instead of only just Maven projects.
        * Things still aren't working, but that was definitely a problem, so we're one step closer. I think I need to investigate getting injection going in my tests without using `spring-test`. Just a thought.

### 2014-07-24, Thursday

* Was busy with work yesterday and just forgot to work on this.
* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Spent some time researching. Looks like I'll have to roll my own mock-free Spring injection provider. Should probably be a JUnit `@Rule`.

### 2014-07-25, Friday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Started implementing my new JUnit `@Rule`. Might need to re-jigger `EmbeddedServer` so that I can launch it with a specific set of Spring configs & profiles.

### 2014-07-26, Saturday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Made good progress in implementing the new `@Rule`. Will need to adjust the web app's initializer to save its Spring context in a container attribute, so that the `@Rule` can pull it out and use it, too.

### 2014-07-28, Monday

* Was busy yesterday prepping for demo at work, and didn't have time to work on this Sunday.
* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Worked on exposing the root `ServletContext`'s Spring `ApplicationContext` for ITs to snag.

### 2014-07-29, Tuesday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Got things almost-working. Current problem is that the service app WAR is not starting up correctly: `"/service-app - No Spring WebApplicationInitializer types detected on classpath"`.

### 2014-07-30, Wednesday

* 0.75h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Worked through the classloader issue with `EmbeddedServer` and additional WARs.
            * Found the following useful page: <http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading>.
            * Calling `additionalWebApp.setParentLoaderPriority(true);` resolves the problem, but could lead to future library mismatch issues. Maybe good enough for now, though.
        * Need to consider adding HSQL driver to regular distribution.

### 2014-07-31, Thursday

* 0.4h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Hacked around a bug in Liquibase. (Not committed.)
        * Back to classloader issues with the Jetty additional WAR.

### 2014-08-01, Friday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Did some research and thought real hard about how to handle the Jetty two-WAR classloader issues.
            * Jetty is being launched with the project classpath, so all of the libraries available in the project are available to each application context.
            * This means that classes from the dependencies of any not-the-current-project WARs may be shadowed by the same classes in the current project.
            * Possible solutions that still use `EmbeddedServer` (basically a classic whitelist vs. blacklist choice):
                * Start up Jetty with its own very restricted classpath. This would be hard to get right, and fragile between different Jetty versions.
                * Mark Spring, etc. as part of Jetty's "server" classes, that application contexts can't see. This would be even more fragile, as the list really ought to include all not-required-for-Jetty project classes.
            * Alternatively, I could use the `jetty-maven-plugin` to run the ITs.
                * I'd lose (or have to recreate) Spring injection for the test classes.
                * Would that be so bad? Really all I'd need is the web service clients-- I think.

### 2014-08-02, Saturday

* 3.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Wired up the Cargo plugin to launch Jetty and host the two WARs.
            * Seems like I've got this all set up right, but I get a zip file error from Jetty when launching the `...-webapp` WAR. Further investigation/cursing needed there.
        * The other integration tests that don't actually require Jetty are broken. They need a config loader.

### 2014-08-03, Sunday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Fixed up a couple of the other broken integration tests. Ended up turning them into just unit tests.
        * Still haven't fixed `CustomerRememberMeServicesIT`.

### 2014-08-04, Monday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Bumped to the latest (final) Spring Security release, 3.2.4.
        * Should probably try the 4.0.0 pre-release to see if it resolves the problem I've got in `SecurityConfig` right now (with the commented-out code).

### 2014-08-05, Tuesday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Worked on implementing `CustomRememberMeServices` a bit more. Need to think about the "TODO" there and then fill in the test for it.

### 2014-08-06, Wednesday

* 0.75h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Started fleshing out `CustomRememberMeServicesTest`. Fun!

### 2014-08-07, Thursday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Continued implementing `CustomRememberMeServices.loginSuccess(...)`.
        * Completed `CustomRememberMeServicesTest`.
        * Now that it's implemented and tested... how does `CustomRememberMeServices` tie in with the rest of the system? How do I fire `loginSuccess(...)` events to it?

### 2014-08-08, Friday

* 0.6h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Recreated `WebAppJettyLauncher` and got things running for dev purposes.
    * Next, I need to figure out which parts of the security config are working and which aren't.

### 2014-08-09, Saturday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Tried getting `ServiceAppJettyLauncher` to create a login for use in manual testing. Didn't work: need to figure out how to fix that.

### 2014-08-10, Sunday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Got `ServiceAppJettyLauncher` to create a dev login.

### 2014-08-11, Monday

* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Got the web application logging in again! (Game logins only-- haven't figured out how to wire up or try guest logins yet.)
    * Definitely need to write some tests for `GameLoginAuthenticationProvider`, as it had a huge, obvious bug (was calling "create login" instead of "login").

### 2014-08-12, Tuesday

* 0.2h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Created `GameLoginAuthenticationProviderTest`.
* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Worked on wiring up `CustomRememberMeServices`.
        * The bean is now being created as part of the configuration.
        * However, the service isn't being called as part of the login process. Need to look up how to enable that, but don't have an internet connection on the plane I'm on right now.

### 2014-08-13, Wednesday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Went through all of my open tabs, looking for clues on how to enable the custom remember me services. No dice.
* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Got the remember me services wired up in `SecurityConfig`.
    * Next, need to think through whether or not I should use a request parameter to enable/disable it, or if it should just always be enabled.

### 2014-08-14, Thursday

* 0.2h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Removed the "remember me" request parameter from `CustomRememberMeServices`.
    * The remember me services should be enabled with every login.
    * They should also be exposed so that other operations (e.g. starting a game) can get the user authenticated anonymously, so that game state can be persisted.
    * I think it'd be best to create a separate `IGuestAuthenticator` bean that handles this, and uses the `CustomRememberMeServices`. Need to read that TODO in `CustomerRememberMeServices`, though, and think about it some.
    * This page discusses programmatic auth and remember me: [Stack Overflow: Log user in with remember-me functionality in Spring 3.1](http://stackoverflow.com/questions/7806921/log-user-in-with-remember-me-functionality-in-spring-3-1).

### 2014-08-15, Friday

* 0.2h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Created the `IGuestLoginManager` interface. Next, need to implement it and then actually use it.

### 2014-08-16, Saturday

* 0.2h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Created the `DefaultGuestLoginManager` implementation. Haven't tested it or tried it out at all yet.

### 2014-08-17, Sunday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started creating unit tests for `DefaultGuestLoginManager`. Realized I'm going to have trouble with my use of `SecurityContextHolder` and will need to refactor that somehow.

### 2014-08-18, Monday

* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Finished the `DefaultGuestLoginManager` unit tests.
    * Next up: integrate it into the actual application.

### 2014-08-23, Saturday

* Haven't worked on this for several days: was first very busy with work, then started reading Cryptonomicon, and then was busy rebuilding server.
* 1.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Banged on things for a while, working out some bugs.
    * Looks like I'm now ready to proceed with fleshing out `GameController` and friends; the security stuff all seems to be working.
    * Probably a good point to commit everything.
    * Also need to fix any tests I broke in my banging tonight.

### 2014-08-24, Sunday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Got the tests passing again.
* 2.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Cleaned things up and committed any standalone bits & pieces I could find.
    * Added some test coverage in a couple of places.
    * Fixed an obnoxious logging problem in the `rps-tourney-webapp` ITs. Created the `loggin-cargo.xml` file.
    * Committed everything else in a big commit against this issue.
* 0.75h: Fixed problems with the Apache config on `eddings` that was preventing GitHub's webhooks from working.

### 2014-08-25, Monday

* 2.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Started filling in `game.jsp`. Got the JSP-generated portion of the "Round History" table done.

### 2014-08-26, Tuesday

* 0.4h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Fleshed out a bit more of `game.jsp` and started adding in the controller code for submitting throws.
* 2.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Got the game (mostly) playable! Played my first game with Erica! Yay!
        * ![First Web Playthrough](rps-tourney-webapp/dev/screenshots/2014-08-26-first-successful-playthrough.png)
        * Looks like there's a bug where redirect URLs collect extra `/` characters appended to the end.
        * May be another bug that prevents launching more than one game per application run.

### 2014-08-27, Wednesday

* 0.1h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Fixed the redirect URL problem.
    * Traced the "can't start a new game" problem to an issue with `CustomerRememberMeServices`. For some reason, returning requests don't have the authentication token cookie.
* 0.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Fixed the "can't start a new game" problem that was caused by a bug with `CustomerRememberMeServices`: had to set the remember-me key in `SecurityConfig`.
* Next steps:
    * Add controls to change the number of rounds in a game.
    * Add a table or somesuch to the homepage that lists all of the games.
    * Tests.

### 2014-08-28, Thursday

* 1.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Worked on implementing the `setMaxRounds(...)` feature, which ended up being surprisingly tricky.
        * Not quite done: the web service needs to return a "mid-air collision (CONFLICT)" status code when two attempts conflict, so that the web app knows what went wrong. Right now it just throws an `IllegalStateException`, which gets translated to a 500 code.

### 2014-08-29, Friday

* 1.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Created `GameConflictException` and customized many of the game, server, and client exceptions.
        * Not quite done: Need to update API, server, and client tests.

### 2014-08-30, Saturday

* 5.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Fixed `GameSessionResourceImplIT.setMaxRoundsConcurrency()` to specifically watch for `GameConflictException`s (had to fix a bunch of bugs in the process).
    * *Tried* and failed to do the same thing for `GameSessionResourceImplIT.submitThrowConcurrency()`. Finally just got frustrated and gave up. See the "FIXME" left in `SpringJpaConfig.persistenceExceptionTranslationPostProcessor()`.
* 2.0h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Added tests for `GameController` and `game.jsp`.
* Next steps:
    * Add a table or somesuch to the homepage that lists all of the games, and add tests for that.
    * Hide the login/logout controls in the webpages (until game login registration is implemented).

### 2014-08-31, Sunday

* 3.5h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Added a table of the player's active games to the homepage.
    * Hid the home page's logout control.
    * Opened the following CXF issue (left a FIXME in `GameSessionClient` for it): [CXF-5980](https://issues.apache.org/jira/browse/CXF-5980). 
* 0.25h: [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20): Building out webapp to allow gameplay.
    * Fixed the player labels on the game page.
* Closed [Issue #20](https://github.com/karlmdavis/rps-tourney/issues/20)!
* Deployed to https://justdavis.com/karl/rps!

### 2014-09-01, Monday

* 1.5h: [Issue #31](https://github.com/karlmdavis/rps-tourney/issues/31): Web application URLs get mangled by proxy
    * Added `AppConfig.getBaseUrl()` and `BaseUrlInterceptor` and switched all of the JSPs to respect it.
* 0.5h: Resolved an intermittent issue with Spring Security and JavaScript resource requests.
* Deployed to https://justdavis.com/karl/rps again.
* 0.75h: [Issue #32](https://github.com/karlmdavis/rps-tourney/issues/32): JAXB errors on eddings.
    * Found & fixed it. No clue why it just started happening, though.
    * Deployed to https://justdavis.com/karl/rps again.
    * Snagged a couple of screenshots to commemorate the first actually playable deployment:
        * [First Playable Deployed Version: Home](rps-tourney-webapp/dev/screenshots/2014-09-01-first-playable-deployment-home.png)
        * [First Playable Deployed Version: Game](rps-tourney-webapp/dev/screenshots/2014-09-01-first-playable-deployment-game.png)
* 0.25h: [Issue #33](https://github.com/karlmdavis/rps-tourney/issues/33): Opponent's move in current round is visible via web service.
    * Found & partially fixed it.
    * Deployed to https://justdavis.com/karl/rps again.

### 2014-09-02, Tuesday

* 0.25h: [Issue #21: Prettify the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Handle enabling/disabling player controls. 
    * Implemented and deployed.
* Probably want to add timestamps to GameSessions next.

### 2014-09-03, Wednesday

* 1.0h: [Issue #34: GameSessions should have creation and modification timestamps](https://github.com/karlmdavis/rps-tourney/issues/34):
  Model changes
    * Added the new timestamp fields, wrote a unit test, and updated the Liquibase changelog.
    * Next, I need to figure out how to create a custom JSP tag to format the `Instant` instances.

### 2014-09-04, Thursday

* 2.0h: [Issue #34: GameSessions should have creation and modification timestamps](https://github.com/karlmdavis/rps-tourney/issues/34):
  JSP changes
    * Created `TemporalFormatTag` and tested it manually.
    * Next, I need to consider how I might add unit tests for it.

### 2014-09-05, Friday

* 0.5h: [Issue #34: GameSessions should have creation and modification timestamps](https://github.com/karlmdavis/rps-tourney/issues/34):
  Unit tests for JSP changes
    * Added the JSP unit tests, tested, and deployed things.

### 2014-09-06, Saturday

* 1.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Started putting in wro4j (for LESS).
    * Got distracted by research on the correct template to start with: Boilerplate? Bootstrap?
    * Decided on Bootstrap, mostly just because I'd like to have some experience with it.
    * Started putting the new template in. Definitely didn't finish, though.
    * Next steps:
        * Switch from Bootstrap's compiled CSS to its raw LESS. This will enable semantic markup.
        * Update the home and game pages to take advantage of the template.

### 2014-09-07, Sunday

* 1.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Spent a lot of time rigging up Bootstrap via the dependency plugin and rigging up wro4j to use that. Got that working.
    * The problem now is that `EmbeddedServer` is only adding the *source* side of `src/main/webapp` to the resources path, not the output side.
    * Oh, just remembered another problem: how do I get Bootstrap's `fonts` folder to where it's supposed to be?

### 2014-09-08, Monday

* 0.25h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Spent some time researching the fonts issue. Learned the following:
        * The wro4j plugin doesn't look like it has any facilities for copying around non-JS/non-CSS resources.
        * However, the bootstrap LESS has a variable that allows for customization of the fonts path: `@icon-font-path`.
    * Next steps:
        1. Add in the LESS font path customization.
        2. Rebuild devenv to use Eclipse JavaEE with m2e-wtp and m2e-wro4j.
        3. Fix `EmbeddedServer`'s resource paths. Should be using wherever m2e-wtp drops things.

### 2014-09-09, Tuesday

* 1.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Tried to get `theme.less` to build. Failed.
        * Opened the following Stack Overflow question: http://stackoverflow.com/questions/25756829/using-twitter-bootstraps-theme-less-with-wro4j-maven-plugin

### 2014-09-10, Wednesday

* 0.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Investigated some of the suggestions recevied on http://stackoverflow.com/questions/25756829/using-twitter-bootstraps-theme-less-with-wro4j-maven-plugin

### 2014-09-11, Thursday

* 0.2h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Tried the new `less4j:1.8.2` release, which fixed the problem I'd been having with `theme.less`.

### 2014-09-12, Friday

* 0.25h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Fixed the webapp resources paths. May have also fixed the bootstrap fonts path, though I'll have to test that still.

### 2014-09-13, Saturday

* 1.75h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Fonts were definitely not working. Messed around with the paths a bunch more and finally fixed it.

### 2014-09-14, Sunday

* 0.2h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Spent some time thinking through how to use Bootstrap: do I start with their suggested template, or do I just start from scratch?
        * I lean more towards building it up from scratch.
        * Don't necessarily have any strong justification for that. Maybe it's good to avoid looking generic?
        * On the other hand, it's also possible to reframe "generic" as "familiar"...
        * Want to sleep on this.

### 2014-09-15, Monday

* 0.5h: [Issue #35: Move development environment to Eclipse JavaEE](https://github.com/karlmdavis/rps-tourney/issues/35):
  Eclipse JavaEE devenv setup.
    * Started creating a new Eclipse install script in Python (rather than Bash).
        * Mostly just to see if it ends up being easier to maintain and use.

### 2014-09-16, Tuesday

* 1.0h: [Issue #35: Move development environment to Eclipse JavaEE](https://github.com/karlmdavis/rps-tourney/issues/35)::
  Eclipse JavaEE devenv setup.
    * Mostly implemented the function to download Eclipse. Slow going, what with picking up a new language and all.

### 2014-09-17, Wednesday

* 1.25h: [Issue #35: Move development environment to Eclipse JavaEE](https://github.com/karlmdavis/rps-tourney/issues/35)::
  Eclipse JavaEE devenv setup.
    * Fixed all the bugs in the download code, verified that it runs.
    * Decided on Python 3.
    * Next: implement Eclipse archive unzipping & shortcut.

### 2014-09-18, Thursday

* 0.6h: [Issue #35: Move development environment to Eclipse JavaEE](https://github.com/karlmdavis/rps-tourney/issues/35)::
  Eclipse JavaEE devenv setup.
    * Implemented extraction of Eclipse install.
    * Next: Eclipse shortcut.

### 2014-09-19, Friday

* 0.4h: [Issue #35: Move development environment to Eclipse JavaEE](https://github.com/karlmdavis/rps-tourney/issues/35)::
  Eclipse JavaEE devenv setup.
    * Ahoy!
    * Implemented Eclipse shortcut.
    * Next: Eclipse plugins.

### 2014-09-20, Saturday

* 1.25h: [Issue #35: Move development environment to Eclipse JavaEE](https://github.com/karlmdavis/rps-tourney/issues/35)::
  Eclipse JavaEE devenv setup.
    * Got the Eclipse plugins installing. Done with the script!
* 0.5h: [Issue #35: Move development environment to Eclipse JavaEE](https://github.com/karlmdavis/rps-tourney/issues/35)::
  Eclipse JavaEE devenv setup.
    * Took care of the issue tracking, commits, etc. to close out this task.
    * Started actually using the new version of Eclipse.

### 2014-09-21, Sunday

* 0.25h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Fixed a JSP error that the new version of Eclipse pointed out (nested single quotes).
    * Tried unsuccessfully to fix a wro4j error now appearing in Eclipse.
    * Next:
        * Try again to fix the wro4j error.
        * Maybe fix all of the JPA errors in Eclipse, just to clean things up.

### 2014-09-22, Monday

* 0.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Poked at the wro4j problem some more (I'm stubborn).
    * Noticed that Maven artifacts aren't downloading at all in Eclipse. Maybe that's related? Need to figure out what's causing those certificate errors.

### 2014-09-23, Tuesday

* 1.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Tried to resolve the m2e certificate errors.
        * Cleaned up the old (and not-kept-up-to-date) Oracle JRE/JDK installs on `jordan-u`.
        * Removed the disabled Oracle Java PPAs on `jordan-u`.
        * Tried to add the StartSSL certificate bundle to the truststore on `jordan-u`.
        * Next thing to try: Add Maven support to the devenv install script. I think perhaps that newer versions of Maven will throw the same errors, even on the command line. That'd make the problem a bit clearer and easier to troubleshoot.

### 2014-09-24, Wednesday

* 1.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Working on adding Maven support to the devenv install script.

### 2014-09-25, Thursday

* 1.5h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Got Maven support working in the devenv script.
    * Running the `p1` build using the new Maven works fine, so that's a dead end.

### 2014-09-26, Friday

* 0.1h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Tried adding an external Maven installation in the m2e configuration, which shouldn't have fixed anything. Except that it did... At least, it looks like it did. I'm guessing something I'd done earlier fixed the problem, and I just hadn't rebuilt to notice it. I wish I knew for certain, though.
    * Now what?
    * Looks like m2e-wtp doesn't copy over everything I was hoping it did: seems to be leaving out the `web.xml`, for instance.
    * If that's actually the way it works, then I'm up a crick a bit: `EmbeddedServer` can **only** be used in ITs. If I want to run the application from Eclipse, I'll have to use an Eclipse plugin to do so.
* 0.75h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Spent some time reading up on WTP. Out of the box, it really only supports Tomcat.
    * It's hard to be sure without firing up a Tomcat instance, but I think it's not pulling a filtered version of `web.xml` and such because the WAR plugin doesn't actually filter those by default. Unless/until I enable such filtering, it can continue to pull them out of `src/main/webapp`.
    * This makes me think that all I need to do is add the m2e-wtp output directory to `EmbeddedServer`, and I'll be back in business.
        * Did that.
        * Things started up, but didn't load the wro4j resources correctly. Further investigation is needed.

### 2014-09-27, Saturday

* 3.0h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Basic game template.
    * Got the resources loading correctly.
        * Had to fix some stuff in `EmbeddedServer` related to path canonicalization.
    * Cleaned up and committed things.
    * Next up: Actually figure out what I want things to look like.

### 2014-09-28, Sunday

* 3.0h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Finished up the game's initial look & feel.
    * Put together an intentionally very basic set of formatting for the application. It isn't fancy, but it's a solid starting point.
    * Cleaned up & committed things, closed the issue.

### 2014-09-29, Monday

* 0.35h: [Issue #21: "Prettify" the game web app](https://github.com/karlmdavis/rps-tourney/issues/21):
  Finished up the game's initial look & feel.
    * Got the site deployed to https://justdavis.com/karl/rps/.
        * Had some trouble with the deployment script. Added a comment to it explaining how I solved it.

### 2014-09-30, Tuesday

* 0.5h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Opened the enhancement issue and added an estimate to it.
* 1.25h: [Issue #39: Cleanup Eclipse JPA validation, etc. errors](https://github.com/karlmdavis/rps-tourney/issues/39):
    * Opened the bug report and added an estimate to it.
    * Worked through all of the JPA validation errors, which led to some painful test failures. Still stuck on those.
        * Hibernate doesn't seem interested in handling case-sensitive `@JoinColumn` names. Further investigation needed.

### 2014-10-01, Wednesday

* 1.25h: [Issue #39: Cleanup Eclipse JPA validation, etc. errors](https://github.com/karlmdavis/rps-tourney/issues/39):
    * Troubleshooting the case-sensitive `@JoinColumn` issue.
        * Traced it down to a bug in Hibernate at `CopyIdentifierComponentSecondPass.doSecondPass(Map):160`.
        * Tried updating to Hibernate 4.3.6.Final, but Nexus is down after upgrading `eddings` to 14.04. Spent some time trying to resolve that. Stuck on an SSL error.

### 2014-10-02, Thursday

* 0.5h: [Issue #39: Cleanup Eclipse JPA validation, etc. errors](https://github.com/karlmdavis/rps-tourney/issues/39):
  Trying to get Nexus back up & running.
    * Fixed. Ended up being a stupid-simple problem: all `/etc/apache2/sites-enabled` files now must have a `.conf` extension; any other files are ignored.
* 0.75h: [Issue #39: Cleanup Eclipse JPA validation, etc. errors](https://github.com/karlmdavis/rps-tourney/issues/39):
  Troubleshooting the case-sensitive `@JoinColumn` issue.
    * Still doesn't work in Hibernate 4.3.6.Final. Not surprised, as it wasn't in the release notes, but it's always good to check.
    * Added a workaround to the DB schema: change the column name via a `<property/>` switch.
    * Filed a bug for it: [HHH-9427: Errors when using mixed-case column name in @JoinColumn](https://hibernate.atlassian.net/browse/HHH-9427).

### 2014-10-03, Friday

* 0.5h: [Issue #39: Cleanup Eclipse JPA validation, etc. errors](https://github.com/karlmdavis/rps-tourney/issues/39):
  PostgreSQL sequences.
    * Started looking through things, hoping to commit them, and noticed the all-lowercase PostgreSQL sequence names I was using.
        * Tried to fix that.
        * Running into errors.
        * Looks like Liquibase is creating the sequences for me; the explicit create statements are redundant.
        * Hibernate is having trouble with the quoted sequence names.

### 2014-10-04, Saturday

* 2.5h: [Issue #39: Cleanup Eclipse JPA validation, etc. errors](https://github.com/karlmdavis/rps-tourney/issues/39):
  PostgreSQL sequences.
    * Confirmed: Postgres is generating automagic sequences. Removed the explicit ones from the schema.
    * Tried switching the sequence `@Entity` fields to `GenerationType.IDENTITY`.
        * Works for `Account`, fails for everything else.
        * Filed a bug report with Hibernate: [HHH-9430: Errors when using GenerationType.IDENTITY on PostgreSQL, when entity has more than one column](https://hibernate.atlassian.net/browse/HHH-9430).
        * Filed a bug report with Hibernate: [HHH-9431: Errors with mixed-case sequence name](https://hibernate.atlassian.net/browse/HHH-9431).
    * Got everything working.
    * Some of the DB schema changes would have been painful to model in Liquibase, so I didn't. I went ahead and broke backwards compatibility with the DB schema, and then took the opportunity to consolidate the Liquibase changelog.
    * Committed everything, closed the issue, etc.

### 2014-10-05, Sunday

* 0.50h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  JPA Modeling.
    * Added the `Account.name` field, and updated `getName()` to use it.
        * Had to fix some code to handle the possibly-`null` result from it.

### 2014-10-06, Monday

* 1.0h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Web service support.
    * Got a lot of the web service written, but left things incomplete and with compile errors.
    * Need to rename `updateAccount(...)` to `saveAccount(...)`, as the method should allow admins to create new accounts, too.

### 2014-10-07, Tuesday

* 0.5h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Web service support.
    * Finished implementing the web service (changed my mind on renaming the method, and on allowing it to create new instances).
        * Need to add some test coverage for the new DAO `merge(...)` method.

### 2014-10-08, Wednesday

* 0.2h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Web service support.
    * Added an IT for the new `merge(...)` DAO method.

### 2014-10-09, Thursday

* 0.1h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Web service support.
    * Checked in model, DAO, and web service code.
* 0.2h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Editable player name labels.
    * Started researching edit-in-place options.
    * What's a better UX: edit-in-place switcheroos, or popup editors?

### 2014-10-10, Friday

* 0.75h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Editable player name labels.
    * Still not sure if I want a popup or switcheroo, but I did decide that I want to ensure whatever I fallsback reasonably for non-Javascript users.
    * Started building out the HTML and CSS for that.

### 2014-10-11, Saturday

* 0.75h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Editable player name labels.
    * Wrote the JS to hide/show the player name form.
    * Need to figure out how to hint to the user that the label is clickable. Not sure on that...

### 2014-10-12, Sunday

* 2.0h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Editable player name labels.
    * Wrote the controller method for the form.
    * Got things (almost) working.
    * Next:
        * Fix the bug where the "onblur" JS prevents the form from being submitted if the user clicks the button (as opposed to just hitting enter).
        * Add test coverage to catch the `@Transactional` bug in the service that I missed.

### 2014-10-13, Monday

* 1.0h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Editable player name labels.
    * Fixed the "onblur" bug that prevented form submissions.
    * Fixed another bug where the `Account` merges were killing `AuthToken`s.
    * Next:
        * Add test coverage to catch the `@Transactional` bug in the service that I missed.
        * Add test coverage to ensure that `AuthToken`s aren't killed on merges.
        * Verify that Player 2 name changes work as expected.

### 2014-10-14, Tuesday

* 0.75h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Editable player name labels.
    * Added test coverage to catch the `@Transactional` bug in the service that I missed.
    * Added test coverage to ensure that `AuthToken`s aren't killed on merges.
    * Next:
        * Verify that Player 2 name changes work as expected.
        * Add webapp ITs for the new functionality.

### 2014-10-15, Wednesday

* 0.75h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Test coverage, cleaning up, etc.
    * Verified that multiplayer name changes work correctly.
    * Fixed a test compile error introduced by some of the earlier changes.
    * Added test coverage to `GameControllerTest`.
    * Next:
        * Add webapp ITs for the new functionality.
        * Add some sort of link back to the homepage.
        * Check all of the tables to see if there's anything that should be added to the schema now, e.g. more timestamps.
        * Add opponents' names to home page game list.

### 2014-10-16, Thursday

* 1.25h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Test coverage, cleaning up, etc.
    * Added webapp ITs for the new functionality.
    * Next:
        * Add opponents' names to home page game list.
        * Add some sort of link back to the homepage.
        * Check all of the tables to see if there's anything that should be added to the schema now, e.g. more timestamps.

### 2014-10-17, Friday

* 1.5h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Home page opponents' name display.
    * Created a tag for displaying the current user's opponent in a given game.
    * Added that tag to the home page's game list.
* 0.25h: [Issue #38: Allow web application users to provide a name or handle for themselves](https://github.com/karlmdavis/rps-tourney/issues/38):
  Cleaned up and committed things.
    * Next:
        * Add some sort of link back to the homepage.
        * Check all of the tables to see if there's anything that should be added to the schema now, e.g. more timestamps.
        * Fix the timestamps on the homepage's game list to look less stupid.

### 2014-10-18, Saturday

* 0.6h: [Issue #42: Check all of the tables to see if additional timestamps are needed](https://github.com/karlmdavis/rps-tourney/issues/42):
  Investigation and implementation.
    * Only ended up needing to add two timestamp fields. Happily, games already had sufficient timestamps.
* 0.6h: [Issue #40: Add a link to the homepage in the template](https://github.com/karlmdavis/rps-tourney/issues/40):
  Investigation and implementation.
    * Used Bootstrap's navbar to implement it. Didn't even try to make it semantic.
* 1.25h: [Issue #43: The template's footer doesn't layout correctly](https://github.com/karlmdavis/rps-tourney/issues/43):
  Investigation and resolution.
    * Made the footer non-sticky, as I decided I didn't like the stickiness (and it was a pain in the ass to get working correctly).
    * Switched the site's template from fluid containers to non-fluid containers.
* 0.75h: [Issue #41: The timestamps used in the homepage's games list look stupid](https://github.com/karlmdavis/rps-tourney/issues/41):
  Investigation and resolution.
    * Added the [PrettyTime](http://ocpsoft.org/prettytime/) library, updated the tag to support it, and updated the home page to use that as the formatter.
* 3.25h: [Issue #44: The rps-tourney-service-app ITs are all failing due to issues with Jetty/Spring classpath scanning](https://github.com/karlmdavis/rps-tourney/issues/44):
  Investigation and workaround.
    * Ran into this bug while attempting to test my timestamp enhancement.
    * Added a workaround to the POM: set `<useManifestOnlyJar>false</useManifestOnlyJar>`.
    * Filed the following Jetty bug to address the root cause: [Eclipse Bug 447790: Embedded Jetty server's classpath parsing/scanning broken with Maven Surefire's useManifestOnlyJar feature](https://bugs.eclipse.org/bugs/show_bug.cgi?id=447790).

### 2014-10-19, Sunday

* 3.0h: [Issue #46: 403 "CSRF token not found" errors when trying to update player name](https://github.com/karlmdavis/rps-tourney/issues/46):
  Investigation and resolution.
    * Took forever to diagnose this problem.
    * Created `SessionCookieConigurator`. Took **forever** to get it working correctly.

### 2014-10-20, Monday

* 0.25h: [Issue #45: Players unable to see opponents' last move when game ends](https://github.com/karlmdavis/rps-tourney/issues/45):
  Investigation.
    * At its root, I think this problem is caused by [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33).
        * My workaround/partial fix for that has a glitch that causes this new problem.
        * Rather than fixing the workaround, I'd rather fix the root cause, which will make the webapp's logic simpler: just display whatever the webservice returns.
* 0.25h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Investigation.
    * Created `GameSessionResourceImplIT.opponentsMoveNotRevealed()` to capture the problem.
    * How to solve this?
        * Why does the webservice return the full JPA `GameSession` entity? Seems like a bad idea: it's mutable, but the web service won't accept updates made to it, and it contains some information that ought to remain hidden.
        * Instead, I should pull a read-only interface out of `GameSession` and have the web service return that.
        * While I'm at it, I should rename `GameSession`. In football, what do you call the individual encounters between teams in a tournament? Either "matches" or just "games". Just going with `Game` would be simpler, I think.
        * Need to add estimates to the issue before I really start in on it.

### 2014-10-30, Thursday

* Missed a number of days: was busy at work with trying to get a release out, and had a business trip to Tucson in there, too.
* 1.0h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Implementation.
    * Put together a plan and estimate, listed in the bug comments.
    * Renamed `GameSession` to `Game` (and renamed related types, fields, etc. to match).

### 2014-10-31, Friday

* 0.2h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Implementation.
    * Committed yesterday's changes.
    * Started creating `GameView`.

### 2014-11-01, Saturday

* 1.25h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Implementation.
    * Refactored an `AbstractGame` superclass out of `Game`. This will be used to reduce code deuplication between `Game` and `GameView`.

### 2014-11-02, Sunday

* 0.35h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Implementation.
    * Implemented `GameView`. Still need to add unit tests for it.

### 2014-11-03, Monday

* 0.25h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Implementation.
    * Created `GameViewTest` to unit test the new `GameView`.
    * Updated `IGameResource` to only return `GameView`s, not `Game`s. Haven't updated its implementations yet.

### 2014-11-04, Tuesday

* 0.5h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Implementation.
    * Fixed all of the compile errors from the `IGameResource` refactor, and got all of the tests passing.
    * Need to remove my earlier workaround from the webapp.

### 2014-11-05, Wednesday

* 0.5h: [Issue #33: Web service (and app) allow players to see their opponent's move in the current round](https://github.com/karlmdavis/rps-tourney/issues/33):
  Resolved.
    * Removed the earlier "partial fix" from the webapp's JSP.
    * Cleaned up and committed things.

### 2014-11-06, Thursday

* 0.4h: Sat down and filed issues for the bugs/enhancements that are still blocking any sort of "go-live".

### 2014-11-07, Friday

* 0.25h: [Issue #49: Games do not have any sort of "Game Over: You Won/Lost" indication](https://github.com/karlmdavis/rps-tourney/issues/49):
  Design and implementation.
    * Wrote up a design in the issue's comments.
    * Started implementation.
* Side note: Just noticed that the `Game.State` enum needs to be moved up into `AbstractGame`.

### 2014-11-08, Saturday

* 1.0h: [Issue #49: Games do not have any sort of "Game Over: You Won/Lost" indication](https://github.com/karlmdavis/rps-tourney/issues/49):
  Implementation.
    * Got everything pretty much implemented.
    * Still needs some formatting help, e.g. maybe make the scores the same size as the throws?
    * Still needs some tests.
* Other things to fix:
    * Just noticed that the `Game.State` enum needs to be moved up into `AbstractGame`.
    * The homepage throws an exception when loading if there's a game.

### 2014-11-09, Sunday

* 0.25h: [Issue #49: Games do not have any sort of "Game Over: You Won/Lost" indication](https://github.com/karlmdavis/rps-tourney/issues/49):
  Implementation.
    * Played with the formatting some more. Still not 100% sure I like it...
    * Still need tests.
* Other things to fix:
    * Just noticed that the `Game.State` enum needs to be moved up into `AbstractGame`.
    * The homepage throws an exception when loading if there's a game.

### 2014-11-10, Monday

* 0.3h: [Issue #49: Games do not have any sort of "Game Over: You Won/Lost" indication](https://github.com/karlmdavis/rps-tourney/issues/49):
  Implementation.
    * Added some test coverage, got the tests passing.
    * Still need to clean things up and commit.
* Other things to fix:
    * Just noticed that the `Game.State` enum needs to be moved up into `AbstractGame`.
    * The homepage throws an exception when loading if there's a game.

### 2014-11-11, Tuesday

* 0.25h: [Issue #49: Games do not have any sort of "Game Over: You Won/Lost" indication](https://github.com/karlmdavis/rps-tourney/issues/49):
  Resolved.
    * Tweaked a little bit more.
    * Committed.
* Spent 0.25h on some miscellaneous stuff:
    * Moved the `Game.State` enum up into its own top-level type.
    * Tweaked the round history display a bit: display players' names in each row, rather than just "Player 1" or "Player 2".
    * Filed [Issue #52](https://github.com/karlmdavis/rps-tourney/issues/52).

### 2014-11-12, Wednesday

* 0.4h: [Issue #52: Error on home page: "javax.ws.rs.ProcessingException: java.net.ConnectException: ConnectException invoking http://localhost:8088/games/: Connection refused"](https://github.com/karlmdavis/rps-tourney/issues/52):
  Resolved.
    * Wrote a couple of ITs for `home.jsp` and then fixed the problem, which was with `GameOpponentTag`.

### 2014-11-13, Thursday

* 0.1h: [Issue #48: Round counts in game history don't account for ties](https://github.com/karlmdavis/rps-tourney/issues/48):
  Investigation.
    * Came up with a plan of attack and estimates.

### 2014-11-14, Friday

* 0.25h: [Issue #48: Round counts in game history don't account for ties](https://github.com/karlmdavis/rps-tourney/issues/48):
  Implementation.
    * Implemented the new `GameRound.getAdjustedRoundIndex()` method, added unit tests for it, and updated `game.jsp` to use it.
    * Except: getting errors when loading the game page after player 2 joins. My guess: `GameView` doesn't pass the `Game` to the rounds. Need to deal with that.
        * Also need to add tests to catch it and make sure that the `game.jsp` logic is working correctly. 

### 2014-11-15, Saturday

* 0.1h: [Issue #48: Round counts in game history don't account for ties](https://github.com/karlmdavis/rps-tourney/issues/48):
  Implementation.
    * Tried working around the "`GameRound` has a null `Game` reference" issue via JAXB's `@XmlID` and `@XmlIDRef` attributes.
    * Kinda' stymied by JPA issues. Need to think on it a bit.

### 2014-11-16, Sunday

* 0.5h: [Issue #48: Round counts in game history don't account for ties](https://github.com/karlmdavis/rps-tourney/issues/48):
  Resolved.
    * Reworked my implementation: `Game` now manages the calculations needed for `GameRound.getAdjustedRoundIndex()`.
    * Committed.

### 2014-11-17, Monday

* 0.35h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Design and estimation.
* 1.0h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Added a `GameController` method that might be able to return JSON (it probably isn't working yet).
    * Restructured the page template and JavaScript so that per-page JavaScript is possible.
    * Started stubbing out the JavaScript that will poll for game state.

### 2014-11-18, Tuesday

* 0.75h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Got JSON responses working.
    * Got the polling working.
    * Next, I need to start actually doing something with the result I get back.

### 2014-11-19, Wednesday

* 1.0h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Got player name and max rounds controls updating as expected.

### 2014-11-20, Thursday

* 0.4h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Got player score controls updating as expected.

### 2014-11-21, Friday

* 0.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Added support for updating Player 1's name.
    * Added support for updating the round counter.
    * Started adding support for updating the round history.

### 2014-11-22, Saturday

* 0.35h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Continued working on updating the round history.
    * Ran into the need for client-side translations again: how do I get translated strings in JavaScript?
        * Looks like the best option: [jquery-i18n-properties](https://github.com/jquery-i18n-properties/jquery-i18n-properties)
* 1.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Finished support for updating the round history.
    * Next: need to add support for the game won/lost logic.

### 2014-11-23, Sunday

* 0.4h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Started adding support for updating the win/loss displays.
        * Need to figure out how to expose the current user to JavaScipt: must be able to determine if the current user won or lost a given game.
    * Found bug: Round counter is "2" before Player 2 joins.

### 2014-11-24, Monday

* 0.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Finished support for updating the win/loss displays.
    * Next up:
        * Write the refresh timing logic.
        * Add ITs for all of the new functionality.
        * File bug: Round counter is "2" before Player 2 joins.

### 2014-11-25, Tuesday

* 0.75h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Implementation.
    * Implenented the exponential backoff for the refresh interval.
    * Next up:
        * Add ITs for all of the new functionality.
        * File bug: Round counter is "2" before Player 2 joins.

### 2014-11-26, Wednesday

* 0.1h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Tried to start implementing tests, but couldn't find any Selenium methods that explicitly cope with AJAX.
        * It looks like the "`findBy...`" methods have implicit waits, but I'm not sure how to write tests that wait for a value to appear. Maybe XPath?
        * Travelling and didn't have an Internet connection to look it up.

### 2014-11-27, Thursday

* 2.0h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Found some Selenium examples for AJAX tests.
    * Started implementing an IT for the new AJAX update functionality.
    * Ran into some content type negotiation isues in my Selenium tests (and, it turns out, also in `GameControllerTest`).
        * Selenium's `HtmlUnitDriver`, by default, emulates IE8's behavior and (effectively) sends "*/*" in its "Accept" header.
        * Spent a lot of time trying to figure out how Spring MVC handles content type negotiation.
        * After a lot of debugging, discovered it comes down to the following chunks of code:
            * `org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.lookupHandlerMethod(String, HttpServletRequest)`
            * `org.springframework.web.servlet.mvc.condition.ProducesRequestCondition.compareTo(ProducesRequestCondition, HttpServletRequest)`
            * `org.springframework.util.MimeType.compareTo(MimeType)`
            * Basically, given two methods that otherwise match, but produce different media types, the media types are sorted alphabetically and the winner selected that way. Ugh.
        * Need to file an enhancement request: Spring MVC should allow for media type priority to be set in an application.
        * In the meantime, need to customize Selenium and `GameControllerTest` to send a different "Accept" header.

### 2014-11-28, Friday

* 0.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Filed [SPR-12481: Mechanism needed to set default content type for application and/or classes, for content type overloading](https://jira.spring.io/browse/SPR-12481).
* 0.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Tested out the sample project that got posted for SPR-12481.
        * Looks like it works there: both when the two content types are specified on a single method, or on separate ones.
        * Started trying out the new Spring version with my code, to see if that is the cause of the fix, but didn't quite have a chance to finish that.

### 2014-11-29, Saturday

* 2.0h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Spent some more time trying to reproduce my problem in the sample project that was posted to SPR-12481.

### 2014-12-16, Tuesday

* Forgot to work on the project on November 30th, and then just took the opportunity for a break. Good times in Skyrim.
* 1.75h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Spent some more time trying to reproduce my problem in the sample project that was posted to SPR-12481.
    * Finally figured out what was going on and added a comment to the issue explaining it.

### 2014-12-17, Wednesday

* 0.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Cleaned up the test cases in `GameIT` a bit to fail when the webapp isn't loading, rather than error.
    * Added a workaround for SPR-12481 in `GameIT`.
    * Stuck trying to get my AJAX test to work with `HtmlUnitDriver`. Either the page has a bunch of CSS and JS errors, or HTMLUnit just isn't complete enough. Not sure which yet.

### 2014-12-18, Thursday

* 0.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Spent some more time trying to get AJAX tests to work with HtmlUnit.
        * Don't think this will happen: the tests end up using the parts of HtmlUnit that require its Jetty 8 libraries.
        * Those libraries conflict with the Jetty 9 dependencies on my classpath.
        * This leaves two options:
            1. Remove the embedded Jetty libraries from my classpath (e.g. download and run Jetty as a separate process).
            2. Stop using HtmlUnit. Use Firefox or somesuch, instead.
        * Actually, thought of another possibility: create `rps-tourney-dev-launchers` or somesuch, that isolates the Jetty dependencies.

### 2014-12-19, Friday

* 0.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Oh man... I fell into this same trap again:
        * Unpackaged Eclipse projects can't be shoved into Jetty correctly. Not really.
        * I mean, you can kind of fake it, but only kind of.
        * Well, how do actual embedded Jetty projects work? Are they shipping a JAR with a WAR in it and running the WAR? Hmmm...
    * Even if I *could* fake it well enough, maybe I should look into an Eclipse Tomcat plugin (again)?
        * It looks like running my WAR projects on Tomcat is already supported by the Eclipse WTP plugin. I just need a Tomcat instance to use with it.
        * I think the best next step here is to add Tomcat to my development environment setup script.
        * If that works out, I'll need to remove my old launchers, and the new module I was creating.

### 2014-12-20, Saturday

* 0.25h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Added Tomcat installation to `devenv-install.py`.
* 0.1h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Tried launching Tomcat, and it of course blew up.
        * Need to specify config file locations.
        * Will need to add HSQL to the WARs, if it isn't already there.

### 2014-12-21, Sunday

* 1.5h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Got Tomcat running the service and webapp correctly.
    * Had to make HSQL an actual/normal dependency of the service. Oh well.
* 1.25h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Testing.
    * Got the `GameIT` test cases all passing. Found and fixed a number of JS bugs in the process.
    * Next steps:
        * Need to clean up the changes (e.g. logging configuration) and commit them.
        * Need to look at SPR-12481 and see if I can clean it up and/or update it.

### 2014-12-22, Monday

* 2.0h: [Issue #51: Game state does not automatically refresh to display other player's actions](https://github.com/karlmdavis/rps-tourney/issues/51):
  Cleaning up and committing.
    * Responded to a new comment on SPR-12481.
    * Cleaned up everything and committed it. Marked the issue as resolved.
* 0.9h: Deployed the new version to https://justdavis.com/karl/rps and played around with it some.
    * Had to blow away and recreate the DB in there again.
    * Also had an odd problem where the Tomcat instance was trying to load a Pulse Audio library and failing. I'd guess a JDK update came along, and Tomcat didn't get restarted.
* 1.0h: [Issue #53: Current round counter is very goofy](https://github.com/karlmdavis/rps-tourney/issues/53):
  Documented, diagnosed, and resolved.
    * Fixed it and added a regression test case.

### 2014-12-23, Tuesday

* 2.25h: [Issue #50: Scary error when trying to make more than one throw in a round: "GameConflictException: Throw already set ..."](https://github.com/karlmdavis/rps-tourney/issues/50):
  Design, implementation, tests, commit.
    * Do I need to add in "nice" error handling for illegal moves in the game, e.g. throwing before start, throwing twice, etc.?
        * I like leaving the user's player controls active, because it's a simple visual cue to indicate which player they are.
        * I also do want to provide a reasonable experience for non-JavaScript browsers.
        * So: yes, I need to add in "nicer" error handling.
* 0.2h: [Issue #47: ArrayIndexOutOfBoundsException: Attempting to make a move in the webapp before the other player has joined](https://github.com/karlmdavis/rps-tourney/issues/47):
  Design, implementation, tests, commit.
    * Fixed it.

### 2014-12-24, Wednesday

* 0.9h: Release planning.
    * Is "create an actual account" functionality required for the first milestone release?
        * I'm imagining folks that want to play on their phone and computer, or their work computers and home computers.
        * Thinking about it, though... that use case is still somewhat supported without named accounts. Games are just "pinned" to a device without named logins.
        * Later, once this feature is added, it should be easy enough for folks to merge their device logins.
    * Is "invite someone to join your game" functionality required for the first milestone release?
        * No, but instructions on how to start a game are needed.
    * Is moving this to a separate domain name required for the first milestone release?
        * Not necessarily, no. It is a really good idea, though.
    * Are AI opponents required for the first milestone release?
        * Not necessarily, no. It might be a good idea, though.
    * Filed a ton of new issues in GitHub. Also filed the [2.0.0-milestone.1](https://github.com/karlmdavis/rps-tourney/milestones/2.0.0-milestone.1) release there.

### 2014-12-25, Thursday

* 1.0h: [Issue #56: Games on home page should be sorted by creation or last update timestamp](https://github.com/karlmdavis/rps-tourney/issues/56):
    * Implemented, added test case, committed.
* 0.4h: [Issue #61: New games should include some instructions](https://github.com/karlmdavis/rps-tourney/issues/61):
    * Implemented, committed. Test case doesn't seem necessary.
* 0.3h: [Issue #66: Error on home page after new game has been joined: "No message found under code 'home.games.game.state.WAITING_FOR_FIRST_THROW'..."](https://github.com/karlmdavis/rps-tourney/issues/66):
    * Implemented, added test case, committed.
* 0.5h: [Issue #67: Tomcat error: "java.io.NotSerializableException: com.justdavis.karl.rpstourney.service.client.CookieStore"](https://github.com/karlmdavis/rps-tourney/issues/67):
    * Found the `-Dsun.io.serialization.extendedDebugInfo=true` switch, which adds extra serialization debugging output to some of these errors.
    * Looks like the problem is the webapp's `GameClientBindings.cookieStore()` bean, which was marked as session-scoped.
        * Setting that to be request scoped instead broke 9 of the webapp's test cases.
        * Haven't investigated that yet. Need to.

### 2014-12-26, Friday

* 1.0h: [Issue #67: Tomcat error: "java.io.NotSerializableException: com.justdavis.karl.rpstourney.service.client.CookieStore"](https://github.com/karlmdavis/rps-tourney/issues/67):
    * Spent a lot of time poking through the code and researching.
        * Spring Security will place all Authentication objects into the session.
            * Haven't found a way to disable that yet. I'd be surprised if there even is, as remember me services aren't mandated. How else, if not sessions, would it keep track of who's logged in already and who hasn't?
        * Still not sure why switching the `CookieStore` to request scope is breaking things. Haven't looked at that too much yet.
        * Will probably need to get Tomcat and SLF4J logging playing nice together for that.

### 2014-12-27, Saturday

* 1.75h: [Issue #67: Tomcat error: "java.io.NotSerializableException: com.justdavis.karl.rpstourney.service.client.CookieStore"](https://github.com/karlmdavis/rps-tourney/issues/67):
    * Figured out how to configure Tomcat in Eclipse WTP. Documented it in `README-DEVENV.md`.
        * Got the application's logging configured correctly for Tomcat, as well.
* 0.75h: [Issue #67: Tomcat error: "java.io.NotSerializableException: com.justdavis.karl.rpstourney.service.client.CookieStore"](https://github.com/karlmdavis/rps-tourney/issues/67):
    * If I want to not save the `CookieStore` in session state, I'll need to do one of the following instead:
        1. Redesign the web service clients to not use a `CookieStore` at all, and to instead save the auth credentials directly.
        2. Rebuild the `CookieStore` on each request, starting from the saved auth credentials.
            * I'm not sure this one would actually be possible, since the ordering of `CookieStore` construction and authentication is backwards.
    * Seems easier for now to just make the `CookieStore` serializable.
        * Started on this, but it's complicated by the fact that the JAX-RS `Cookie` class itself isn't serializable.

### 2014-12-28, Sunday

* 2.25h: [Issue #67: Tomcat error: "java.io.NotSerializableException: com.justdavis.karl.rpstourney.service.client.CookieStore"](https://github.com/karlmdavis/rps-tourney/issues/67):
    * Made both `CookieStore` and `Account` serializable.
    * Added tests, committed, and resolved.

### 2014-12-29, Monday

* 0.2h: [Issue #63: Move the webapp to its own domain name](https://github.com/karlmdavis/rps-tourney/issues/63):
    * Decided to get a 3-year basic cert from gandi.net.
    * Started documenting the production setup in the `README-PRODUCTION.md` file.

### 2014-12-30, Tuesday

* 0.5h: [Issue #63: Move the webapp to its own domain name](https://github.com/karlmdavis/rps-tourney/issues/63):
    * Purchased an SSL certificate.
        * Waiting for validation (via DNS record) to complete.
* 0.2h: [Issue #68: Add GitHub link to webapp](https://github.com/karlmdavis/rps-tourney/issues/68):
    * Implemented, committed, and resolved.
* 2.0h: [Issue #63: Move the webapp to its own domain name](https://github.com/karlmdavis/rps-tourney/issues/63):
    * Got the SSL certificate installed.
    * Got Apache configured.
        * Documented this in the `README-PRODUCTION.md` file.
        * Not seeing error messages when I try to throw before game start, so something may still be off there.
* 0.25h: [Issue #70: "View Page Source" in Firefox displays JSON](https://github.com/karlmdavis/rps-tourney/issues/70):
    * Fixed, committed, resolved.
* 1.75h: [Issue #71: Games not displaying warning messages in production](https://github.com/karlmdavis/rps-tourney/issues/71):
    * Working on the problem.

### 2014-12-31, Wednesday

* 2.25h: [Issue #71: Games not displaying warning messages in production](https://github.com/karlmdavis/rps-tourney/issues/71):
    * Tracked down and resolved the bug. Committed and resolved.
* 1.6h: [Issue #72: Add an edit icon/button to the current player's name](https://github.com/karlmdavis/rps-tourney/issues/72):
    * Found and resolved a problem with the POM's handling of Bootstrap fonts in Eclipse WTP.
    * Added the new edit icon, committed, and resolved.
* 0.5h: [Issue #72: Add an edit icon/button to the current player's name](https://github.com/karlmdavis/rps-tourney/issues/72):
    * Reopened due to test failures: https://justdavis.com/jenkins/job/rps-tourney/com.justdavis.karl.rpstourney$rps-tourney-webapp/137/testReport/
    * Found and fixed the problems. Committed and closed.
* 2.0h: [Issue #69: Update README prior to the 2.0.0-milestone.1 release](https://github.com/karlmdavis/rps-tourney/issues/69):
    * Updated the main `README.md` file.
    * Added a `README-ARCHITECTURE.md` file.
* 0.6h: [Issue #72: Add an edit icon/button to the current player's name](https://github.com/karlmdavis/rps-tourney/issues/72):
    * Found and resolved a problem where JS updates were removing the edit icon.
    * Then fixed the bug and test case failure that change caused.
* 0.5h: [Issue #73: Webapp 404s when setting max rounds](https://github.com/karlmdavis/rps-tourney/issues/73):
    * Added a test case, resolved, commited, and closed.
* 1.25h: Released [2.0.0-milestone.1](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A2.0.0-milestone.1)!
    * This time included the work to resolve [Issue #74: Unable to run release:prepare](https://github.com/karlmdavis/rps-tourney/issues/74).

### 2015-01-01, Thursday

* 0.75h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Started fleshing out the "Your Account" page.

### 2015-01-02, Friday

* 0.75h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Finished the "Your Account" page's implementation. No tests yet.

### 2015-01-03, Saturday

* 0.25h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on committing things.
    * Fixed a minor bug in the old `login.jsp`. No idea if it actually works now, though.

### 2015-01-04, Sunday

* 2.1h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Fixed a problem with `SecurityConfig`: login and logout pages **required** users to already be authenticated.
        * Reformatted the config there to read easier.
        * Added a couple of lengthy comments explaining how things are configured and why.
    * Tweaked `login.jsp` and `account.jsp` some.
    * Started creating the "Register an Account" page.
        * Got it mostly laid out.
        * Doesn't do anything yet.

### 2015-01-05, Monday

* 1.0h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got registration working.
    * Fixed a bug where redirecting to the home page with a new `Account` would throw an exception.
        * Was caused by `getGamesForPlayer(...)` calling `findOrCreateAPlayer(...)` without a transaction.
        * Need to create a test case for that, and also for the new DAO method added.

### 2015-01-07, Wednesday

* Just completely forgot to touch the side project yesterday.
* 0.25h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Added test cases, cleaned up, and committed the fix for the bug in `getGamesForPlayer(...)`.

### 2015-01-08, Thursday

* 1.35h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Started thinking about how a login/logout control in the template should look.
        * I think a top-right chunk of text that switched between "Signed in as Foo" and "Login here" is a decent start.
        * Maybe only put a logout control on the account details page. Not something I want folks using that often.
        * Didn't implement any of this yet; just thinking through it.
    * Created `AuthenticationIT` and started filling in tests for it.
        * Found and fixed a few bugs that it exposed, at various layers.

### 2015-01-09, Friday

* 0.1h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Cleaned up and committed a few things.
* 1.0h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Ran the build, fixed all the failures it had, cleaned things up, and got everything thus far committed.
* 1.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Created `AccountNameTag`.
        * No tests yet, and adding them will probably require some refactoring.
    * Added a sign in/account control to the template.
        * No tests yet.
    * Next steps:
        * Add tests for those two items.
        * Start implementing account merges on log in & registration.
        * Add a log out link to the account page.

### 2015-01-10, Saturday

* 2.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Wrote the tests for the sign-in/account control and such.
        * Spent a lot of time figuring out how to handle mock Spring injection in `AccountNameTag`.

### 2015-01-11, Sunday

* 0.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Just thinking through the merging design...
    * How to handle merging during login and registration?
        * The page should know whether or not the user is currently logged in and whether that account has a guest login but no game login.
        * Idea 1: If that's the case, it should have an enabled-by-default checkbox to "Merge this device's XX games into new login."
        * Idea 2: Keep track of all past guest accounts in a client-side cookie.
            * List those logins in a panel on the `/account` page, with a "Merge" button for each.
        * I like that, with Idea 2, the clients are tracking something more than just their session ID.
            * This would make it easier for me to migrate things smoothly if the game ever transitions to a JavaScript-only application.
    * How to handle the merges themselves?
        * Idea 1: Replace the `Player` on all of the old `Account`'s games, point the old guest login to the new `Account`. Delete the old `Account`, etc.
            * Pros: Least amount of impact on other things, due to not creating duplicate `Player`s.
            * Cons: If I want to audit those operations, I have to track them in a separate table.
        * Idea 2: Leave the old `Account`'s `Player` instances, but point them and the old guest login at the new `Account`.  Delete the old `Account`, etc.
            * Pros: The splicate `Player`s become a sort of audit log themselves.
            * Cons: Seems like a bad idea to have duplicate `Player`s.
        * I think I'll go with Idea 1, and ensure I add in the tables, etc. to audit those operations.
        * Note: The game logic almost certainly will break once I allow for player 1 and 2 to be represented by the same `Account`. However, this seems like the kind of thing I want to support anyways (e.g. to allow AIs to play against themselves).
            * That could really complicate statistics, though, as it'd affect win/loss percentages.
            * Let's say a given AI has only ever played three games, all against itself. What's it's win/loss percentage? I think I'd have to exclude such games from those calculations, and the percentage would be "N/A", same as for any new account.

### 2015-01-12, Monday

* 0.1h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Added the `IAccountsResource.getLogins()` method, but did not implement it.
    * Something to fix: `GuestLoginIdentity` has no timestamps.

### 2015-01-13, Tuesday

* 0.1h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Started implementing `AccountsClient.getLogins()`.
        * Likely going to have trouble with JAX-B and the logins interface.

### 2015-01-14, Wednesday

* 0.35h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * In order to query for logins via JPA, I need to decide on one of the following options:
        * Manually query each separate implementation and glue the results back together.
        * Give all of the logins a common abstract base class.
    * Leaning towards the second option, but need to investigate the mapping options (e.g. `@MappedSuperclass`) and how well Hibernate supports queries with them.

### 2015-01-15, Thursday

* 0.25h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Did some research: can't use `@MappedSuperclass`, as it won't allow for the kinds of queries I'd like to do.
        * Thinking of using `@Inheritance(strategy=InheritanceType.JOINED)`, instead.
        * The really tricky part will be the DB upgrade.
        * I'm really annoyed that guest logins don't have creation timestamps. Guess I'll have to default those to the upgrade date-time.

### 2015-01-16, Friday

* 0.2h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Created `AbstractLoginIdentity`. Haven't yet set it as the superclass for anything.

### 2015-01-17, Saturday

* 0.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Just realized: guest logins don't actually contain the auth tokens they're supposedly associated with. That might be very bad?
        * As things stand, guest logins have absolutely 0 extra fields associated with them: they're the same as the base class.
    * Set the game and guest logins to extend `AbstractLoginIdentity`.

### 2015-01-18, Sunday

* 0.25h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Tried to write the Liquibase update script.
        * Stuck figuring out how to merge guest and game login IDs.

### 2015-01-19, Monday

* 0.35h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Realized that game & guest logins don't have to be merged: there won't be any game logins at all (yet).
    * Got the Liquibase update working for HSQL DB (I think), but still have PostgreSQL issues to resolve.

### 2015-01-20, Tuesday

* 0.25h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * The PostgreSQL issues look like a Liquibase bug.
    * Tried updating to the latest Liquibase version, but that causes compilation issues. Need to resolve those and see what happens.

### 2015-01-21, Wednesday

* 0.25h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Fixed the problem that was causing PostgreSQL failures.
    * Ran a build, turned up all kinds of problems.
    * Still need to add comments to the Liquibase changes.

### 2015-01-24, Saturday

* Got slammed with work and traveling to San Antonio on Thursday, and forgot to work on this.
* Was too busy and tired from PAX South yesterday to even think about it.
* 0.2h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Cleaned up the Liquibase change log a bit.

### 2015-01-25, Sunday

* 0.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got the build passing again. Was just more problems with the Liquibase change sets.

### 2015-01-26, Monday

* 1.0h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Cleaned up and committed:
        * The Liquibase version update in `jessentials-misc`.
        * The creation, etc. of `AbstractLoginIdentity`.
    * Implemented the DAO and web service method to return the logins. Web service method failing its IT, though.

### 2015-01-27, Tuesday

* 1.1h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Created the `LoginIdentities` JAXB wrapper class.
    * Need to update the web service to use it.

### 2015-01-28, Wednesday

* 0.1h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got `IAccountsResource.getLogins()` implemented and working.

### 2015-01-29, Thursday

* 0.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Committed the changes put in place to add `IAccountsResource.getLogins()` to the web service.
    * Spent some time figuring out next steps.
    * Need to customize the login form to allow for merging accounts.
        * Looks like I can either:
            * Add a "login success handler" that processes extra form fields.
            * Completely reimplement the Spring Security login interceptor.
        * Not sure which of those two is the right move. Maybe the first one, for now?

### 2015-01-30, Friday

* 0.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Tried adding a controller for the login page `GET`.
        * Doesn't appear to be working. I'd guess it can't work.
        * Need to look up examples of weird login schemes with Spring Security, to find one that requires a custom controller.

### 2015-01-31, Saturday

* 1.5h: [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Something in Spring or Spring Security prevents me from providing a "/login" controller, even if form login is disabled.
        * Think I finally found an article that discusses this: [Spring 3 and Spring Security: Setting your Own Custom /j_spring_security_check Filter Processes URL](http://mark.koli.ch/spring-3-and-spring-security-setting-your-own-custom-j-spring-security-check-filter-processes-url).
        * And this article provides an example of Java-based customization of Spring Security filters: [Spring Security Custom FilterChainProxy using Java Configuration](http://shazsterblog.blogspot.com/2014/07/spring-security-custom-filterchainproxy.html).
    * Here's another problem: the `AuthToken` cookies aren't being cleaned up when users login or register.
        * Doesn't make much sense for the session to be authenticated for one account, while the requests are carrying around an `AuthToken` for a different account.
        * And, unfortunately, those `AuthToken` cookies weren't ever being used: Spring Security ignores them if there's an active session.
        * Just confirmed: they **are** used if the session cookie is removed.
    * Really not sure which way to go with this...
        * Need to sit down and first write out how the whole mess should work.

### 2015-02-01, Sunday

* 0.25h (9:37-9:52): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Authentication design:
        * Accounts are already automatically merged on registration.
        * Users only need to be offerred the option to merge during sign in.
            * And the merge option only makes sense if they've signed in to an account different from the guest one.
        * Perhaps the right way to handle this is to redirect after a successful login when merging is an option.
            * The login success handler should remove the `AuthToken` cookie if the user logged in with a different account, and if so, also redirect users to the interstitial merge page.
            * The interstitial page should use request params, rather than form params, so that it plays nice with browser history.
    * I really ought to rename the login types to "DeviceLogin" and "EmailLogin".
        * Except that it isn't really a "device" login, as different browsers, different users, etc. don't share it. Hmm...

### 2015-02-02, Monday

* 0.35h (21:25-21:47): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Stubbed out `GameLoginSuccessHandler` and wrote a very detailed class JavaDoc comment for it.
        * This comment tweaks the design I laid out yesterday some. Makes things a bit more robust.

### 2015-02-03, Tuesday

* 0.15h (23:55-00:04): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Started to flesh out `GameLoginSuccessHandler`. Didn't get too far-- pretty tired.
    * Might want to refactor `AuthToken`s to be associated with `GuestLoginIdentity`s, rather than `Account`s pretty soon. Just something to consider.

### 2015-02-04, Wednesday

* 0.4h (22:15-22:39): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Fleshed out `GameLoginSuccessHandler` some more. Stopped at an awkward spot. Oh well.

### 2015-02-05, Thursday

* 0.3h (22:12-22:30): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Fleshed out `GameLoginSuccessHandler` some more. Decent progress.

### 2015-02-06, Friday

* 0.4h (22:49-23:13): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Okay, I just figured out a reason for `AuthToken`s to be separate from guest logins:
        * Authenticating with a game login sets an `AuthToken` cookie.
        * That's how the web service clients actually *stay* authenticated.
    * This will force me to rejigger my design a bit.
        * The `GameLoginAuthenticationProvider` will have to grab the "old" `AuthToken` and save it in a cookie to allow for merging it later.
        * In fact, I really don't even need `GameLoginSuccessHandler` at all: that functionality can all be handled in the auth provider.
    * Stopped partway into fixing that.

### 2015-02-07, Saturday

* 0.25h (23:15-23:30): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got things implemented a bit further.

### 2015-02-08, Sunday

* 0.5h (18:50-19:19): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Oh, for crying out loud: there's no simple way to inject the `HttpServletResponse` in non-controller beans with Spring MVC.
        * Now I'm back to needing the auth success handler.
        * Except I'm not sure that I have any way to pass the previous login to it...
    * Wandered across this random and cool article on stateless authentication with Spring Security: [Spring Security - Stateless Cookie Based Authentication with Java Config](http://sleeplessinslc.blogspot.com/2012/02/spring-security-stateless-cookie-based.html).
        * Might be useful in the future.

### 2015-02-09, Monday

* 0.75h (21:11-21:56): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got `GameLoginSuccessHandler` mostly implemented. I think. Provided I don't end up having to throw out the design. Again.

### 2015-02-10, Tuesday

* 0.15h (20:31-20:41): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Finished implementing `GameLoginSuccessHandler`. (Hopefully.)
    * Next up: Need to redirect users to the account page, and add "merge device history" controls to it.

### 2015-02-11, Wednesday

* 0.5h (20:40-21:10): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got the redirection working, and wired up the `GameLoginSuccessHandler` to actually run.
    * Spent some time trying to test things, but the behavior seems to be way off.
        * Not seeing `AuthToken` cookies.
            * In FF, I only checked this when registering (which wouldn't set them, right now). Should check anon logins, too.
        * Oddities in Chrome:
            * Can't set player name in games.
            * After starting an anonymous game, the "Sign In" button is still displayed. Auth isn't working in Chrome?

### 2015-02-12, Thursday

* 1.0h (6:13-7:14): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Spent a lot of time trying to figure out what the problem is.
    * Chrome **definitely** was not handling the `JSESSIONID` cookies as expected. Spent some time fixing that.
    * The game still isn't working correctly after that, though.
    * Just noticed: seems to work, at least better, in production.

### 2015-02-13, Friday

* 0.5h (15:53-15:23): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Still not sure what's going on, but have learned a bit more:
        * Enabled Tomcat session logging, and it looks like the browser sessions are getting expired **very** quickly.
        * The default `SessionCookieConfig` for Tomcat doesn't set a max age, so cookies are session-based rather than persistent.
        * Things are working correctly in Firefox still. Perhaps the `AuthToken` cookie is saving the day for me there? (Since FF accepts cookies with a `localhost` domain.)

### 2015-02-14, Saturday

* 1.35h (8:28-9:49): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Tried disabling the `AuthToken` cookie, to see what happens in FF. I end up with the same behavior as in FF.
    * Watching the Tomcat session log, it seems to be expiring sessions almost immediately.
        * I was misreading that, actually. It's just *checking* for sessions to expire, but is not finding any.
    * Found the bug! The guest login manager, `DefaultGuestLoginManager`, was creating Spring Security `Authentication` instances, but was never applying them to the user's session.
        * The only reason this was ever working is that redirect requests were picking up the `AuthToken` cookie, and the `CustomRememberMeServices` *was* authenticating the session correctly.
        * Added some unit test coverage. Should probably also add FF and Chrome to the ITs, but... that will just make them a whole lot more obnoxious to run.
    * Next steps:
        * File a separate issue for adding browsers to the ITs.
        * Finish fixing the compile errors in the tests.
        * Add tests for `CookiesUtils` that ensure the cookie security properties are correct.
        * Add tests for `GameLoginSuccessHandler`... possibly.

### 2015-02-15, Sunday
 
* 0.4h (15:13-15:36): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Got the compile errors fixed in Eclipse, ran a build, and fixed a couple of compile errors that turned up.
        * Hadn't run a build on the command line since 2015-01-25. Interesting.
        * Still have some test failures from there to fix: `SessionCookieConfiguratorTest`.

### 2015-02-16, Monday
 
* 0.15h (11:16-11:26): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Got the build passing.

### 2015-02-17, Tuesday
 
* 0.5h (22:46-23:14): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Got things committed.
    * Marked the issue as resolved. Probably a tad premature. Still need to:
        * Add unit tests for `CookiesUtils`.
        * Look through the commit and see if there was anything that should have been cleaned up.

### 2015-02-18, Wednesday
 
* 0.3h (22:30-22:48): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Started implementing `CookiesUtilsTest`.
    * Looked through the commit to see if there was anything that should have been cleaned up. Didn't find much.

### 2015-02-19, Thursday
 
* 0.25h (21:50-22:04): [Issue #76: Users are not signed in as guests when starting a new game in Chromium](https://github.com/karlmdavis/rps-tourney/issues/76):
    * Finished `CookiesUtilsTest`.

### 2015-02-20, Friday
 
* 0.2h (21:16-21:25,23:33-23:40): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Things left to do on this issue:
        * Test the `GameLoginSuccessHandler` manually.
        * Ensure that there are unit and integration tests for `GameLoginSuccessHandler`.
        * Ensure that registration and login set `AuthToken` cookies.
        * If it's okay for registration to auto-merge anonymous accounts, why isn't the same true for login?
        * Add "merge device history" controls to the account page.
    * Tried testing `GameLoginSuccessHandler`: logged in as anon in FF, registered via Chrome, tried to login to game account in FF.
        * Didn't have a merge cookie, so it appears to not be working.
        * I'm pretty tired, though, so maybe I'm just missing something.

### 2015-02-21, Saturday
 
* 0.9h (8:48-9:43): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Starting an anon game and then registering works as expected.
    * Confirmed yesterday's bug: `GameLoginSuccessHandler` doesn't seem to be running at all.
        * Fixed this and got it working as expected via manual testing.
    * Also need to add a logout link somewhere, probably the account page.
    * Really need to think about this: If it's okay for registration to auto-merge anonymous accounts, why isn't the same true for login?

### 2015-02-22, Sunday
 
* 0.4h (22:05-22:28): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Here's another thought: Ideally, it'd be possible for admins to merge user's accounts.
        * In order for admins to be able to do this, the DB really ought to track associations between accounts.
        * For example: "Device with anon account A logged into Account B".
        * Yeah... I think that, perhaps, tracking this stuff only with a cookie is a poor choice.
    * Would it be better to store these links in a single-purpose `AccountAssociations` table, or in a more general purpose `AccountEvents` table that can be used for general auditing? Or both?
        * PostgreSQL 9.3 (the version on `eddings`) has a `json` type, but doesn't really support indexing it.
        * PostgreSQL 9.4 is only available from the PostgreSQL project's APT repo, not from the default Trusty repos.
        * I should probably also be tracking `Account` property changes, e.g. when users change thier name.
    * Need to think about all of this some more...
    * (No code written tonight. Just spent time thinking through all of the above.)

### 2015-02-23, Monday
 
* 0.5h (22:10-22:41): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Should auditing be done at the DB level or app level?
        * What auditing events could I actually catch at the DB level? It's not like there's a DB "login" method.
        * App level, it is.
    * It's tempting to use JSON to store audit events, because it's just so much simpler right now.
        * But it makes my IT situation worse, as I'll likely have to upgrade to PostgreSQL 9.4.
        * And what if the auditing data or schema changes in the future? Migrating all of that will suck a lot more than an explicit SQL schema.
    * Most of the discussions on the internet seem to revolve around tracking data changes.
        * But for this application, most data that's changed captures it's own history. For example: game moves.
    * I think the best option is to make separate `Audit*` tables for each operation I want to track.
        * This is the only operation that's both performant and provides a realistic schema upgrade path.

### 2015-02-24, Tuesday
 
* 0.25h (21:59-22:15): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * I started creating an `AuditAccountAssociation` class. While writing the JavaDoc for it, I had a thought:
        * I'm not 100% comfortable with automatically merging accounts on login, because:
            * Users perhaps didn't intend that to happen.
            * There's no way to undo it.
        * But now I'm thinking that, as long as I track the info needed to undo it, I'm actually *mostly* comfortable with doing it automatically.
        * I think I'll just create an `AuditAccountMerge` class, instead.
     * Started creating `AuditAccountMerge`.

### 2015-02-25, Wednesday
 
* 0.2h (23:35-23:47): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on `AuditAccountMerge` a bit more.

### 2015-02-26, Thursday
 
* 0.3h (21:13-21:33): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on `AuditAccountMerge` a bit more. Created `AuditAccountGameMerge`.

### 2015-02-27, Friday
 
* 0.4h (21:47-22:09): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * During an `Account` merge, should `Player` instances be modified or replaced and then deleted?
        * Replaced and then deleted. No reason to accumulate extra instances. They'd just end up confusing things.
    * Think I got the fields for the two new entities worked out.

### 2015-02-28, Saturday
 
* 0.1h (01:40-01:46): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * (Ended up working on the project after midnight, as I got distracted by books earlier in the day. Will fudge the commit times, though.)
    * Started on the Liquibase changelog updates for the new entities.

### 2015-03-01, Sunday
 
* 0.1h (00:04-00:10): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * (Ended up working on the project after midnight, as I got distracted by books again. Will fudge the commit times, though.)
    * Continued work on the Liquibase changelog updates for the new entities.

### 2015-03-02, Monday
 
* 0.15h (22:41-22:44,23:30-23:36): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Completed the Liquibase changelog updates for the new entities (didn't test them, though).

### 2015-03-03, Tuesday
 
* 0.35h (21:39-22:00): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Completed the `AuditAccountMerge` and `AuditAccountGameMerge` classes (I think).

### 2015-03-04, Wednesday
 
* 0.1h (20:38-20:44): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got the build passing again.
    * Need to make a "work in progress" commit so that I don't lose my attempt to handle login account merges via cookies-- just in case I end up needing to revisit that idea later.

### 2015-03-05, Thursday
 
* 0.2h (22:10-22:24): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Committed my work-in-progress code that handles merges via a cookie and an account page button (never created the button).
        * Just want to have it in the history, in case I end up deciding that's actually the right way to go.

### 2015-03-06, Friday
 
* 0.25h (18:00-18:16): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Stubbed out the `IAccountsResource.mergeFromDifferentAccount(Account)` method.
    * Updated `GameLoginSuccessHandler` to use it, and updated its JavaDoc, etc. to reflect that new behavior.

### 2015-03-07, Saturday
 
* 0.1h (21:11-21:17): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Implemented `AccountsClient.mergeFromDifferentAccount(Account)`.

### 2015-03-08, Sunday
 
* 0.5h (21:53-22:21): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Whoops. Had to redesign the web service method's signature. It's now `IAccountsResource.mergeAccount(long, UUID)`. 

### 2015-03-09, Monday
 
* 0.3h (20:21-20:38): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * (Re-)Implemented the client method.

### 2015-03-10, Tuesday
 
* 0.4h (20:18-20:40): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
    * Need to add an `IAccountsResource.isAccountAnonymous()` method.

### 2015-03-11, Wednesday
 
* 0.15h (23:11-23:20): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.

### 2015-03-12, Thursday
 
* 0.25h (22:45-23:01): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.

### 2015-03-13, Friday
 
* 0.2h (22:43-22:55): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Need to review it, but I think the service implementation is complete, excepting all of the methods it needs that don't yet exist.
        * Should probably write an IT for it first, given how stupidly complex it is.
        * Also need to go through and add some detailed comments, and perhaps restructure things some.

### 2015-03-14, Saturday

* 0.15h (00:30-00:38): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * (Ended up working on the project after midnight, as I spent most of the day reading. Will fudge the commit times to earlier, though.)
    * Worked on the server-side implementation.
        * Reviewed the code, finding and fixing a security bug in the process.
        * Tweaked some little stuff.

### 2015-03-15, Sunday

* 0.15h (20:35-20:44): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Reviewed the code again, found and fixed some more bugs.

### 2015-03-16, Monday

* 0.1h (21:23-21:30): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Started implementing some of the missing JPA stuff.

### 2015-03-17, Tuesday

* 0.15h (21:18-21:21,21:51-21:56): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.

### 2015-03-19, Thursday

* Forgot to work on this yesterday. No real reason... just got distracted.
* 0.2h (22:10-22:22): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.

### 2015-03-20, Friday

* 0.1h (23:05-23:10): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.

### 2015-03-21, Saturday

* 0.2h (22:59-23:10): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Fixed the last of the compile errors.
        * Verified that Tomcat still launches, and the webapp's home page works (didn't test further than that).
        * Ran a build, and encountered some errors there that will need to be fixed.

### 2015-03-22, Sunday

* 0.1h (21:00-21:06): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got builds passing again.

### 2015-03-23, Monday

* 0.7h (22:32-22:35,23:04-23:43): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Manually tested things and worked my way through a lot of bugs.
            * Didn't finish: still have more bugs to find & fix.
            * Really, really need automated tests here. Must have fixed over a dozen bugs that tests could have caught.

### 2015-03-24, Tuesday

* 0.1h (22:50-22:57): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Manually tested things and fixed the last bug preventing login account merges from working.
    * Next step: Really, really need automated tests here.

### 2015-03-25, Wednesday

* 0.3h (22:22-22:42): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Started adding test coverage. Didn't get very far.

### 2015-03-26, Thursday

* 0.3h (23:11-23:30): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Continued working on the test coverage. Didn't get very far.
        * Need to decide whether it's a security problem for the marshalled version of `AuditAccountMerge` to contain non-safe-view `Game` instances.

### 2015-03-26, Thursday

* 0.3h (23:11-23:30): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Continued working on the test coverage. Didn't get very far.
        * Need to decide whether it's a security problem for the marshalled version of `AuditAccountMerge` to contain non-safe-view `Game` instances.

### 2015-03-27, Friday

* 0.55h (21:27-21:59): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * So `AuditAccountMerge` instances reference/contain `Game` instances.
            * Those `Game` instances are security-sensitive.
            * So far, the only use case for marshalling `AuditAccountMerge` is as part of the `merge(...)` response.
            * I can't really customize the XML marshalling in any meaningful way, without preventing unmarshalling.
            * Can I switch the fields to just contain `Game` IDs, rather than full references? I think this is probably the best option.

### 2015-03-28, Saturday

* 0.2h (22:01-22:11,23:33-23:36): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Thinking about the design question from yesterday... I still don't even know if these objects will ever be used in the UI.
            * Until I have a use case for that, I don't think spending time on the JAXB representation is worthwhile.
        * Finished the unit tests for `AuditAccountMerge`, but didn't yet update the web service to not return them, or remove the JAXB annotations.

### 2015-03-29, Sunday

* 0.1h (23:41-23:47): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Updated the web service to no longer return `AuditAccountMerge` instances, and removed the JAXB annotations from those classes.

### 2015-03-30, Monday

* 0.35h (21:13-21:33): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Added some DAO tests.

### 2015-03-31, Tuesday

* 0.35h (21:35-21:57): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Finished the DAO tests and started on the web service tests.

### 2015-04-01, Wednesday

* 0.25h (21:58-22:12): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Finished the web service tests.

### 2015-04-02, Thursday

* 0.4h (22:44-23:06): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Added a webapp test, to ensure merging accounts on login works as expected.

### 2015-04-03, Friday

* 0.25h (22:06-22:21): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Started creating `GameLoginSuccessHandlerTest`.
        * Need to move `AuthenticationIT` to the `security` package.

### 2015-04-04, Saturday

* 0.35h (10:16-10:37): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Worked on `GameLoginSuccessHandlerTest`.
        * Need to resolve the DI issue in `GameLoginSuccessHandler`.

### 2015-04-05, Sunday

* 0.25h (22:30-22:45): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * On abstracting `AccountsClient` to have a factory...
            * Why does it need a factory in the first place? Because sometimes, a user ought to be allowed to impersonate a different account.
            * Impersonation seems like a general concern that I might want to have a general mechanism for, especially once I start adding admin functions.
            * However, I won't be adding admin functions anytime soon and I think it's best to treat this as a once-off for now.
        * Stubbed out `IAccountsClientFactory`, but have not yet implemented it or refactored things to use it.

### 2015-04-06, Monday

* 1.25h (10:50-12:07): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Worked on the server-side implementation.
        * Finished `GameLoginSuccessHandlerTest`.
        * Next up: anything else to test? If not, need to go clean up the accounts page, and deliver.

### 2015-04-07, Tuesday

* 0.25h (23:32-23:48): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Fixed a DI error and verified that the build now passes.
    * Removed the "insert merge history here" note in the accounts page. Not sure that anyone would find that useful.
    * Next up: Rework the login control to still allow for registration when users are authenticated anonymously.
        * May need to add an `isAnon` method somewhere... hmmm.

### 2015-04-08, Wednesday

* 1.0h (20:29-21:28): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Updated the webapp template such that the account control only shows as logged in for non-anonymous accounts.
        * This likely maps pretty well with how users would understand things.
    * Started refactoring `Account` to also contain its logins. Will be tricky to get JAXB working with this, but should be possible.

### 2015-04-09, Thursday

* 0.55h (22:01-22:39): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Continued refactoring `Account` to also contain its logins.
        * Not sure if I'll be able to make JAXB handle the circular references in a sane & useful fashion.

### 2015-04-10, Friday

* ?h (23:34-?): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Continued refactoring `Account` to also contain its logins.
        * Fixed marshalling in `AccountTest`. Still need to update the unmarshalling test case.

### 2015-04-11, Saturday

* 0.1h (13:00-13:06): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Continued refactoring `Account` to also contain its logins.
        * No luck with `@XmlTransient` preserving cyclic references during unmarshalling.
        * Need to read the following and look for other possible solutions: http://forums.java.net/jive/thread.jspa?threadID=13670.

### 2015-04-12, Sunday

* 0.4h (11:47-12:23): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Finished refactoring `Account` to also contain its logins.
    * Got `AccountTest` passing.
    * Removed all of the various `getLogins...` methods (DAO and service).
    * Need to remove the "isMergeable" junk from `LoginController` (not sure if it ever worked, anyways).

### 2015-04-13, Monday

* 0.4h (23:21-23:44): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Removed `LoginController` after verifying that it isn't actually being called/used.
    * Fixed `accountControl.tag`.
    * Ran a build, which found some problems in `GameIT` that I haven't yet fixed.

### 2015-04-14, Tuesday

* 0.25h (23:23-23:39): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Spent time triaging the issue that's failing the builds.
        * It's an unfortunate problem: the cycle-prevention workarounds I put in place for JAXB don't affect the JSON representations of the model objects.
        * To solve this, I think I'll need to switch CXF to using Jackson instead, and possibly customize it some.
        * Did not have time to actually do that.

### 2015-04-15, Wednesday

* 0.4h (22:40-23:15): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Updated POM to include Jackson's JAX-RS provider.
        * Things are failing due to the Nexus certificate change.
    * Started updating `SpringConfig` to use Jackson, but can't finish until I get the Nexus issues resolved.

### 2015-04-16, Thursday

* 0.9h (20:08-21:01): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got Nexus working (updated the devenv instructions).
    * Got Jackson enabled.
    * Need to figure out how to rebuild the cycle after deserialization with Jackson. Found a couple of promising links, but haven't given them a whirl yet.

### 2015-04-17, Friday

* 0.25h (23:19-23:35): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Took a stab at getting things working with `@JsonIdentityInfo`. Failed, but too tired to really troubleshoot.

### 2015-04-18, Saturday

* 1.75h (18:04-19:05,19:49-20:31): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Still trying to get the build passing.
    * Was way off on my earlier diagnosis: the problems had nothing to do with JSON.
    * Turns out that CXF wasn't calling the JAXB 'afterUnmarshal(...)' hook.
        * My best guess is that the version of JAXB I was forcing the web service to use, 2.1, is old and doesn't support that hook. That's just a guess, though.
        * To test the theory, I decided to just go with the version of JAXB in whatever JRE I'm using.
        * Except the OpenJDK JAXB version is somehow not compatible with CXF, due to `ClassNotFoundException`s. See my Issue #32 for details.
        * I was curious if the latest CXF release, 3.0.4, had resolved this problem so I upgraded to it.
        * It *looks* like that problem is fixed there, though I had to stop injecting `AccountSecurityContext` types for some reason.
    * I left things with a failing test case in `AuthenticationIT` that I still need to diagnose.

### 2015-04-19, Sunday

* 1.35h (13:08-13:50,15:00-15:38): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got the build passing.
        * The web service's game login method was not updating the parent `Account.getLogins()` field, and so the web app couldn't see the new login from that direction.
    * Added a list of email addresses to the account page.

### 2015-04-20, Monday

* 0.5h (22:27-22:56): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Did some manual testing, just to confirm that things are all working like they should.
        * Found and fixed a bug: Trying to register when you already had an anonymous account failed. No idea why the build passed yesterday-- can't think of anything I changed that would have broken this. Oh well.

### 2015-04-21, Tuesday

* 0.25h (23:17-23:31): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Did some more manual testing, and everything seems to work as expected.
    * Only two issues I can find:
        * Merging accounts discards one of the user's names.
        * Games don't work correctly if the same account ends up being both players after a merge. No crashes, but it's not playable.
    * I think I should fix the first problem and just file a defect for the second.

### 2015-04-22, Wednesday

* 0.15h (23:42-23:50): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Decided not to audit merged names, as I'd have to collect those in another table, which seems excessive.
    * Did set the names to merge (target wins, if both are set).

### 2015-04-23, Thursday

* 2.4h (10:58-12:52,23:00-23:30): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Added test coverage to verify that names are merged.
    * Tried to go through and pull out the half-baked Jackson stuff I'd put in earlier. Didn't go well.
        * Turns out, the `-webapp` project has always used Jackson for JSON conversion, and I'd just forgotten.
        * I think the real problem with that is the lack of test coverage. Need to add a test or three to the webapp.
        * Should I also explicitly add Jackson tests to the `-api` project? I think not, as they're only valid if their Jackson config matches the one used by the webapp. Yuck.
        * Probably need to add the Jackson annotations library to the `-api` project, though.
    * Started writing a `-webapp` IT for JSON. Looks like it gets stuck in an endless loop, and produces infinite JSON.
    * Went back later and started working to fix the JSON rendering. It looks like the JSON is now respecting the JAXB `@Ignore` annotations.
        * Have to decide if I want to tweak that more, or if it's good enough.
        * Also have to add a JSON parsing library to the webapp's test classpath, to flesh out that test case some.

### 2015-04-24, Friday

* 2.0h (9:05-11:07): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Realized in the shower this morning: Jackson isn't respecting the `@Ignore` annotations: it's just that the webapp received those fields from the web service as `null`, and they of course stayed `null`.
        * Makes me want to go back and remove the cycle-related `@XmlTransient` annotations. But not worth it right now.
    * After a chair nap to mull things over, got the tests finished, fixed, and the build is now passing.
    * Committed all of my changes to date and pushed them, marking the issue as resolved.

### 2015-04-25, Saturday

* 0.55h (23:10-23:44): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Decided to push a release now that this is closed.
    * First, need to push a release of `jessentials-misc`.
        * That release failed miserably. Checked things in at the wrong version somehow.
        * Didn't have time to fix tonight.

### 2015-04-26, Sunday

* 1.25h (11:48-13:01): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Got the `jessentials-parent` POM fixed up and released it-- I'd never updated the Nexus repos in it when they were renamed.
    * Released `jessentials-misc`.
    * Released `2.0.0-milestone.2` of RPS.
    * Deployed that RPS release to `eddings`. Had to wipe the DB, as I'd made changes to the Liquibase log.
        * Shame on me!
    * Need to test the production deployment a bit to ensure everything works as expected there.

### 2015-04-27, Monday

* 0.45h (21:56-22:23): [Issue #62: The game webapp should allow users to create a named login/account](https://github.com/karlmdavis/rps-tourney/issues/62):
    * Tested the production instance out. Found and filed the following issues:
        * [Issue #78: "You Won" / "You Lost" display wrong: a 3 to 1 win reports "You Lost"](https://github.com/karlmdavis/rps-tourney/issues/78)
        * [Issue #79: Round history table updates goofily: rows out of order](https://github.com/karlmdavis/rps-tourney/issues/79)
        * [Issue #80: Erica's FF encounters an "invalid certificate" warning when accessing rpstourney.com](https://github.com/karlmdavis/rps-tourney/issues/80)
* Think I need to do some more testing and a bugfix release next.

### 2015-04-28, Tuesday

* 0.15h (23:46-23:56): [Issue #78: "You Won" / "You Lost" display wrong: a 3 to 1 win reports "You Lost"](https://github.com/karlmdavis/rps-tourney/issues/78)
    * Wrote a test case for this. Didn't have time to run or debug it.

### 2015-04-29, Wednesday

* 0.6h (21:57-22:32): [Issue #78: "You Won" / "You Lost" display wrong: a 3 to 1 win reports "You Lost"](https://github.com/karlmdavis/rps-tourney/issues/78)
    * Got the test case finished and fixed the bug.
    * The problem basically boils down to my use of `@JsonIdentityInfo`: because game data objects reference users in so many places, the serialization wasn't very predictable. I'm not sure, but it might have even serialized things differently if someone was a player in the game or not. Too goofy for the JS to cope with.
    * Fixed the JSON serialization with a different more limited set of annotations that work just great.

### 2015-04-30, Thursday

* 0.05h (22:52-22:55): [Issue #78: "You Won" / "You Lost" display wrong: a 3 to 1 win reports "You Lost"](https://github.com/karlmdavis/rps-tourney/issues/78)
    * Committed fix.
* 0.4h (22:55-23:19): [Issue #79: Round history table updates goofily: rows out of order](https://github.com/karlmdavis/rps-tourney/issues/79)
    * Wrote a test case for this and started trying to debug it. Didn't finish.

### 2015-05-01, Friday

* 0.75h (22:35-23:20): [Issue #79: Round history table updates goofily: rows out of order](https://github.com/karlmdavis/rps-tourney/issues/79)
    * Worked on the test case some more. `ExpectedConditions.not(...)` is broken somehow.

### 2015-05-02, Saturday

* 1.0h (21:00-22:01): [Issue #79: Round history table updates goofily: rows out of order](https://github.com/karlmdavis/rps-tourney/issues/79)
    * Worked on the test case some more. Found & fixed a number of JS bugs, and the test case now passes.
        * Was inserting the result row before inserting round rows.
        * Wasn't wrapping the result text in `td` cell.
        * Wasn't properly determining what to "name" the winner.
            * Still may not have this correct in the server-side version.

### 2015-05-03, Sunday

* 0.3h (11:43-11:55,13:25-13:31): [Issue #79: Round history table updates goofily: rows out of order](https://github.com/karlmdavis/rps-tourney/issues/79)
    * Fixed the server-side winner name bug.
* Next, I think I should fix the intermittent test case failures, and then the FF cert error.

### 2015-05-05, Tuesday

* *Was out sick yesterday-- from work and from this project. Terrible, awful allergies.*
* 0.2h (22:23-22:36): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Started looking at this, but the old stack traces in the issue comments are no longer valid.
    * Set things to build a bunch of times to force a new failure.
    * Got a failure, but didn't have a chance to investigate it yet.

### 2015-05-06, Wednesday

* 0.75h (21:01-21:45): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Found and fixed a problem: `GameController` was never calling `prepareRound(...)`.
    * Found another problem that will need to be fixed: `Game.submitThrow(...)` calls `prepareRound(...)`, which could cause an unnecessary conflict.
    * Cannot, however, figure out why the IT's call to `prepareRound(...)` is failing.
        * It should be the only thread running at that point in time, so I don't see what could cause a conflict.
        * Need to try rerunning, and probably adding some extra logging to figure out what's up.

### 2015-05-07, Thursday

* 0.2h (23:12-23:25): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * The `prepareRound(...)` failure seems to be a Heisenbug: I added logging to the web service to help track it down, and couldn't get it to reproduce. It **did** appear before I added that logging, though.
    * Instead, a different failure appeared during my last run for the same test case. I'll need to investigate it first, I guess.

### 2015-05-08, Friday

* 0.45h (22:57-23:25): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Couldn't parse the gigantic log from last night's failure, so tried re-running just the one test case. Back to the `prepareRound(...)` failure.
    * It doesn't make much sense to me yet, but I see one client request leaving, but see it handled twice by the server.
        * Can't tell if there's a bug in the client, in the server, or if this is just one of those HTTP things.
        * Need to run a traffic sniffer and see what's happening at that level.
        * Might also want to just consider making `prepareRound(...)` private and handling it internally, though this would require manual transaction management.

### 2015-05-09, Saturday

* 2.55h (12:44-13:44,21:50-23:24): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Spent a while trying to use `tcpdump` to debug things, but ran into problems.
        * Have to set it to use a non-random port, or there's too much noise.
        * Had to add a random query parameter to the `prepareRound(...)` web service calls, to keep track of them.
        * Still not too sure to how to read `tcpdump`'s output.
    * Tried again later to debug via `tcpdump`.
        * Can't seem to capture outgoing loopback traffic with it, so can't tell what's wrong.
        * Maybe if I switched the client from `localhost` to my private IP?
        * If not, I need to just try and crank up the CXF logging, and try debugging that way.

### 2015-05-10, Sunday

* 0.3h (21:49-22:07): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Tried again to debug via `tcpdump`.
        * Can't seem to capture outgoing loopback traffic with it, even if it's addressed to the `10.0.0...` IP, rather than `localhost`.
        * I need to just try and crank up the CXF logging, and try debugging that way.

### 2015-05-11, Monday

* 0.7h (22:15-22:55): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Spent some time trying to get the CXF client logging at a low level. No luck so far.
        * Need to read a bit about JUL's `logging.properties` file, I think.
        * Short-term, maybe I should just add the SLF4J bridge programmatically.

### 2015-05-25, Monday

* *On vacation from May 9 through May 31. Only working on side projects intermittently during that period.*
* 2.9h (14:37-15:30,18:10-18:20,20:48-22:41): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Spent some time trying to get the CXF client logging at a low level. No luck so far.
        * Delved in via the debugger and proved that CXF is not using the JRE's `HttpURLConnection`.
        * Tried turning on "all" logging, but could not reproduce the problem-- it alters the timing enough to "fix" things.
            * Gah, when enabled this way, there **are** log entries being fired for the "`s.n.w.p.http.HttpURLConnection`" category (expands to "`sun.net.www.protocol.http.HttpURLConnection`").
        * Finally got JUL logging working: programmatically set the root JUL level to `ALL` and programmatically install `SLF4JBridgeHandler`.
            * From the logs, it looks like things are going out once, but coming in to Jetty twice.
            * From my earlier `tcpdump` attempts, I recall that it looked like things were coming in twice at that layer, too.
    * Tried debugging using an HTTP proxy: [zaproxy / OWASP ZAP](https://code.google.com/p/zaproxy/wiki/Introduction).
        * The failing requests look like they're being issued three times, with one or two of them failing with the following: "HTTP/1.1 502 Bad Gateway ... ZAP Error [java.net.SocketException]: Broken pipe". Progress!
        * This leads me to suspect Jetty as the culprit, though it could still be something to do with the client.
        * Need to either update to latest Jetty, and/or replace Jetty with Tomcat.

### 2015-06-01, Monday

* *Was on vacation from May 9 through May 31. Only worked on side projects intermittently during that period.*
* 0.3h (07:56-08:15): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Updated to Jetty 9.2.1, kicked off test run.
        * Looks to be working now, but will leave running for a while to verify.

### 2015-06-02, Tuesday

* 0.4h (06:51-07:14): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Different kind of failure now: `SocketException: Unexpected end of file from server`, received by the client on a throw.
        * Though the client *thinks* the request failed, it looks like the server did actually process it, so the second try fails for legit reasons. To the client, though, both requests looked like they failed. Test goes boom.
        * Oh, I think the proxy I was using may just have died, causing the error.
        * Restarted the test without the proxy involved.

### 2015-06-04, Wednesday

* 0.1h (23:45-23:51): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * The test run yesterday was still passing after several hours, so I'd say things are now fixed.
    * Started cleaning up and committing things. More to do still.

### 2015-06-08, Monday

* *Had a 3-day camping trip to prep for and attend.*
* 1.1h (06:29-07:35): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Cleaned up and committed everything, but did not push as the build is failing.
    * Looks like the `jessentials` "delayed install/deploy" thingy isn't actually installing artifacts at the end of a build. Need to figure out why.

### 2015-06-09, Tuesday

* 0.75h (06:42-07:26): [Issue #37: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/37)
    * Fixed the problems with installAtEnd/deployAtEnd.
    * Pushed to GitHub.

### 2015-06-10, Wednesday

* 0.3h (06:17-06:36): [Issue #77: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/77)
    * Update in issue comment.
    * Left tests running in loop, about 35s each run.

### 2015-06-11, Thursday

* 0.3h (08:40-08:57): [Issue #77: Intermittent test failures in GameSessionResourceImplIT](https://github.com/karlmdavis/rps-tourney/issues/77)
    * Tests running in loop haven't failed yet, after about 2500 iterations.
    * Marked this issue as resolved.
    * Cleaned up the issues & milestones a bit.

### 2015-06-12, Friday

* 0.25h (08:29-08:45): [Issue #80: Erica's FF encounters an "invalid certificate" warning when accessing rpstourney.com](https://github.com/karlmdavis/rps-tourney/issues/80)
    * A `ctrl+F5` refresh on Erica's FF removed all of the errors and warnings.
    * I'm not entirely sure what was up, but I was seeing some errors in the details that indicate perhaps one of the linked resources was responsible for some of the problem.
    * Nonetheless, a test on ssllabs.com has indicated that I have some minor problems to resolve.

### 2015-06-13, Saturday

* 1.05h (20:34-20:50,21:45-22:24,22:34-22:43): [Issue #80: Erica's FF encounters an "invalid certificate" warning when accessing rpstourney.com](https://github.com/karlmdavis/rps-tourney/issues/80)
    * Fixed the CA chain problem.
    * Resolved the GitHub issue.

### 2015-06-14, Sunday

* 0.4h (17:46-18:08): [Issue #81: Release 2.0.0-milestone.3](https://github.com/karlmdavis/rps-tourney/issues/81)
    * Ran the Maven release.
    * Deployed the release to production.
    * Need to fix the page's copyright year.
    * Need to update the README to be more interesting.

### 2015-06-15, Monday

* 0.2h (07:50-08:02): [Issue #82: The webapp displays "2014" (last year) in the copyright section](https://github.com/karlmdavis/rps-tourney/issues/82)
    * Resolved.
* Need to update the README to be more interesting.

### 2015-06-16, Tuesday

* 0.7h (04:54-05:36): [Issue #85: Need to update the README to be more interesting and relevant](https://github.com/karlmdavis/rps-tourney/issues/85)
    * Updated the Jenkins [Embeddable Build Status Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Embeddable+Build+Status+Plugin) plugin, which was having caching issues that prevented it from actually being displayed.
    * Created test user accounts that I can use in screenshots.

### 2015-06-17, Wednesday

* 0.15h (23:26-23:35): [Issue #85: Need to update the README to be more interesting and relevant](https://github.com/karlmdavis/rps-tourney/issues/85)
    * Figured out how to do the screen capture:
        1. Use RecordMyDesktop to capture video.
        2. Use ffmpeg to convert the video to a set of image frames: <http://xmodulo.com/convert-video-animated-gif-image-linux.html>.
        3. Use ImageMagick to combine the frame into an animated GIF.
    * However, first I need to tighten up the webapp template a bit, or I'm going to need a giant video/image.

### 2015-06-18, Thursday

* 0.25h (22:32-22:36,23:35-23:47): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Basically, just stared at the UI for a while...
        * I always underestimate the importance of a really awesome UI. Need to make milestone 4 all about making the UI awesome.
        * Should move the scores to the sides of the throw controls, and reword them as "Has Won X of Y Rounds", with the number really large and the words small.
        * Need to make the throw controls more snazzy... just not sure how yet.

### 2015-06-19, Friday

* 2.35h (16:10-16:28,19:16-20:27,22:18-23:10): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Created `GameTitleTag` and almost got it working.

### 2015-06-20, Saturday

* 0.15h (21:00-21:08): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Work on `GameTitleTag`.
        * Realized that the Spring injection in these kinds of objects has to be done manually. See `AccountNameTag`.

### 2015-06-21, Sunday

* 0.75h (18:03-18:33,19:05-19:11,20:31-20:39): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Need to HTML-escape custom tag outputs. See `HtmlEscapingAwareTag` and use the same util.
    * Need to really rework things:
        * For consistency, `GameTitleTag` should use a new `PlayerNameTag` that just renders a properly-tagged `span` with the name.
            * For players with a set name, always display that.
            * For anonyous players, always display "Anonymous".
            * If the player is the current user, append " (You)" to the name.
        * Rename `gamePlayerName.jsp` to `playerNameEditable.jsp`.
        * Use the new `PlayerNameTag` everywhere: title, scores, round history, home page.

### 2015-06-22, Monday

* 0.75h (05:14-05:59): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Created `PlayerNameTag`. Haven't written tests for it yet, though.

### 2015-06-23, Tuesday

* 0.5h (08:31-9:00): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Completed tests for `PlayerNameTag` and restructured it so that other custom tags can leverage it.

### 2015-06-24, Wednesday

* 0.25h (08:49-09:04): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Reworked `GameTitleTag` and its tests to use `PlayerNameTag`.
    * Started updating the JS to cope with the changes, but will need to reproduce the player name logic there.

### 2015-06-25, Thursday

* 1.0h (06:22-07:23): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Finished updating the JS, and also found a couple more places to shove `PlayerNameTag` into.
        * Found and fixed another bug or two in the JS.
    * Need to finish fixing the ITs.
    * Probably need to make the new strings a bit shorter, too.

### 2015-06-26, Friday

* 0.8h (20:38-21:24): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Trying to fix the ITs led me to realizing that I still needed to shove the `PlayerNameTag` into the round history and game results.
        * Got that done in the JS, but still not done with the JSP.

### 2015-06-27, Saturday

* 0.55h (14:05-14:39): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Worked on the ITs. Only one failure left.

### 2015-06-28, Sunday

* 0.25h (15:42-15:57): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Got my title and player name changes committed.
* 1.5h (17:20-17:50,20:59-22:00): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Added a "Game created ..." subtitle. Committed it.
    * Started building out the new scores display.
        * How should I handle the editable player names? My new design didn't account for that.
    * Came back later, and got the names & score displays done.
    * My phone doesn't seem to be using any of the CSS or JS... oh, it's because all of the refs to them are via `localhost`.

### 2015-06-29, Monday

* 0.2h (23:09-23:21): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Bug: Game page's title includes unrendered markup.
    * Bug: Game page's title doesn't get updated when player names do.
    * Tweaked the "edit name" icon's CSS a bit.
    * Tested the new layout on my phone: looks good so far!
    * I'm really wondering if the game page's `<h1/>` is adding anything.
        * Should games have a "name" attribute? If so, who can set it? Is it editable?

### 2015-06-30, Tuesday

* 0.45h (23:08-23:34): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Got the throw controls styled correctly.
    * CSS Bug: Bootstrap columns need to be applied to outer `<div/>`s, with their content inside.
        * Otherwise, the alignment and gutters aren't applied correctly.

### 2015-07-01, Wednesday

* 0.35h (08:49-09:11): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Added a whole bunch of silly extra "wrapper" elements and used them to fix the Bootstrap rows & columns.

### 2015-07-02, Thursday

* 0.7h (21:44-22:26): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Tweaked the layout and UX more, particularly the game controls section.
    * Bug: Joined the game in Chromium, but did not get authenticated by doing so.

### 2015-07-03, Friday

* 0.6h (9:25-10:00): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Tweaked the join & max rounds styling to be more consistent.
* 0.45h (10:00-10:28): [Issue #89: Webapp cookies not set correctly for IP-only domains](https://github.com/karlmdavis/rps-tourney/issues/89)
    * Resolved and committed.
* 2.9h (13:41-15:14,18:15-19:36): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Fixed some problems with the JS's handling of player order.
    * Added more won/lost styling. I think that was a big improvement.
    * Fixed many of the ITs. Still four broken ones, though.
    * Still need to fix the player order in the history table, too.
* 0.5h (22:53-23:23): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Fixed some more of the ITs. Still have two broken, though.

### 2015-07-04, Saturday

* 1.45h (10:49-12:15): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Fixed the remaining ITs.
    * Committed the changes so far.
* 0.3h (13:30-13:48): [Issue #90: Remove extra JSON dependency in rps-tourney-webapp](https://github.com/karlmdavis/rps-tourney/issues/90)
    * Resolved and committed.
* 0.6h (14:05-14:42): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Fixed the game page's title attribute, so that it doesn't include escaped HTML.
* 2.4h (17:31-19:55): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Things to do:
        * Still need to fix the player order in the history table, too.
        * Should games have a "name" attribute? If so, who can set it? Is it editable?
        * Need to add a bit of `margin-bottom` to the player panels.
        * Move the "new game" button up a section.
        * Rework the home page text a bit.
        * Check `AbstractGame` for unused methods.
        * Remove unused strings.
    * Corrected the player order in the history table.
    * Added a bit of `margin-bottom` to the player panels.
    * Moved the "new game" button up a section.
* 0.35h (23:55-00:16): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Reworked the home page text a bit.
    * Fixed the "Your Games" section being completely empty glitch (when no games were created yet).
    * Still need to do:
        * Should games have a "name" attribute? If so, who can set it? Is it editable?
        * Check `AbstractGame` for unused methods.
        * Remove unused strings.

### 2015-07-05, Sunday

* 1.0h (06:29-07:28): [Issue #91: Clean up unused methods, strings, etc.](https://github.com/karlmdavis/rps-tourney/issues/91)
    * Resolved and committed.
* 0.6h (07:31-07:43,08:40-09:05): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Stared at things for a while.
    * Experimented with removing the game page's headers and other not-absolutely-needed elements.
    * Made the template's headers all smaller, so they look less silly on the game page.
    * Need to have JS update page title, too.
* 0.45h (09:05-09:33): [Issue #91: Clean up unused methods, strings, etc.](https://github.com/karlmdavis/rps-tourney/issues/91)
    * Also went through and refactored the message names to reflect where they're actually being used. Committed again.
* 1.2h (09:44-10:57): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Get game page titles updating dynamically (in HTML header).
    * Made the game controls less wide and replaced the round controls' text with icons.
    * Am I done with this task now? Should probably play test it a bit with Erica, first.
* 1.15h (12:05-13:13): [Issue #87: The webapp UI needs to be more awesome](https://github.com/karlmdavis/rps-tourney/issues/87)
    * Set the navbar to collapse at small sizes, rather than wrap to a new line.
* 3.7h (16:05-16:29,17:34-20:51): [Issue #88: Game users can set their name to an empty string and other "bad" values](https://github.com/karlmdavis/rps-tourney/issues/88)
    * Added the bean validation dependencies that will be used.
    * Had to futz around a lot with CXF to get bean validation enabled, but finally did. (See the comments in `CxfBeanValidationInInterceptor` for details.)
    * Resolved and committed.
* 0.75h (22:05-22:49): Released `2.0.0-milestone.4`.
    * Release and deployed.
    * Had some trouble deploying as the Tomcat manager app's `/usr/share/tomcat7-admin/manager/WEB-INF/web.xml` file had been overwritten again. Just updated it based on <https://justdavis.com/karl/it/davis/servers/eddings/tomcat.html>, and things started working.
* 0.45h (22:49-23:15): [Issue #85: Need to update the README to be more interesting and relevant](https://github.com/karlmdavis/rps-tourney/issues/85)
    * Reworked the `README.md` file a good bit. Still more to do, but that's a solid start.
    * Also still need to work on a screen capture of the webapp.

### 2015-07-06, Monday

* 0.55h (22:21-22:55): [Issue #85: Need to update the README to be more interesting and relevant](https://github.com/karlmdavis/rps-tourney/issues/85)
    * Resolved and committed.
    * Opted to not put together another animated GIF, and just went with a static screenshot, instead.
        * Did leave a comment in the issue with my research into how that could be done, though.

### 2015-07-07, Tuesday

* 0.15h (08:13-08:21): Spent some time filing the bugs and enhancements that I've had kicking around in my brain for a while.

### 2015-07-08, Wednesday

* 0.3h (05:40-05:58): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Started to design the requirements, which will eventually need to be posted to the issue:
        1. A new game's "invite a player" section will have to be replaced with a dropdown that allows players to choose _how_ a second player will be chosen. For now, the only two options will be "Invite A Friend" and "Play Against AI".
        2. The "Play Against AI" option should have two sub-options: select a specific AI or a random one (at a particular skill level. The control for selecting a specific AI should probably be combo box, as there may eventually be hundreds to choose from. The option to select a random skill-based one should be the default sub-option, with the easiest one of the three or so selected as the default.
        3. Once a game is started, play proceeds normally for the human player. The AI will not attempt to modify the number of rounds in the game.
        4. Each time a game/round is started, a job must be created and queued for the AI subsystem to pick up and work. The job creation & queueing is the responsibility of the web service.
        5. The project will need a separate module/service, named something like `rps-tourney-ai-manager`, which is responsible for monitoring the job queue and spawning workers to deal with new jobs.

### 2015-07-09, Thursday

* 0.25h (23:08-23:23): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Continued to design the requirements, which will eventually need to be posted to the issue:
        2. The "Play Against AI" option should have two sub-options: select a specific AI or a difficulty level.
            1. The control for selecting a specific AI should probably be combo box, as there may eventually be hundreds to choose from.
            2. The option to select a difficulty level should be the default sub-option, with the easiest one of the three (or so) selected as the default.
            3. The first release should only have the difficulty level option.
        4. Each time a game/round is started, a job must be created and queued for the AI subsystem to pick up and work. The job creation & queueing is the responsibility of the web service.
            1. There may eventually be different types of jobs (e.g. play a whole game between two AI players), so the queue design must accomodate that.
        5. The project will need a separate module/service, `rps-tourney-ai-manager`, which is responsible for monitoring and managing the job queue.
            1. Workers will run in separate processes/services. They must send heartbeats to the manager, so it can track how many workers are available.
            2. If cloud is supported, the manager will be able to start new cloud instances to deal with high load.
                1. The first version should not support cloud. It won't be needed initially.
            3. The manager may also be responsible for freeing "stuck" jobs, such that a different worker can pick them up.
        6. The project will also need a `rps-tourney-ai-worker` module. This may run as a WAR for convenience, but will basically just be a thread pool that picks jobs up off of the queue and works them.
            1. Security for allowing and restricting AI players' actions against the web service will need to be carefully controlled.

### 2015-07-10, Friday

* 0.15h (08:49-08:59): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Should the first release of the webservice skip all of that architectural complexity, and just run the AI every time the human player makes a throw?
        * What's the goal here: to just get AI opponents for people, or to have fun playing with cloud stuff?
            * Both.
        * Am I in a rush to get AI support out?
            * Only a small one.
        * Will I do a 2.0 release before rematches, console support, Android support, and player stats are available?
            * This is ultimately the important question.
            * I think the honest answer right now, though, is that I don't know.
            * Accordingly, I think it makes sense to implement AI in small steps, so I leave open the option of releasing earlier.

### 2015-07-11, Saturday

* Need to file an issue: ensure that there's a DB constraint enforcing just one `Player` record per `Account`.
* 0.35h (11:13-11:33): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * I've decided to leave jobs and queues out of the design for my first go-round at this.
    * Let's size this bad boy:
        * 2.0h: Create an `IAiPlayer` interface and "random idiot" implementation.
        * 6.0h: Create a web service method that returns the list of available AI players. Calling this method will populate the DB, as necessary.
        * 2.0h: Create a web service method that allows players to join an AI player to their game.
        * 6.0h: Update the `submitThrow(...)` method in the web service to automatically request and record AI players' throws, if playing against AI.
        * 6.0h: Rework the "invite an opponent" controls to allow selecting AI players.
        * 2.0h: Add an IT that plays through a game with an AI opponent.
        * 8.0h: Write a couple of more AI implementations, that correspond roughly to "medium" and "hard".
        * 32.0h total

### 2015-07-12, Sunday

* 0.1h (07:44-07:50): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Posted the requirements and sizings to the issue.

### 2015-07-13, Monday

* 0.3h (07:55-08:12): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Started speccing out `IAiPlayer`.

### 2015-07-14, Tuesday

* 0.05h (09:19-09:23): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Didn't get much done. Just kind of stared at things.
* 0.15h (22:39-22:47): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Started refactoring `AiPlayerId` into `AiPlayerRecord`, as a full entity.
        * Really need to decide what the ID/PK for the entity will be, and how that will eventually work out with plugins.

### 2015-07-15, Wednesday

* 0.2h (09:20-09:31): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Specced out the `AiPlayerRecord` requirements some on paper.

### 2015-07-16, Thursday

* 0.3h (22:55-23:14): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Found this very useful example of compiling and using Java source at runtime: [Stack Overflow: How do you dynamically compile and load external java classes?](http://stackoverflow.com/a/21544850/1851299)
    * Kept speccing things out on paper. Still need to figure out how to model things, with consideration for future functionality.

### 2015-07-17, Friday

* 0.45h (18:37-19:05): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Think I finally settled on how to handle things:
        * For built-in AIs, `Player` will have a `builtInAiName` field that can be set.
        * For user-provided AIs, `Player` will have an entity relation field that can be set.
        * Some manager class will be created to abstract away the differences between built-in and user-provided AIs. Games will call this, instead of trying to figure out which is which themselves.
    * Started creating the `BuiltInAi` enum, per the above.

### 2015-07-18, Saturday

* 0.15h (23:16-23:24): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Wrote the first AI logic implementation.
    * Pretty sure I need to rip out `AiPlayerRecord`, but I'm pretty tired so I'll wait until tomorrow to do that.

### 2015-07-19, Sunday

* 0.9h (10:50-11:06,11:25-12:00,12:31-12:34): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Cleaned things up a bit. Not really very productive, sadly.
    * Need to think a bit about how AI-only games will be created, and the permissions issues around that.
        * Any human can play any AI player at any time.
        * Once user-created AIs are allowed, only the owners of those AIs can create games where their AIs are Player 1.
        * Admins can create games however they want.

### 2015-07-20, Monday

* 0.5h (10:05-10:15,12:06-12:11,12:20-12:34): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Added an `inviteOpponent(...)` method to the web service.
    * Need to create a method for finding AI players.
    * Need to write something that progresses gameplay for AIs.

### 2015-07-21, Tuesday

* 0.2h (23:07-23:19): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Created the `IPlayersResource` interface.

### 2015-07-22, Wednesday

* 0.95h (11:22-11:30,13:17-14:07): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * A bit stuck figuring out which layer should own the list of AIs to display to the users.
        * Options:
            1. Return an ordered list of AIs from the web service.
                * Frontend still needs to have translations for each AI name.
                * Actually, it might be kind of fun to leave the AIs names alone and just rank them by win %.
            2. Make the frontend request specific AIs.
        * I think the decision comes down to: does each AI require some kind of translation?
        * Ultimately, each AI player will just display its Player#name field, unless I want to really rework things.
        * And no, I don't really want to rework things, so no translation is required.
    * Got a good chunk more of the web service implemented.
    * Need to write something that progresses gameplay for AIs.
    * Need to write something that calculates `Player` statistics.

### 2015-07-23, Thursday

* 0.25h (21:57-22:11): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Stubbed out `AiGameplayHelper` and started plugging it in to the web service.

### 2015-07-24, Friday

* 1.05h (18:35-19:39): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Started `AiGameplayHelperTest` and created `OneSidedDieBrain`.

### 2015-07-25, Saturday

* 0.7h (21:49-22:31): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Finished `AiGameplayHelperTest`.
    * Almost finished `AiGameplayHelper`.
    * Need to rework `GameView` to accept a `Player` instead of an `Account`, so that it works with AIs.

### 2015-07-26, Sunday

* 0.55h (20:42-21:14): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Refactored `GameView`, but haven't yet updated anything else to cope with the changes.

### 2015-07-27, Monday

* 0.05h (22:11-22:16): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Fixed a bunch of easy compile errors from the `GameView` refactor. Still need to fix the JS for it.

### 2015-07-28, Tuesday

* 0.1h (09:06-09:12): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Fixed up the JS from the `GameView` refactor (adjusted all occurrences of `viewUser`).

### 2015-07-29, Wednesday

* 0.15h (22:07-22:16): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Fixed a bug in `AiGameplayHelperTest`.
    * Implemented `GameClient.inviteOpponent(...)`.
    * Need to write integration tests.

### 2015-07-30, Thursday

* 0.15h (09:14-09:24): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Implemented the new `PlayersDaoImpl` methods. Still need to write tests for them.

### 2015-07-31, Friday

* 0.3h (23:30-23:49): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Added the new IT cases for `PlayersDaoImpl`. Not yet passing.

### 2015-08-01, Saturday

* 1.0h (10:27-10:46,11:41-12:10,12:46-12:58): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got `PlayersDaoImplIT` passing.
    * Fixed `AiPlayersInitializer`.

### 2015-08-02, Sunday

* 0.25h (23:14-23:30): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Started writing `GameResourceImplIT.playGameWithAi()`.
    * Started writing `PlayersClient`.
    * Need to write `PlayersResourceImplIT`.
    * Need to finish implementing my addition to `IPlayersResource`.

### 2015-08-03, Monday

* 0.3h (22:35-22:53): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got my `IPlayersResource` addition finished.
    * Got everything compiling.
    * Finished writing `GameResourceImplIT.playGameWithAi()`, but it and many other test cases are failing.

### 2015-08-04, Tuesday

* 0.55h (09:36-10:08): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Spent a while screwing with `AiPlayerInitializer` to get it to play nice with `GameResourceImplIT`. No dice.

### 2015-08-05, Wednesday

* 0.3h (06:37-06:56): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Solved the `AiPlayerInitializer` issues in `GameResourceImplIT`.
    * `GameResourceImplIT` is still not passing, though.

### 2015-08-06, Thursday

* 0.2h (08:26-08:37): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Fixed one test case, but another is still broken.

### 2015-08-07, Friday

* 0.55h (07:09-07:42): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Fixed `GameResourceImplIT`.
    * Created `PlayersResourceImplIT`.
        * Still have a failure in here to resolve.

### 2015-08-08, Saturday

* 0.3h (22:16-22:34): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got `PlayersResourceImplIT` passing.
    * Ran a full build, which passed.

### 2015-08-09, Sunday

* 0.75h (09:05-9:50): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Reviewed and committed changes so far.
    * Removed my workaround for [CXF-5980: JAX-RS 2.0 client: response.readEntity(new GenericType<...>{}) fails with "unexpected element" UnmarshalException](https://issues.apache.org/jira/browse/CXF-5980)
        * Added a comment to the issue explaining my original mistake.
    * Went through and rebased to fix the goofed commit from 2015-06-23.
        * Had to `git push --force`.

### 2015-08-10, Monday

* 0.25h (20:42-20:56): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Sketched out a simple UI design for AI selection in the webapp.
        * Might want to use tabs instead of radio buttons. More mobile-friendly.

### 2015-08-11, Tuesday

* 0.35h (22:10-22:32): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Thinking about the Opponent Selection UI...
        * What should the top-level choice control be for AI/friend?
            * Don't like radio buttons.
            * I'm not a huge fan of tabs, either.
            * Maybe vertical pills?
            * Dropdowns are okay, but make things less discoverable.
        * How bad is it if the content resizes depending on the top-level selection?
            * Pretty bad, and would likely indicate that non-JS browsers are screwed.

### 2015-08-12, Wednesday

* 0.2h (22:39-22:50): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Started creating the invite opponent form.
    * Decided to go with radio buttons for now. May re-evaluate once I see how they look.

### 2015-08-13, Thursday

* 0.2h (22:29-22:42): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Worked more on invite opponent form.
    * Thinking of using this for AI selector: [seiyria/bootstrap-slider](https://github.com/seiyria/bootstrap-slider).

### 2015-08-14, Friday

* 0.25h (23:22-23:36): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Worked more on invite opponent form. It needs a submit button.

### 2015-08-15, Saturday

* 0.25h (22:16-22:30): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Implemented `inviteOpponent` in the controller.
    * Need to verify that the service handles security and bad input correctly.

### 2015-08-17, Monday

* _Just forgot to work on this yesterday. No good reason._
* 0.6h (19:32-20:07): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Added an IT covering `inviteOpponent(...)`'s security.
    * Worked on debugging the frontend "invite opponent" form.
        * Still need to fix `editablePlayerName.tag`.

### 2015-08-18, Tuesday

* 0.3h (07:20-07:39): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got things working enough to play against an AI opponent.
    * Still need to polish the "invite opponent" form, and add a check for the radio button.

### 2015-08-19, Wednesday

* 0.75h (08:06-08:52): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got the "invite opponent" form's JS working.
    * Need to work on the form's styling.
    * Need to fix the JS error on the game page from before an opponent is selected.

### 2015-08-20, Thursday

* 0.7h (23:04-23:46): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Started styling the controls.
        * Problem: radio buttons (and checkboxes) can't be reliably styled. Can't make them big enough to match other controls.

### 2015-08-21, Friday

* 0.3h (18:54-19:11): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Applying the CSS 'scale' property does resize radio buttons, but they look blurry.
    * I could use Bootstrap's "pill" components, but then the form only works with JavaScript.
    * Theoretically, background-image-replacement buttons could work just with CSS, using advanced selectors.

### 2015-08-22, Saturday

* 1.15h (08:24-08:30, 17:57-19:00): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Spent some time switching the radio controls to [WTF, forms?](http://wtfforms.com/). Still don't have the sizing right, though.

### 2015-08-23, Sunday

* 0.2h (23:13-23:26): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * I think that the WTF Forms idea is good, but that the implementation needs some work.
        * Should switch from using background shading for the radio button to a font.

### 2015-08-24, Monday

* 0.95h (23:02-23:59): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Worked on switching the radio buttons to using [Font Awesome](https://fortawesome.github.io/Font-Awesome/).

### 2015-08-25, Tuesday

* 0.55h (21:37-22:10): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got the radio buttons finished up. They look & feel correct now. Tested in Firefox and Chromium.

### 2015-08-28, Friday

* _Was way too busy with work and meetups this week, so skipped a few days._
* 0.2h (23:28-23:39): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Fixed a minor thing: the AI dropdown is now sized correctly.

### 2015-08-29, Saturday

* 2.2h (18:00-20:12): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Spent some time trying to clean up the "Game Setup" design to look better. Not done.
    * Spent way too damned long tracking down a problem with Font Awesome: the font resources weren't actually being loaded correctly.
        * No idea why it was displaying correctly on my desktop, but it definitely wouldn't on Android, which is how I noticed there was a problem at all.
    * Also fixed the last of the compile errors.

### 2015-08-30, Sunday

* 0.55h (14:19-14:52): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got the "invite opponent" form design more or less done, I think. I'm now pretty happy with how it looks.
    * Need to write ITs and fix that one JS bug I noticed.

### 2015-08-31, Monday

* 0.3h (23:15-23:32): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Took my first stab at writing `GameIT.playGameVsAi()`.
    * Most of the test cases in there now seem broken, actually. Need to fix that.

### 2015-09-01, Tuesday

* 0.15h (23:51-23:59): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * I ran a command line build and discovered that only the new test case is failing.
    * Kicked off a Clean in Eclipse, since there's something goofy up with my previous run.

### 2015-09-02, Wednesday

* 0.25h (23:43-23:59): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Interestingly, way more things were still failing in Eclipse.
        * Two problems: had a different IP address in the app configs and needed another clean.

### 2015-09-03, Thursday

* 0.4h (21:02-21:04,21:21-21:43): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got the ITs passing.

### 2015-09-04, Friday

* 0.6h (07:27-08:02): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * I think I'm almost done.
    * Only problem is the AI player's name resets to "Anonymous Player" when the JS updates it.
* 0.9h (20:43-21:37): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Trying to get `BuiltInAi` to serialize its properties via Jackson. Not having much luck.

### 2015-09-05, Saturday

* 0.75h (09:07-09:52): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Fixed the `BuiltInAi` serializtion problem.
    * Committed the webapp enhancements.
* 2.15h (12:21-13:12,13:20-14:37): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Added two new AI strategies.
    * Need to add the display strings for them, and a test to make sure I've always done that.
* 0.9h (21:31-22:24): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Reworked my "hard" AI to match the strategy it was supposed to.
    * Added display strings for the new AIs and a test verifying that.
    * Ran a build, which failed. Need to investigate.

### 2015-09-06, Sunday

* 2.25h (06:29-06:59,07:49-07:53,08:06-09:47): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Got the build passing again.
        * Took a while to figure out.
        * When passing in a List via a JAX-RS client, you have to add the values one at a time. Stupid, but oh well.
* 0.7h (11:07-11:49): [Issue #64: Allow webapp users to play against AI opponents](https://github.com/karlmdavis/rps-tourney/issues/64)
    * Cleaned up and committed everything. Closed the issue. 
* 0.35h (11:50-12:10): Release planning.
    * What else might I need to do before release?
        * Clean up my logs.
        * Ensure that access logs are being collected.
* 0.3h (15:12-15:30): [Issue #97: Error in browser console before games are started: "uncaught exception: Missing player"](https://github.com/karlmdavis/rps-tourney/issues/97)
    * Resolved and pushed.
* 0.1h (15:57-16:03): [Issue #96: The DB should have a UNIQUE constraint that ensures a 1:1 relationship between Player and Account](https://github.com/karlmdavis/rps-tourney/issues/96)
    * Investigated and closed as invalid, with a comment explaining why.
* 0.35h (16:04-16:14,16:55-17:07): [Issue #94: Throw controls should be hidden/disabled after a game has ended](https://github.com/karlmdavis/rps-tourney/issues/94)
    * Resolved and committed.
* 0.2h (17:08-17:19): [Issue #92: Errors on Tomcat startup in production: java.io.NotSerializableException: com.justdavis.karl.rpstourney.service.api.auth.game.GameLoginIdentity](https://github.com/karlmdavis/rps-tourney/issues/92)
    * Resolved and committed.
* 1.0h (22:05-23:05): [Issue #98: Push the 2.0.0-milestone.5 release](https://github.com/karlmdavis/rps-tourney/issues/98)
    * Got the release performed and deployed. Yay!
    * Closed the issue.

### 2015-09-07, Monday

* 0.35h (08:54-09:16): [Issue #99: SNI not working from Erica's computer](https://github.com/karlmdavis/rps-tourney/issues/99)
    * Resolved and closed the issue.

### 2015-09-08, Tuesday

* 0.2h (22:00-22:12): [Issue #83: Trying to view a "not found" game results in a 500 error, instead of a 404](https://github.com/karlmdavis/rps-tourney/issues/83)
    * Wrote `GameExceptionHandler`. Need to add a test case. Maybe.

### 2015-09-09, Wednesday

* 0.25h (21:29-21:43): [Issue #83: Trying to view a "not found" game results in a 500 error, instead of a 404](https://github.com/karlmdavis/rps-tourney/issues/83)
    * Added a test case.
    * Committed and closed the issue.

### 2015-09-10, Thursday

* 0.1h (20:08-20:15): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Decided to start working this one next.
    * Filed a couple of additional issues.
    * Fixed a compiler warning in the old console code, just to kick things off.

### 2015-09-11, Friday

* 0.15h (20:32-20:42): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Started fleshing out the console options to allow for network play.
    * Is the production web service remotely accessible?

### 2015-09-12, Saturday

* 0.2h (06:37-06:50): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued working on the options, and also added the web service dependencies.

### 2015-09-13, Sunday

* 0.2h (06:43-06:54): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued working on the options.

### 2015-09-14, Monday

* 0.15h (23:45-23:53): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued working on the options.

### 2015-09-15, Tuesday

* 0.6h (20:46-21:23): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued working on the options. Almost done with them, I think. Just need to figure out how to parse AI.

### 2015-09-16, Wednesday

* 0.25h (21:32-21:47): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued working on the options. Couldn't quite get AI parsing.

### 2015-09-17, Thursday

* 0.6h (19:55-20:30): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Got the options parsing completed. Started reworking `ConsoleApp`.

### 2015-09-18, Friday

* 0.15h (23:36-34:46): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued working on `ConsoleApp`. Kind of a mess, but coming along.

### 2015-09-19, Saturday

* 0.4h (18:41-19:05): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued working on `ConsoleApp`. Even bigger mess, but still coming along.

### 2015-09-20, Sunday

* 0.15h (22:53-23:01): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Started (barely) refactoring the mess to clean it up.

### 2015-09-21, Monday

* 0.45h (21:14-21:42): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued refactoring the mess to clean it up.

### 2015-09-22, Tuesday

* 0.1h (21:21-21:26): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Finished the refactoring for now, at least.
    * Just barely started reworking `ConsoleGameDriver`.

### 2015-09-23, Wednesday

* 0.15h (21:22-21:30): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued reworking `ConsoleGameDriver`.

### 2015-09-24, Thursday

* 0.55h (06:24-06:56): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued reworking `ConsoleGameDriver`.
* 0.3h (21:03-21:13,21:57-21:59,22:53-22:58): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued reworking `ConsoleGameDriver`.

### 2015-09-25, Friday

* 0.2h (22:24-22:35): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued reworking `ConsoleGameDriver`.

### 2015-09-26, Saturday

* 0.85h (08:08-08:58): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued reworking `ConsoleGameDriver`. Stopped in the middle of a method.

### 2015-09-27, Sunday

* 0.15h (23:16-23:25): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued reworking `ConsoleGameDriver`.

### 2015-09-28, Monday

* 0.1h (22:17-22:22): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Continued reworking `ConsoleGameDriver`.

### 2015-09-29, Tuesday

* 0.2h (23:34-23:47): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Started adding in i18n support, which will be used for supplying AI names.

### 2015-09-30, Wednesday

* 0.2h (20:43-20:56): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Wrapped up AI support.
    * Added some compile errors, but I think I'm about done reworking `ConsoleGameDriver`.

### 2015-10-01, Thursday

* 0.1h (23:01-23:07): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Fixed some of the compiler errors, but still more left.

### 2015-10-02, Friday

* 0.2h (21:19-21:31): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Fixed the compiler errors.
    * Need to fix and update the tests, next.

### 2015-10-03, Saturday

* 0.2h (12:30-12:41): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on the tests some.
    * Will need separate `LocalGameClient` instances for each player.

### 2015-10-04, Sunday

* 0.15h (23:13-23:23): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Found a pretty bad `FIXME` in `AiGameplayHelper`. Would become a major problem when player-created AIs are allowed.
    * Worked on getting the tests passing. Not done.
    * Think I was wrong about needing separate `LocalGameClient` instances.

### 2015-10-05, Monday

* 0.15h (21:50-21:59): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * All of the current tests pass, which is a pleasant surprise.
    * Started adding tests to cover network gameplay.
        * Added Cargo to the POM. May need to also add a dependency on the WAR to get the build order right.
        * Need to create an IT that uses the web service.

### 2015-10-06, Tuesday

* 0.4h (22:38-23:02): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Spent a while looking for an `InputStream` I could use in my IT. Failed, and so started creating a new one.

### 2015-10-07, Wednesday

* 0.3h (22:33-22:51): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Finished writing the `MockInputStream`. Who knows? It might even work.

### 2015-10-08, Thursday

* 0.15h (21:24-21:33): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.

### 2015-10-09, Friday

* 0.15h (23:07-23:16): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.
        * It's now doing enough to actually fail.
    * I should also add a test that plays against a web service client player, which I could control.

### 2015-10-10, Saturday

* 0.15h (23:51-23:59): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.
        * I was wrong: my `MockInputStream` has to block when waiting for more input, not just return `-1`. Need to fix that.

### 2015-10-11, Sunday

* 0.2h (23:46-23:59): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.
        * Not sure my current approach will work. How will I respond to output if there's only one thread?

### 2015-10-12, Monday

* 0.6h (22:02-22:20,22:45-23:02): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.

### 2015-10-13, Tuesday

* 0.25h (22:53-23:08): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.

### 2015-10-14, Wednesday

* 0.05h (21:12-21:14): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.

### 2015-10-15, Thursday

* 0.35h (21:34-21:54): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.
        * Too sick to make much progress. Do I need a tee stream? I don't know. I'll figure it out tomorrow.

### 2015-10-19, Monday

* _Was "out sick" the last three days with the flu._
* 0.4h (22:10-22:35): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked on `ConsoleAppIT` some more.
        * I think I got the test case done. Need to review it.
        * Are other IT test cases needed right now?

### 2015-10-20, Tuesday

* 0.2h (08:17-08:28): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Finished `ConsoleAppIT`.
        * Don't think additional test cases are needed, but should check with Sonar.
    * Need to do some manual build verification.

### 2015-10-21, Wednesday

* 0.5h (19:59-20:28): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Ran a full command line build, got some errors.
        * Spent a while hammering out issues with the POM.
            * Had to upgrade JaCoCo to tolerate Java 8.
            * Had to add some test dependencies.

### 2015-10-22, Thursday

* 0.15h (19:58-20:07,20:39-20:40): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Got the command line build passing.
        * Had to provide the service config file in `src/test/resources`.
    * Found a new error: console script fails if passed no arguments.
* 0.5h (20:08-20:38): [Issue #102: The console application can only be run from within the bundle directory](https://github.com/karlmdavis/rps-tourney/issues/102)
    * Resolved and committed.

### 2015-10-23, Friday

* 0.35h (22:31-22:53): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Fixed a bunch of problems with local games.
        * I'd been thinking `Game.id` would be `null` for them, but it's not.
    * Need to add an IT for local games, given all of those problems.

### 2015-10-24, Saturday

* 0.3h (09:53-10:11): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Refactored the IT a bit and added a test case for local play.
    * Got test logging working correctly-- just had to add the `logback-test.xml` file.

### 2015-10-25, Sunday

* 0.05h (10:06-10:10): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Committed and pushed. Then closed the issue.
        * I only reviewed them briefly, but the changes actually looked pretty clean for once.
* 0.15h (14:17-14:27): [Issue #103: Builds failing on Jenkins due to Sonar problems: "Can not execute SonarQube analysis: java.io.IOException: Incompatible version 1007"](https://github.com/karlmdavis/rps-tourney/issues/103)
    * Looks like Sonar needs to be updated.
* 1.25h (18:22-18:29,21:01-21:24,21:45-22:30): [Issue #103: Builds failing on Jenkins due to Sonar problems: "Can not execute SonarQube analysis: java.io.IOException: Incompatible version 1007"](https://github.com/karlmdavis/rps-tourney/issues/103)
    * Updated SonarQube on `eddings` to 5.1.2. Used Puppet (still).
    * Unfortunately, the builds are now all failing on Jenkins with an odd SonarQube plugin error. Need to keep investigating.

### 2015-10-26, Monday

* 0.85h (06:27-07:17): [Issue #103: Builds failing on Jenkins due to Sonar problems: "Can not execute SonarQube analysis: java.io.IOException: Incompatible version 1007"](https://github.com/karlmdavis/rps-tourney/issues/103)
    * Finally figured out the problem: Jenkins' local `.m2` repo was corrupt, and not downloading the latest SonarQube plugins.
        * Removing the repo and letting it be rebuilt fixed this.
        * Also had to reinstall the Git SCM plugin to SonarQube.
    * Need to set SonarQube exclusions for the vendor JavaScript.

### 2015-10-27, Tuesday

* 0.3h (10:03-10:22): [Issue #103: Builds failing on Jenkins due to Sonar problems: "Can not execute SonarQube analysis: java.io.IOException: Incompatible version 1007"](https://github.com/karlmdavis/rps-tourney/issues/103)
    * Set the exclusions needed.
    * Closed the issue.

### 2015-10-28, Wednesday

* 0.3h (20:15-20:33): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Reviewed the coverage and issues sound by SonarQube in `rps-tourney-console`.
    * Started refactoring `ConsoleGameDriver.playGameSession(...)` to reduce its complexity.
    * Also need to review the "TODO"s. Why isn't SonarQube listing those?

### 2015-10-29, Thursday

* 0.45h (20:46-21:14): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Finished refactoring `ConsoleGameDriver.playGameSession(...)` to reduce its complexity.
        * Committed and pushed.
    * Still need to review the "TODO"s. Why isn't SonarQube listing those?

### 2015-10-30, Friday

* 0.35h (22:47-23:08): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * The current SonarQube profile was almost empty: only had 17 rules.
        * Had to restore the default SonarQube profile, and then customize it a bit.
        * Just disabled the rule complaining about tabs. Dumb rule.
        * Re-ran the builds.
        * Yay! More issues! Need to start working through them tomorrow.

### 2015-10-31, Saturday

* 0.45h (18:09-18:37): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Worked through many of the issues Sonarqube found, committing as I went.
    * Had one change that I needed to test, ran into an odd and unrelated failure in the build.
        * Need to file a separate issue for that.

### 2015-11-01, Sunday

* 0.3h (07:21-07:40): [Issue #104: IT Failure: "java.lang.instrument.IllegalClassFormatException: Error while instrumenting class com/steadystate/css/parser/SACParserCSS3TokenManager"](https://github.com/karlmdavis/rps-tourney/issues/104)
    * Resolved.
* 0.15h (07:41-07:45,08:54-08:58): [Issue #15: Update console game to support web service](https://github.com/karlmdavis/rps-tourney/issues/15)
    * Reviewed the remaining SonarQube issues, which are all just unit test coverage shortages that I don't care to address now.

### 2015-11-02, Monday

* 0.6h (07:36-07:41,20:08-20:39): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Started working on updating the docs.

### 2015-11-03, Tuesday

* 0.2h (06:06-06:10,22:47-22:56): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Got the WTP setup for Tomcat documented.
    * Need to go through and upgrade things. It's time.

### 2015-11-04, Wednesday

* 0.15h (22:55-23:03): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Reviewed and committed the docs.
    * Still need to go through the tools and update all of them. Maybe except for Tomcat.
    * Also need to check: is there anything the script should be doing, but isn't?

### 2015-11-05, Thursday

* 0.15h (20:40-20:49): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Updated Maven and Tomcat in the script. Need to update Eclipse.

### 2015-11-06, Friday

* 0.35h (06:56-07:16): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Started reading about Eclipse Oomph, the new Eclipse install helper.

### 2015-11-07, Saturday

* 0.5h (09:07-09:30,10:10-10:18): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Played around with Oomph. Should probably try using it to setup a new workspace.

### 2015-11-09, Monday

* _Just forgot to work on it yesterday. Spent a lot of time working on my website, instead._
* 1.1h (20:22-21:29): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Updated the Eclipse install, and the Eclipse plugins install.
        * Left DataNucleus alone for now.
    * Fired up the new version and got everything running.
* Idea for next task: performance tests, plus whatever deletion APIs are required to support them.

### 2015-11-10, Tuesday

* 0.4h (22:08-22:31): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Finished the docs, committed, resolved the issue.

### 2015-11-10, Tuesday

* 0.4h (22:08-22:31): [Issue #10: Create developer documentation](https://github.com/karlmdavis/rps-tourney/issues/10)
    * Finished the docs, committed, resolved the issue.

### 2015-11-11, Wednesday

* 0.95h (19:56-20:54): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Spent a lot of time researching various options. Most of them suck.
    * Notes:
        * [jmh](http://openjdk.java.net/projects/code-tools/jmh/) is probably the best of the bunch, as it understands differences between throughput, timing, etc.
        * The tests will need to examine throughput at different levels of concurrency.
        * The amount of concurrency I can test is limited by the number of CPU cores I have.
    * Put together a work breakdown and estimates.

### 2015-11-12, Thursday

* 0.45h (05:45-06:13): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Created a baseline benchmark.
    * I'm now going to have to run the web service embedded in these benchmarks. How am I going to manage that?

### 2015-11-13, Friday

* 0.35h (23:14-23:35): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Stubbed out some of the benchmark setup/state code.

### 2015-11-14, Saturday

* 0.75h (16:22-17:07): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Continued stubbing out the benchmark setup/state code.
        * Will use Cargo to start new Tomcat instances.
        * Cargo supports working with pre-existing instances.
        * When I want to add WTP support, I can add that via a giant `if` block that checks to see if WTP is already running, and if so, just uses it.

### 2015-11-15, Sunday

* 1.6h (14:17-14:22,15:12-16:43): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Created the new `jessentials-tomcat` project, started trying to figure out how it should work.
    * Didn't get too far, pretty poor attention span today.

### 2015-11-16, Monday

* 0.35h (07:25-07:46): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Worked on getting Tomcat to run in embedded mode. Made good progress.

### 2015-11-17, Tuesday

* 0.65h (09:05-09:43): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Spent a lot of time thinking about issues I'm running into: working directories, embedded vs. fork, etc.
    * I think it's best to write the code to allow for all 3 modes: external server, fork, or embedded.
    * The webapps will need rejiggering (later) to allow me to put their logs and config files somewhere other than the working directory.

### 2015-11-18, Wednesday

* 0.2h (21:15-21:26): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Worked on the Tomcat utilities a bit more.
    * To try and regain focus, I think I need to start from the test/benchmark code, and implement what it requires.

### 2015-11-19, Thursday

* 0.5h (06:24-06:45,06:49-06:58): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Need to debug the benchmark, to try and find how to pull out the Tomcat port.
    * Can't get the benchmark to build, though-- must have goofed something. Maybe with State handling?

### 2015-11-20, Friday

* 0.65h (21:40-22:19): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Wasn't hitting my breakpoint.
        * Might be because of earlier Tomcat errors (need to config the WAR).
        * Or, might be because of JMH.
        * If it's JMH, I can just run the main method inside `TomcatServerHelper`.
    * Need to figure out the WAR configs...
        * Well, I need to support servers running in a separate JVM. The only config options there are:
            * Home directory config files.
            * Working directory config files.
            * Environment variables.
            * Java system properties.
        * For embedded instances, the additional config options are:
            * Static fields, unless classloader stuff prevents this.
        * I also need to allow for the possibility of multiple instances running at once (but not in embedded mode). That precludes:
            * Home directory config files.
            * Working directory config files.
        * That really only leaves me with system properties as a viable option.
    * Also have to figure out how to handle logging. Right now, Logback uses the working directory. This is bad, because that's often the project root.
        * Does Logback's config file format support variables?
            * Yes, just use the `${varName}` syntax, which supports Java system properties. The `${varName:-defaultValue}` syntax can be used to supply default values.

### 2015-11-21, Saturday

* 1.05h (09:19-10:21): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * The config file stuff was easy to fix: the config loader already supports system properties.
    * Had a bit of trouble adding the WAR. Turns out that m2e (or Maven, in general) won't add WARs to the classpath at all.
        * Need to rejigger that to have a `ModuleWar` kind of object, instead.
* 0.5h (11:40-12:10): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got things running... sort of. Looks like trying to run it embedded will have classpath conflicts.
* 0.4h (12:48-13:12): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Unless I create a filtered ClassLoader from scratch, I'm out of luck with using Tomcat in embedded mode.
        * I don't want to do that now.
        * Started refactoring the embedded version into a locally-installed version.

### 2015-11-22, Sunday

* 1.05h (12:34-13:04,13:52-14:28): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Thinking about yesterday, I'm surprised that no one's made a Maven-filtered `ClassLoader`. Might be an interesting open source project.
    * Got things working: looks like Tomcat and the web service are running, though I haven't quite verified that yet.

### 2015-11-23, Monday

* 0.25h (09:09-09:25): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got the benchmark running! ... and then it died several iterations in, to some weird Tomcat error.
    * Once that weird error is addressed, though, I'll need to clean things up and commit them.
* 0.5h (09:59-10:30): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got it running for real! Yay!
    * Had to set JMH's timeout, which required upgrading to the latest JMH release.
    * Got the following result: `GameDisplayBenchmarks.retrieveGameAsUnauthenticatedUser  thrpt   10  1799.015 ± 747.155  ops/s`
* Open source idea: it'd be great if there was a Maven plugin for JMH. And a Sonar one, too.

### 2015-11-24, Tuesday

* 0.3h (09:39-09:58): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Cleaned up the code, to get ready for the first commit.
    * After that's committed, I think I'd really like to work on getting the benchmark to run against production.

### 2015-11-25, Wednesday

* 0.55h (19:06-19:38): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got `jessentials-tomcat` cleaned up and pushed.
    * Need to fix the game's logging directory to be configurable, and otherwise clean up and push the benchmarks code.

### 2015-11-26, Thursday

* 0.65h (09:05-09:44): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Took me a while to figure out what's happening with logging...
        * It may be different in production, but locally, the WARs' `logback.xml` files are being completely ignored.
        * Logback config is being passed in as a system property to the entire Tomcat instance.
        * The `./logs/` directory I was getting in the benchmarks project, was actually from the `logback.xml` for **that** project.
        * I've updated it to just log to the console (though it currently produces no output, anyways).
    * Next steps:
        * Look at logging in production. If they're not being used, I should remove the WARs' `logback.xml` files to prevent this sort of confusion in the future.
        * Ensure that logging is correctly configured for the Tomcat instances started by the benchmarks. Probably to a file? 

### 2015-11-27, Friday

* 2.6h (12:32-13:54,14:34-15:49): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Finally got it. What a collection of dumb mistakes on my part.
        * Wasn't setting the logging path in the both spots in the config file, for one.
        * Had let myself get lost on a complete tangent, wondering about production logging config.

### 2015-11-28, Saturday

* 0.8h (15:42-16:30): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Cleaned up and committed the benchmarks in place so far.
    * Next steps:
        * Have the benchmarks run against PostgreSQL?
        * Have the benchmarks run against production.
        * Have the benchmarks run Tomcat in embedded mode, to allow for profiling.

### 2015-11-29, Sunday

* 0.45h (12:14-12:40): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * How to run against production? Options:
        1. Don't worry about creating fake data. Maybe tag it, so that it can be ignored, when needed.
        2. Delete anything I create, after the benchmark run.
        3. Somehow rig up a giant DB transaction that all of the benchmarks run within.
    * If I do go the "delete it after" route, I should probably still ensure things are tagged somehow, in case the cleanup fails.
        * Using fixed accounts is probably enough to cover this: just delete all games, etc. associated with those accounts.
    * What, exactly, will I need to be able to delete?
        * Not accounts (not for this, anyways).
        * Logins (assuming that I'll want to create guest logins for the benchmarks).
        * Players.
        * Game and rounds.
    * I'll also need some sort of admin account to manage this. That'll require a way to customize the production password (and to pass that customized password into the benchmarks).
    * Of course, if I want to actually run all this against production, I'll have to deploy a new version out there.

### 2015-11-30, Monday

* 0.45h (08:53-09:05,10:01-10:17): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Working on `AdminAccountInitializer`, which will ensure that the web service always has a default admin account.

### 2015-12-01, Tuesday

* 0.45h (10:05-10:32): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Finished the first cut of `AdminAccountInitializer`. Not tested, yet.

### 2015-12-02, Wednesday

* 1.1h (14:16-15:23): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Updated the `ServiceConfigTest` unit tests to include `AdminAccountConfig`. Found & fixed a bug they caught.
    * Created `AdminAccountInitializerIT`. It told me that the `AdminAccountInitializer` wasn't being called at all.
        * Rejiggered it to work like `AiPlayerInitializer`. Fixed!
    * Committed my admin account changes.

### 2015-12-03, Thursday

* 0.3h (20:17-20:36): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Implemented a `Game` delete in the web service and DAO. Need to add tests for it.

### 2015-12-04, Friday

* 0.35h (10:26-10:47): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Added a test case for the DAO delete, which is failing in a very interesting way.
        * Need to research more: JPA caching and cascade removes.
* 0.35h (13:41-14:03): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * All but one FK contraint in the DB is missing! Filed this as a new issue.
* 1.35h (14:04-14:51,15:52-15:59,16:55-17:21): [Issue #106: DB tables are missing constraints](https://github.com/karlmdavis/rps-tourney/issues/106)
    * I just love that, with Liquibase, this failure will be memorialized in my DB changelog for all eternity. Yay...
    * Something weird is going on, trying to add a FK on `GuestLoginIdentities`...
        * In change set `3`, I add the FK and then (accidentally) remove all FKs on that table.
        * However, in my new change set, I can't (re-)create the FK because it already exists. Except it doesn't; I don't see it there.
    * Also, to add to my pain, the DAO ITs are always provisioning the schema and setting up entity managers for HSQL.
        * My Spring profiles are all pretty whacked, it seems.
        * I'm starting to think that a part of my pain with the FK recreation is due to this.
        * Need to file this as a separate issue, stash all my current changes, fix the profiles, then come back to the FK mess. 

### 2015-12-05, Saturday

* 1.85h (07:46-08:27,14:48-15:57): [Issue #107: The web service DAO ITs always provision an extra HSQL DB](https://github.com/karlmdavis/rps-tourney/issues/107)
    * Stashed my changes for #106.
    * Thinking through the problem...
        * The DAO tests are different from the other ITs in that they don't just need to override beans, they need to exclude them.
        * It seems like it'd be best to have a separate configuration just for these tests.
        * Either that, or just move them to a different project entirely. That seems like overkill, though.
    * Currently trying to programmatically verify the application's schema. Not going very well.
* 1.05h (20:14-21:16): [Issue #107: The web service DAO ITs always provision an extra HSQL DB](https://github.com/karlmdavis/rps-tourney/issues/107)
    * Gave up on the programmatic schema verification.
    * Got everything else cleaned up, fixed, and committed.
    * The configs before were pretty damn screwy, so I'm quite happy with this little detour-- cleans up a lot of weird stuff.

### 2015-12-06, Sunday

* 0.35h (12:03-12:25): [Issue #106: DB tables are missing constraints](https://github.com/karlmdavis/rps-tourney/issues/106)
    * Tried updating HSQL and Liquibase.
        * The HSQL upgrade doesn't fix it.
        * The Liquibase update just starts running into this: [CORE-2425: Type VARBINARY(256) changed to BLOB on HSQLDB](https://liquibase.jira.com/browse/CORE-2425).
    * Fixing #107 was huge, though, as it turns out the problem only exists with HSQL.
    * Think I'll need to hack and invalidate the change set that originally created-then-deleted the FK.

### 2015-12-07, Monday

* 0.45h (08:17-08:45): [Issue #106: DB tables are missing constraints](https://github.com/karlmdavis/rps-tourney/issues/106)
    * Think I got this fixed.
        * However, this forum thread makes me wonder if Liquibase does actually support the `validCheckSum` option: [Liquibase Forums: Calculation of checksum md5sum](http://forum.liquibase.org/topic/calculation-of-checksum-md5sum).
        * If not, this may blow up when I try to push it to production.
    * Still need to review and commit it.

### 2015-12-08, Tuesday

* 0.15h (07:21-07:30): [Issue #106: DB tables are missing constraints](https://github.com/karlmdavis/rps-tourney/issues/106)
    * Cleaned up, committed, and resolved.
* 1.1h (10:19-10:40,10:58-11:05,12:44-13:23): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Fixed the problem with game deletes: `CriteriaDelete` isn't honoring `CascadeType.REMOVE`.
    * Now seeing a really odd problem in `GameResourceImplIT.submitThrowConcurrency()`, which should be unrelated...

### 2015-12-09, Wednesday

* 1.35h (13:11-13:38,14:20-14:33,15:45-16:27): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Fixed the problem with `GameResourceImplIT.submitThrowConcurrency()`. Why'd it start happening now?
        * Oh yeah, duh. I'd updated the method's `toString` earlier for some reason or other.
    * Cleaned up and committed the `Game` delete functionality.
    * Got the benchmarks deleting the `Game` that they create.

### 2015-12-10, Thursday

* 0.65h (12:41-12:49,15:20-15:29,18:17-18:31,18:41-18:49): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Next step: update the benchmarks so that they can run against other Tomcat instances: production or WTP.
    * `TomcatServerHelper` isn't the right place for the new/existing server logic. `ServerState` is.
    * Made decent progress on that, but was getting too tired, so stopped mid-thought.
* 0.1h (21:50-21:57): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Working on the new/existing server logic in `ServerState`.

### 2015-12-10, Thursday

* 0.65h (12:41-12:49,15:20-15:29,18:17-18:31,18:41-18:49): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Next step: update the benchmarks so that they can run against other Tomcat instances: production or WTP.

### 2015-12-11, Friday

* 1.2h (07:55-08:05,09:58-10:18,10:29-10:37,11:04-11:37): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got the benchmarks to run against a WTP instance of Tomcat!
    * Definitely runs slower. I wonder if that's because of GC? Or maybe logging?
    * Next steps?
        * Have the benchmarks run Tomcat in embedded mode, to allow for profiling.
        * Have the benchmarks run against PostgreSQL?
            * This isn't really valuable, except to allow for apples-to-apples comparisons against production.
        * Have the benchmarks run against production.
            * Won't get profiling data there. Can't. That's okay, though: I'm still quite curious!

### 2015-12-12, Saturday

* 0.05h (10:08-10:11): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Cleaned up and committed WIP.
* 0.65h (17:20-17:38,18:25-18:45): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Played around with the JMH profilers, just to see what they can do.
    * Had to install `linux-tools-common` and `linux-tools-generic` to get some of them to work.
    * Also had to temporarily install `linux-tools-3.13.0-68-generic`, which can be uninstalled after the next reboot. Was just needed for the currently-in-use kernel.
    * `StackProfiler`: seems to work well enough, though obviously can't tell me much yet.
    * `LinuxPerfNormProfiler`: provides tons of very low-level information that unfortunately doesn't mean much to me
    * `Hotspot*Profiler`: generally provide information that's more useful to the JVM devs, though I might be able to use some of the threading and locking and GC info.
    * Overall, I'm not terribly impressed with the profiling options here. Netbeans' profiler is way better.
    * Given that, is getting Tomcat running embedded all that useful? Not really, no.
    * Next steps:
        * Ensure that the benchmarks have two standard users with email addresses that they always create/use
            * Needed to ensure that benchmark "oopses" in production can be fixed, if necessary.
        * Add more benchmarks.

### 2015-12-13, Sunday

* 1.25h (19:18-20:10,20:31-20:53): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Worked on adding `GameplayBenchmarks`.
    * Running into an odd error that I'll need to track down.

### 2015-12-14, Monday

* 0.85h (21:33-22:25): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Figured out the problem from yesterday: I just hadn't updated the WAR, so that it included the new web service method.
    * This was obscured by a new issue that I filed: [Issue #108: Web service throws 500 errors at AuthenticationFilter:137 if a 404 is encountered](https://github.com/karlmdavis/rps-tourney/issues/108). I should fix that soon.
    * Have `GameplayBenchmarks` running single-threaded, but it fails when running with more than that. Some sort of HSQL issue?

### 2015-12-15, Tuesday

* 0.35h (21:03-21:24): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Need to set the default transaction level for HSQL to `MVCC`.
        * The default level, `LOCKS`, locks entire tables, which is causing the `org.hibernate.exception.LockAcquisitionException: could not execute statement` benchmark errors.
        * Transaction levels: [HSQL: Sessions and Transactions](http://hsqldb.org/doc/guide/sessions-chapt.html#snc_tx_mvcc)
        * DB properties: [HSQL: Properties](http://www.hsqldb.org/doc/guide/dbproperties-chapt.html)
        * Set `hsqldb.tx=mvcc` when creating the DBs, in the URL.

### 2015-12-16, Wednesday

* 0.3h (21:03-21:20): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Just set the property in the URL for the benchmarks, as the HSQL connector was designed to basically just pass-through the URLs. Don't want to change that unless/until this is also required for other use cases.
    * With that change, `GameplayBenchmarks` is now running fine. Only managing 40 ops/second, though, which is surprisingly low!
    * Next steps:
        * Commit things.
        * Start adding benchmarks that include the webapp.
            * Do I want to put those benchmarks in the same project?
                * For now, yeah: think I do. Makes code reuse a lot easier.

### 2015-12-17, Thursday

* 0.4h (21:35-21:50): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Added `PlayersResourceImplIT.findOrCreatePlayer()`.
    * Committed `GameplayBenchmarks` and the changes it required.

### 2015-12-18, Friday

* 0.5h (20:07-20:38): General admin stuff.
    * Ran the new Eclipse Mars formatter on all of the Java files.
    * Removed the long-obsoleted `rps-tourney-api` module.

### 2015-12-19, Saturday

* 0.2h (16:40-16:52): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Started creating the webapp benchmarks.
    * I'll refactor & rename everything once those are in place.

### 2015-12-20, Sunday

* 0.65h (07:29-07:33,07:47-08:23): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Continued working on `WebAppGameDisplayBenchmarks`.
    * Going to use HtmlUnit to load the web pages, even though it probably uses more CPU time than I'd like.

### 2015-12-21, Monday

* 0.6h (21:43-22:18): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Continued working on `WebAppGameDisplayBenchmarks`.

### 2015-12-22, Tuesday

* 0.7h (22:25-23:06): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Finished a first cut of `WebAppGameDisplayBenchmarks`.
    * Jeepers, HtmlUnit is *slow*. Need to see if there's anything I can do to cope with that...
        * Maybe run it once during `@Setup`, and somehow figure out which resources it pulls, then just pull the resources in the benchmark?

### 2015-12-23, Wednesday

* 0.75h (20:48-21:34): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got HtmlUnit's performance back up by disabling CSS and JS.
        * Does this still request those resources and just not process them?
        * Need to enable access logging in a run against WTP and find out.
        * Couldn't get access logging enabled in Cargo.

### 2015-12-24, Thursday

* 0.2h (21:23-21:36): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * HtmlUnit does not load CSS, JS, etc. resources unless CSS & JS are enabled. Drat.
    * Note: I **am** closing the HtmlUnit resources quickly in each iteration, so that's not the cause of the slowness.
    * Have to decide how to proceed.
        * Definitely might as well toss HtmlUnit. It's not doing anything for me.
        * Do I want to add in loading the CSS and JS resources? Probably, yeah.

### 2015-12-25, Friday

* Merry Christmas!
* 0.95h (07:26-07:39,07:48-08:31): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got the benchmark loading all of the various resources, except:
        * Can't get it to load the webapp JSON. 500 error that I need to investigate.
        * If one of the resources fails to load (e.g. the webapp JSON), I end up with hung threads. No idea why.

### 2015-12-26, Saturday

* 0.3h (12:26-12:35,12:43-12:51): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Realized why the benchmarks weren't logging the webapp: I had to rebuild the WAR to include the logging config change I'd made.
    * Filed Issue #109. Need to fix that, and the thread hanging issue it causes.
* 0.1h (12:36-12:42): [Issue #109: Benchmarks unable to download Tomcat: http://www.us.apache.org/dist/tomcat/tomcat-7/v7.0.65/bin/apache-tomcat-7.0.65.tar.gz](https://github.com/karlmdavis/rps-tourney/issues/109)
    * Fixed, committed.

### 2015-12-27, Sunday

* 1.1h (07:56-09:01): [Issue #110: Unable to retrieve game JSON before game starts: HTTP 500](https://github.com/karlmdavis/rps-tourney/issues/110)
    * Fixed, committed.
* 0.3h (09:21-09:40): [Issue #105: Need performance and load tests](https://github.com/karlmdavis/rps-tourney/issues/105)
    * Got `WebAppGameDisplayBenchmarks` almost finished.
    * Next steps:
        * Pull out the HtmlUnit stuff.
        * Add a benchmark for just passive refreshes.
        * Clean things up and commit.
        * Refactor the project a whole bunch.

### 2015-12-28, Monday

* 0.25h (06:27-06:41): [Issue #110: Unable to retrieve game JSON before game starts: HTTP 500](https://github.com/karlmdavis/rps-tourney/issues/110)
    * Pulled out the HtmlUnit stuff.
    * Added a benchmark for just passive refreshes.
* 0.4h (19:50-19:54,20:24-20:47): [Issue #110: Unable to retrieve game JSON before game starts: HTTP 500](https://github.com/karlmdavis/rps-tourney/issues/110)
    * Cleaned up and committed things.
    * Renamed and refactored the project.
    * Committed the refactoring, resolved the issue.

### 2015-12-29, Tuesday

* 0.5h (19:31-20:00): [Issue #108: Web service throws 500 errors at AuthenticationFilter:137 if a 404 is encountered](https://github.com/karlmdavis/rps-tourney/issues/108)
    * Resolved and committed.
* Next steps:
    * Look at Issue #36. Might have already fixed it, back when I made `src/main/webapp` not a source folder.
    * Need to file an issue to consolidate down to just one app server handler for ITs.
    * Need to deal with follow up on some of the third party bugs I've filed.

### 2015-12-31, Thursday

* _Just completely forgot to work on this project yesterday. No particular reason._
* 0.3h (22:13-22:31): [Issue #36: Stop using src/main/webapp resources in EmbeddedServer in ITs](https://github.com/karlmdavis/rps-tourney/issues/36)
    * Came up with an approach to address this issue. Documented it as a comment on the issue itself.

### 2016-01-01, Friday

* 0.7h (13:46-13:49,14:53-15:32): [Issue #36: Stop using src/main/webapp resources in EmbeddedServer in ITs](https://github.com/karlmdavis/rps-tourney/issues/36)
    * Updated the `rps-tourney-service-app` POM to launch the WAR in Tomcat, via Cargo.
        * There is a Tomcat plugin for Maven, but it looks mostly dead.
    * How am I going to rejigger the service ITs to run against an external Tomcat instance?
        * `AccountsResourceImplIT` (and probably many others) expects to be able to wipe the DB schema after each test case. It also injects DAOs to create mock data for the tests to run against.

### 2016-01-02, Saturday

* 0.3h (21:06-21:24): [Issue #36: Stop using src/main/webapp resources in EmbeddedServer in ITs](https://github.com/karlmdavis/rps-tourney/issues/36)
    * Started `ClientConfig.createConfigFromSystemProperties()`.
        * Just realized: it doesn't store the admin credentials to use. And it shouldn't. Poop. Now where do I stick this functionality?
        * I guess a new class is needed for the tests to use, that includes `ClientConfig`. Bother.

### 2016-01-03, Sunday

* 0.3h (17:51-18:08): [Issue #36: Stop using src/main/webapp resources in EmbeddedServer in ITs](https://github.com/karlmdavis/rps-tourney/issues/36)
    * Created `TestsConfig` and updated `NotFoundErrorsIT` to use it.

### 2018-01-08, Monday

* 1.1h (04:00-05:07): Tried getting build to pass in the new Jenkins server.
    * Updated config with similar changes as those made to `jessentials`. Still failing, but with a JaCoCo Java class level error.
    * Looks like Java 7 is EOL'd, but RPS is still using that all over the place. Android _mostly_ supports Java 8 now, it seems.
    * Java 9 is out now, too! But let's see if we can just get things running on 8, first. Baby steps.
    * Updated `JAVA_HOME` in `~/.bashrc_local` on `jordan-u` to point to Java 8.
    * Tried building RPS locally, but ran into errors that look related to what I was working on a couple years ago.
    * Stashed those changes temporarily, and the build passes! But looks like it's using JDK 7 from the toolchain.

### 2018-01-09, Tuesday

* 0.4h (2205-2230): Picked up incomplete work to switch ITs to Tomcat.
    * Tomcat is starting, but going boom while deploying WAR, due to dependency issue.

### 2018-01-10, Wednesday

* 0.4h (0650-0715): Picked up incomplete work to switch ITs to Tomcat.
    * Tried adding missing dependency to POM: ran into another missing dependency. Ideas:
        * Check `git diff`: did I remove something from POMs or dependency POMs?
        * Compare Cargo Tomcat's libs to Tomcat libs from old `eddings`.
* 0.5h (2130-2200): Figured out what's up with the IT failures.
    * The `-api` project had a typo in a dependency: prefixed the hibernate `groupId` with a dot. Fixing that typo causes these classpath errors, for reasons I don't fully understand yet. Definitely has something to do with how Maven handles transitive exclusions. Upgrading to latest Maven didn't fix it.

### 2018-01-11, Thursday

* 0.7h (0618-0700): Switch ITs to Tomcat.
    * Had the same typo for different artifacts in both the `-app` and `-api` POMs.
    * Adding `hibernate-core` as a direct dependency to `-app` fixed the problem.
    * Next challenge: the `-app` project is using Spring injection all over the place in its ITs. If I move them to Tomcat, that has to be ripped out. Is this still a good idea?
        * How do folks deal with webapp resources with Spring Test?

### 2018-01-12, Friday

* 0.4h (2150-2215): Issue #36: Put it on ice, after documenting my thoughts and committing the WIP changes.
* 1.75h (2216-0002): Issue #111: Upgrade runtime/platform dependencies.
    * Spent way too long thinking about whether or not it's a good idea to change project versions in Jenkins builds:
        * I think I've settled on "no, it's not a good idea"?
        * Doesn't really provide a lot of value as depending on SNAPSHOT versions that you don't have checked out locally is almost always a bad idea anyways?
        * And Maven just... doesn't support it well. It's going to break version ranges and other "get the latest" behaviors.

### 2018-01-13, Saturday

* 5h: Issue #111: Upgrade runtime/platform dependencies.
    * Wasn't tracking time, really.
    * Fixed several problems with Jenkins to get the `jessentials` `Jenkinsfile` build working. And finally succeeded!

### 2018-01-14, Sunday

* 3h (0700-0740,0950-1045): Issue #111: Upgrade runtime/platform dependencies.
    * Going through the `jessentials-parent` POM and upgrading things there, reading change logs.
        * Stopped tracking time on this, as I was at it off and on for much of the day. Maybe 3h total?

### 2018-01-15, Monday

* 1h (1000-1015,2230-2315): Issue #111: Upgrade runtime/platform dependencies.
    * Upgraded to Eclipse Oxygen, which is required by JUnit 5.
    * Finished upgrading the `jessentials-*` dependencies and plugins. Got that committed and passing in Jenkins.
    * Note: Need to add SonarQube support to `Jenkinsfile`. Whoops.

### 2018-01-20, Saturday

* 4h (unknown): Issue #111: Upgrade runtime/platform dependencies.
    * Got `jessentials` refresh completed.
    * Got SonarQube analysis working for `jessentials`.
        * Spent an embarassing amount of time trying to solve nonexistent problems with "IT coverage not being included" (it was). Too damned sleep-deprived.

### 2018-01-26, Friday

* 2.25h (1715-1615,2130-2245): Issue #111: Upgrade runtime/platform dependencies.
    * Released `jessentials-*` projects.
    * Started updating everything in the `rps-tourney` POMs.

### 2018-01-27, Saturday

* 5.0h (unknown): Issue #111: Upgrade runtime/platform dependencies.
    * Started updating dependencies.

### 2018-01-28, Sunday

* 3.5h (unknown): Issue #111: Upgrade runtime/platform dependencies.
    * Mostly done updating dependencies:
        * Still need to update to JUnit 5.
        * Didn't move to very latest Jetty; just to latest of the previous release series (still supported!).
        * Have two `-webapp` test failures to resolve. Only 2! That's crazypants.

### 2018-01-29, Monday

* 1:25h (0838-1003): Issue #111: Upgrade runtime/platform dependencies.
    * Resolved one test failure.
    * Still stuck on the `GameIT` failure, though. Looks like it thinks the form is `display:none` when it goes to submit the name change.

### 2018-01-30, Tuesday

* 0:30h (2157-2227): Issue #111: Upgrade runtime/platform dependencies.
    * Trying to debug test failure. Started upgrading jQuery.

### 2018-01-31, Wednesday

* 0:33h (0810-0811,2115-2147): Issue #111: Upgrade runtime/platform dependencies.
    * Kicked off a `mvn verify` with the new jQuery version. Still failed in the same place.
    * Tried to find how/where to debug the jQuery execution in HtmlUnit. No luck yet.

### 2018-02-01, Thursday

* 3:00h (2015-2315): Issue #111: Upgrade runtime/platform dependencies.
    * Finally tracked down the remaining `GameIT.updateName()` test failure to an apparent HtmlUnit bug. Sent message to the `htmlunit-user` mailing list about it.
    * Build is now passing locally!
    * Need to take a look and see if anything else ought to be updated. JUnit 5, maybe?
    * Then, need to get build passing in Jenkins.

### 2018-02-03, Saturday

* 3:03h (1326-1410,1440-1530,2106-2235): Issue #111: Upgrade runtime/platform dependencies.
    * Got benchmarks running again.
    * Started cleaning up for commit. Still need to:
        1. Get wro4j changes finalized.
        2. Fix the Liquibase changelog, to avoid checksum errors.
        3. Switch to a new branch and commit.

### 2018-02-04, Sunday

* 4:57h (0800-0920,1101-1111,1300-1500,2155-2322): Issue #111: Upgrade runtime/platform dependencies.
    * Finalized wro4j changes.
    * Got Jenkins build working.
    * Cleaned up the Liquibase changelog.
    * Fixed intermittent testcase.
    * Fixed code coverage data collection.
    * Setup new Jenkins project.
    * Added benchmarks to `Jenkinsfile`.
    * Committed, merged, resolved.
    * Next up: deployment.

### 2018-02-10, Saturday

* 0:28h (2110-2138): Issue #111: Upgrade runtime/platform dependencies.
    * Worked to fix build failure by tweaking Jenkins config.
    * Looks like Nexus still doesn't see the `jenkins` user, though.
* 0:15h (2139-2154): Issue #113: Deploy to new `eddings`.
    * Copied files from `justdavis-ansible.git`.
    * Started updating that README.

### 2018-02-11, Sunday

* 5:17h (0807-0901,0929-1356): Issue #111: Upgrade runtime/platform dependencies.
    * Fixed the build.
        * Was on baby and chore duty for most of this time, so not very productive.
* 3:57h (1457-1731,2114-2337): Issue #113: Deploy to new `eddings`.
    * Got deployment working!
    * Still need to:
        1. Get `test.sh` working.
        2. Restore the old `rps` DB.
        3. Poke at the old DB to verify what the admin account is. Update config XML if necessary.
        4. Finish play to also generate cert, install Apache, config Apache.
        5. Wire up `Jenkinsfile` to actually deploy.

### 2018-02-12, Monday

* 1:33h (0830-0959,2129-2133): Issue #114: Liquibase production DB checksum error.
    * Restored old production `rps` DB.
    * Investigated, but couldn't find cause.
    * As a workaround, just added a `<validChecksum/>` entry.
    * Committed, tested, merged, deployed.
* 2:49h (2134-0023): Issue #113: Deploy to new `eddings`.
    * Verified by poking at the DB: my guess as to the admin account was correct.
    * Had to argue with Tomcat and Apache a lot, but got the production site deployed again!
    * Yay!!!

### 2018-02-13, Tuesday

* 0:42h (2140-2222): Issue #118: Duplicate key violation during guest login.
    * Pushed possible fix to PR.
    * Still need to figure out why errors weren't being captured in the `-webapp` 's log.

### 2018-02-14, Wednesday

* 1:34h (2125-2259): Issue #120: Errors not being logged.
    * Got 500 errors handled properly.
    * Still trying to get 404s handled correctly.

### 2018-02-15, Thursday

* 1:32h (0828-1000,2142-2215): Issue #120: Errors not being logged.
    * Implemented a fix for this and Issue #95. Need to get it passing tests.

### 2018-02-16, Friday

* 0:45h (0735-0820): Issue #120: Errors not being logged.
    * Got it passing tests and pushed out to a PR.
    * Now have 3 PRs to merge, but need to be careful, since builds still not safe to run concurrently.
* 0:45h (2200-2245): Issue #113: Deployment.
    * Merged everything and redeployed. Success!
    * Thought real hard about things:
        * Test envs will be hard. Shouldn't go in as part of this issue.
        * Need to start getting `Jenkinsfile` to run deploys.

### 2018-02-17, Saturday

* 6:50h (1000-1015,1140-1215,1430-1630,2100-0100): Issue #113: Deployment.
    * Got the `Jenkinsfile` running deployments successfully.
    * Merged to `master` and things got a bit odd: ran benchmarks even though I hadn't enabled them. No idea why...

### 2018-02-18, Sunday

* 2:20h (1000-1220): Issue #125: Jenkinsfile tweaks.
    * Spent way too long stumped by host key checking, but finally came up with a good solution.
* 1:15h (1325-1440): Issue #127: Ansible Vault password security.
    * Resolved.
