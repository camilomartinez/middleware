#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <stdbool.h>
#include "gc.h"

struct ImageParameters {
	int width;
	int height;
	int maxvalue;
};

int main(int argc, char *argv[]) {
	FILE *fp;
	char buffer[100];
	// pgm format parameters
	struct ImageParameters params;
	int width, height, maxvalue;
	// Read command line parameters
	char* input_filename = argv[1];
	if (!input_filename) {
		// No command line input was given
		fprintf(stderr, "Input file required!\nAborting execution...\n");	
		exit(1);
	}

	fp = fopen(input_filename, "r");
	// Initialize parameters
	read_header(fp, &params);
	width = params.width;
	height = params.height;
	maxvalue = params.maxvalue;
	// New max value corrected with gamma	
	maxvalue = gamma_encode(maxvalue, maxvalue);
	
	// Reads line by line, encodes them and print
	int i;
	// Encoded as row, column
	int **values;
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

	// Encodes the content	
	for (i = 0; i < height; ++i) {
		int* line = values[i];
		encode_line(line, width, maxvalue);
	}

	// Prints the content
	for (i = 0; i < height; ++i) {
		int* line = values[i];
		print_line(line, width);
	}	

	// Free all the values
	for (i = 0; i < height; ++i) {
		free(values[i]);
	}
	free(values);
	// Make sure to close the file
	fclose(fp);
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
	params->maxvalue = atoi(start_tokenize(buffer));
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

void encode_line(int *line, int width, int maxvalue) {
	int i;
	for (i=0; i<width; i++) {
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

void print_line(int *line, int width) {
	int i;
	for (i=0; i<width; i++) {
		int value = line[i];
		printf("%d ", value);
	}
	printf("\n");
}
