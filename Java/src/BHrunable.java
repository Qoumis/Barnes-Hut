import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class BHrunable implements Runnable{
    
    private BHtree root;
    private ArrayList<Body> bodies;
    private int from, to;
    private CyclicBarrier barrier;

    public BHrunable(BHtree root, ArrayList<Body> bodies, int from, int to, CyclicBarrier barrier){
        this.root = root;
        this.bodies = bodies;
        this.from = from;
        this.to = to;
        this.barrier = barrier;

        //System.out.println("From : " + from + " To : " + to);
    }

    public void run() {
        try{
            for(int i = from; i < to; i++){
                Body b = bodies.get(i);
                root.netForce(b);
            }

            //wait for all the threads to finish calculating net forces, then update positions
            barrier.await();

            for(int i = from; i < to; i++){
                Body b = bodies.get(i);
                b.updatePosition();
            }
        }
        catch(Exception e){
            System.out.println("Error : " + e);
            System.exit(1);
        }
    }
}
