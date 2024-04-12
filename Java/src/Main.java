import java.io.*;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static void main(String[] args) throws InterruptedException{

        Utils utils = new Utils();

        if(args.length != 4) {
            System.out.println("Invalid number of arguments. Correct usage: java Main <input_file> #iterations #threads <output_file>");
            System.exit(1);
        }

        try{
            Utils.ITERATIONS = Integer.parseInt(args[1]);
            Utils.NUM_THREADS = Integer.parseInt(args[2]);
        }
        catch(Exception e) {
            System.out.println("Error : " + e);
            System.out.println("Correct usage: java Main <input_file> #iterations #threads <output_file>");
            System.exit(1);
        }
        utils.parseFile(new File(args[0]));

        if(Utils.NUM_THREADS <= 0){
            for(int i = 0; i < Utils.ITERATIONS; i++)
                utils.startSimulationSequential();
        }
        else{
            Thread threads[] = new Thread[Utils.NUM_THREADS];
            CyclicBarrier barrier = new CyclicBarrier(Utils.NUM_THREADS);
            for(int i = 0; i < Utils.ITERATIONS; i++)
                utils.startSimulationParallel(threads, barrier);
        }

        utils.writeToFile(new File(args[3]));

    }

  
}