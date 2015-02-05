#!/usr/bin/env bash
mpirun -np 2 -v --host mpi-node-1,mpi-node-2 gc in_feep.pgm