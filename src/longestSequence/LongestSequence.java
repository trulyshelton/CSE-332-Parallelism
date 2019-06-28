package longestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class LongestSequence {
	
	private static ForkJoinPool POOL = new ForkJoinPool();
	
    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
    	return POOL.invoke(new GetLongestTask(val, 0, arr.length, arr, sequentialCutoff)).longestRange;
    }

    private static void usage() {
        System.err.println("USAGE: LongestSequence <number> <array> <sequential cutoff>");
        System.exit(2);
    }
    
    private static class GetLongestTask extends RecursiveTask<SequenceRange> {
		int val, lo, hi, CUTOFF;
		int[] arr;
		
		public GetLongestTask(int val, int lo, int hi, int[] arr, int CUTOFF) {
			this.val = val;
			this.lo = lo;
			this.hi = hi;
			this.CUTOFF = CUTOFF;
			this.arr = arr;
		}
	
		@Override
		public SequenceRange compute() {
			if (hi - lo <= CUTOFF) {
				return sequential(arr, lo, hi, val);
			}
			int mid = lo +  (hi - lo) / 2;
			GetLongestTask LEFT = new GetLongestTask(val, lo, mid, arr, CUTOFF);
			GetLongestTask RIGHT = new GetLongestTask(val, mid, hi, arr, CUTOFF);
			RIGHT.fork();
			SequenceRange leftSR = LEFT.compute();
			SequenceRange rightSR = RIGHT.join();
			int left = leftSR.matchingOnLeft;
			int right = rightSR.matchingOnRight;
			int longest, length = leftSR.sequenceLength + rightSR.sequenceLength;
			if (leftSR.matchingOnRight + rightSR.matchingOnLeft == length) {
				left = length;
				right = length;
				longest = length;
			} else {
				if (leftSR.matchingOnLeft == leftSR.sequenceLength) {
					left = left + rightSR.matchingOnLeft;
					longest = left;
				} else if (rightSR.matchingOnRight == rightSR.sequenceLength) {
					right = right + leftSR.matchingOnRight;
					longest = right;
				}
				longest = Math.max(leftSR.longestRange, rightSR.longestRange);
				longest = Math.max(leftSR.matchingOnRight + rightSR.matchingOnLeft, longest);
			}
			
			return new SequenceRange(left, right, longest, length);
		}
    }
    
    private static SequenceRange sequential(int[] arr, int lo, int hi, int val) {
    	int left = 0, right = 0, longest = 0, length = hi - lo;
    	if (arr[lo] == val) {
    		int i = lo;
    		while(i < hi && arr[i] == val) { left++; i++; }
    	}
    	if (left == length) { 
    		longest = left;
    		right = left;
    	} else {
    		if (arr[hi-1] == val) {
    			int i = hi-1;
    			while(lo <= i && arr[i] == val) { right++; i--; }
    		}
    		int current = 0;
    		while(lo < hi) {
    			if (arr[lo++] == val) {
    				current++;
    			} else {
    				longest = Math.max(current, longest);
    				current = 0;
    			}
    		}
    	}
    	return new SequenceRange(left, right, longest, length);
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
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}