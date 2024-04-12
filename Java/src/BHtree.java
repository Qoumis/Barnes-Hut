public class BHtree {
    
    //left top, left bottom, right top, right bottom quads
    private BHtree LT, LB, RT, RB;
    //this can represent an actual body if the node is a leaf node OR a snapshot of the center of mass of the quadrant if the node is an internal node 
    private Body body;
    //this is the region of the universe that the BHtree represents
    private Quad quad;

    public BHtree(Quad quad){
        this.quad = quad;
        this.body = null;
        this.LT = null;
        this.LB = null;
        this.RT = null;
        this.RB = null;
    }

    public void insertBody(Body b){
        if(this.body == null){
          //  System.out.println("Inserting Body " + b.getName() + " with mass : " + b.getMass() + " in Quad " + this.quad.getCenter().getX() + " , " + this.quad.getCenter().getY() + " with size " + this.quad.getSize());
            this.setBody(b);
            return;
        }

        if(isLeaf()){
            double centerX = this.quad.getCenter().getX();
            double centerY = this.quad.getCenter().getY();
            double quadSize = this.quad.getSize();

            //leaf node with a body already in it, so we need to divide
            this.RT = new BHtree(new Quad(new Point(centerX + quadSize/2, centerY + quadSize/2), quadSize/2));
            this.LT = new BHtree(new Quad(new Point(centerX - quadSize/2, centerY + quadSize/2), quadSize/2));
            this.LB = new BHtree(new Quad(new Point(centerX - quadSize/2, centerY - quadSize/2), quadSize/2));
            this.RB = new BHtree(new Quad(new Point(centerX + quadSize/2, centerY - quadSize/2), quadSize/2));

            //insert the existing & the new body into the appropriate quadrants
            insertInApprQuad(this.body);
            insertInApprQuad(b);
        }
        else{   
            // update the center of mass of the 'dummy' body in that quadrant
            double totalMass = this.body.getMass() + b.getMass();
            double x = (this.body.getX() * this.body.getMass() + b.getX() * b.getMass()) / totalMass;
            double y = (this.body.getY() * this.body.getMass() + b.getY() * b.getMass()) / totalMass;

            this.body = new Body(x, y, 0, 0, totalMass, "Center of mass snapshot");
            // find the appropriate quadrant to insert the new body b
            insertInApprQuad(b);
        }
    }

    public void printTree(BHtree node){
        if(node == null)
            return;

        if(node.getBody() != null){
            if(node.isLeaf())
                System.out.println(node.getBody().toString());
            else
                System.out.println("Internal " + node.getBody().getName() + " | Center of mass -> " + node.getBody().getMass() + " | uniSize -> " + node.getQuad().getSize() + " center -> " + node.getQuad().getCenter().getX() + " , " + node.getQuad().getCenter().getY());
        }
        printTree(node.getRT());
        printTree(node.getLT());
        printTree(node.getLB());
        printTree(node.getRB());
    }

    public boolean isLeaf(){
        return (this.LT == null && this.LB == null && this.RT == null && this.RB == null);
    }

    public void insertInApprQuad(Body body){
        if(RT.quad.containsBody(body))
            RT.insertBody(body);
        else if(LT.quad.containsBody(body))
            LT.insertBody(body);
        else if(LB.quad.containsBody(body))
            LB.insertBody(body);
        else if(RB.quad.containsBody(body))
            RB.insertBody(body);
        else
            System.out.println("Body " + body.getName() + " is not in any quad");
    }

    public void netForce(Body b){
        if(this.getBody() == null )
            return;

        //if the node is a leaf node and its not body b (the body we calculate the force on), then calculate and update the net force of b
        if(this.isLeaf()){
            if(!this.getBody().equals(b))
                b.updateNetForce(this.getBody());
        } 
        //internal node
        else{
            double s = this.quad.getSize();
            double d = this.getBody().getDistance(b);

            //if the width of the region of the internal node devided by the distance between the body b and the center of mass of the internal node
            //is less than the threshold value, then the internal node is far away and we calculate the force that is applied to b 
            //from the (snapshot) center of mass of the internal node (from all the bodies in that quad)
            if(s/d < 0.5)
                b.updateNetForce(this.getBody());

            //if the internal node is not far away, then we recursively call the netForce method on the children of the internal node
            else{
                this.RT.netForce(b);
                this.LT.netForce(b);
                this.LB.netForce(b);
                this.RB.netForce(b);
            }
            
        }
    }

    public void setBody(Body body){
        this.body = body;
    }

    public void setQuad(Quad quad){
        this.quad = quad;
    }

    public BHtree getLT(){
        return this.LT;
    }

    public BHtree getLB(){
        return this.LB;
    }

    public BHtree getRT(){
        return this.RT;
    }

    public BHtree getRB(){
        return this.RB;
    }

    public Body getBody(){
        return this.body;
    }

    public Quad getQuad(){
        return this.quad;
    }

}

//this class represents a quadrant of the universe after each division
class Quad {
    private Point center;
    private double size; 
    public Quad(Point center, double size){
        //System.out.println("Creating Quad with center " + center.getX() + " , " + center.getY() + " and subUNIsize " + size + "(actual length is " + size*2 + ")");
        this.center = center;
        this.size = size;
    }

    public boolean containsBody(Body b){
        double x = b.getX();
        double y = b.getY();

        double xLeft = this.center.getX() - this.size, xRight = this.center.getX() + this.size;
        double yTop = this.center.getY() + this.size, yBot = this.center.getY() - this.size;

       // System.out.println("x : " + xLeft + " , " + xRight + " y : " + yBot + " , " + yTop + " | " + x + " , " + y);

        if(x >= xLeft && x <= xRight && y >= yBot && y <= yTop)
            return true;
        
        return false;
    }

    public void setCenter(Point center){
        this.center = center;
    }

    public void setSize(double size){
        this.size = size;
    }

    public Point getCenter(){
        return this.center;
    }

    public double getSize(){
        return this.size;
    }

}


//this class is used to represent the center of a quadrant
class Point {
    private double x, y;
    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }
}