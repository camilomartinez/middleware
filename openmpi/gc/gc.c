#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "gc.h"
// gamma correction parameters
#define GAMMA 1.5
#define A 1

int main(int argc, char *argv[]) {
	const char* SPACE = " ";	
	FILE *fp;
	char buffer[100];
	char* token;
	unsigned int width, height, maxvalue;
	
	fp = fopen("feep.pgm", "r");
	// Confirms first line is pgm format
	fgets(buffer, sizeof(buffer), fp);
	printf("%s",buffer);
	if(buffer[0] != 'P' || buffer[1] != '2'){
		exit(1);
	}
	fgets(buffer, sizeof(buffer), fp);
	printf("%s",buffer);
	if(buffer[0] == '#'){
		// Skip second line if comment	
		fgets(buffer, sizeof(buffer), fp);
	}
	// Get dimensions
	token = (char*)strtok(buffer, SPACE);
	width = atoi(token);
	token = (char*)strtok(NULL, SPACE);
	height = atoi(token);
	// Get maxvalue
	fgets(buffer, sizeof(buffer), fp);
	maxvalue = atoi((char*)strtok(buffer, SPACE));
	// New max value corrected with gamma	
	maxvalue = gamma_encode(maxvalue, maxvalue);
	printf("%d\n",maxvalue);
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

void read_line(char *buffer, int *line, int width)
{
	int i;
	char *token_source, *token;
	for (i=0; i<width; i++) {		
		if (i == 0) {
			token_source = buffer;
		} else {
			token_source = NULL;
		}
		token = (char*)strtok(token_source, " ");
		int value = atoi(token);
		line[i] = value; 
	}
}

void encode_line(int *line, int width, int maxvalue){
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