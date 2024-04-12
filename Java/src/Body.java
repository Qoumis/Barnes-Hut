public class Body {
    
    private double x;
    private double y;
    private double Vx;
    private double Vy;
    private double mass;
    private String name;

    private double Fx;
    private double Fy;

    public Body(double x, double y, double Vx, double Vy, double mass, String name){
        this.x = x;
        this.y = y;
        this.Vx = Vx;
        this.Vy = Vy;
        this.mass = mass;
        this.name = name;

        this.Fx = 0;
        this.Fy = 0;
    }

    public void updatePosition(){
        double Ax = this.Fx / this.mass;
        double Ay = this.Fy / this.mass;

        this.setVx(this.Vx + Ax * Utils.DT);
        this.setVy(this.Vy + Ay * Utils.DT);

        this.setX(this.x + this.Vx * Utils.DT);
        this.setY(this.y + this.Vy * Utils.DT);

        this.setFx(0);
        this.setFy(0);
    }

    public double getDistance(Body b){
        double dx = b.getX() - this.x;
        double dy = b.getY() - this.y;

        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public void updateNetForce(Body b){
        double r = this.getDistance(b);
        double F = (Utils.G * this.mass * b.getMass()) / Math.pow(r, 2);

        this.setFx(F * (b.getX() - this.x) / r);
        this.setFy(F * (b.getY() - this.y) / r);
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public void setVx(double Vx){
        this.Vx = Vx;
    }

    public void setVy(double Vy){
        this.Vy = Vy;
    }

    public void setMass(double mass){
        this.mass = mass;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setFx(double Fx){
        this.Fx = Fx;
    }

    public void setFy(double Fy){
        this.Fy = Fy;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getVx(){
        return this.Vx;
    }

    public double getVy(){
        return this.Vy;
    }

    public double getMass(){
        return this.mass;
    }

    public String getName(){
        return this.name;
    }

    public double getFx(){
        return this.Fx;
    }

    public double getFy(){
        return this.Fy;
    }

    public String toString(){
        return this.name + ": x:" + this.x + " y:" + this.y + " mass:" + this.mass;
    }

}
