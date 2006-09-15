@echo off
rem Make sure your database is started
echo --
echo PLEASE MAKE SURE YOUR DATABASE IS STARTED!
echo Supported databases:
echo   MySQL 4/5
echo   Hypersonic DB (hsqldb, included)
echo Please refer to 'wdbearmanager_sql.properties' for connection settings.
echo --
rem Start application
java -jar wdbearmanager.jar %1 %2 %3 %4 %5 %6 %7
