@echo off
title Kepler Server - Habbo Hotel Emulation

set CLASSPATH=kepler.jar;lib\HikariCP-3.4.1.jar;lib\mariadb-java-client-2.3.0.jar;lib\netty-all-4.1.33.Final.jar;lib\slf4j-log4j12-1.7.25.jar;lib\slf4j-api-1.7.25.jar;lib\log4j-1.2.17.jar;lib\commons-configuration2-2.2.jar;lib\commons-lang3-3.9.jar;lib\commons-lang-2.6.jar;lib\commons-validator-1.6.jar;lib\gson-2.8.0.jar;lib\spring-security-crypto-5.7.3.jar;lib\bcprov-jdk15on-1.70.jar;lib\chesslib-1.1.1.jar;lib\commons-beanutils-1.9.2.jar;lib\commons-logging-1.2.jar;lib\commons-digester-1.8.1.jar;lib\commons-collections-3.2.2.jar
java -classpath "%CLASSPATH%" org.alexdev.kepler.Kepler %*

pause