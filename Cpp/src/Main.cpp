#include "Utils.h"
#include <tbb/tbb.h>

using namespace tbb;

int main(int argc, char* argv[]){

    if (argc != 5)
    {
        cout << "Invalid number of arguments. Correct usage: BarnesHut <input_file> #iterations #threads <output_file>" << endl;
        exit(1);
    }

    ITERATIONS = atoi(argv[2]);
    NUM_THREADS = atoi(argv[3]);
    
    ifstream input_file(argv[1]);
    if (!input_file.is_open()) {
        cout << "Error! Failed to open file : " << argv[1] << endl;
        exit(1);
    }

    ofstream output_file(argv[4]);
    if (!output_file.is_open()) {
        cout << "Error! Failed to open file : " << argv[3] << endl;
        exit(1);
    }

    parseFile(input_file);

    if(NUM_THREADS <= 0){
        for(int i = 0; i < ITERATIONS; i++)
            startSimulationSequential();
    }
    else{
        global_control global_limit(global_control::max_allowed_parallelism, NUM_THREADS);
        for(int i = 0; i < ITERATIONS; i++)
            startSimulationParallel();
    }

    writeToFile(output_file);

    freeBodies();

    input_file.close();
    output_file.close();
    return 0;
}

