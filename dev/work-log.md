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
