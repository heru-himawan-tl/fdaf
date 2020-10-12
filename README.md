# FDAF (F.D.A.F) - Flexible Database Access Application Framework

FDAF (or F.D.A.F) is a free, open-source, MVC framework for creating
Java web applications based [JSF (JavaServer Face)](http://www.javaserverfaces.org/),
and also mainly designed for creating enterprise application,
implementing the standard Java EE. It favors both convention and
configuration, makes implementation and configuration to be more simple,
and lets the programmer write less of codes.

## Why should you use FDAF?

### Engineering View: From The CRUD Principle Into The Modern Web-Application Design

A Java web-application to handle dynamic contents is basically a _database
access application_ relies on CRUD (Create - Read - Update - Delete) operations.
It applies the _Model-View-Control_ or _MVC_ architecture, in which the
_Model_ represents the business or database code, the _View_ represents the
page design code, and the _Controller_ represents the navigational code.

In modern versions of Java EE, there's a default MVC framework called JSF
(JavaServer Face). Following this framework, Facelets is used for the _View_
and the _Controller_ is given. But you don't need to implement Facelets,
instead, you need to apply the concept called the _backing bean_ (which is
often referred to as the _Model_, but isn't a pure _Model_ itself). The
backing bean delegates to the real _Model_ such as _EJB services_. The backing
bean can also take up some _Controller_ responsibilities, for example
issuing a redirect, or putting a message in a kind of queue for the _View_
to display. 

The modern design of a Java web-application, also in FDAF fashion, applies
the separation between web-tier (web-application itself) as the _View_ and as
the _Controller_ over the business-tier as the _Model_. There are various
technical considerations in applying this kind of design pattern,
for example:

- Security matter: In order to achieve some security norms, the business
layer has to reside in a more secure place that will not exposed directly to
Internet.

- Interactivity matter: A web-application must keep its function interactively
without encountered total interruption whenever the business-tier fall in
serious problem. For example, when business-tier encountered database-server
problem, the web-tier will keep interact to the user by informing current
states to let the user take reasonable decision in working with the
web-application in current situation.

### The Solution: Reduce The Write Of Thousand Lines Of Code, Debugging Complexity, & Compilation Complexity

FDAF offers the solutions regarding the above described engineering view, by
providing ready-to-implement API and JSF components to let the developers
build the modern web-application that follows the modern version of Java EE.
The API collections include the abstractions and interfaces to apply modern
& advanced web-application's _View_ layer & _Controller_ and complex _Model_
of business logic application. Implementing FDAF's API(s) and FDAF's JSF
components will reduce the complexity of code and debugging; you no need
to write thousand lines of code or markup.

FDAF also includes the compilation-tools to reduce the compilation
complexities. For example, you no need to write the complicated JPA
entity-classes with their complicated bunch of "setter" and "getter"
methods, but you need only to write the _primitive model of JPA
entity-classes_ and let FDAF do the rest things to do. FDAF will help you
to organize your web-application & business-logic-application source-codes
to be a  _ready-to-distribute-and-compilable-source-code_. And, if you want,
FDAF will help you to compile your web-application &
business-logic-application into _ready-to-deploy-application_ in the form
of _WAR (Web Application Archive)_, _EAR (Enterprise Application Archive)_,
or a standalone JAR (if apply FDAF as a Thorntail application).

## Builtin Ready-To-Use-Application-Modules Source Codes

The distribution of FDAF includes the following ready-to-use-web-application-modules
source codes:

- Administrator Dashboard
- User Session Management Boards:
  - Login
  - User Account Registration
  - Password Reset
- Account Management Boards:
  - Add Administrator
  - Staff Invitation
  - User (with listing, editor (to add new record & alter), view, remove, mass remove, and check orphan data)
  - Department (with listing, editor (to add new record & alter), view, remove, mass remove, and check orphan data)
  - Role (with listing, editor (to add new record & alter), view, remove, mass remove, and check orphan data)
  - User Group (with listing, editor (to add new record & alter), view, remove, mass remove, and check orphan data)
  - User Group Member (with listing, editor (to add new record & alter), view, remove, mass remove, and check orphan data)
  - Employee (with listing, editor (to add new record & alter), view, remove, mass remove, and check orphan data)
- Mailer Configuration Board

Besides the above listed builtin sources, you will find ready-to-use
business-logic-application-modules source codes within FDAF.

## Supported Application Server Platform

FDAF supports the following application server platforms for deployment:

- [WildFly](https://www.wildfly.org/)
- [GlashFish](https://javaee.github.io/glassfish/)
- [Payara](https://www.payara.fish/)
- [Thorntail](https://thorntail.io/)

Notice: Thorntail development was discontinued, the support for Thorntail
probably will be unexpectedly defective in the future.

## Supported JPA Providers

FDAF for this initial release supports only the following JPA providers:

- [Hibernate](https://hibernate.org/orm/)
- [EclipseLink](https://www.eclipse.org/eclipselink/)

## Documentation

Documentation (API, Manuals, & Getting Started) not available yet within this
initial release.

## License

Copyright (c) [Heru Himawan Tejo Laksono](https://www.linkedin.com/in/heru-himawan-tejo-laksono-65485716/).
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holders nor the names of its
   contributors may be used to endorse or promote products derived from this
   software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

