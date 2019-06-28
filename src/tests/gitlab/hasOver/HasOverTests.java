package tests.gitlab.hasOver;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import cse332.exceptions.NotYetImplementedException;
import hasOver.HasOver;
import tests.exceptions.InformativeException;
import tests.TestsUtility;

public class HasOverTests extends TestsUtility {
    public static int[] TEST_ARRAY_0 = new int[] {2, 17, 19, 8, 21, 17, 35, 0, 4, 1};
    public static int[] TEST_ARRAY_1 = new int[] {-2, -17, -19, -8, -21, -17, -35, -4, -1};
    public static Random RANDOM = new Random(332134);

    public static final int FULLY_SEQUENTIAL = Integer.MAX_VALUE;
    public static final int REASONABLE_CUTOFF = 100;
    public static final int FULLY_PARALLEL = 1;

    public static final int MEDIUM_SIZE = 1000;
    public static final int MEDIUM_MAX  = 100;

    public static final int LARGE_SIZE  = 2000000;
    public static final int LARGE_MAX   = 1000;

    public static final int HUGE_SIZE   = 6000000;

    public static void main(String[] args) { 
        new HasOverTests().run();
    }

    protected void run() {
        END_WITH_EXIT = true;
        SHOW_TESTS = true;

        ALLOWED_TIME = 19000;

        for (int i = 1; i < 4; i++) {
            test("tiny" + i);
            test("negative" + i);
        }

        for (int i = 1; i < 9; i++) {
            test("medium" + i);
            test("large" + i);
        }

        finish();
    }
 
    public static int tiny1() { return runTest(17, TEST_ARRAY_0, true); }
    public static int tiny2() { return runTest(35, TEST_ARRAY_0, false); }
    public static int tiny3() { return runTest(40, TEST_ARRAY_0, false); }

    public static int negative1() { return runTest(-17, TEST_ARRAY_1, true); }
    public static int negative2() { return runTest(-1, TEST_ARRAY_1, false); }
    public static int negative3() { return runTest(0, TEST_ARRAY_1, false); }


    public static int medium1() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, -1), false); }
    public static int medium2() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, 0), true); }
    public static int medium3() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, MEDIUM_SIZE - 1), true); }
    public static int medium4() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, MEDIUM_SIZE / 2), true); }
    public static int medium5() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, MEDIUM_SIZE / 4 - 1), true); }
    public static int medium6() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, MEDIUM_SIZE / 2 + 17), true); }
    public static int medium7() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, 17), true); }
    public static int medium8() { return runTest(MEDIUM_MAX, create(MEDIUM_SIZE, MEDIUM_MAX, -1), false); }

    public static int large1() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, -1), false); }
    public static int large2() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, 0), true); }
    public static int large3() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, LARGE_SIZE - 1), true); }
    public static int large4() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, LARGE_SIZE / 2), true); }
    public static int large5() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, LARGE_SIZE / 4 - 1), true); }
    public static int large6() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, LARGE_SIZE / 2 + 17), true); }
    public static int large7() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, 17), true); }
    public static int large8() { return runTest(LARGE_MAX, create(LARGE_SIZE, LARGE_MAX, -1), false); }

    public static int checkParallelism() {
        int best = 0, conseq = 0;
        int[] input = create(HUGE_SIZE, LARGE_MAX, -1);

        long seqTime, reasonableTime, paraTime = 0; 
        long start = System.currentTimeMillis(); 

        boolean fullySequential = runTest(LARGE_MAX, input, false, FULLY_SEQUENTIAL) == 1;
        seqTime = System.currentTimeMillis() - start;

        boolean reasonableCutoff = runTest(LARGE_MAX, input, false, REASONABLE_CUTOFF) == 1;
        reasonableTime = System.currentTimeMillis() - (seqTime + start);

        boolean fullyParallel = runTest(LARGE_MAX, input, false, FULLY_PARALLEL) == 1;
        paraTime = System.currentTimeMillis() - (reasonableTime + seqTime + start);

        if (!fullySequential || !reasonableCutoff || !fullyParallel) {
            return 0;
        }

        double seq = seqTime;
        double reasonable = reasonableTime;
        double para = paraTime;

        return (seq > para && para > reasonable) ? 1 : 0;
    }

    public static int[] create(int size, int max, int where) {
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = RANDOM.nextInt(max);
        }

        // Place one value > max
        if (where > -1) {
            array[where] = max + 1;
        }

        return array;
    }

    private static int runTest(int num, int[] array, boolean expected) {
        return runTest(num, array, expected, REASONABLE_CUTOFF);
    }

    private static int runTest(int num, int[] array, boolean expected, int cutoff) {
        String numString = Integer.toString(num);
        String arrayString = Arrays.toString(array);
        arrayString = arrayString.substring(1, arrayString.length() - 1); 
        String cutoffString = Integer.toString(cutoff);
        String result = runMain(HasOver.class, new String[]{numString, arrayString, cutoffString}).trim();
        return (result != null && result.equals(expected ? "true" : "false")) ? 1 : 0;
    }
}
