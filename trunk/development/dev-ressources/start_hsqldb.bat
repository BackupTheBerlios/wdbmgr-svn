@echo off
rem Start hypersonic database
title Hypersonic SQL Database
echo Start script for the hsql db
java -Xmx512m -cp .\jars\hsqldb-1.7.3.3.jar org.hsqldb.Server
