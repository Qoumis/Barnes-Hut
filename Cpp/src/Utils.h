#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <cmath>

using namespace std;

#ifndef UTILS_H
#define UTILS_H

extern int ITERATIONS;
extern int NUM_THREADS;
extern int NUM_BODIES;
extern double UNIVERSE_SIZE;

extern const int DT;
extern const double G;


#endif 

void parseFile(ifstream &inputFile);
void writeToFile(ofstream &outputFile);
void startSimulationSequential();
void startSimulationParallel();
void freeBodies();