#!/bin/bash

# References:
# * https://github.com/icholy/ttygif

# The following packages must be installed prior to running this script:
# * imagemagick
# * ttyrec

# Get the script's directory. Reference: http://stackoverflow.com/questions/59895/can-a-bash-script-tell-what-directory-its-stored-in
scriptDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Get the project directory and pull the version from the POM.
projectDirectory=${scriptDirectory}/..
projectVersion=`xmllint --xpath "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']/text()" ${projectDirectory}/pom.xml`

# Clean up/create the directory we'll use.
gifDirectory=${projectDirectory}/target/record-gif
rm -rf ${gifDirectory}
mkdir ${gifDirectory}

# Get the ttygif project from git. Build it.
git clone https://github.com/icholy/ttygif.git ${gifDirectory}/ttygif.git &> /dev/null
(cd ${gifDirectory}/ttygif.git && make > /dev/null)

# Ready to go.
echo Record a console session by running:
echo $ ttyrec myrecording
echo 
echo Convert the recording into an animated GIF by running:
echo $ ${gifDirectory}/ttygif.git/ttygif myrecording
echo $ ${gifDirectory}/ttygif.git/concat.sh terminal.gif

