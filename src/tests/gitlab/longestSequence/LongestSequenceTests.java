package tests.gitlab.longestSequence;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import cse332.exceptions.NotYetImplementedException;
import longestSequence.LongestSequence;
import tests.exceptions.InformativeException;
import tests.TestsUtility;

public class LongestSequenceTests extends TestsUtility {
    public static Random RANDOM = new Random(332134);

    public static final int FULLY_SEQUENTIAL = Integer.MAX_VALUE;
    public static final int REASONABLE_CUTOFF = 100;
    public static final int FULLY_PARALLEL = 1;

    public static final int NUM_SMALL_TESTS = 500;
    public static final int NUM_LARGE_TESTS = 5;


    public static final int SMALL_SIZE  = 100;
    public static final int LARGE_SIZE  = 100000;
    public static final int HUGE_SIZE   = 300000000;

    public static void main(String[] args) {
        new LongestSequenceTests().run();
    }

    protected void run() {
        END_WITH_EXIT = true;
        SHOW_TESTS = true;
        PRINT_STDERR = true;
        PRINT_TESTERR = true;

        ALLOWED_TIME = 25000;

        test("checkSmallSequential");
        test("checkSmallParallel");
        test("checkLarge");
        finish();
    }

    public static int checkSmallSequential() { 
        boolean good = true;
        for (int i = 0; i < NUM_SMALL_TESTS; i++) {
            good &= testSmallBitArray(i, FULLY_SEQUENTIAL, 0);
            good &= testSmallBitArray(i, FULLY_SEQUENTIAL, 1);
        }
        return good ? 1 : 0;
    }

    public static int checkSmallParallel() {
        boolean good = true;
        for (int i = 0; i < NUM_SMALL_TESTS; i++) {
            good &= testSmallBitArray(i, FULLY_PARALLEL, 0);
            good &= testSmallBitArray(i, FULLY_PARALLEL, 1);
        }
        return good ? 1 : 0;
    }


    public static int checkLarge() {
        boolean good = true;
        for (int i = 0; i < NUM_LARGE_TESTS; i++) {
            good &= testRandomBitArray(LARGE_SIZE, REASONABLE_CUTOFF, 0);
            good &= testRandomBitArray(LARGE_SIZE, REASONABLE_CUTOFF, 1);
        }
        return good ? 1 : 0;
    }

    public static int checkParallelism() {
        int best = 0, conseq = 0;
        int[] bits = new int[HUGE_SIZE];
        for (int i = 0; i < HUGE_SIZE; i++) {
            bits[i] = RANDOM.nextBoolean() ? 1 : 0;
            if (bits[i] == 0) {
                conseq++;
            }
            else {
                conseq = 0;
            }
            best = Math.max(best, conseq);
        }

        long seqTime, reasonableTime, paraTime = 0; 
        long start = System.currentTimeMillis(); 

        boolean fullySequential =  runTest(0, bits, FULLY_SEQUENTIAL, best) == 1; 
        seqTime = System.currentTimeMillis() - start;

        boolean reasonableCutoff =  runTest(0, bits, REASONABLE_CUTOFF, best) == 1; 
        reasonableTime = System.currentTimeMillis() - (seqTime + start);

        boolean fullyParallel =  runTest(0, bits, FULLY_PARALLEL, best) == 1; 
        paraTime = System.currentTimeMillis() - (reasonableTime + seqTime + start);

        if (!fullySequential || !reasonableCutoff || !fullyParallel) {
            return 0;
        }

        return (paraTime > seqTime && seqTime > reasonableTime) ? 1 : 0;
    }

    private static boolean testSmallBitArray(int num, int cutoff, int match) {
        int best = 0;
        int conseq = 0;
        int[] bits = new int[SMALL_SIZE];
        for (int i = SMALL_SIZE - 1; i >= 0; i--) {
            bits[i] = (num >> i) & 1;
            if (bits[i] == match) {
                conseq++;
            }
            else {
                conseq = 0;
            }
            best = Math.max(best, conseq);
        }
        return runTest(match, bits, cutoff, best) == 1; 
    }

    private static boolean testRandomBitArray(int size, int cutoff, int match) {
        int best = 0;
        int conseq = 0;
        int[] bits = new int[size];
        for (int i = 0; i < size; i++) {
            bits[i] = RANDOM.nextBoolean() ? 1 : 0;
            if (bits[i] == match) {
                conseq++;
            }
            else {
                conseq = 0;
            }
            best = Math.max(best, conseq);
        }
        return runTest(match, bits, cutoff, best) == 1; 
    }

    private static int runTest(int num, int[] array, int cutoff, int expected) {
        return LongestSequence.getLongestSequence(num, array, cutoff) == expected ? 1 : 0;
    }
}
