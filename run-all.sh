#!/usr/bin/env bash
./build.sh
./deploy/api-server/run-in-docker.sh ${1}
./deploy/all/run-in-docker.sh ${1}