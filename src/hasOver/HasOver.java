package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class HasOver {
	
	private static ForkJoinPool POOL = new ForkJoinPool();
	
    public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {
        return POOL.invoke(new HasOverTask(val, 0, arr.length, arr, sequentialCutoff));
    }
    
    private static boolean sequentialHasOver(int[] arr, int lo, int hi, int val) {
    		while (lo < hi) {
    			if (arr[lo++] > val) { return true; }
    		}
    		return false;
    }
    
    private static class HasOverTask extends RecursiveTask<Boolean> {
    		int val, lo, hi, CUTOFF;
    		int[] arr;
    		
    		public HasOverTask(int val, int lo, int hi, int[] arr, int CUTOFF) {
    			this.val = val;
    			this.lo = lo;
    			this.hi = hi;
    			this.CUTOFF = CUTOFF;
    			this.arr = arr;
    		}
    	
		@Override
		public Boolean compute() {
			if (hi - lo <= CUTOFF) {
				return sequentialHasOver(arr, lo, hi, val);
			}
			int mid = lo +  (hi - lo) / 2;
			HasOverTask left = new HasOverTask(val, lo, mid, arr, CUTOFF);
			HasOverTask right = new HasOverTask(val, mid, hi, arr, CUTOFF);
			right.fork();
			return left.compute() || right.join();
		}
    	
    }

    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]); 
            String[] stringArr = args[1].replaceAll("\\s*",  "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
        
    }
}
