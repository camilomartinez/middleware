#include "mpi.h"
#include <stdio.h>

int main(int argc, char *argv[]) {
	int numtasks, rank, sendcount, recvcount, source;
	float sendbuf [4][4] = { {1,2,3,4}, {5,6,7,8}, {9,10,11,12}, {13,14,15,16} };
	float recvbuf[4];

	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &numtasks);

	if (numtasks == 4) {
		source = 0;
		sendcount = 4;
		recvcount = 4;
		MPI_Scatter (sendbuf, sendcount, MPI_FLOAT, recvbuf, recvcount, MPI_FLOAT, source, MPI_COMM_WORLD);
		printf("rank= %d Results: {%f, %f, %f, %f}\n", rank, recvbuf[0], recvbuf[1], recvbuf[2], recvbuf[3] );
	} else {
		printf("Error, you must specify 4 tasks\n");
	}

	MPI_Finalize();
}