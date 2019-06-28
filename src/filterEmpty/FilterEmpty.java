package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import cse332.exceptions.NotYetImplementedException;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    public static int[] filterEmpty(String[] arr) {
        int[] bitset = mapToBitSet(arr);
//        System.out.println(java.util.Arrays.toString(bitset));
        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bitset);
//        System.out.println(java.util.Arrays.toString(bitsum));
        int[] result = mapToOutput(arr, bitsum);
        return result;
    }

    public static int[] mapToBitSet(String[] arr) {
    	int[] result = new int[arr.length];
        POOL.invoke(new MapToBS(result, arr, 0, result.length, 1));
        return result;
    }

    public static class MapToBS extends RecursiveAction {
    	int[] result;
    	String[] arr;
    	int lo, hi, cutoff;
    	
    	public MapToBS(int[] result, String[] arr, int lo, int hi, int cutoff) {
    		this.result = result;
    		this.arr = arr;
    		this.lo = lo;
    		this.hi = hi;
    		this.cutoff = cutoff;
    	}
    	
		@Override
		protected void compute() {
			if (hi - lo <= cutoff) {
				for (int i = lo; i < hi; i++) {
		        	result[i] = (arr[i] == null || arr[i].isEmpty()) ? 0 : 1;
		        }
			} else {
				int mid = lo + (hi - lo)/2;
				MapToBS left = new MapToBS(result, arr, lo, mid, cutoff);
				MapToBS right = new MapToBS(result, arr, mid, hi, cutoff);
				right.fork();
				left.compute();
				right.join();
			}
			
		}
    	
    }
    
    public static int[] mapToOutput(String[] input, int[] bitsum) {
    	int[] result = new int[bitsum.length > 0 ? bitsum[bitsum.length-1] : 0];
    	POOL.invoke(new MapToOP(result, input, bitsum, 0, input.length, 1));
    	return result;
    }
    
    public static class MapToOP extends RecursiveAction {
    	int[] result, bitsum;
    	String[] arr;
    	int lo, hi, cutoff;
    	
    	public MapToOP(int[] result, String[] arr, int[] bitsum, int lo, int hi, int cutoff) {
    		this.result = result;
    		this.bitsum = bitsum;
    		this.arr = arr;
    		this.lo = lo;
    		this.hi = hi;
    		this.cutoff = cutoff;
    	}
    	
		@Override
		protected void compute() {
			if (hi - lo <= cutoff) {
				for (int i = lo; i < hi; i++) {
					if (bitsum[i] > (i > 0 ? bitsum[i-1] : 0)) {
						result[bitsum[i]-1] = arr[i].length();
					}
		        }
			} else {
				int mid = lo + (hi - lo)/2;
				MapToOP left = new MapToOP(result, arr, bitsum, lo, mid, cutoff);
				MapToOP right = new MapToOP(result, arr, bitsum, mid, hi, cutoff);
				right.fork();
				left.compute();
				right.join();
			}
			
		}
    	
    }
    

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}