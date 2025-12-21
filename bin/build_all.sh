#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
set -u

DIR=`dirname "$0"`
DIR=`cd "${DIR}/.."; pwd`

CURDIR=`pwd`
cd $DIR

MVN_BIN="${MVN:-}"
if [ -z "${MVN_BIN}" ]; then
  if command -v mvn >/dev/null 2>&1; then
    MVN_BIN="mvn"
  elif [ -n "${SPARK_HOME:-}" ] && [ -x "${SPARK_HOME}/build/mvn" ]; then
    MVN_BIN="${SPARK_HOME}/build/mvn"
  else
    echo "ERROR: Maven not found. Install 'mvn' or set MVN=/path/to/mvn (or SPARK_HOME pointing at a Spark source tree with build/mvn)." >&2
    exit 1
  fi
fi

"${MVN_BIN}" clean package "$@"
