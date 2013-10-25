#!/bin/bash

# Get the script's directory. Reference: http://stackoverflow.com/questions/59895/can-a-bash-script-tell-what-directory-its-stored-in
scriptDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Get the project directory and pull the version from the POM.
projectDirectory=${scriptDirectory}/..
projectVersion=`xmllint --xpath "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']/text()" ${projectDirectory}/pom.xml`

# Clean up/create the directory we'll use.
rm -rf ${projectDirectory}/target/extracted-dist/
mkdir ${projectDirectory}/target/extracted-dist/

# Unpack the -dist bundle.
tar --extract --ungzip --file $scriptDirectory/../target/rps-tourney-console-${projectVersion}-dist.tar.gz --directory ${projectDirectory}/target/extracted-dist/

# Run the app from the unpacked bundle.
(cd ${projectDirectory}/target/extracted-dist/rps-tourney-console-${projectVersion} && ./rpstourney.sh "$@")
