#!/bin/bash
SCRIPT_PATH=$(readlink -f $0)
BIN_DIR=$(dirname $SCRIPT_PATH)
kill $(cat $BIN_DIR/server.pid)