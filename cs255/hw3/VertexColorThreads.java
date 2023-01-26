import java.io.*;
import java.util.*;

public class VertexColorThreads extends Thread {

    static ArrayList<String> arr;
    static ArrayList<Integer> keys;
    static ArrayList<Integer> list;
    static ArrayList<ArrayList<Integer>> lists;
    static Map<Integer, Boolean> mark;
    static Map<Integer, ArrayList<Integer>> adjList;
    VertexColorThreads spawner;
    int low;
    int high;
    boolean marking;
    boolean unmarking;
    boolean adding;
    boolean done;

    public VertexColorThreads(VertexColorThreads spawner, int low, int high, boolean marking, boolean unmarking, boolean adding) {
        this.spawner = spawner;
        this.low = low;
        this.high = high;
        this.marking = marking;
        this.unmarking = unmarking;
        this.adding = adding;
        done = false;
    }

    public void run() {
        VertexColorThreads firstHalf = null;
        VertexColorThreads secondHalf = null;

        if (marking){
            // for all v in V do in parallel
            //     if d(v) = 0 then add v to I and delete v from V
            //     else mark v with probability 1/(2d(v))
            if (low == high) {
                int key = keys.get(low);
                int prob = 2 * adjList.get(key).size();
                if (prob == 0) {
                    synchronized(this){
                        list.add(key);
                    }
                }
                else {
                    synchronized(this){
                        boolean isMarked = markRandom(prob);
                        mark.put(key, isMarked);                       
                    }
                }
            } else {
                int mid = (low + high) / 2;
                firstHalf = new VertexColorThreads(this, low, mid, true, false, false);
                secondHalf = new VertexColorThreads(this, mid+1, high, true, false, false);
                firstHalf.start();
                secondHalf.start();
            }
            if (low != high) {
                firstHalf.sync();
                secondHalf.sync();
            }
            done = true;
        }
        else if (unmarking){
            // for all (u,v) in E do in parallel
            //     if both u and v are marked
            //         then unmark the lower degree vertex
            if (low == high) {
                synchronized(this){
                    int key1 = keys.get(low);
                    for (int ind2 = low + 1; ind2 < keys.size(); ind2++) {
                        int key2 = keys.get(ind2);
                        if (mark.get(key1) != null && mark.get(key2) != null){
                            if (adjList.get(key1).contains(key2) && mark.get(key1) == true && mark.get(key2) == true) {
                                if (key1 < key2) {
                                    mark.replace(key1, false);
                                } else {
                                    mark.replace(key2, false);
                                }
                            }
                        }
                    }
                }
            } else {
                int mid = (low + high) / 2;
                firstHalf = new VertexColorThreads(this, low, mid, false, true, false);
                secondHalf = new VertexColorThreads(this, mid+1, high, false, true, false);
                firstHalf.start();
                secondHalf.start();
            }
            if (low != high) {
                firstHalf.sync();
                secondHalf.sync();
            } 

            done = true;
        }
        else if (adding){
            // for all v in V do in parallel
            //     if v is marked then add v to S
            if (low == high) {
                synchronized(this){
                    
                    int key = keys.get(low);
                    if (mark.get(key) != null){
                        if (mark.get(key)) {
                            list.add(key);
                        }
                    }
                }

            } else {
                int mid = (low + high) / 2;
                firstHalf = new VertexColorThreads(this, low, mid, false, false, true);
                secondHalf = new VertexColorThreads(this, mid+1, high, false, false, true);
                firstHalf.start();
                secondHalf.start();
            }

            if (low != high) {
                firstHalf.sync();
                secondHalf.sync();
            } 

            done = true;
            
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

    public static boolean markRandom(int prob){
        Random random = new Random();
        int num = random.nextInt(prob);
        if (num == 1) {
            return true;
        }
        return false;
    }

    public static void makeAdjList(ArrayList<String> arr) {
        // adjacency list keeps track of which value is connected to which other vertices
        adjList = new HashMap<>();

        for (int i = 0; i < arr.size(); i++) {
            String[] split = arr.get(i).split(",");
            int vertex1 = Integer.parseInt(split[0]);
            int vertex2 = Integer.parseInt(split[1]);

            if (!adjList.containsKey(vertex1)){
                ArrayList<Integer> newList = new ArrayList<>();
                newList.add(vertex2);
                adjList.put(vertex1, newList);
            } else {
                adjList.get(vertex1).add(vertex2);
            }

            if (!adjList.containsKey(vertex2)){
                ArrayList<Integer> newList = new ArrayList<>();
                newList.add(vertex1);
                adjList.put(vertex2, newList);
            } else {
                adjList.get(vertex2).add(vertex1);
            }
        }
    }

    public static void main(String[] args) {
        String filename = args[0];

        lists = new ArrayList<ArrayList<Integer>>();
        // look for file name and add to array
        arr = new ArrayList<>();
        try {
            File file = new File(filename);
            FileReader fr = new FileReader(file);

            BufferedReader br = new BufferedReader(fr);
            String line;
            
            while ((line = br.readLine()) != null) {
                arr.add(line);
            }
            fr.close();

            // make an adjacency list of the input
            makeAdjList(arr);

            while (!adjList.isEmpty()) {

                keys = new ArrayList<>();
                list = new ArrayList<>();
                mark = new HashMap<>();

                for (int key : adjList.keySet()) {
                    keys.add(key);
                }

                VertexColorThreads markThread = new VertexColorThreads(null, 0, keys.size()-1, true, false, false);
                markThread.start();
                markThread.sync();

                VertexColorThreads unmarkThread = new VertexColorThreads(null, 0, keys.size()-1, false, true, false);
                unmarkThread.start();
                unmarkThread.sync();

                VertexColorThreads addThread = new VertexColorThreads(null, 0, keys.size()-1, false, false, true);
                addThread.start();
                addThread.sync();

                // I = I union S
                if (!list.isEmpty())
                    lists.add(list);

                // delete S union gamma(S) from V and all incident edges
                for (int i = 0; i < list.size(); i++) {
                    // go into each of key's vertices and remove key from their lists
                    if (list.get(i) != null){
                        int key = list.get(i);
                        for (int j = 0; j < adjList.get(key).size(); j++){
                            int vertex = adjList.get(key).get(j);
                            if (adjList.containsKey(vertex))
                                adjList.get(vertex).remove(Integer.valueOf(key));
                            adjList.get(key).remove(Integer.valueOf(j));
                        }
                        adjList.remove(Integer.valueOf(key));                        
                    }
                }

            }

            // output results
            for (int i = 0; i < lists.size(); i++){
                for (int j = 0; j < lists.get(i).size(); j++) {
                    System.out.println(lists.get(i).get(j) + "," + i);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Could not find file: " + filename);
            e.printStackTrace();
        }
    }
}

// javac VertexColorThreads.java
// java VertexColorThreads file.txt