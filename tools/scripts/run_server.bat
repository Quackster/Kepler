@echo off
title Kepler Server - Habbo Hotel Emulation

java --enable-native-access=ALL-UNNAMED -classpath "kepler.jar;lib\*" org.alexdev.kepler.Kepler %*

pause
