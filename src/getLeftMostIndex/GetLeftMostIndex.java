package getLeftMostIndex;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
	private static ForkJoinPool POOL = new ForkJoinPool();
	
    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        return POOL.invoke(new FOUND(needle, haystack, 0, haystack.length - needle.length + 1, sequentialCutoff));
    }

    private static int sequential(char[] needle, char[] haystack, int lo, int hi) {
    	while (lo < hi && lo + needle.length <= haystack.length) {
	    	int i = 0;
	    	while (needle[i] == haystack[lo+i]) {
	    		if (i == needle.length - 1) {
	    			return lo;
	    		}
	    		i++;
	    	}
	    	lo++;
    	}
    	return -1;
    }
    
    private static class FOUND extends RecursiveTask<Integer> {
    	char[] needle, haystack;
    	int lo, hi, cutoff;
    	
    	public FOUND(char[] needle, char[] haystack, int lo, int hi, int cutoff) {
    		this.haystack = haystack;
    		this.needle = needle;
    		this.lo = lo;
    		this.hi = hi;
    		this.cutoff = cutoff;
    	}
    	
		@Override
		protected Integer compute() {
			if (hi - lo <= cutoff) {
				return sequential(needle, haystack, lo, hi);
			}
			int mid = lo +  (hi - lo) / 2;
			FOUND left = new FOUND(needle, haystack, lo, mid, cutoff);
			FOUND right = new FOUND(needle, haystack, mid, hi, cutoff);
			right.fork();
			int leftResult = left.compute();
			int rightResult = right.join();
			return leftResult == -1 ? rightResult : leftResult;
		}
    	
    }
    
    
    
    
    
    
    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
