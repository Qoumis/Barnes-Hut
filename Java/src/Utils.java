import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class Utils {
    
    static int ITERATIONS;
    static int NUM_THREADS;
    static int NUM_BODIES;
    static double UNIVERSE_SIZE;

    final static int DT = 1;
    final static double G = 6.674 * Math.pow(10, -11);

    private ArrayList<Body> bodies;

    void parseFile(File input_file){
        this.bodies = new ArrayList<Body>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(input_file));
            NUM_BODIES = Integer.parseInt(br.readLine());
            UNIVERSE_SIZE = Double.parseDouble(br.readLine());
            for(int i = 0; i < NUM_BODIES; i++){
                String[] body = br.readLine().split(" ");
                double x = Double.parseDouble(body[0]);
                double y = Double.parseDouble(body[1]);
                double Vx = Double.parseDouble(body[2]);
                double Vy = Double.parseDouble(body[3]);
                double mass = Double.parseDouble(body[4]);
                String name = body[5];
                Body b = new Body(x, y, Vx, Vy, mass, name);
                bodies.add(b);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error while reading file:\n" + e);
            System.exit(1);
        }
    }

    void writeToFile(File output_file){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(output_file));
            bw.write(NUM_BODIES + "\n");
            bw.write(UNIVERSE_SIZE + "\n");
            for(Body b : bodies)
                bw.write(b.getX() + " " + b.getY() + " " + b.getVx() + " " + b.getVy() + " " + b.getMass() + " " + b.getName() + "\n");
            bw.close();
        } catch (IOException e) {
            System.out.println("Error while writing to file:\n" + e);
            System.exit(1);
        }
    }  

    void startSimulationSequential(){
        BHtree root = new BHtree(new Quad(new Point(0, 0), UNIVERSE_SIZE));

        for(Body b : bodies)
            root.insertBody(b);

      //root.printTree(root);
        for(Body b : bodies){
            root.netForce(b);
            b.updatePosition();
        }
    }

    void startSimulationParallel(Thread[] threads, CyclicBarrier barrier) throws InterruptedException{
        BHtree root = new BHtree(new Quad(new Point(0, 0), UNIVERSE_SIZE));

        for(Body b : bodies)
            root.insertBody(b);

        for(int i = 0; i < NUM_THREADS; i++){
            int from = i * (NUM_BODIES / NUM_THREADS);
            int to;

            if(i == NUM_THREADS - 1)
                to = NUM_BODIES;
            else
                to = from + (NUM_BODIES / NUM_THREADS);

            threads[i] = new Thread(new BHrunable(root, bodies, from, to, barrier));
            threads[i].start();
        }

        for(int i = 0; i < NUM_THREADS; i++)
            threads[i].join();
    }
}
