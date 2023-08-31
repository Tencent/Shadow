#!/usr/bin/env bash

set -eo pipefail

git restore gradle/wrapper gradlew gradlew.bat
git clean -fdx .