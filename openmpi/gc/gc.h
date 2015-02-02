#ifndef GC_H_   /* Include guard */
#define GC_H_

void get_line(char *buffer, FILE *fp);
bool verify_file_format(char *buffer);
void read_line(char *buffer, int *line, int width);
void encode_line(int *line, int width, int maxvalue);
void print_line(int *line, int width);
int gamma_encode(int v_in, int maxvalue);
int gamma_correction(int v_in, int maxvalue, double gamma);

#endif // GC_H_