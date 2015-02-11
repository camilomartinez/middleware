#include <stdio.h>
#include <omp.h>

int main(int argc, char *argv[]) {
	int data1[] = {0,3,2,9,4,2,6,5,6,4};
	int data2[] = {0,1,2,3,4,5,6,7,8,9};
	printf("Equal count is %d\n", equal_count(data1, data2, 10));
}

int equal_count(int *data1, int *data2, int size) {
	int count =0, i;
	#pragma omp parallel for shared(data1, data2, size) private(i) reduction(+:count) num_threads(2)
	for (i = 0; i < size; ++i) {
		int threadId = omp_get_thread_num();
		if (data1[i] == data2[i]) {
			++count;
			printf("Current count is %d from thread %d\n", count, omp_get_thread_num());
		}		
	}
	return count;
}