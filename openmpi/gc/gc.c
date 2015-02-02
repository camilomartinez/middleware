#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <stdbool.h>
#include "gc.h"

int main(int argc, char *argv[]) {
	const char* SPACE = " ";	
	FILE *fp;
	char buffer[100];
	char* token;
	unsigned int width, height, maxvalue;
	// Read command line parameters
	char* input_filename = argv[1];
	if (!input_filename) {
		// No command line input was given
		fprintf(stderr, "Input file required!\nAborting execution...\n");	
		exit(1);
	}

	fp = fopen(input_filename, "r");
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
	get_dimensions(buffer, &width, &height);
	// Get maxvalue
	fgets(buffer, sizeof(buffer), fp);
	maxvalue = atoi(start_tokenize(buffer));
	// New max value corrected with gamma	
	maxvalue = gamma_encode(maxvalue, maxvalue);
	
	// Reads line by line, encodes them and print
	int *line;
	while (fgets(buffer, sizeof(buffer), fp)) {
		// Allocate memory for line
		line = (int*)malloc(width*sizeof(*line));
		read_line(buffer, line, width);
		encode_line(line, width, maxvalue);
		print_line(line, width);
		free(line);
	}
	fclose(fp);
}

bool verify_file_format(char *buffer) {
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

void get_dimensions(char *buffer, int *width, int *height) {
	char *token;
	// Get dimensions
	token = start_tokenize(buffer);
	*width = atoi(token);
	token = continue_tokenize();
	*height = atoi(token);
}

void read_line(char *buffer, int *line, int width) {	
	int i;
	char *token_source, *token;
	for (i=0; i<width; i++) {		
		if (i == 0) {
			token_source = buffer;
		} else {
			token_source = NULL;
		}
		token = (char*)strtok(token_source, SEPARATOR);
		int value = atoi(token);
		line[i] = value; 
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

void encode_line(int *line, int width, int maxvalue) {
	int i;
	for (i=0; i<width; i++) {
		int value_in = line[i];
		int value_out = gamma_encode(value_in, maxvalue);
		line[i] = value_out; 
	}	
}

void print_line(int *line, int width) {
	int i;
	for (i=0; i<width; i++) {
		int value = line[i];
		printf("%d ", value);
	}
	printf("\n");
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