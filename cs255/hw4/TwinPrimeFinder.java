import java.io.*;
import java.util.*;
import java.math.*;

public class TwinPrimeFinder {

	public static int expMod(int a, int x, int n)
	{   
	    if (a == 0)
	        return 0;
	    if (x == 0)
	        return 1;
     
	    long y;
	    if (x % 2 == 0) {
	        y = expMod(a, x / 2, n);
	        y = (y * y) % n;
	    } else {
	        y = a % n;
	        y = (y * expMod(a, x - 1, n) % n) % n;
	    }
	     
	    return (int)((y + n) % n);
	}

	public static int BigIntMod(int a, int x, int n) {
		BigInteger big1,big2,exp,result;  
		big1=new BigInteger(Integer.toString(a));  
        big2=new BigInteger(Integer.toString(x));  
        exp=new BigInteger(Integer.toString(n));  

        result=big1.modPow(exp,big2);  
        return result.intValue();
	}

	public static boolean Witness(int a, int n) {
		// find t and u where n - 1 = 2^t * u
		int[] vars = FindVars(n);
		int t = vars[0];
		int u = vars[1];

		int[] x = new int[t+1];

		x[0] = BigIntMod(a, u, n);

		for (int i = 1; i <= t; i++) {
			x[i] = (int)(Math.pow(x[i-1], 2)) % n;
			int new_n = n-1;
			if (x[i] == 1 && x[i-1] != 1 && x[i-1] != new_n) {
				return true;
			}
		}

		if (x[t] != 1) {
			return true;
		}

		return false;
	}

	public static int[] FindVars(int n) {
		int new_n = n - 1;
		int t = 0;
		int u = 0;
		while (u % 2 != 1) {
			t++;
			u = new_n / (int)(Math.pow(2, t));
		}
		int[] vars = new int[]{t, u};
		return vars;
	}

	public static boolean Miller_Rabin(int n, int s) {
		for (int j = 0; j < s; j++) {
			Random random = new Random();
        	int a = random.nextInt(n-2) + 1;
        	if (Witness(a, n)) {
        		return false;//"Composite";
        	}
		}
		return true;//"Prime";
	}

    public static void main(String[] args) {

        // String n1 = args[0]; // length of binary number
        // String n2 = args[1]; // num trials
        // int num1 = Integer.parseInt(n1);
        // int num2 = Integer.parseInt(n2);
        
        // int low = (int) Math.pow(2, num1-1);
        // int high = (int) Math.pow(2, num1); 

        // for (int i = low + 1; i < high; i=i+2){

        // 	if (Miller_Rabin(i, num2)) {
        // 		System.out.println(Integer.toBinaryString(i));
        // 		break;
        // 	}

        // }

    	System.out.println(Miller_Rabin(1000003, 10));

    }
}

// javac PrimeFinder.java
// java PrimeFinder 3 7