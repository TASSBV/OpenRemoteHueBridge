
echo OFF
CLS
set basePath=D:\openremotebridge
set antPath=%basePath%\vendor\apache-ant-1.9.2\bin

:MENU
ECHO.
ECHO ...............................................
ECHO choose your task, or 6 t o EXIT.
ECHO ...............................................
ECHO.
ECHO 1 - Build Beehive
ECHO 2 - Build Designer
ECHO 3 - Build Controller
ECHO 4 - Build All
ECHO 5 - Start Server (clears all files in "tomcat_root/webaps"
ECHO				copy new war's from the output directoys)
ECHO 6 - EXIT
ECHO.

SET /P M=Type 1, 2, 3, 4, 5 or 6 then press ENTER:
IF %M%==1 GOTO buildBeehive
IF %M%==2 GOTO buildDesigner
IF %M%==3 GOTO buildController
IF %M%==4 GOTO buildAll
IF %M%==5 GOTO runServer
IF %M%==6 GOTO EOF


:buildBeehive
D:
cd %antPath%
call ant war -buildfile %basePath%\Beehive_3_0_0_Beta1\build.xml
pause
GOTO MENU

:buildDesigner
D:
cd %antPath%
call ant war -buildfile %basePath%\Designer_2_13_9_Rosemary_20130330\build.xml
pause
GOTO MENU

:buildController
D:
cd %antPath%
call ant war -buildfile %basePath%\openremote106-ORCJAVA-300\build.xml
pause
GOTO MENU

:buildAll
D:
echo ----------------- Building Beehive -----------------
cd %antPath%
call ant war -buildfile %basePath%\Beehive_3_0_0_Beta1\build.xml
echo ----------------- Building Designer -----------------
cd %antPath%
call ant war -buildfile %basePath%\Designer_2_13_9_Rosemary_20130330\build.xml
echo ----------------- Building Controller -----------------
cd %antPath%
call ant war -buildfile %basePath%\openremote106-ORCJAVA-300\build.xml
echo ----------------- Building Complete -----------------
pause
GOTO MENU

:runServer
echo ----------------- DELETING ALL FILES IN WEBAPPS -----------------
echo Press any key to continue, or close the script
PAUSE > nul
D:
cd %basePath%\Software\apache-tomcat-6.0.18\webapps
del. /s /Q
rmdir. /s /Q
echo ----------------- Copying War's -----------------
xcopy %basePath%\Beehive_3_0_0_Beta1\output\beehive.war  %basePath%\apache-tomcat-6.0.18\webapps
xcopy %basePath%\Designer_2_13_9_Rosemary_20130330\output\modeler.war  %basePath%\apache-tomcat-6.0.18\webapps
xcopy %basePath%\openremote106-ORCJAVA-300\output\controller.war  %basePath%\apache-tomcat-6.0.18\webapps
echo ----------------- Copy Complete - Start Server -----------------
cd %basePath%\Software\apache-tomcat-6.0.18\bin
start cmd /K call openremote run
echo -------- Server started in new window, press any key to continue --------
pause > nul
GOTO MENU
