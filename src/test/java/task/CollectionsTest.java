package task;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class CollectionsTest {

    public static final Integer[] ARRAY = {-2, -1, 0, 1, 2};
    public static final Integer[] ARRAY_SQUARED = {4, 1, 0, 1, 4};
    public static final Integer[] ARRAY_FILTERED = {0, 1, 2};
    public static final Integer[] ARRAY_TAKEN_WHILE = {-2, -1, 0};
    public static final Integer[] ARRAY_TAKEN_UNLESS = {-2, -1};
    public static final Integer[] ARRAY_FOLD = {2, 3};
    public static final Iterable<Integer> LIST = Arrays.asList(ARRAY);
    public static final Iterable<Integer> LIST_SQUARED = Arrays.asList(ARRAY_SQUARED);
    public static final Iterable<Integer> LIST_FILTERED = Arrays.asList(ARRAY_FILTERED);
    public static final Iterable<Integer> LIST_TAKEN_WHILE = Arrays.asList(ARRAY_TAKEN_WHILE);
    public static final Iterable<Integer> LIST_TAKEN_UNLESS = Arrays.asList(ARRAY_TAKEN_UNLESS);
    public static final Iterable<Integer> LIST_FOLD = Arrays.asList(ARRAY_FOLD);

    @org.junit.Test
    public void testMap() throws Exception {
        assertEquals(Collections.map(Function1Test.SQUARE, LIST), LIST_SQUARED);
    }

    @org.junit.Test
    public void testFilter() throws Exception {
        assertEquals(Collections.filter(PredicateTest.GE, LIST), LIST_FILTERED);
    }

    @org.junit.Test
    public void testTakeWhile() throws Exception {
        assertEquals(Collections.takeWhile(PredicateTest.LE, LIST), LIST_TAKEN_WHILE);
    }

    @org.junit.Test
    public void testTakeUntil() throws Exception {
        assertEquals(Collections.takeUnless(PredicateTest.GE, LIST), LIST_TAKEN_UNLESS);
    }

    @org.junit.Test
    public void testTakeFoldr() throws Exception {
        Number res = Collections.foldr(Function2Test.SND, 1, LIST_FOLD);
        assertEquals(res, new Integer(2));
    }

    @org.junit.Test
    public void testTakeFoldl() throws Exception {
        Number res = Collections.foldl(Function2Test.SND, 1, LIST_FOLD);
        assertEquals(res, new Integer(3));
    }


}
