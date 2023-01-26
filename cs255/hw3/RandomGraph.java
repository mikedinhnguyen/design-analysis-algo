import java.io.*;
import java.util.*;

public class RandomGraph {

    static Map<Integer, ArrayList<Integer>> edgeList;

    public static boolean coinFlip(double edgeProb) {
        // take probability like .33 * 100 = 33
        // choose a random number from 1 to 100
        // if value in 1 to probability val (like 33 for example), return true

        int prob = (int) (edgeProb * 100);

        Random random = new Random();
        int coin = random.nextInt(100);
        if (coin >= 1 && coin <= prob) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        edgeList = new HashMap<>();
        int numberVertices = Integer.parseInt(args[0]);
        double edgeProb = Double.parseDouble(args[1]);

        // for each vertex loop through and see if it shares an edge with any other vertex
        for (int i = 0; i < numberVertices; i++) {
            for (int j = 0; j < numberVertices; j++) {
                if (i != j) {
                    // check for edge
                    boolean hasEdge = coinFlip(edgeProb);
                    if (hasEdge) {

                        if (!edgeList.containsKey(i)){
                            // check if i is in j before adding j in i
                            if (edgeList.containsKey(j) && edgeList.get(j).contains(i)){
                                // don't add it if there is already an edge!
                            } else {
                                // add the edge
                                ArrayList<Integer> newList = new ArrayList<>();
                                newList.add(j);
                                edgeList.put(i, newList);                                
                            }

                        } else {
                            if (!edgeList.get(i).contains(j))
                                edgeList.get(i).add(j);
                        }
                    }
                }
            }
        }

        // output the data
        for (int i = 0; i < edgeList.size(); i++){
            if (edgeList.containsKey(i)){
                for (int j = 0; j < edgeList.get(i).size(); j++) {
                    System.out.println(i + "," + edgeList.get(i).get(j));
                }
            }
        }

        if (edgeList.size() == 0) {
            System.out.println("No graph was made with the inputs.");
        }

    }
}

// javac RandomGraph.java
// java RandomGraph 5 0.5