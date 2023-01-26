import java.io.*;
import java.util.*;

public class ApproxTSP {

	static int[][] adjList;

	public static void ApproxTSPTour(int[][] graph, int cost) {
		// select a vertex r in graph to be a root vertex
		// T = MST-Prim(G, c, r)
		// 
	}

	public static void makeAdjList(ArrayList<String> arr) {
		int size = arr.size() + 1;
		adjList = new int[size][size];
		// for i = 1 to n, check if character is not 0, if so add to adjList, else move on
		for (int i  = 1; i < size; i++) {
			int index = i;
			String[] split = arr.get(i-1).split(" ");
			for (int j = 1; j <= split.length; j++) {
				adjList[i][j] = Integer.parseInt(split[j-1]);;
			}
		}

	}

    public static void main(String[] args) {

    	ArrayList<String> arr = new ArrayList<>();
        try {
            File file = new File(args[0]);
            FileReader fr = new FileReader(file);

            BufferedReader br = new BufferedReader(fr);
            String line;
            
            while ((line = br.readLine()) != null) {
                arr.add(line);
            }
            fr.close();

            // make an adjacency list of the input
            makeAdjList(arr);

        } catch (IOException e) {
            System.out.println("Could not find file: " + args[0]);
            e.printStackTrace();
        }

        int size = arr.size() + 1;
        
        for (int i = 1; i < size; i++) {
        	for (int j = 1; j < size; j++) 
        		System.out.print(adjList[i][j] + " ");
        	System.out.println();
        }


    }
}

// javac ApproxTSP.java
// java ApproxTSP