#include <string>
#include <sstream>
#include <vector>
#include <iostream>
#include <cmath>

class Body{
    private:
        double x;
        double y;
        double Vx;
        double Vy;
        double mass;
        string name;

        double Fx;
        double Fy;

    public:
        Body(double x, double y, double Vx, double Vy, double mass, string name){
            this->x = x;
            this->y = y;
            this->Vx = Vx;
            this->Vy = Vy;
            this->mass = mass;
            this->name = name;

            this->Fx = 0;
            this->Fy = 0;
        }

        void setX(double x){
            this->x = x;
        }

        void setY(double y){
            this->y = y;
        }

        void setVx(double Vx){
            this->Vx = Vx;
        }

        void setVy(double Vy){
            this->Vy = Vy;
        }

        void setMass(double mass){
            this->mass = mass;
        }

        void setName(string name){
            this->name = name;
        }

        void setFx(double Fx){
            this->Fx = Fx;
        }

        void setFy(double Fy){
            this->Fy = Fy;
        }

        double getX(){
            return x;
        }

        double getY(){
            return y;
        }

        double getVx(){
            return Vx;
        }

        double getVy(){
            return Vy;
        }

        double getMass(){
            return mass;
        }

        string getName(){
            return name;
        }

        double getFx(){
            return Fx;
        }

        double getFy(){
            return Fy;
        }

        double getDistance(Body *b){
            double dx = b->getX() - this->x;
            double dy = b->getY() - this->y;

            return sqrt(pow(dx, 2) + pow(dy, 2));
        }

        void updateNetForce(Body *b){
            double r = this->getDistance(b);
            double F = (G * this->mass * b->getMass()) / pow(r, 2);

            this->setFx(F * (b->getX() - this->x) / r);
            this->setFy(F * (b->getY() - this->y) / r);
        }

        void updatePosition(){
            double Ax = this->Fx / this->mass;
            double Ay = this->Fy / this->mass;

            this->setVx(this->Vx + Ax * DT);
            this->setVy(this->Vy + Ay * DT);

            this->setX(this->x + this->Vx * DT);
            this->setY(this->y + this->Vy * DT);

            this->setFx(0);
            this->setFy(0);
        }

        string toString(){
            stringstream ss;
            ss << this->name << ": x: " << this->x << " | y: " << this->y <<" | Vx: " << this->Vx << " | Vy: " << this->Vy << " | mass: " << this->mass;
            return ss.str();
        }
};

class Point{
    private:
        double x;
        double y;

    public:
        Point(double x, double y){
            this->x = x;
            this->y = y;
        }

        void setX(double x){
            this->x = x;
        }

        void setY(double y){
            this->y = y;
        }

        double getX(){
            return x;
        }

        double getY(){
            return y;
        }
};

class Quad{

    private:
        Point center;
        double size;
    
    public:
        Quad(Point center, double size): center{center}, size{size} {}

        bool containsBody(Body *b){
            double x = b->getX();
            double y = b->getY();

            double xLeft = this->center.getX() - this->size, xRight = this->center.getX() + this->size;
            double yTop = this->center.getY() + this->size, yBot = this->center.getY() - this->size;

            if(x >= xLeft && x <= xRight && y >= yBot && y <= yTop)
                return true;
            
            return false;
        }

        void setCenter(Point center){
            this->center = center;
        }

        void setSize(double size){
            this->size = size;
        }

        Point getCenter(){
            return center;
        }

        double getSize(){
            return size;
        }
};

class BHtree{
    private:
        //left top, left bottom, right top, right bottom quads
        BHtree * LT; 
        BHtree * LB;
        BHtree * RT;
        BHtree * RB;
        //this can represent an actual body if the node is a leaf node OR a snapshot of the center of mass of the quadrant if the node is an internal node 
        Body * body;
        //this is the region of the universe that the BHtree represents
        Quad quad;

    public:
        BHtree(Quad quad): quad{quad} {
            this->LT = NULL;
            this->LB = NULL;
            this->RT = NULL;
            this->RB = NULL;
            this->body = NULL;
        }

        void insertBody(Body *b){
            if(this->body == NULL){
                this->setBody(b);
                return;
            }

            if(isLeaf()){
                double centerX = this->quad.getCenter().getX();
                double centerY = this->quad.getCenter().getY();
                double quadSize = this->quad.getSize();

                //leaf node with a body already in it, so we need to divide
                RT = new BHtree(Quad(Point(centerX + quadSize/2, centerY + quadSize/2), quadSize/2));
                LT = new BHtree(Quad(Point(centerX - quadSize/2, centerY + quadSize/2), quadSize/2));
                LB = new BHtree(Quad(Point(centerX - quadSize/2, centerY - quadSize/2), quadSize/2));
                RB = new BHtree(Quad(Point(centerX + quadSize/2, centerY - quadSize/2), quadSize/2));

                //insert the existing & the new body into the appropriate quadrants
                insertInApprQuad(this->body);
                insertInApprQuad(b);
            }
            else{   
                // update the center of mass of the 'dummy' body in that quadrant
                double totalMass = this->body->getMass() + b->getMass();
                double x = (this->body->getX() * this->body->getMass() + b->getX() * b->getMass()) / totalMass;
                double y = (this->body->getY() * this->body->getMass() + b->getY() * b->getMass()) / totalMass;

                this->body = new Body(x, y, 0, 0, totalMass, "Center of mass snapshot");
                // find the appropriate quadrant to insert the new body b
                insertInApprQuad(b);
            }
        }

        void insertInApprQuad(Body *body){
            if(RT->quad.containsBody(body))
                RT->insertBody(body);
            else if(LT->quad.containsBody(body))
                LT->insertBody(body);
            else if(LB->quad.containsBody(body))
                LB->insertBody(body);
            else if(RB->quad.containsBody(body))
                RB->insertBody(body);
            else
                cout << "Body " << body->getName() << " is not in any quad" << endl;
        }

        bool isLeaf(){
            return this->LT == NULL && this->LB == NULL && this->RT == NULL && this->RB == NULL;
        }

        void printTree(BHtree *node){
            if(node == NULL)
                return;

            if(node->getBody() != NULL){
                if(node->isLeaf())
                    cout << node->getBody()->toString() << endl;
                else
                    cout << "Internal " << node->getBody()->getName() << " | Center of mass -> " << node->getBody()->getMass() << " | uniSize -> " << node->getQuad().getSize() << " center -> " << node->getQuad().getCenter().getX() << " , " << node->getQuad().getCenter().getY() << endl;
            }
            printTree(node->getRT());
            printTree(node->getLT());
            printTree(node->getLB());
            printTree(node->getRB());
        }

        void netForce(Body *b){
            if(this->getBody() == NULL )
                return;

            //if the node is a leaf node and its not body b (the body we calculate the force on), then calculate and update the net force of b
            if(this->isLeaf()){
                if(!(this->getBody() == b))
                    b->updateNetForce(this->getBody());
            } 
            //internal node
            else{
                double s = this->quad.getSize();
                double d = this->getBody()->getDistance(b);

                //if the width of the region of the internal node devided by the distance between the body b and the center of mass of the internal node
                //is less than the threshold value, then the internal node is far away and we calculate the force that is applied to b 
                //from the (snapshot) center of mass of the internal node (from all the bodies in that quad)
                if(s/d < 0.5)
                    b->updateNetForce(this->getBody());

                //if the internal node is not far away, then we recursively call the netForce method on the children of the internal node
                else{
                    this->RT->netForce(b);
                    this->LT->netForce(b);
                    this->LB->netForce(b);
                    this->RB->netForce(b);
                }
                
            }
        }

        void setBody(Body *b){
            this->body = b;
        }

        void setQuad(Quad quad){
            this->quad = quad;
        }

        BHtree * getLT(){
            return LT;
        }

        BHtree * getLB(){
            return LB;
        }

        BHtree * getRT(){
            return RT;
        }

        BHtree * getRB(){
            return RB;
        }

        Body * getBody(){
            return body;
        }

        Quad getQuad(){
            return quad;
        }
};

