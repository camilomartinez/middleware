#!/usr/bin/env bash
# eth1 must be the public network interface if there are communications issues check this
time mpirun --mca btl_tcp_if_include eth1 -np 4 --host mpi-node-1,mpi-node-2,mpi-node-3,mpi-node-4 -output-filename log gc in_feep.pgm 