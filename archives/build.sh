#!/bin/bash
trap 'echo "${BASH_SOURCE[0]}: line ${LINENO}: status ${?}: user ${USER}: func ${FUNCNAME[0]}"' ERR
set -o errexit
set -o errtrace

bash ./gradlew build

/bin/cp -f build/api-spec/*.yaml build/api-spec/*.json api/
/bin/cp -f build/docs/asciidoc/*.html api/
