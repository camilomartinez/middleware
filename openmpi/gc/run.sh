#!/usr/bin/env bash
mpirun --mca btl_tcp_if_include eth1 -np 4 --host mpi-node-1,mpi-node-2 -output-filename log gc in_feep.pgm 