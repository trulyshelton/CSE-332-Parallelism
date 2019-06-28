package tests.gitlab.filterEmpty;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import cse332.exceptions.NotYetImplementedException;
import filterEmpty.FilterEmpty;
import tests.exceptions.InformativeException;
import tests.TestsUtility;

public class FilterEmptyTests extends TestsUtility {
    public static Random RANDOM = new Random(332134);

    public static final int NUM_SMALL = 250;
    public static final int SMALL_SIZE  = 10;

    public static final int NUM_LARGE = 10;
    public static final int LARGE_SIZE  = 100000;

    public static final int MAX_STRING_LENGTH = 30;

    public static void main(String[] args) {
        new FilterEmptyTests().run();
    }

    protected void run() {
        END_WITH_EXIT = true;
        SHOW_TESTS = true;

        ALLOWED_TIME = 15000;

        test("checkSmall");
        test("checkLarge");
        finish();
    }

    public static int checkSmall() { 
        boolean good = true;
        for (int i = 0; i < NUM_SMALL; i++) {
            String[] input = makeInput(i, SMALL_SIZE);
            int[] output = filter(input);
            good &= runTest(input, output) == 1;
        }
        return good ? 1 : 0;
    }

    public static int checkLarge() {
        boolean good = true;
        for (int i = 0; i < NUM_LARGE; i++) {
            String[] input = makeRandomInput(LARGE_SIZE);
            good &= runTest(input, filter(input)) == 1;
        }
        return good ? 1 : 0;
    }

    private static String makeStringOfLength(int length) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            str.append("" + ('a' + RANDOM.nextInt(26)));
        }
        return str.toString();
    }

    private static String[] makeInput(int num, int size) {
        String[] input = new String[size];
        for (int i = size - 1; i >= 0; i--) {
            input[i] = ((num >> i) & 1) == 1 ? makeStringOfLength(RANDOM.nextInt(MAX_STRING_LENGTH) + 1) : "";
        }
        return input;
    }

    private static int[] filter(String[] input) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < input.length; i++) {
            if (input[i].length() > 0) {
                list.add(input[i].length());
            }
        }
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    private static String[] makeRandomInput(int size) {
        String[] input = new String[size];
        for (int i = size - 1; i >= 0; i--) {
            input[i] = RANDOM.nextBoolean() ? makeStringOfLength(RANDOM.nextInt(MAX_STRING_LENGTH) + 1) : "";
        }
        return input;
    }

    private static int runTest(String[] input, int[] expected) {
        String inputString = Arrays.toString(input);
        if (inputString.length() > 0) {
            inputString = inputString.substring(1, inputString.length() - 1);
        }
        String result = runMain(FilterEmpty.class, new String[]{inputString}).trim();
        return (result != null && result.equals(Arrays.toString(expected))) ? 1 : 0;
    }
}
