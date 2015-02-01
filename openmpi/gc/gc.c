#include <stdio.h>

int main(int argc, char *argv[]) {
	// Just read the file
	FILE *fp;
	char buffer[100];

	fp = fopen("feep.pgm", "r");
	while (fgets(buffer, sizeof(buffer), fp)) {
		printf("%s",buffer);
	}
	fclose(fp);
}