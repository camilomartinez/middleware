#!/usr/bin/env bash

apt-get update
apt-get install -y openmpi-bin openmpi-common openssh-client openssh-server libopenmpi1.3 libopenmpi-dbg libopenmpi-dev
# Useful dev tools
apt-get install gdb -y
apt-get install make -y