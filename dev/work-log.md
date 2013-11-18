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
