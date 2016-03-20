package task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {

    public static final Predicate<Integer> ge = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x >= 0;
        }
    };

    public static final Predicate<Integer> le = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x <= 0;
        }
    };

    @org.junit.Test
    public void testApply() throws Exception {
        assertTrue(ge.apply(1));
        assertFalse(le.apply(1));
    }

    @org.junit.Test
    public void testOr() throws Exception {
        assertTrue(ge.or(le).apply(1));
        assertTrue(le.or(ge).apply(0));
    }

    @org.junit.Test
    public void testAnd() throws Exception {
        assertTrue(ge.and(le).apply(0));
        assertFalse(le.and(ge).apply(1));
    }

    @org.junit.Test
    public void testNot() throws Exception {
        assertTrue(ge.not().apply(-1));
        assertFalse(ge.not().apply(0));
        assertFalse(ge.not().apply(1));
    }

    @org.junit.Test
    public void testAlways() throws Exception {
        assertTrue(Predicate.ALWAYS_TRUE.apply(1));
        assertFalse(Predicate.ALWAYS_FALSE.apply(true));
    }

}
