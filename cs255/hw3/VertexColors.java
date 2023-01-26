import java.io.*;
import java.util.*;

public class VertexColors extends Thread {

    static ArrayList<String> arr;
    static Map<Integer, ArrayList<Integer>> adjList;
    static ArrayList<ArrayList<Integer>> lists;
    static int[][] adjMat;
    boolean done;

    public static boolean markRandom(int prob){
        double random = 1 + (Math.random() * prob);
        if ((int)random == 1) {
            return true;
        }
        return false;
    }

    // TODO: parallelize
    public static void makeAdjList(ArrayList<String> arr) {

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

            makeAdjList(arr);

            // I = empty set

            System.out.println(adjList.keySet());
            System.out.println(adjList.values());
            

            while (!adjList.isEmpty()) {
            ArrayList<Integer> list = new ArrayList<>();
            
            // TODO: parallelize
            Map<Integer, Boolean> mark = new HashMap<>();

            for (int key : adjList.keySet()) {
                // if d(v) = 0, add to I and delete v from V
                int prob = 2 * adjList.get(key).size();
                if (prob == 0) {
                    list.add(key);

                    continue;
                }
                boolean isMarked = markRandom(prob);
                mark.put(key, isMarked);
            }

            System.out.println(mark.keySet());
            System.out.println(mark.values());

            for (int key1 : adjList.keySet()) {
                for (int key2 : adjList.keySet()) {
                    if (key1 == key2)
                        continue;
                    if (adjList.get(key1).contains(key2) && mark.get(key1) == true && mark.get(key2) == true) {
                        if (key1 < key2) {
                            mark.replace(key1, false);
                        } else {
                            mark.replace(key2, false);
                        }
                    }
                }
            }

            System.out.println("AFTER UNMARKING");
            System.out.println(mark.keySet());
            System.out.println(mark.values());
            
            for (int key : mark.keySet()) {
                if (mark.get(key)) {
                    list.add(key);
                }
            }

            // I = I union S
            if (!list.isEmpty())
                lists.add(list);
            else{
                System.out.println("No elements, continue");
                continue;
            }

            // delete S union gamma(S) from V and all incident edges
            for (int i = 0; i < list.size(); i++) {
                // go into each of key's vertices and remove key from their lists
                int key = list.get(i);
                for (int j = 0; j < adjList.get(key).size(); j++){
                    int vertex = adjList.get(key).get(j);
                    adjList.get(vertex).remove(Integer.valueOf(key));
                    adjList.get(key).remove(Integer.valueOf(j));
                }
                adjList.remove(Integer.valueOf(key));
            }

            System.out.println(adjList.keySet());
            System.out.println(adjList.values());
        }

        for (int i = 0; i < lists.size(); i++){
            for (int j = 0; j < lists.get(i).size(); j++) {
                System.out.println(lists.get(i).get(j) + "," + i);
            }
        }

            // VertexColorThreads thread = new VertexColorThreads(null, 0, arr.size() - 1, Integer.MIN_VALUE);
            // thread.start();
            
        } catch (IOException e) {
            System.out.println("Could not find file: " + filename);
            e.printStackTrace();
        }
    }
}

// javac VertexColorThreads.java
// java VertexColorThreads file.txt