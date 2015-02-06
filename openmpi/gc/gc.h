#ifndef GC_H_   /* Include guard */
#define GC_H_

// gamma correction parameters
#define GAMMA 1.5
#define A 1
// Separator
#define SEPARATOR  " "
// Custom data types
typedef struct ImageParameters ImageParameters;

int* read_pgm_file(char*, ImageParameters*);
void read_header(FILE*, ImageParameters*);
void verify_file_format(FILE*);
void skip_comment(FILE*);
void get_dimensions(FILE*, ImageParameters*);
int* read_content(FILE*, ImageParameters*);
void set_value(int*, int, int, int, int);
void encode_line(int*, int, int);
int gamma_encode(int, int);
int gamma_correction(int, int, double);
void print_content(char*, ImageParameters*, int*);
void fprint_line(FILE*, int*, int, int);
void print_process_line_debug(char*, int, char*, int*, int);

#endif // GC_H_