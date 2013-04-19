#!/bin/bash

SPOOFAX_DEBUG_DIR=`dirname "$(cd ${0%/*}/ && echo $PWD/${0##*/})"`

function GitClean {
	git clean -f -dx .
	git clean -f -X .
	git clean -f -x .
	git checkout .
}

function Prepare {
	# copy stuff to utils
	mkdir $SPOOFAX_DEBUG_DIR/org.strategoxt.imp.debug.instrumentation/utils
	cp $SPOOFAX_DEBUG_DIR../spoofax-project-utils/* $SPOOFAX_DEBUG_DIR/org.strategoxt.imp.debug.instrumentation/utils
}

GitClean
Prepare

# build debug.runtime.libraries
cd $SPOOFAX_DEBUG_DIR/org.spoofax.debug.interfaces
ant -f build.main.xml clean
ant -f build.main.xml


cd $SPOOFAX_DEBUG_DIR/org.spoofax.debug.interfaces.java
ant -f build.main.xml clean
ant -f build.main.xml


# the following project contains stratego code
cd $SPOOFAX_DEBUG_DIR/org.strategoxt.imp.debug.instrumentation


ANT_STATS_DIR=$SPOOFAX_DEBUG_DIR/stats/ant-stats/
ANT_LOGS_DIR=$SPOOFAX_DEBUG_DIR/stats/ant-logs/

mkdir -p $ANT_STATS_DIR
mkdir -p $ANT_LOGS_DIR

# arguments to configure AntStatistics
THIRD_PARTY_DIR="$SPOOFAX_DEBUG_DIR/../thirdparty"
ANT_STAT_EXTRA_ARGS="-Dantstatistics.directory=$ANT_STATS_DIR"
ANT_STAT_ARGS="-lib $THIRD_PARTY_DIR -logger de.pellepelster.ant.statistics.AntStatisticsLogger $ANT_STAT_EXTRA_ARGS"

ANT_EXTRA_ARGS="$ANT_STAT_ARGS"

LIBDSLDI_DIR="$SPOOFAX_DEBUG_DIR/../dist-libdsldi"
ARGS="-lib $LIBDSLDI_DIR $ANT_EXTRA_ARGS "
ant -f build.main.external.xml $ARGS instrument-all
