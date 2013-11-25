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
    * Found a commit on trunk (3.0-SNAPSHOT) that fixes it: https://fisheye6.atlassian.com/changelog/cxf?cs=1482410
* 0.5h: Read up on Spring Security and Apache Shiro.
    * Spring Security seems to be tightly coupled to the rest of Spring, and with it itself. Don't think it'd be easy to integrate without dragging along the rest of Spring.
    * Apache Shiro doesn't look too bad. Not sure, but it might make it difficult to operate statelessly, as it seems to be big on session management.
* 4.0h: Hacked around the bug in CXF's ContainerRequestContextImpl.getSecurityContext().
    * Created unit tests for AuthenticationFilter and AuthorizationFilter.
    * (Kind of guessing on time here, was off & on all day.)
