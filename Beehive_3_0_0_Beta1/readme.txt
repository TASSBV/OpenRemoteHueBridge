
================================================================================
===                                                                          
===                 BEEHIVE DEPLOYMENT GUIDE                                
===                                                                          
=== version 2.0.0                                http://www.openremote.org   
================================================================================

I. Introduction 
===============
"Beehive" is a web interface for the Beehive database, which is a device database hosted by OpenRemote 
that attempts to collect all manner of configuration information about various devices present in homes.
It's purpose is to seed the [modeler tools] with data that a home-owner or 
professional installer can use to discover device configurations.

II. Requirements
================
The "Beehive" requires this 3rd party software:
1) JBoss 4.2.3 GA or above, Tomcat 5.5.26 or above
2) Java 1.5 or above
3) MySQL 5.0 or above
4) Ant 1.7.1 or above

III. "Beehive" site deployment
==============================================
1) modify following configuration variables in "%PROJECT_ROOT%/config/config.properties"
	file (see comments inside this file for details):
    a) set "jdbc.username" parameter value to username to MySQL (for example: scott)
    b) set "jdbc.password" parameter value to password to MySQL (for example: tiger)
    c) set "work.dir" parameter value to the folder which is the workspace of the beehive, including svn-repos, workCopy and syncHistory directory.
    d) set "icons.dir" parameter value to where the icons folder is
    
2) modify the quartz time in "%PROJECT_ROOT%/config/spring-quartz.xml"

3) run "ant war" to get the war file in "%PROJECT_ROOT%/output" directory. 

4) create a database in MySQL named "beehive" using default charset 'utf8'.

5) run the sql in "%PROJECT_ROOT%/sql/import.sql" to seed the whole database.

6) if you choose JBoss as a web server, you MUST ensure that "jaxb-api.jar"(jaxb 2.1 API) 
	exists in the "%JBOSS_ENDORSED_DIRS%"(e.g. %JBOSS_HOME%/lib/endorsed).
7) find the svn configuration in /etc/subversion/config (windows,C:\Documents and Settings\%your user name%\Application Data\Subversion\config),
   remove '*.a' from the 'global-ignores' property and append 'Thumbs.db' to its value.

IV. Supported functions
=======================
In the current iteration we are focusing on collecting infrared (LIRC) configuration data into Beehive.
We have set several goals for this work in an attempt to make LIRC data more consumable for users:

1) Allow easier searching of LIRC data
2) Improve the ease of locating correct remote control data by introducing alternative device categories
3) Expose LIRC data for third party applications via HTTP/REST interface

V. "Beehive" logging
====================================
You can modify the log4j configuration in "%PROJECT_ROOT%/config/log4j.properties".

VI. "Beehive" javadoc
====================================
You can generate javadoc by using "ant javadoc"