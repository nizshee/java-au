package task;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class CollectionsTest {

    public static final Integer[] array = {-2, -1, 0, 1, 2};
    public static final Integer[] arraySquared = {4, 1, 0, 1, 4};
    public static final Integer[] arrayFiltered = {0, 1, 2};
    public static final Integer[] arrayTakenWhile = {-2, -1, 0};
    public static final Integer[] arrayTakenUnless = {-2, -1};
    public static final Integer[] arrayFold = {2, 3};
    public static final Iterable<Integer> list = Arrays.asList(array);
    public static final Iterable<Integer> listSquared = Arrays.asList(arraySquared);
    public static final Iterable<Integer> listFiltered = Arrays.asList(arrayFiltered);
    public static final Iterable<Integer> listTakenWhile = Arrays.asList(arrayTakenWhile);
    public static final Iterable<Integer> listTakenUnless = Arrays.asList(arrayTakenUnless);
    public static final Iterable<Integer> listFold = Arrays.asList(arrayFold);

    @org.junit.Test
    public void testMap() throws Exception {
        assertEquals(Collections.map(Function1Test.square, list), listSquared);
    }

    @org.junit.Test
    public void testFilter() throws Exception {
        assertEquals(Collections.filter(PredicateTest.ge, list), listFiltered);
    }

    @org.junit.Test
    public void testTakeWhile() throws Exception {
        assertEquals(Collections.takeWhile(PredicateTest.le, list), listTakenWhile);
    }

    @org.junit.Test
    public void testTakeUntil() throws Exception {
        assertEquals(Collections.takeUnless(PredicateTest.ge, list), listTakenUnless);
    }

    @org.junit.Test
    public void testTakeFoldr() throws Exception {
        assertEquals(Collections.foldl(Function2Test.pow, 2, listFold).intValue(), 64);
    }

    @org.junit.Test
    public void testTakeFoldl() throws Exception {
        assertEquals(Collections.foldl(Function2Test.pow, 2, listFold).intValue(), 64);
    }


}
