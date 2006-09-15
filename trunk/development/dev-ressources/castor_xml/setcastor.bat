@echo off
@rem Script to set the CLASSPATH for the generation of the CASTOR
@rem classes to process the XML config file
@rem First Release: 2005 March 13
@rem Latest Change: 2005 Oct 14
echo ibiblio/Maven style is assumed
echo Assuming, that you downloaded all necessary files to %USERPROFILE%\.maven
echo This is done using the "build.xml" file
echo Lousy.kizura@gmail.com, Oct 2005
set jar_res=%userprofile%\.maven\repository
echo JAR_RES set to '%JAR_RES%'
set classpath=%CLASSPATH%;%jar_res%\castor\jars\castor-0.9.7.jar
set classpath=%CLASSPATH%;%jar_res%\xerces\jars\xerces-2.6.2.jar
set classpath=%CLASSPATH%;%jar_res%\xerces\jars\xercesImpl-2.6.2.jar
set classpath=%CLASSPATH%;%jar_res%\log4j\jars\log4j-1.2.9.jar
set classpath=%CLASSPATH%;%jar_res%\commons-logging\jars\commons-logging-1.0.4.jar

