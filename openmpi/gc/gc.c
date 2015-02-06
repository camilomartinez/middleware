#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "mpi.h"
#include "gc.h"

struct ImageParameters {
	int width;
	int height;
	int maxvalue;
};

int main(int argc, char *argv[]) {
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
	int numtasks, rank, name_len;
	char processor_name[MPI_MAX_PROCESSOR_NAME];
	// Init MPI
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
	MPI_Get_processor_name(processor_name, &name_len);
	// Reading the file only done by the root
	if (rank==0) {
		// Read command line parameters
		char* input_filename = argv[1];
		if (!input_filename) {
			// No command line input was given
			fprintf(stderr, "Error: Input file required!\nAborting execution...\n");	
			exit(1);
		}
		output_filename = argv[2];
		if (!output_filename) {
			// No command line input was given
			printf("Using default output file: out.pgm\n");
			output_filename = "out.pgm";
		}
		// Read all the content of the file
		values = read_pgm_file(input_filename, &params);	
	}
	// Broadcast parameters that are need by other processes
	int data[3];
	// Load them on the root
	if (rank==0) {
		data[0] = params.width;
		data[1] = params.height;
		data[2] = params.maxvalue;		
	}
	printf("Processor %s, rank %d. Before broadcast\n", processor_name, rank);
	MPI_Bcast(&data, 3, MPI_INT, 0, MPI_COMM_WORLD);
	printf("Processor %s, rank %d. After broadcast\n", processor_name, rank);
	// Set them on slaves
	if (rank!=0) {
		params.width = data[0];
		params.height = data[1];
		params.maxvalue = data[2];
	}
	// Check received values
	printf("Processor %s, rank %d. width: %d, maxvalue: %d\n", processor_name, rank, params.width, params.maxvalue);
	// Encode by scattering
	int totalcount = params.width * params.height;
	int sendcount = (totalcount) / numtasks;
	printf("Process %s, rank %d. totalcount: %d sendcount: %d\n", processor_name, rank, totalcount, sendcount);
	// Allocated dynamically per process
	int* line = (int*)malloc(sendcount*sizeof(int));
	// Send chunk to each process
	if (rank==0) {
		print_process_line_debug(processor_name, rank, "scattered", values, totalcount);
	}
	MPI_Scatter(values, sendcount, MPI_INT,
		line, sendcount, MPI_INT,
		0, MPI_COMM_WORLD);
	// Encode lines received
	print_process_line_debug(processor_name, rank, "received", line, sendcount);
	encode_line(line, sendcount, params.maxvalue);
	print_process_line_debug(processor_name, rank, "encoded", line, sendcount);
	// Get all the chunks back
	MPI_Gather(line, sendcount, MPI_INT,
		values, sendcount, MPI_INT,
        0, MPI_COMM_WORLD);
	// Free dynamically allocated buffer
	free(line);	
	if (rank==0) {
		print_process_line_debug(processor_name, rank, "gathered", values, totalcount);
		int scatteredcount = sendcount * numtasks;
		int missingcount = totalcount - scatteredcount;
		if (missingcount > 0) {
			line = &(values[scatteredcount]);
			encode_line(line, missingcount, params.maxvalue);
		}
	}
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

int* read_pgm_file(char *filename, ImageParameters *params) {
	int* values;
	FILE *fp;
	char ch;	
	fp = fopen(filename, "r");
	// Check if file was opened
	if (fp == NULL) {
		fprintf(stderr, "Error: Unable to open file %s\nAborting execution...\n", filename);
		exit(8);
	}
	read_header(fp, params);
	values = read_content(fp, params);
	fclose(fp);
	return values;
}

void read_header(FILE *fp, ImageParameters *params) {
	verify_file_format(fp);
	skip_comment(fp);
	get_dimensions(fp, params);
	// New max value corrected with gamma
	int maxvalue = params->maxvalue;	
	params->maxvalue = gamma_encode(maxvalue, maxvalue);		
}

void verify_file_format(FILE *fp) {
	char ch;
	// Confirms it's pgm format
	ch = getc(fp);
	if (ch == 'P') {
		ch = getc(fp);
		if (ch != '2') {
			fprintf(stderr, "Error: Not valid pgm file type\nAborting execution...\n");
			exit(1);
		}
	} else {
		fprintf(stderr, "Error: Not valid pgm file type\nAborting execution...\n");
		exit(1);
	}
	// skip to the newline
	while(getc(fp) != '\n');
}

void skip_comment(FILE *fp) {
	// If comment skip till the end of line
	while (getc(fp) == '#')
	{
		while (getc(fp) != '\n');
	}
}

void get_dimensions(FILE *fp, ImageParameters *params) {
	// Avoid issue with width not being read correctly
	fseek(fp, -1, SEEK_CUR);
	fscanf(fp,"%d", &((*params).width));
	fscanf(fp,"%d", &((*params).height));
	fscanf(fp,"%d", &((*params).maxvalue));
}

int* read_content(FILE *fp, ImageParameters *params) {
	// Values encoded as a single array
	// Ordered by row block of width elements
	// element (i,j) is at position [i*width+j]
	// with both i and j starting at zero
	int i, j, value, *values;
	int height = params->height;
	int width = params->width;
	// Allocate  array for all values
	values = (int*)malloc(width*height*sizeof(int));
	// Read all the content
	for (i = 0; i < height; ++i) {
		for (j = 0; j < width; ++j) {
			fscanf(fp,"%d", &value);
			set_value(values, width, i, j, value);
		}
	}
	return values;
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

void print_process_line_debug(char* name, int rank, char *event, int *line, int size) {
	int i;
	printf("Processor %s, rank %d %s:\n", name, rank, event);
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