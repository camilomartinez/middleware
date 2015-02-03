#ifndef GC_H_   /* Include guard */
#define GC_H_

// gamma correction parameters
#define GAMMA 1.5
#define A 1
// Separator
#define SEPARATOR  " "
// Custom data types
typedef struct ImageParameters ImageParameters;

void read_header(FILE*, ImageParameters*);
void verify_file_format(char*);
bool is_comment(char*);
void get_dimensions(char*, ImageParameters*);
int** read_content(FILE*, ImageParameters*);
int* read_line(char *, int);
char* start_tokenize(char *);
char* continue_tokenize();
void encode_content(int** values, ImageParameters *params);
void encode_line(int*, ImageParameters *params);
int gamma_encode(int, int);
int gamma_correction(int, int, double);
void print_content(char*, ImageParameters*, int**);
void print_line(FILE*, int*, int);
void free_values(int**, int);

#endif // GC_H_