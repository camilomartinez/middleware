#include <stdio.h>

int main(int argc, char *argv[]) {
	const char* SPACE = " ";	
	FILE *fp;
	char buffer[100];
	char* token;
	unsigned int width, height, maxvalue;
	
	fp = fopen("feep.pgm", "r");
	// Confirms first line is pgm format
	fgets(buffer, sizeof(buffer), fp);
	if(buffer[0] != 'P' || buffer[1] != '2'){
		exit(1);
	}
	fgets(buffer, sizeof(buffer), fp);
	if(buffer[0] == '#'){
		// Skip second line if comment	
		fgets(buffer, sizeof(buffer), fp);	
	}
	// Get dimensions
	token = strtok(buffer, SPACE);
	width = atoi(token);
	token = strtok(NULL, SPACE);
	height = atoi(token);
	printf("width: %d, height: %d\n", width, height);	
	// Get maxvalue
	fgets(buffer, sizeof(buffer), fp);
	maxvalue = strtok(buffer, SPACE);

	while (fgets(buffer, sizeof(buffer), fp)) {
		printf("%s",buffer);
		token = strtok(buffer, SPACE);
		while (token) {
    	//	printf("token: %s\n", token);
    		token = strtok(NULL, SPACE);
		}		
	}
	fclose(fp);
} 