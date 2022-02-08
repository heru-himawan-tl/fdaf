The F.D.A.F Framework Custom Core Source
========================================

If you want to add additional custom core sources, you should work within this
directory, `/custom-core-source/`. You shouldn't touch everything inside the
`/core-source/` directory.

You may add e.g. your own `/custom-core-source/base/src/main/java/your_custom_package`
directory and all sources within, and so on. The F.D.A.F framework initializer
will combine your custom sources with any other generated compilable sources.

Places For Custom Web Application Design Parts
----------------------------------------------

The following directories and/or files are intended to place or add custom web application design parts:

- `/custom-core-source/webapp/src/main/java/fdaf/webapp/base/` - directory to place custom web application API classes.
- `/custom-core-source/webapp/src/main/java/fdaf/webapp/bean/common/` - directory to place custom JSF managed beans purposed as common web controller.
- `/custom-core-source/webapp/src/main/java/fdaf/webapp/bean/` - directory to place free custom JSF managed beans.
- `/custom-core-source/webapp/src/main/java/fdaf/webapp/bean/system/` - directory to place custom JSF managed beans purposed as system web controller.
- `/custom-core-source/webapp/src/main/java/fdaf/webapp/bean/utility/` - directory to place custom JSF managed beans purposed as utility.
- `/custom-core-source/webapp/src/main/java/fdaf/webapp/servlet/` - directory to place custom java servlet classes.
- `/custom-core-source/webapp/src/main/java/fdaf/webapp/websocket/` - directory to place custom FDAF-based web socket end-point classes.
- `/custom-core-source/webapp/src/main/resources/fdaf/webapp/rc/messages/custom_callback_message.properties` - resource bundle file to define custom callback messages.
- `/custom-core-source/webapp/src/main/resources/fdaf/webapp/rc/messages/custom_label.properties` - resource bundle file to define custom web user interface labels.
- `/custom-core-source/webapp/src/main/resources/fdaf/webapp/rc/messages/custom_message.properties` - resource bundle file to define custom messages.
- `/custom-core-source/webapp/src/main/resources/META-INF/resources/component/` - directory to place custom JSF component files.
- `/custom-core-source/webapp/src/main/resources/META-INF/resources/css/` - directory to place custom cascading style sheets files.
- `/custom-core-source/webapp/src/main/resources/META-INF/resources/js/` - directory to place custom JavaScript files.
- `/custom-core-source/webapp/src/main/webapp/` - directory to place custom JSF files of program pages.
- `/custom-core-source/webapp/src/main/webapp/WEB-INF/templates/` - directory to place custom JSF program template files.
- `/custom-core-source/webapp/src/main/webapp/web-resources/` - directory to place custom miscellaneous files.

Places For Custom Base API Classes 
----------------------------------

You may add or put your custom FDAF base API classes such as interface or
abstraction inside:  
`/custom-core-source/base/src/main/java/`

It is recommended to declare the FDAF base API classes under package named
`fdaf.base`, which will be placed inside:  
`/custom-core-source/base/src/main/java/fdaf/base/`

Places For Custom Enterprise Application Parts
----------------------------------------------

The following directories are intended to place or add custom enterprise application parts:

- `/custom-core-source/src/main/java/fdaf/logic/ejb/callback/` - directory to place custom enterprise java beans for facade callbacks.
- `/custom-core-source/src/main/java/fdaf/logic/ejb/` - directory to place custom enterprise java beans for general purpose.
- `/custom-core-source/src/main/java/fdaf/logic/ejb/facade/` - directory to place custom enterprise java beans for facade services.
- `/custom-core-source/src/main/java/fdaf/logic/ejb/repository/` - directory to place custom enterprise java beans for JPA entity repository.
- `/custom-core-source/src/main/java/fdaf/logic/ejb/services/` - directory to place custom singleton enterprise java beans.

