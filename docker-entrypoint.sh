#!/usr/bin/env bash
set -e

# Make sure the data directory is accessible
chown -R kepler:kepler /usr/src/kepler/db

gosu valgrind "$@"
