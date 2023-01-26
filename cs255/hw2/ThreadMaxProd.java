import java.io.*;
import java.util.*;

public class ThreadMaxProd extends Thread {

    static ArrayList<String> arr;
    int low;
    int high;
    int c;
    int highestVal;
    int index;
    ThreadMaxProd spawner;
    boolean done;

    public ThreadMaxProd(ThreadMaxProd spawner, int low, int high, int highestVal) {
        this.spawner = spawner;
        this.low = low;
        this.high = high;
        this.highestVal = highestVal;
        done = false;
        index = -1;
        c = 0;
    }

    public void run() {
        // for all in the array, perform the product operation
        // split array into half, until we get to one 
        // multiply and give index of "low"
        ThreadMaxProd firstHalf = null;
        ThreadMaxProd secondHalf = null;
        done = false;

        if (low == high) {
            index = low;
            String[] split = arr.get(low).split(",");
            c = Integer.parseInt(split[0]) * Integer.parseInt(split[1]);
            highestVal = Math.max(highestVal, c);
        }
        else {
            int mid = (low + high) / 2;
            firstHalf = new ThreadMaxProd(this, low, mid, this.highestVal);
            secondHalf = new ThreadMaxProd(this, mid+1, high, this.highestVal);
            firstHalf.start();
            secondHalf.start();
        }
        if (low != high) {
            firstHalf.sync();
            secondHalf.sync();
            highestVal = Math.max(firstHalf.highestVal, secondHalf.highestVal);
            index = highestVal == firstHalf.highestVal ? firstHalf.index : secondHalf.index;
        }
        done = true;
        if (spawner == null) {
            System.out.println(index + "," + highestVal);
        }
    }

    public synchronized void sync() {
        if (!done) {
            try {
                wait();
            }
            catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String filename = args[0];

        // look for file name and add to array
        arr = new ArrayList<>();
        try {
            File file = new File(filename + ".txt");
            FileReader fr = new FileReader(file);

            BufferedReader br = new BufferedReader(fr);
            String line;
            
            while ((line = br.readLine()) != null) {
                arr.add(line);
            }
            fr.close();
            
            // perform operation and return value
            // long startTime = System.nanoTime();
            ThreadMaxProd parent = new ThreadMaxProd(null, 0, arr.size() - 1, Integer.MIN_VALUE);
            parent.start();
            // long endTime = System.nanoTime();
            // long duration = (endTime - startTime);
            // System.out.println("Runtime: " + duration + " s");
            
        } catch (IOException e) {
            System.out.println("Could not find file: " + filename + ".txt");
            e.printStackTrace();
        }
    }
}

// javac ThreadMaxProd.java
// java ThreadMaxProd file