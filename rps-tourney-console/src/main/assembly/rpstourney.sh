#!/bin/bash

# cd into the RPS bundle, to enable use of relative paths here.
cd "${BASH_SOURCE%/*}" || exit

# Run the JAR, passing all args to it.
java -jar rps-tourney-console-${project.version}.jar "$@"
