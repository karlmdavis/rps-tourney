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
