#ifndef GC_H_   /* Include guard */
#define GC_H_

// gamma correction parameters
#define GAMMA 1.5
#define A 1
// Separator
#define SEPARATOR  " "

void read_header(FILE*, int*, int*, int*);
bool verify_file_format(char*);
bool is_comment(char*);
void get_dimensions(char*, int*, int*);
void read_line(char*, int*, int);
char* start_tokenize(char *);
char* continue_tokenize();
void encode_line(int*, int, int);
void print_line(int*, int);
int gamma_encode(int, int);
int gamma_correction(int, int, double);

#endif // GC_H_