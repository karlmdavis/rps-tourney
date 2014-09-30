#!/bin/bash

scriptDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
pom=$scriptDir/pom.xml

# Specify the rps-tourney-* version to deploy as the first argument, e.g. 2.0.0-SNAPSHOT.
# If not specified, it will be left out of the command, and the POM will default to the
# project's current version.
# Please note that, for some reason, Cargo will only deploy artifacts that have been 
# deployed to Nexus (via `mvn clean deploy`); artifacts in the local repository do not
# seem to be used.
versionArg=""
if [[ -n $1 ]]
  then
    versionArg="-Drps.version=$1"
fi

mvn -f $pom org.codehaus.cargo:cargo-maven2-plugin:redeploy $versionArg
