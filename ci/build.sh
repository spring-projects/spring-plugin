#!/bin/bash

set -euo pipefail

MAVEN_OPTS="-Duser.name=jenkins -Duser.home=/tmp/spring-plugin-maven-repository" ./mvnw -P${PROFILE} -Dmaven.test.skip=true clean deploy -B
