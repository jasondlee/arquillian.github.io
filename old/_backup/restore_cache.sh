#!/bin/bash

LIGHT_GREEN='\033[1;32m'
GREEN='\033[0;32m'
CLEAR='\033[0m'
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CACHE_DIR=${SCRIPT_DIR}/../_tmp/lanyrd

echo -e "${LIGHT_GREEN}-> Updating timestamp of all cached files ...${CLEAR}"
find . -exec touch {} \;

echo -e "${LIGHT_GREEN}-> Restoring to _tmp ...${CLEAR}"
[ -d $CACHE_DIR ] || mkdir -p $CACHE_DIR
rm -rf $CACHE_DIR/*
cp -R ${SCRIPT_DIR}/lanyrd/* $CACHE_DIR

echo -e "${GREEN}Done${CLEAR}"
