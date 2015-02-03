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
	// Encoded as row, column
	int **values;
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
		// Encoded as row, column
		values = read_content(fp, &params);		
		fclose(fp);		
	}
	// Broadcast parameters that are need by other processes
	int data[2];
	// Load them on the root
	if (rank==0) {
		data[0] = params.width;
		data[1] = params.maxvalue;		
	}
	MPI_Bcast(&data, 2, MPI_INT, 0, MPI_COMM_WORLD);
	// Set them on slaves
	if (rank!=0) {
		params.width = data[0];
		params.maxvalue = data[1];
	}
	// Check all values were received
	printf("Process %d. width: %d, maxvalue: %d\n", rank, params.width, params.maxvalue);
	//MPI_Bcast(void* data, int count, MPI_Datatype datatype, int root, MPI_Comm communicator)
	// Print done only by the root
	if (rank==0) {			
		encode_content(values, &params);
		// Print new values to output file
		print_content(output_filename, &params, values);
		// Cleanup
		free_values(values, params.height);
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

int** read_content(FILE *fp, ImageParameters *params) {
	char buffer[100];
	// Values encoded as row, column
	int i, **values;
	int height = params->height;
	int width = params->width;
	// Allocate all arrays
	values = (int**)malloc(height*sizeof(int*));
	// Read all the content
	for (i = 0; i < height; ++i) {
		if (!fgets(buffer, sizeof(buffer), fp)) {
			fprintf(stderr, "Height greater than number of content lines\nAborting execution...\n");	
			exit(1);
		}
		values[i] = read_line(buffer, width);		
	}
	return values;
}

int* read_line(char *buffer, int width) {	
	int i, *line;
	char *token;
	// Allocate space
	line = (int*)malloc(width*sizeof(*line));
	for (i=0; i<width; i++) {		
		if (i == 0) {
			token = start_tokenize(buffer);
		} else {
			token = continue_tokenize();
		}
		int value = atoi(token);
		line[i] = value; 
	}
	return line;
}

char* start_tokenize(char *buffer) {
	// Setup tokenizer reference
	return (char*)strtok(buffer, SEPARATOR);
}

char* continue_tokenize() {
	// Continue using previous reference
	return (char*)strtok(NULL, SEPARATOR);
}

void encode_content(int** values, ImageParameters *params) {
	int i;
	for (i = 0; i < params->height; ++i) {
		int* line = values[i];
		encode_line(line, params);
	}
}

void encode_line(int *line, ImageParameters *params) {
	int i;
	for (i=0; i<params->width; i++) {
		int value_in = line[i];
		int value_out = gamma_encode(value_in, params->maxvalue);
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

void print_content(char* filename, ImageParameters *params, int **values)
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
		int* line = values[i];
		print_line(fp, line, params->width);
	}
	fclose(fp);
}

void print_line(FILE *fp, int *line, int width) {
	int i;
	for (i=0; i<width; i++) {
		int value = line[i];
		fprintf(fp, "%d ", value);
	}
	fprintf(fp, "\n");
}

void free_values(int **values, int height) {
	int i;
	// Free all the rows
	for (i = 0; i < height; ++i) {
		free(values[i]);
	}
	// Free the array of pointers
	free(values);
}