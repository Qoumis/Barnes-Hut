#include "Utils.h"
#include "Classes.h"
#include <tbb/tbb.h>

using namespace tbb;

int ITERATIONS;
int NUM_THREADS;
int NUM_BODIES;
double UNIVERSE_SIZE;

const int DT = 1;
const double G = 6.674 * pow(10, -11);

vector<Body *> bodies;

void parseFile(ifstream &input_file) {
    string line;
    getline(input_file, line);
    NUM_BODIES = stoi(line);
    getline(input_file, line);
    UNIVERSE_SIZE = stod(line);

    for(int i = 0; i < NUM_BODIES; i++) {
        getline(input_file, line);

        vector<string> bodyData;
        string token;
        stringstream ss(line);
        while(getline(ss, token, ' '))
            bodyData.push_back(token);

        double x = stod(bodyData[0]);
        double y = stod(bodyData[1]);
        double Vx = stod(bodyData[2]);
        double Vy = stod(bodyData[3]);
        double mass = stod(bodyData[4]);
        string name = bodyData[5];

        bodies.push_back(new Body(x, y, Vx, Vy, mass, name));
    }
}

void writeToFile(ofstream &output_file){

    output_file << NUM_BODIES << endl;
    output_file << UNIVERSE_SIZE << endl;
    for(Body *b : bodies) 
        output_file << b->getX() << " " << b->getY() << " " << b->getVx() << " " << b->getVy() << " " << b->getMass() << " " << b->getName() << endl;
    
}

void freeTree(BHtree *node){
    if(node == NULL)
        return;

    freeTree(node->getRT());
    freeTree(node->getLT());
    freeTree(node->getLB());
    freeTree(node->getRB());

    /*if(node->isLeaf() && node->getBody() != NULL){
        delete node->getBody();
        node->setBody(NULL); 
    }*/
    
    delete node;
}

void startSimulationSequential(){
    BHtree *root = new BHtree(Quad(Point(0, 0), UNIVERSE_SIZE));

    for(Body *b : bodies)
        root->insertBody(b);

    //root->printTree(root);

    for(Body *b : bodies) 
        root->netForce(b);
    
    for(Body *b : bodies)
        b->updatePosition();

    freeTree(root);
    //cout << "Simulation done" << endl;
}

void startSimulationParallel(){
    BHtree *root = new BHtree(Quad(Point(0, 0), UNIVERSE_SIZE));

    for(Body *b : bodies)
        root->insertBody(b);

    parallel_for(blocked_range<size_t>(0, (size_t)NUM_BODIES), 
    [&](blocked_range<size_t> &r)->void{
        for(size_t i = r.begin(); i != r.end(); i++)
            root->netForce(bodies[i]);
    });

    parallel_for(blocked_range<size_t>(0, (size_t)NUM_BODIES), 
    [&](blocked_range<size_t> &r)->void{
        for(size_t i = r.begin(); i != r.end(); i++)
            bodies[i]->updatePosition();
    });

    freeTree(root);
}

void freeBodies(){
    for(Body *b : bodies)
        delete b;
}