# FDAF (F.D.A.F) - Flexible Database Access Application Framework

FDAF (or F.D.A.F) is a free, open-source, MVC framework for flexibly creating
Java web applications based [JSF (JavaServer Face)](http://www.javaserverfaces.org/),
and also mainly designed for flexibly creating enterprise application,
implementing the standard Java EE. It favors both convention and
configuration, makes implementation and configuration to be more simple,
and even lets the programmer write less of codes.

## Why should you use FDAF?

### Technical View

A Java web-application with dynamic contents basically a _database access
application_ relies on CRUD (Create - Read - Update - Delete) operations.
It applies the _Model-View-Control_ or _MVC_ architecture, in which the
_Model_ represents the business or database code, the _View_ represents the
page design code, and the _Controller_ represents the navigational code.

In modern versions of Java EE, there's a default MVC framework called JSF
(JavaServer Face). Following this framework, Facelets is used for the _View_
and the _Controller_ is given. But you don't need to implement Facelets,
instead, you need to apply the concept called the _backing bean_ (which is
often referred to as the _Model_, but isn't a pure _Model_ itself). The
backing bean delegates to the real model such as _EJB services_. The backing
bean can also take up some _Controller_ responsibilities, for example
issuing a redirect, or putting a message in a kind of queue for the _View_
to display.

The modern design of a Java web-application applies the separation between
web-tier (web-application itself) as the _View_ and as the _Controller_ over
the business-tier as the _Model_. There are some technical considerations
in applying this design pattern, for example in case of security matter: _In
order to achieve some security norms, the business layer has to reside in a
more secure place that will not exposed directly to Internet_.

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

