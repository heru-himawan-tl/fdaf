# FDAF (F.D.A.F) - Flexible Database Access Application Framework

FDAF (or F.D.A.F) is a free, open-source, MVC framework for flexibly creating
Java web applications based [JSF (JavaServer Face)](http://www.javaserverfaces.org/),
and also mainly designed for flexibly creating enterprise application,
implementing the standard Java EE. It favors both convention and
configuration, makes implementation and configuration to be more simple,
and even lets the programmer write less of codes.

## Why should you use FDAF?

A Java web-application as basically is database access application relies on
CRUD (Create - Read - Update - Delete) operations. The design will become
little bit complex, since it must provide several automation-controls to
allow application for properly executing the tasks as requested by users
in which the application must also be synchronizing itself to "be friendly
& interactivally in serving the users". In this case, there will be consist
thousand lines of code that programmer have to write. Here FDAF provides the
things to reduce such a complexity, so that the programmer will only need to
write JSF markups as necessary for a web-application's presentation-layer,
and/or write less Java codes for both side of web-tiers and business-logic-tiers.

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

## Author & Copyright Information

FDAF is designed by [Heru Himawan Tejo Laksono](https://www.linkedin.com/in/heru-himawan-tejo-laksono-65485716/)

Copyright (c) by Heru Himawan Tejo Laksono. All rights reserved.

## License

Copyright (c) Heru Himawan Tejo Laksono. All rights reserved.

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

