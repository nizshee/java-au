package task;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {

    public static final Predicate<Integer> GE = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x >= 0;
        }
    };

    public static final Predicate<Integer> LE = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x <= 0;
        }
    };


    @org.junit.Test
    public void testApply() throws Exception {
        assertTrue(GE.apply(1));
        assertFalse(LE.apply(1));
    }

    @org.junit.Test
    public void testOr() throws Exception {
        Indicator indicator = new Indicator();
        assertTrue(GE.or(indicator).apply(1));
        assertFalse(indicator.wasApplied());
        assertTrue(LE.or(GE).apply(0));
    }

    @org.junit.Test
    public void testAnd() throws Exception {
        assertTrue(GE.and(LE).apply(0));
        Indicator indicator = new Indicator();
        assertFalse(LE.and(indicator).apply(1));
        assertFalse(indicator.wasApplied());
    }

    @org.junit.Test
    public void testNot() throws Exception {
        assertTrue(GE.not().apply(-1));
        assertFalse(GE.not().apply(0));
        assertFalse(GE.not().apply(1));
    }

    @org.junit.Test
    public void testAlways() throws Exception {
        assertTrue(Predicate.ALWAYS_TRUE.apply(1));
        assertFalse(Predicate.ALWAYS_FALSE.apply(true));
    }

    private class Indicator extends Predicate<Object> {

        private Boolean applied = false;

        public Boolean wasApplied() {
            return applied;
        }

        @Override
        public Boolean apply(Object o) {
            applied = true;
            return true;
        }
    }

}
