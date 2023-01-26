import java.io.*;
import java.util.*;

public class MaxProd {
    public static void main(String[] args) {
        // TODO: accept input of filename
        System.out.println("Please enter file name with vector data:");
        Scanner sc = new Scanner(System.in);
        String filename = sc.nextLine();

        ArrayList<String> arr = new ArrayList<>();

        try {
            File file = new File(filename + ".txt");
            FileReader fr = new FileReader(file);

            BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
            StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
            String line;
            
            
            while ((line = br.readLine()) != null) {
                arr.add(line);
            }
            fr.close(); // closes the stream and release the resources
            
        } catch (IOException e) {
            System.out.println("Could not find file: " + filename + ".txt");
            e.printStackTrace();
        }
        int highestVal = Integer.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < arr.size(); i++) {
            String[] split = arr.get(i).split(",");
            int result = Integer.parseInt(split[0]) * Integer.parseInt(split[1]);
            if (highestVal < result) {
                highestVal = result;
                index = i;
            }
        }
        System.out.println(index + "," + highestVal);
    }
}
