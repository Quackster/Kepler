#!/bin/sh
exec java \
    --enable-native-access=ALL-UNNAMED \
    -classpath "kepler.jar:lib/*" \
    org.alexdev.kepler.Kepler
