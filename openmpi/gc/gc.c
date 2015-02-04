#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <stdbool.h>
#include "mpi.h"
#include "gc.h"

struct ImageParameters {
	int width;
	int height;
	int maxvalue;
};

int main(int argc, char *argv[]) {
	FILE *fp;
	char* output_filename;
	// pgm format parameters
	struct ImageParameters params;
	// pgm content values
	// Encoded as a single array
	// Ordered by row block of width elements
	// element (i,j) is at position [i*width+j]
	// with both i and j starting at zero
	int *values;
	// MPI parameters
	int numtasks, rank;
	// Init MPI
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
	// Reading the file only done by the root
	if (rank==0) {
		// Read command line parameters
		char* input_filename = argv[1];
		if (!input_filename) {
			// No command line input was given
			fprintf(stderr, "Input file required!\nAborting execution...\n");	
			exit(1);
		}
		output_filename = argv[2];
		if (!output_filename) {
			// No command line input was given
			printf("Using default output file: out.pgm\n");
			output_filename = "out.pgm";
		}
		// Read file	
		fp = fopen(input_filename, "r");
		// Initialize parameters
		read_header(fp, &params);
		values = read_content(fp, &params);		
		fclose(fp);		
	}
	// Broadcast parameters that are need by other processes
	int data[3];
	// Load them on the root
	if (rank==0) {
		data[0] = params.width;
		data[1] = params.height;
		data[2] = params.maxvalue;		
	}
	MPI_Bcast(&data, 3, MPI_INT, 0, MPI_COMM_WORLD);
	// Set them on slaves
	if (rank!=0) {
		params.width = data[0];
		params.height = data[1];
		params.maxvalue = data[2];
	}
	// Check received values
	printf("Process %d. width: %d, maxvalue: %d\n", rank, params.width, params.maxvalue);
	// Encode by scattering
	int totalcount = params.width * params.height;
	int sendcount = (totalcount) / numtasks;
	printf("Process %d. totalcount: %d sendcount: %d\n", rank, totalcount, sendcount);
	// Allocated dynamically per process
	int* line = (int*)malloc(sendcount*sizeof(int));
	// Send chunk to each process
	if (rank==0) {
		print_process_line_debug(rank, "scattered", values, totalcount);
	}
	MPI_Scatter(values, sendcount, MPI_INT,
		line, sendcount, MPI_INT,
		0, MPI_COMM_WORLD);
	// Encode lines received
	print_process_line_debug(rank, "received", line, sendcount);
	encode_line(line, sendcount, params.maxvalue);
	print_process_line_debug(rank, "encoded", line, sendcount);
	// Get all the chunks back
	MPI_Gather(line, sendcount, MPI_INT,
		values, sendcount, MPI_INT,
        0, MPI_COMM_WORLD);
	if (rank==0) {
		print_process_line_debug(rank, "gathered", values, totalcount);
	}
	free(line);
	// Single machine encode
	//encode_content(values, &params);		
	// Print done only by the root
	if (rank==0) {			
		// Print new values to output file
		print_content(output_filename, &params, values);
		// Cleanup
		free(values);
	}
	MPI_Finalize();
}

void read_header(FILE *fp, ImageParameters *params) {
	char buffer[100];
	// Confirms first line is pgm format
	fgets(buffer, sizeof(buffer), fp);
	verify_file_format(buffer);
	// Skip comment
	fgets(buffer, sizeof(buffer), fp);
	if(is_comment(buffer)){
		// Skip second line if comment	
		fgets(buffer, sizeof(buffer), fp);
	}
	// Get dimensions
	get_dimensions(buffer, params);
	// Get maxvalue
	fgets(buffer, sizeof(buffer), fp);
	int maxvalue = atoi(start_tokenize(buffer));
	// New max value corrected with gamma
	params->maxvalue = gamma_encode(maxvalue, maxvalue);		
}

void verify_file_format(char *buffer) {
	// Confirms it's pgm format
	if(buffer[0] != 'P' || buffer[1] != '2'){
		// Wrong file format
		fprintf(stderr, "Expected pgm file format\nAborting execution...\n");
		exit(1);
	}
}

bool is_comment(char *buffer) {
	return (buffer[0] == '#');
}

void get_dimensions(char *buffer, ImageParameters *params) {
	char *token;
	// Get dimensions
	token = start_tokenize(buffer);
	params->width = atoi(token);
	token = continue_tokenize();
	params->height = atoi(token);
}

int* read_content(FILE *fp, ImageParameters *params) {
	char buffer[100];
	// Values encoded as a single array
	// Ordered by row block of width elements
	// element (i,j) is at position [i*width+j]
	// with both i and j starting at zero
	int i, *values;
	int height = params->height;
	int width = params->width;
	// Allocate  array for all values
	values = (int*)malloc(width*height*sizeof(int));
	// Read all the content
	for (i = 0; i < height; ++i) {
		if (!fgets(buffer, sizeof(buffer), fp)) {
			fprintf(stderr, "Height greater than number of content lines\nAborting execution...\n");	
			exit(1);
		}
		read_line(buffer, width, values, i);
	}
	return values;
}

void read_line(char *buffer, int width, int *values, int line_number) {	
	int i;
	char *token;
	// Read and set the whole line
	for (i=0; i<width; i++) {		
		if (i == 0) {
			token = start_tokenize(buffer);
		} else {
			token = continue_tokenize();
		}
		int value = atoi(token);
		set_value(values, width, line_number, i, value);
	}
}

char* start_tokenize(char *buffer) {
	// Setup tokenizer reference
	return (char*)strtok(buffer, SEPARATOR);
}

char* continue_tokenize() {
	// Continue using previous reference
	return (char*)strtok(NULL, SEPARATOR);
}

void set_value(int *values, int width, int i, int j, int value) {
	int position = i*width + j;
	values[position] = value;
}

int get_value(int* values, int width, int i, int j) {
	int position = i*width + j;
	return values[position];
}

void encode_line(int *line, int size, int maxvalue) {
	int i;
	for (i=0; i<size; i++) {
		int value_in = line[i];
		int value_out = gamma_encode(value_in, maxvalue);
		line[i] = value_out; 
	}	
}

int gamma_encode(int v_in, int maxvalue) {
	return gamma_correction(v_in, maxvalue, ((double)1/GAMMA));
}

int gamma_correction(int v_in, int maxvalue, double gamma) {
	if (v_in == 0) {
		return 0;
	}
	double v_out = A * pow((double) v_in, gamma);
	int result = round(v_out);
	if (v_out > maxvalue) {
		result = maxvalue;
	} else if (result < 0) {
		result = 0;
	}
	return result;
}

void print_content(char* filename, ImageParameters *params, int *values)
{
	int i;
	FILE* fp = fopen(filename, "w+");
	// Print header
	// Format
	fprintf(fp, "P2\n");
	// width height
	fprintf(fp, "%d %d\n", params->width, params->height);
	// maxvalue
	fprintf(fp, "%d\n", params->maxvalue);
	// content
	for (i = 0; i < params->height; ++i) {
		fprint_line(fp, values, params->width, i);
	}
	fclose(fp);
}

void fprint_line(FILE *fp, int *values, int width, int line_number) {
	int i;
	for (i=0; i<width; i++) {
		int value = get_value(values, width, line_number, i);
		fprintf(fp, "%d ", value);
	}
	fprintf(fp, "\n");
}

void print_process_line_debug(int rank, char *event, int *line, int size) {
	int i;
	printf("Process %d %s:\n", rank, event);
	for (i=0; i<size; i++) {
		int value = line[i];
		if (i != (size - 1)) {
			printf("%d ", value);		
		} else {
			// Avoid space at the end of the line
			printf("%d", value);
		}
	}
	// End of line
	printf("\n");
}