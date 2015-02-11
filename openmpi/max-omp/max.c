#include <stdio.h>
#include <omp.h>

int main(int argc, char *argv[]) {
	int data[] = {0,3,1,9,8,2,7,5,6,4};
	printf("Max is %d\n", max(data, 10));
}

int max(int *data, int size) {
	int i, threadId, curval, result = 0;
	// max supports 10 threads
	int maxval[] = {0,0,0,0,0,0,0,0,0,0};
	#pragma omp parallel for shared(data, size, maxval) private(i,curval) num_threads(2)
	for (i = 0; i < size; ++i) {
		threadId = omp_get_thread_num();
		curval = data[i];
		if (curval > maxval[threadId]) {
			maxval[threadId] = curval;		
			printf("Current max is %d from thread %d\n", curval, omp_get_thread_num());
		}		
	}

	for (i = 0; i < 10; ++i) {
		if (maxval[i] > result) {
			result = maxval[i];
		}
	}	
	return result;
}


