rps-tourney-webservice
===========
*Development Notes*


## Introduction

This file contains notes on the development of this project.


## Selecting a JAX-RS Framework

The two primary choices I'm aware of are:

* [Jersey](https://jersey.java.net/index.html): the reference implementation of the JAX-RS specification
* [Apache CXF](http://cxf.apache.org/): the Apache web services framework, which includes support for JAX-RS

To make a long story short: I've decided to go with CXF for the time being. I checked out Jersey first, but was unhappy with some design choices they made:

1. They wrote their own DI library, [hk2](http://hk2.java.net/), and Jersey has a hard dependency on it. While options exist to bridge between it and Guice, this just sounds frustrating to deal with.
1. They wrote their own deployment container, [Grizzly](http://grizzly.java.net/). While other standard webapp containers *can* be used, e.g. Jetty, their home-baked one seems to be the best supported and the default. It's definitely not a big issue, but was a bit off-putting.

The official documentation for CXF is a bit rough, but there's tons of sample code out there, which makes that less of an issue.


## Useful References

This project was my first foray into the world of Java web services. I came across the following useful references:

* JAX-RS with Apache CXF
    * [Apache CXF Docs: JAX-RS : Services Configuration](http://cxf.apache.org/docs/jaxrs-services-configuration.html)
    * [Apache CXF Jax-RS Sample Code](http://svn.apache.org/viewvc/cxf/trunk/distribution/src/main/release/samples/jax_rs/basic/src/main/java/demo/jaxrs/server/)
    * [BookApplication.java from Apache CXF Test](http://svn.apache.org/repos/asf/cxf/trunk/systests/jaxrs/src/test/java/org/apache/cxf/systest/jaxrs/BookApplication.java)
    * [JAX-RS With Apache CXF](http://www.techiekernel.com/2012/12/jax-rs-with-apache-cxf.html)
        * I found this reference later in my search and it ended up being the most useful/correct.
        * The [GitHub project](https://github.com/karasatishkumar/JAXRS-CXF) associated with it was particularly helpful.
    * [ Converting Jersey REST Examples to Apache CXF](http://www.jroller.com/gmazza/entry/jersey_samples_on_cxf)
        * Don't think I actually used this, but there's a lot of helpful-looking material here.
    * [JAX-RS 2.0 New Features](http://www.slideshare.net/algermissen/jaxrs-20-new-features)
        * This is an *amazing* resource. Wish I'd read it much earlier.
* How to Embed Jetty
    * [Jetty Docs: Embedding Jetty](http://www.eclipse.org/jetty/documentation/current/embedding-jetty.html)
    * [Apache CXF Wiki: JAX-RS Testing](https://cwiki.apache.org/confluence/display/CXF20DOC/JAXRS+Testing)
    * Configuring it via a `web.xml` file
        * [Stack Overflow: Add web application context to Jetty](http://stackoverflow.com/questions/4390093/add-web-application-context-to-jetty)
        * [Stack Overflow: Add resources to Jetty programmatically](http://stackoverflow.com/questions/3718221/add-resources-to-jetty-programmatically)
* Serving JSON with Apache CXF
    * [Apache CXF Docs: JAX-RS: Data Bindings](http://cxf.apache.org/docs/jax-rs-data-bindings.html)
    * [Apache CXF Docs: JSON Overview](http://cxf.apache.org/docs/json-support.html)


## Authentication

The game should support "guest" players: players that have not explicitly created an account. As much as possible, it should try to support these players fully: allowing them to specify a name, allowing them to leave and restart the game without losing their state, tracking their game history, etc. It should allow these users to later "upgrade" their account either by authenticating with a third-party (such as Google, if they're on an Android device) or by specifying a username and password specific to the game itself.

                                   +---------+
                                   | Account |
                                   +----+----+
                                        |
                                        | (has one or more)
                                        |
                                    +---v---+
                                    | Login |
             +---------------------->---^---<---------------------+
             |                          |                         |
             | (is a)                   | (is a)                  | (is a)
             |                          |                         |
    +--------+----------+    +----------+----------+   +----------+--------+
    | Guest Auth Cookie |    | Username & Password |   | OAuth Credentials |
    +-------------------+    +---------------------+   +-------------------+

Each login mechanism will have separate web service methods that allow clients/users to authenticate. Once authenticated, whatever login mechanism was used will return an authentication cookie (the "auth token") for the Account instance that the login is associated with. All other web service methods that require authentication will simply check for a valid auth token and use that to determine what user/Account is making the call.
