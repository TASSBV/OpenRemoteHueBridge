OpenRemoteHueBridge
===================


This project is on Github to help people setup OpenRemote fast and simple. 
We did this because we had some problems ourselfs. 
After following the description you will have the following parts running:
- Designer 2.13.9
- Controller 1.0.6
- Beehive 3.0.0 Beta1  

This project also contains the implementation of the HueBridge Protocol in OpenRemote to control the Philips HueBridge.

Configuring the Beehive
------------------------
The "Beehive" requires this 3rd party software:

- For the project Tomcat is used as server, a runable version is located in the repository 
- Java EE 6 JDK was used in the project, make sure to set the "JAVA\_HOME" Environment variables in windows (EXAMPLE: JAVA\_HOME C:\Program Files\Java\jdk1.6.0_45)
- MySQL 5.6.13 was used for the project (the installer can be found in the repository
- Ant 1.9.2 was used (a version can be found in the repository)

After you installed/found all requirements follow the following steps ->> 
Open the config.properties (Beehive_3_0_0_Beta1\config) 

If Needed

- Change the jdbc url
- Change the User name and Password
- Change the work.dir to en existing directory
- Change the icons.dir

Now start your MySQL server

- Create a database called beehive (or any other name if you changed the jdbc connection url) using default charset 'utf8'
- Run the SQL script located in Beehive_3_0_0_Beta1\sql\import.sql this will create all needed tables in the database.
- Remove the '*.a' and add Thumbs.db from the global svn ignore list (right click, tortoise, settings)

Configuring the Designer
------------------------
To build, you will need GWT SDK from Google. The version used for the project is located in: Vendor\gwt-2.4.0  
  Modify the build.properties (Designer_2_13_9_Rosemary_20130330) file to target your GWT.  
JavaEE SDK 6 is required for build. Apache ANT 1.7 or higher is required for the build scripts. Version 1.9.2 can be found in the repository.

Open the config.properties Designer_2_13_9_Rosemary_20130330\config and change the following:

If needed
- Change the jdbc url
- Change the username and password
- Change the beehive.REST.Root.Url to your ip address (or localhost if you plan to run everything locally)
- Change the irService.REST.Root.Url to your ip address (or localhost if you plan to run everything locally)
- Change the webapp.server.root to your ip address (or localhost if you plan to run everything locally)

Change the mail settings, to the correct server. If there is no mail server available leave it empty, user accounts can be activated in the database in the user table.

- Open the build.properties in Designer_2_13_9_Rosemary_20130330
- Change the path of the gwt.sdk located in \Vendor\gwt-2.4.0

Configuring the Controller
--------------------------

To configure the controller you need to change the settings in the config.properties file. This file can be found in openremote106inteli\config.
At the bottom of the file you can change the urls to point to your own beehive and designer. It is also possible to point to the official website if you wish to only adjust the controller. The official beehive url is composed different then the local beehive as is seen in the beehive rest url below. The online official modeler url is composed normally but starts with http://designer.openremote.com/. Use localhostif you run beehive and designer locally.
In the config.properties

Change the beehive REST Url with the ip address where beehive is hosted.
```
//Example:
beehive.REST.Root.Url = http://10.10.4.96:8080/beehive/rest/
#beehive.REST.Root.Url = http://beehive.openremote.org/3.0/alpha5/rest/   //online beehive
```

Change the controller syncing line to false.
```  
//Example:
controller.performBeehiveSyncing=false
```

Change the access useraccount service url with the ip address where the modeler is hosted. 
```
//Example:
beehiveAccountService.REST.Root.Url = http://10.10.4.96:8080/modeler/uas/rest/
#beehiveAccountService.REST.Root.Url = http://designer.openremote.com/uas/rest/   //online modeler
```

Change the access devicediscovery service url with the ip address where the modeler is hosted.
```
//Example:
beehiveDeviceDiscoveryService.REST.Root.Url = http://10.10.4.96:8080/modeler/dds/rest/
#beehiveDeviceDiscoveryService.REST.Root.Url = http://designer.openremote.com/dds/rest/    //online modeler
```

Change the access controllercommand service url with the ip address where the modeler is hosted. 
```
//Example:
beehiveControllerCommandService.REST.Root.Url = http://10.10.4.96:8080/modeler/ccs/rest/
#beehiveControllerCommandService.REST.Root.Url = http://designer.openremote.com/ccs/rest//     //online modeler
```

How to add your own protocol to the controller is well explained on the official website here:  http://www.openremote.org/display/docs/Developer+How+To+-+Adding+Your+Own+Protocol+to+OpenRemote+Boss+2.0+Controller

Building
----------
When all the projects are configured, you can use the build and run script to build all the projects and run the Tomcat server. Todo this we need to change a variable in the script, depending on where you have placed the checkout.

- located the script in the Build-RunScript folder
- Open the script in an editor (like notepad++)
- On line 4 you will find the "basePath" variable, change this to reflect the path of the base folder where you copied these projects to. (Example: D:\openremotebridge make sure to not place any trailing slash)
- Save the script in a location you can find it
- Open the script by double clicking
- Follow the steps in the script
 
Authorzing user without mail
------------------------------

When the designer cant send a verification mail for whatever reason the error handling is not optimal. A new user can still register at the designer you host but the user will get the message that the username is not available. 
The user will be still be created in Beehive but needs to be given permission manually. This can be done to set Value of "Valid" in the user table to "1". The user should now be able to login.

