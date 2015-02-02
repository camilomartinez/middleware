#include <stdio.h>
#include <math.h>
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
		printf("%s",buffer);	
	}
	// Get dimensions
	token = strtok(buffer, SPACE);
	width = atoi(token);
	token = strtok(NULL, SPACE);
	height = atoi(token);
	printf("width: %d, height: %d\n", width, height);	
	// Get maxvalue
	fgets(buffer, sizeof(buffer), fp);
	maxvalue = atoi(strtok(buffer, SPACE));
	// New max value corrected with gamma	
	maxvalue = gamma_encode(maxvalue, maxvalue);
	printf("%d\n",maxvalue);		
	while (fgets(buffer, sizeof(buffer), fp)) {
		//printf("%s",buffer);
		token = strtok(buffer, SPACE);
		while (token) {
			int token_in = atoi(token);
			int token_out = gamma_encode(token_in, maxvalue);
    		printf("%d ", token_out);
    		token = strtok(NULL, SPACE);
		}
		printf("\n");
	}
	fclose(fp);
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