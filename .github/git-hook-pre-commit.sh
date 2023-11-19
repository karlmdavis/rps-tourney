#!/bin/bash

##
# This Git pre-commit hook script verifies that the Java source code is formatted consistently, and errors
# (preventing commit) if it is not.
#
# Git will call this script from the repository root. A non-zero exit code will halt the commit, unless
# `--no-verify` is specified.
##


# Stop script on errors.
set -o pipefail  # Non-zero returns from piped commands throw ERR.
set -o errtrace  # Non-zero returns in functions throw ERR.
set -o nounset   # Attempts to use an uninitialized variable throw ERR.
set -o errexit   # Non-zero returns from the main script throw ERR.

# This function can be called to handle errors.
function error_handler() {
  local CALLER_LINENO="$1"
  local MESSAGE="$2"
  local CODE="${3:-1}"

  local FULL_MESSAGE="Error on or near line ${CALLER_LINENO}: \"${MESSAGE}\". Exiting with status: ${CODE}."
  echo -e "${FULL_MESSAGE}"
  exit "${CODE}"
}

# Trap any errors, calling error() when they're caught.
trap 'error_handler ${LINENO}' ERR

echo 'Running fmt plugin check.'
# TODO switch plugin group after we're past Java 8
if mvn -f apps/ spotless:check >/dev/null 2>&1; then
  echo 'Verified Java source code format: a-okay.'
else
  echo -e "Inconsistencies discovered in Java formatting check. Run 'mvn spotless:check' for details or 'mvn spotless:apply' to automatically apply the required formatting."
  exit 1
fi
