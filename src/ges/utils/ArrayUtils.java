package ges.utils;

import java.util.ArrayList;

public class ArrayUtils {

	public static ArrayList<int[]> chunkArray(int[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        ArrayList<int[]> output = new ArrayList<int[]>();

        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            int[] temp = new int[length];
            System.arraycopy(array, start, temp, 0, length);
            output.add(temp);
        }

        return output;
    }
	
	  public static int[] concat(int[] array1, int[] array2) {
	        int aLen = array1.length;
	        int bLen = array2.length;
	        int[] result = new int[aLen + bLen];

	        System.arraycopy(array1, 0, result, 0, aLen);
	        System.arraycopy(array2, 0, result, aLen, bLen);

	        return result;
	    }
	
}
