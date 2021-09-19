# FDAF (F.D.A.F) - Flexible Database Access Application Framework

FDAF (or F.D.A.F) is a free, open-source, MVC framework for creating
Java web applications based [JSF (JavaServer Face)](http://www.javaserverfaces.org/),
and also mainly designed for creating enterprise application,
implementing the standard Java EE. It favors both convention and
configuration, makes implementation and configuration to be more simple,
and lets the programmer write less of codes.

## Engineering Views Behind FDAF Invention

A Java web-application to handle dynamic contents basically a _database
access application_ relies on CRUD operations (Create - Read - Update - Delete).
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
layer has to reside in a more secure place that will not be exposed directly to
Internet.

- Interactivity matter: A web-application must keep its function interactively
without encountered total interruption whenever the business-tier fall in
serious problem. For example, when business-tier encountered database-server
problem, the web-tier will keep interact to the user by informing current
states to let the user take reasonable decision in working with the
web-application in current situation.

## FDAF Features

### FDAF Gears

FDAF provides the gears to simplify the build of Java EE web application and
business logic application:

- FDAF Web Application API's - abstraction to base and simplify web application
  backing beans, web application controllers, and web socket application.
- FDAF JSF Components - reusable JSF components to simplify creation of view
  layers such as data record list, data record viewer, data record editor,
  configuration editor board, and any web page models.
- FDAF Business Logic API's - abstraction to base business logic application
  or business tear that can be easy to integrate with the web tear. 
- FDAF Compilation Tools:
  - Initializer - tool to initialize and organize the compilable source
  - Modeler - tool to model the JPA entity class based the primitive entity
    class
  - Build Invoker - tool to invoke build processes to finalize the compilable
    source into ready-to-deploy application

### Builtin Ready-To-Use-Application-Modules Source Codes

The distribution of FDAF includes the following ready-to-use web application
modules source codes:

- Administrator Dashboard  
- User Session Management Boards:  
  - Login  
  - User Account Registration  
  - Password Reset  
- Account Management Boards:  
  - Add Administrator  
  - Staff Invitation  
  - User [1]  
  - Department [1]  
  - Role [1]  
  - User Group [1]  
  - User Group Member [1]  
  - Employee [1]  
- Common Configuration Board  
- Mailer Configuration Board  
- File Manager

Modules marked as [1] are builtin with the following functions:  
- listing (with search tool, ordering tool, pagination)  
- editor (to add new record & alter)  
- single record viewer  
- single record removal  
- search tool  
- mass removal  
- orphan data check  

Besides the above listed builtin sources, you will find ready-to-use
business-logic-application-modules source codes within FDAF.

### Supported Application Server Platform

FDAF supports the following application server platforms for deployment:

- [WildFly](https://www.wildfly.org/)   
- [Apache Tomee Plume >= V.8.x.x](https://tomee.apache.org/)  
- [Thorntail](https://thorntail.io/)  

Notice:
- Thorntail development was discontinued, the support for Thorntail
probably will be unexpectedly defective in the future.  
- The newer [GlashFish](https://javaee.github.io/glassfish/) and
[Payara](https://www.payara.fish/) were removed from the list of
supported application server, since they have bugs those
prevent FDAF application to work with them.  
- Due [Apache Tomee Plus](https://tomee.apache.org/) applies OpenJPA
& MyFaces, it prevents FDAF application for working properly. 

### Supported JPA Providers

FDAF for this initial release supports only the following JPA providers:

- [Hibernate](https://hibernate.org/orm/)  
- [EclipseLink](https://www.eclipse.org/eclipselink/)  

## Source Code

FDAF source code is available at GITHUB: [https://github.com/heru-himawan-tl/fdaf](https://github.com/heru-himawan-tl/fdaf)

## Application Example

An FDAF application example source code is available at GITHUB:
[https://github.com/heru-himawan-tl/fdaf-application-example](https://github.com/heru-himawan-tl/fdaf-application-example)

## License

Copyright (c) [Heru Himawan Tejo Laksono](https://github.com/heru-himawan-tl).
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

