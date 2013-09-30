

I. Build and Deployment Requirements
====================================

To build, you will need to install GWT SDK from Google first. Currently GWT version 2.0.4
is required. Modify the build.properties file to target your GWT 2.0.4 SDK install location.

Java SDK 6 is required for build. 

Apache ANT 1.7 or higher is required for the build scripts.


II. "Modeler" site deployment
==============================================
1) share a database named 'beehive' with Beehive in MySQL.
2) modify following configuration variables in "%PROJECT_ROOT%/config/config.properties"
    file (see comments inside this file for details):
    a) set "jdbc.url" parameter value to MySQL URL 
    b) set "jdbc.username" parameter value to username to MySQL (for example: scott)
    c) set "jdbc.password" parameter value to password to MySQL (for example: tiger)
    d) set "beehive.REST.Root.Url" parameter value to your Beehive REST Root URL
    e) set "beehive.lircdconf.REST.Url" parameter value to your Beehive REST lirc.conf URL
    f) set "beehive.RESR.Icon.Url" parameter value to your Beehive REST icons URL
    g) set "os.webapps.root" parameter value to your web server webapps root (eg: E:\\apache-tomcat-5.5.28\\webapps)
    h) set "webapp.server.root" parameter value to your web server root (eg: http://localhost:8080)
    i) set "controller.config.path" parameter value to your controller config xml descriptor file path
    
    j) set "mail.sender.host" to your mail host (eg: smtp.163.com)
    k) set "mail.sender.port" to your mail port (eg: 25)
    l) set "mail.sender.username" to your mail username (eg: openremote@163.com)
    m) set "mail.sender.password" to your mail password 
    n) set "mail.smtp.auth.on" to true if your mail service needs auth.
    o) set "mail.smtp.timeout" to your mail timeout in millisecond. (eg: 25000)

    
3) modify following configuration variables in "%PROJECT_ROOT%/build.properties"
    a) set parameter 'gwt.sdk' in build.properties to your GWT SDK home. This is required by GWT build.
    b) set parameter 'deploy.dir' in build.properties to your webapps folder of Tomcat.
     
4) run 'ant deploy' in command line under project dir. 

   
III. Supported functions
=======================
In the current iteration we are focusing on infrared (LIRC), KNX, X10, HTTP, TCP/IP, Telnet protocol.
1) create device, command, macro
2) create panel, group, screen
3) dnd switch, button, grid
4) export zip
5) building modeler: sensor, slider, switch
6) ui designer: slider, image, label
7) template save, share
    
IV. "Modeler" logging
====================================
You can modify the log4j configuration in "%PROJECT_ROOT%/config/log4j.properties".

V. "Modeler" javadoc
====================================
You can generate javadoc by using "ant javadoc"
