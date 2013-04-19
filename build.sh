#!/bin/bash

# build debug.runtime.libraries
cd org.spoofax.debug.interfaces.jar
ant -f build.main.xml clean
ant -f build.main.xml

cd ../

cd org.spoofax.debug.interfaces.java.jar
ant -f build.main.xml clean
ant -f build.main.xml

cd ../

# the following project contains stratego code
cd org.strategoxt.imp.debug.instrumentationi

LIBDSLDI_DIR="../dist-libdsldi"
ARGS="instrument-all -lib $LIBDSLDI_DIR"
ant -f build.main.external.xml $ARGS
